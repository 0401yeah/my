import { AppRouteRecord } from '@/types/router'

export const userRoutes: AppRouteRecord = {
  name: 'UserCenter',
  path: '/user',
  component: '/index/index',
  meta: {
    title: '个人中心',
    icon: 'ri:user-line',
    roles: ['normal']
  },
  children: [

    {
      path: 'profile',
      name: 'UserProfile',
      component: '/user/profile',
      meta: {
        title: '个人信息',
        icon: 'ri:user-line',
        keepAlive: false
      }
    },
    {
      path: 'record',
      name: 'UserRecord',
      component: '/user/record',
      meta: {
        title: '个人记录',
        icon: 'ri:history-line',
        keepAlive: false
      }
    },
    {
      path: 'notification',
      name: 'UserNotification',
      component: '/user/notification',
      meta: {
        title: '通知中心',
        icon: 'ri:bell-line',
        keepAlive: false
      }
    }
  ]
}

export const vehicleRoutes: AppRouteRecord = {
  name: 'VehicleCenter',
  path: '/user/vehicle',
  component: '/index/index',
  meta: {
    title: '车辆中心',
    icon: 'ri:car-line',
    roles: ['normal']
  },
  children: [
    {
      path: 'my-vehicle',
      name: 'MyVehicle',
      component: '/user/vehicle/my-vehicle',
      meta: {
        title: '我的车辆',
        icon: 'ri:car-fill',
        keepAlive: false
      }
    },
    {
      path: 'add-vehicle',
      name: 'AddVehicle',
      component: '/user/vehicle/add-vehicle',
      meta: {
        title: '添加车辆',
        icon: 'ri:car-add-line',
        keepAlive: false
      }
    },
    {
      path: 'record',
      name: 'ParkingRecord',
      component: '/user/vehicle/record',
      meta: {
        title: '停车记录',
        icon: 'ri:history-line',
        keepAlive: false
      }
    },

  ]
}

export const parkingRoutes: AppRouteRecord = {
  name: 'SmartParking',
  path: '/user/parking',
  component: '/index/index',
  meta: {
    title: '智慧停车',
    icon: 'ri:parking-line',
    roles: ['normal']
  },
  children: [
    {
      path: 'nearby',
      name: 'NearbyParking',
      component: '/user/parking/nearby',
      meta: {
        title: '附近停车场',
        icon: 'ri:map-pin-line',
        keepAlive: false
      }
    },
    {
      path: 'reservation',
      name: 'ParkingReservation',
      component: '/user/parking/reservation',
      meta: {
        title: '预约停车',
        icon: 'ri:calendar-book-line',
        keepAlive: false
      }
    },

  ]
}

export const serviceRoutes: AppRouteRecord = {
  name: 'ServiceHall',
  path: '/user/service',
  component: '/index/index',
  meta: {
    title: '服务大厅',
    icon: 'ri:service-line',
    roles: ['normal']
  },
  children: [
    {
      path: 'server',
      name: 'ValetParking',
      component: '/user/service/valet',
      meta: {
        title: '代泊车申请',
        icon: 'ri:car-wash-line',
        keepAlive: false
      }
    },
    {
      path: 'complain',
      name: 'QuickHelp',
      component: '/user/service/help',
      meta: {
        title: '一键求助',
        icon: 'ri:question-line',
        keepAlive: false
      }
    },
    {
      path: 'charge',
      name: 'UserCharge',
      component: '/user/service/charge',
      meta: {
        title: '充电服务',
        icon: 'ri:battery-charge-line',
        keepAlive: false
      }
    }
  ]
}
