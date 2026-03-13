import type { App } from 'vue'
import { createRouter, createWebHashHistory } from 'vue-router'
import { staticRoutes } from './routes/staticRoutes'
import { configureNProgress } from '@/utils/router'
import { setupBeforeEachGuard } from './guards/beforeEach'
import { setupAfterEachGuard } from './guards/afterEach'

// 创建路由实例
export const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    ...staticRoutes,
    {
      path: '/home',
      name: 'Home',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '我的首页',
        icon: 'ri:home-line'
      },
      children: [
        {
          path: '',
          name: 'HomeContent',
          component: () => import('@/views/home/index.vue'),
          meta: {
            title: '我的首页'
          }
        }
      ]
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '数据监控与运营',
        icon: 'ri:dashboard-line'
      },
      children: [
        {
          path: 'board',
          name: 'DashboardBoard',
          component: () => import('@/views/dashboard/board/index.vue'),
          meta: {
            title: '可视化数据看板'
          }
        },
        {
          path: 'zombie',
          name: 'DashboardZombie',
          component: () => import('@/views/dashboard/zombie/index.vue'),
          meta: {
            title: '僵尸车分级预警'
          }
        },
        {
          path: 'strategy',
          name: 'DashboardStrategy',
          component: () => import('@/views/dashboard/strategy/index.vue'),
          meta: {
            title: '运营策略调整'
          }
        }
      ]
    },
    {
      path: '/business',
      name: 'Business',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '基础业务管理',
        icon: 'ri:briefcase-4-line'
      },
      children: [
        {
          path: 'vehicle',
          name: 'BusinessVehicle',
          component: () => import('@/views/business/vehicle/index.vue'),
          meta: {
            title: '车辆综合查询'
          }
        },
        {
          path: 'blacklist',
          name: 'BusinessBlacklist',
          component: () => import('@/views/business/blacklist/index.vue'),
          meta: {
            title: '黑白名单管理'
          }
        },
        {
          path: 'family',
          name: 'BusinessFamily',
          component: () => import('@/views/business/family/index.vue'),
          meta: {
            title: '亲情组监管'
          }
        },
        {
          path: 'record',
          name: 'BusinessRecord',
          component: () => import('@/views/business/record/index.vue'),
          meta: {
            title: '停车记录管理'
          }
        },
        {
          path: 'vas-order',
          name: 'BusinessVasOrder',
          component: () => import('@/views/business/vas-order/index.vue'),
          meta: {
            title: '增值服务订单'
          }
        },
        {
          path: 'customer',
          name: 'BusinessCustomer',
          component: () => import('@/views/system/admin/index.vue'),
          meta: {
            title: '车主账号管理'
          }
        }
      ]
    },
    {
      path: '/config',
      name: 'Config',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '停车场配置',
        icon: 'ri:settings-3-line'
      },
      children: [
        {
          path: 'lot',
          name: 'ConfigLot',
          component: () => import('@/views/config/lot/index.vue'),
          meta: {
            title: '停车场管理'
          }
        },
        {
          path: 'space',
          name: 'ConfigSpace',
          component: () => import('@/views/config/space/index.vue'),
          meta: {
            title: '车位余量管理'
          }
        },

        {
          path: 'facility',
          name: 'ConfigFacility',
          component: () => import('@/views/config/facility/index.vue'),
          meta: {
            title: '综合设施管理'
          }
        },
        {
          path: 'fee-setup',
          name: 'ConfigFeeSetup',
          component: () => import('@/views/config/fee-setup/index.vue'),
          meta: {
            title: '收费标准设置'
          }
        }
      ]
    },
    {
      path: '/finance',
      name: 'Finance',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '财务核算',
        icon: 'ri:money-cny-circle-line'
      },
      children: [
        {
          path: 'order',
          name: 'FinanceOrder',
          component: () => import('@/views/finance/order/index.vue'),
          meta: {
            title: '订单明细查询'
          }
        },
        {
          path: 'report',
          name: 'FinanceReport',
          component: () => import('@/views/finance/report/index.vue'),
          meta: {
            title: '财务报表中心'
          }
        },
        {
          path: 'invoice',
          name: 'FinanceInvoice',
          component: () => import('@/views/finance/invoice/index.vue'),
          meta: {
            title: '发票综合管理'
          }
        },
        {
          path: 'expense',
          name: 'FinanceExpense',
          component: () => import('@/views/parking/finance/expense.vue'),
          meta: {
            title: '支出管理'
          }
        }
      ]
    },
    {
      path: '/service',
      name: 'Service',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '客户互动与服务',
        icon: 'ri:customer-service-2-line'
      },
      children: [
        {
          path: 'complaint',
          name: 'ServiceComplaint',
          component: () => import('@/views/service/complaint/index.vue'),
          meta: {
            title: '咨询与投诉处理'
          }
        },
        {
          path: 'maintain',
          name: 'ServiceMaintain',
          component: () => import('@/views/service/maintain/index.vue'),
          meta: {
            title: '维修工单跟进'
          }
        },
        {
          path: 'notice',
          name: 'ServiceNotice',
          component: () => import('@/views/service/notice/index.vue'),
          meta: {
            title: '通知与公告发布'
          }
        }
      ]
    },
    {
      path: '/system',
      name: 'System',
      component: () => import('@/views/index/index.vue'),
      meta: {
        title: '权限与系统管理',
        icon: 'ri:shield-keyhole-line'
      },
      children: [
        {
          path: 'admin',
          name: 'SystemAdmin',
          component: () => import('@/views/system/admin/index.vue'),
          meta: {
            title: '管理员账号管理'
          }
        },
        {
          path: 'module-switch',
          name: 'SystemModuleSwitch',
          component: () => import('@/views/system/module-switch/index.vue'),
          meta: {
            title: '功能模块开关'
          }
        },
        {
          path: 'api-config',
          name: 'SystemApiConfig',
          component: () => import('@/views/system/api-config/index.vue'),
          meta: {
            title: '第三方接口管理'
          }
        },
        {
          path: 'params',
          name: 'SystemParams',
          component: () => import('@/views/system/params/index.vue'),
          meta: {
            title: '系统参数配置'
          }
        },
        {
          path: 'log',
          name: 'SystemLog',
          component: () => import('@/views/system/log/index.vue'),
          meta: {
            title: '系统操作日志'
          }
        }
      ]
    }
  ]
})

// 初始化路由
export function initRouter(app: App<Element>): void {
  configureNProgress() // 顶部进度条
  setupBeforeEachGuard(router) // 路由前置守卫
  setupAfterEachGuard(router) // 路由后置守卫
  app.use(router)
}

// 主页路径，默认使用菜单第一个有效路径，配置后使用此路径
export const HOME_PAGE_PATH = ''
