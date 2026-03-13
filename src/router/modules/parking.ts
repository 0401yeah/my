import { AppRouteRecord } from '@/types/router'

const parkingRoutes: AppRouteRecord[] = [
  {
    name: 'Console',
    path: '/console',
    component: '/parking/console/index',
    meta: {
      title: '控制台',
      icon: 'ri:dashboard-line',
      roles: ['admin', 'normal']
    }
  },
  {
    name: 'Resource',
    path: '/resource',
    component: '/index/index',
    meta: {
      title: '资源调度',
      icon: 'ri:settings-3-line',
      roles: ['admin', 'normal']
    },
    children: [
      {
        path: 'vehicle',
        name: 'ResourceVehicle',
        component: '/parking/resource/vehicle',
        meta: {
          title: '车辆管理',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'equipment',
        name: 'ResourceEquipment',
        component: '/parking/resource/equipment',
        meta: {
          title: '设备管理',
          roles: ['admin', 'normal']
        }
      }
    ]
  },
  {
    name: 'Config',
    path: '/config',
    component: '/index/index',
    meta: {
      title: '配置管理',
      icon: 'ri:settings-2-line',
      roles: ['admin', 'normal']
    },
    children: [
      {
        path: 'lot',
        name: 'ConfigLot',
        component: '/config/lot/index',
        meta: {
          title: '停车场管理',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'space',
        name: 'ConfigSpace',
        component: '/config/space/index',
        meta: {
          title: '车位管理',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'facility',
        name: 'ConfigFacility',
        component: '/config/facility/index',
        meta: {
          title: '综合设施管理',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'fee-setup',
        name: 'ConfigFeeSetup',
        component: '/config/fee-setup/index',
        meta: {
          title: '收费标准设置',
          roles: ['admin', 'normal']
        }
      },

    ]
  },
  {
    name: 'Vehicle',
    path: '/vehicle',
    component: '/index/index',
    meta: {
      title: '车辆运维',
      icon: 'ri:car-line',
      roles: ['admin', 'normal']
    },
    children: [
      {
        path: 'archive',
        name: 'VehicleArchive',
        component: '/parking/vehicle/archive',
        meta: {
          title: '车辆档案',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'blacklist',
        name: 'VehicleBlacklist',
        component: '/parking/vehicle/blacklist',
        meta: {
          title: '黑白名单',
          roles: ['admin', 'normal']
        }
      }
    ]
  },
  {
    name: 'Business',
    path: '/business',
    component: '/index/index',
    meta: {
      title: '业务流水',
      icon: 'ri:file-list-3-line',
      roles: ['admin', 'normal']
    },
    children: [
      {
        path: 'record',
        name: 'BusinessRecord',
        component: '/parking/business/record',
        meta: {
          title: '停车记录',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'monitor',
        name: 'BusinessMonitor',
        component: '/parking/business/monitor',
        meta: {
          title: '实时监控',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'customer',
        name: 'BusinessCustomer',
        component: '/system/admin',
        meta: {
          title: '车主账号管理',
          roles: ['admin', 'normal']
        }
      }
    ]
  },
  {
    name: 'System',
    path: '/system',
    component: '/index/index',
    meta: {
      title: '系统管理',
      icon: 'ri:settings-4-line',
      roles: ['admin']
    },
    children: [
      {
        path: 'role',
        name: 'SystemRole',
        component: '/parking/system/role',
        meta: {
          title: '角色管理',
          roles: ['admin']
        }
      },
      {
        path: 'log',
        name: 'SystemLog',
        component: '/parking/system/log',
        meta: {
          title: '系统日志',
          roles: ['admin']
        }
      },
      {
        path: 'menu',
        name: 'SystemMenu',
        component: '/parking/system/menu',
        meta: {
          title: '系统菜单',
          roles: ['admin']
        }
      }
    ]
  },
  {
    name: 'Finance',
    path: '/finance',
    component: '/index/index',
    meta: {
      title: '财务审计',
      icon: 'ri:money-cny-circle-line',
      roles: ['admin']
    },
    children: [
      {
        path: 'analysis',
        name: 'FinanceAnalysis',
        component: '/parking/finance/analysis',
        meta: {
          title: '收支分析',
          roles: ['admin']
        }
      },
      {
        path: 'expense',
        name: 'FinanceExpense',
        component: '/parking/finance/expense',
        meta: {
          title: '支出管理',
          roles: ['admin']
        }
      }
    ]
  },
  {
    name: 'Service',
    path: '/service',
    component: '/index/index',
    meta: {
      title: '服务互动',
      icon: 'ri:customer-service-2-line',
      roles: ['admin', 'normal']
    },
    children: [
      {
        path: 'complaint',
        name: 'ServiceComplaint',
        component: '/parking/service/complaint',
        meta: {
          title: '申诉处理',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'announcement',
        name: 'ServiceAnnouncement',
        component: '/parking/service/announcement',
        meta: {
          title: '系统公告',
          roles: ['admin', 'normal']
        }
      },
      {
        path: 'order-follow',
        name: 'ServiceOrderFollow',
        component: '/parking/service/order-follow/index',
        meta: {
          title: '服务工单跟进',
          roles: ['admin', 'normal']
        }
      }
    ]
  },
  {
    name: 'Account',
    path: '/account',
    component: '/index/index',
    meta: {
      title: '账户权限',
      icon: 'ri:user-settings-line',
      roles: ['admin']
    },
    children: [
      {
        path: 'user',
        name: 'AccountUser',
        component: '/parking/account/user',
        meta: {
          title: '用户管理',
          roles: ['admin']
        }
      },
      {
        path: 'partner',
        name: 'AccountPartner',
        component: '/parking/account/partner',
        meta: {
          title: '合作单位',
          roles: ['admin']
        }
      }
    ]
  }
]

export default parkingRoutes
