/**
 * 路由转换器
 *
 * 负责将菜单数据转换为 Vue Router 路由配置
 *
 * @module router/core/RouteTransformer
 * @author Art Design Pro Team
 */

import type { RouteRecordRaw } from 'vue-router'
import type { AppRouteRecord } from '@/types/router'
import { ComponentLoader } from './ComponentLoader'
import { IframeRouteManager } from './IframeRouteManager'

interface ConvertedRoute extends Omit<RouteRecordRaw, 'children'> {
  id?: number
  children?: ConvertedRoute[]
  component?: RouteRecordRaw['component'] | (() => Promise<any>)
}

export class RouteTransformer {
  private componentLoader: ComponentLoader
  private iframeManager: IframeRouteManager

  constructor(componentLoader: ComponentLoader) {
    this.componentLoader = componentLoader
    this.iframeManager = IframeRouteManager.getInstance()
  }

  /**
   * 转换路由配置
   */
  transform(route: AppRouteRecord, depth = 0): ConvertedRoute {
    const { component, children, ...routeConfig } = route

    // 基础路由配置
    const converted: ConvertedRoute = {
      ...routeConfig,
      component: undefined
    }

    // 处理不同类型的路由
    if (route.meta.isIframe) {
      this.handleIframeRoute(converted, route, depth)
    } else if (this.isFirstLevelRoute(route, depth)) {
      // 一级路由：有子路由的目录，或者没有子路由的独立页面
      if (children && children.length > 0) {
        // 有子路由的目录，使用 Layout 包裹
        this.handleDirectoryRoute(converted, route, children)
      } else {
        // 没有子路由的独立页面，使用 Layout 包裹
        this.handleFirstLevelRoute(converted, route, component as string)
      }
    } else {
      // 二级及以下路由：具体的页面组件
      this.handleNormalRoute(converted, component as string)
    }

    // 递归处理子路由（只有一级目录才需要递归处理子路由）
    if (children?.length && depth === 0) {
      converted.children = children.map((child) => this.transform(child, depth + 1))
    }

    return converted
  }

  /**
   * 判断是否为一级路由（需要 Layout 包裹）
   */
  private isFirstLevelRoute(route: AppRouteRecord, depth: number): boolean {
    return depth === 0
  }

  /**
   * 处理目录类型路由（有子路由的一级菜单）
   */
  private handleDirectoryRoute(
    converted: ConvertedRoute,
    route: AppRouteRecord,
    children: AppRouteRecord[]
  ): void {
    // 目录使用 Layout 组件
    converted.component = this.componentLoader.loadLayout()
    // 提取路径的第一段作为路由路径
    converted.path = this.extractFirstSegment(route.path || '')
    // 清空 name，避免路由命名冲突
    converted.name = ''
    // 标记为一级路由
    route.meta.isFirstLevel = true
  }

  /**
   * 处理 iframe 类型路由
   */
  private handleIframeRoute(
    targetRoute: ConvertedRoute,
    sourceRoute: AppRouteRecord,
    depth: number
  ): void {
    if (depth === 0) {
      // 顶级 iframe：用 Layout 包裹
      targetRoute.component = this.componentLoader.loadLayout()
      targetRoute.path = this.extractFirstSegment(sourceRoute.path || '')
      targetRoute.name = ''

      targetRoute.children = [
        {
          ...sourceRoute,
          component: this.componentLoader.loadIframe()
        } as ConvertedRoute
      ]
    } else {
      // 非顶级（嵌套）iframe：直接使用 Iframe.vue
      targetRoute.component = this.componentLoader.loadIframe()
    }

    // 记录 iframe 路由
    this.iframeManager.add(sourceRoute)
  }

  /**
   * 处理一级菜单路由
   */
  private handleFirstLevelRoute(
    converted: ConvertedRoute,
    route: AppRouteRecord,
    component: string | undefined
  ): void {
    converted.component = this.componentLoader.loadLayout()
    converted.path = this.extractFirstSegment(route.path || '')
    converted.name = ''
    route.meta.isFirstLevel = true

    converted.children = [
      {
        ...route,
        component: component ? this.componentLoader.load(component) : undefined
      } as ConvertedRoute
    ]
  }

  /**
   * 处理普通路由
   */
  private handleNormalRoute(converted: ConvertedRoute, component: string | undefined): void {
    if (component) {
      converted.component = this.componentLoader.load(component)
    }
  }

  /**
   * 提取路径的第一段
   */
  private extractFirstSegment(path: string): string {
    const segments = path.split('/').filter(Boolean)
    return segments.length > 0 ? `/${segments[0]}` : '/'
  }
}
