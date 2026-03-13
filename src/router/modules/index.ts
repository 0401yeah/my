import { AppRouteRecord } from '@/types/router'
import { templateRoutes } from './template'
import { widgetsRoutes } from './widgets'
import { examplesRoutes } from './examples'
import { systemRoutes } from './system'
import { articleRoutes } from './article'
import { resultRoutes } from './result'
import { exceptionRoutes } from './exception'
import { safeguardRoutes } from './safeguard'
import { helpRoutes } from './help'
import parkingRoutes from './parking'

export const routeModules: AppRouteRecord[] = [
  ...(parkingRoutes as AppRouteRecord[]),
  templateRoutes as AppRouteRecord,
  widgetsRoutes as AppRouteRecord,
  examplesRoutes as AppRouteRecord,
  systemRoutes as AppRouteRecord,
  articleRoutes as AppRouteRecord,
  resultRoutes as AppRouteRecord,
  exceptionRoutes as AppRouteRecord,
  safeguardRoutes as AppRouteRecord,
  ...(helpRoutes as AppRouteRecord[])
]
