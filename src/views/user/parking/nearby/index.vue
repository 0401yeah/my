<template>
  <div class="nearby-parking">
    <div class="main-content">
      <div class="parking-list">
        <div class="search-bar">
          <ElInput
            v-model="searchQuery"
            placeholder="搜索停车场名称或地址..."
            prefix-icon="el-icon-search"
            class="search-input"
            @keyup.enter="handleSearch"
          />
          <ElButton type="primary" class="search-button" @click="handleSearch" :loading="searching">
            搜索
          </ElButton>
        </div>
        
        <div class="list-container">
          <ElSkeleton v-if="loading" :rows="3" animated />
          <ElEmpty v-else-if="parkingList.length === 0" description="暂无停车场数据" />
          <div v-else class="parking-items">
            <div
              v-for="parking in parkingList"
              :key="parking.id"
              class="parking-item"
              :class="{ active: selectedParking?.id === parking.id }"
              @click="selectParking(parking)"
            >
              <div class="parking-header">
                <span class="parking-name">{{ parking.name }}</span>
                <ElTag :type="getStatusType(parking)" size="small">
                  {{ getStatusText(parking) }}
                </ElTag>
              </div>
              <div class="parking-address">{{ parking.address }}</div>
              <div class="parking-info">
                <span class="info-item">
                  <i class="el-icon-location"></i>
                  {{ parking.distance ? `${parking.distance}km` : '计算中...' }}
                </span>
                <span class="info-item">
                  <i class="el-icon-parking"></i>
                  {{ parking.availableSpaceNumber || 0 }}/{{ parking.parkingSpaceNumber || 0 }}车位
                </span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="map-container">
        <div ref="mapElement" class="map-element"></div>
        
        <div class="map-legend">
          <div class="legend-item">
            <span class="legend-dot green"></span>
            <span>空闲</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot yellow"></span>
            <span>繁忙</span>
          </div>
          <div class="legend-item">
            <span class="legend-dot red"></span>
            <span>拥挤</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { ElMessage, ElEmpty, ElSkeleton } from 'element-plus'
import { lotApi } from '@/api/business'

declare const L: any

const searchQuery = ref('')
const searching = ref(false)
const loading = ref(false)
const parkingList = ref<any[]>([])
const selectedParking = ref<any>(null)
const mapElement = ref<HTMLElement | null>(null)
let map: any = null
let markers: any[] = []
let userMarker: any = null

const getStatusType = (parking: any) => {
  const total = parking.parkingSpaceNumber || 1
  const available = parking.availableSpaceNumber || 0
  const ratio = available / total
  if (ratio > 0.2) return 'success'
  if (ratio > 0.05) return 'warning'
  return 'danger'
}

const getStatusText = (parking: any) => {
  const total = parking.parkingSpaceNumber || 1
  const available = parking.availableSpaceNumber || 0
  const ratio = available / total
  if (ratio > 0.2) return '空闲'
  if (ratio > 0.05) return '繁忙'
  return '拥挤'
}

const initMap = () => {
  if (typeof L === 'undefined') {
    loadLeaflet()
    return
  }
  createMap()
}

const loadLeaflet = () => {
  const css = document.createElement('link')
  css.rel = 'stylesheet'
  css.href = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.css'
  document.head.appendChild(css)
  
  const script = document.createElement('script')
  script.src = 'https://unpkg.com/leaflet@1.9.4/dist/leaflet.js'
  script.onload = createMap
  document.head.appendChild(script)
}

const createMap = () => {
  if (!mapElement.value || typeof L === 'undefined') return
  
  map = L.map(mapElement.value, {
    zoomControl: true,
    zoomControlOptions: { position: 'bottomright' }
  }).setView([32.4836, 119.9226], 13)
  
  L.tileLayer('https://webrd0{s}.is.autonavi.com/appmaptile?lang=zh_cn&size=1&scale=1&style=8&x={x}&y={y}&z={z}', {
    subdomains: ['1', '2', '3', '4'],
    attribution: '© 高德地图',
    maxZoom: 18
  }).addTo(map)
  
  const LocationControl = L.Control.extend({
    options: { position: 'bottomright' },
    onAdd: function() {
      const btn = L.DomUtil.create('div', 'leaflet-bar leaflet-control')
      btn.style.cssText = 'width:36px;height:36px;border-radius:8px;background:#fff;display:flex;align-items:center;justify-content:center;cursor:pointer;box-shadow:0 2px 5px rgba(0,0,0,0.2);margin-bottom:10px;'
      btn.innerHTML = '<div style="width:12px;height:12px;border-radius:50%;background:#007aff;box-shadow:0 0 0 2px rgba(0,122,255,0.3);"></div>'
      btn.onclick = getCurrentLocation
      return btn
    }
  })
  map.addControl(new LocationControl())
  
  setTimeout(() => {
    map.invalidateSize()
  }, 300)
  
  // 监听窗口大小变化，重新调整地图大小
  window.addEventListener('resize', () => {
    if (map) {
      map.invalidateSize()
    }
  })
}

const geocodeAddress = async (address: string): Promise<{ lat: number; lng: number } | null> => {
  try {
    const url = `https://restapi.amap.com/v3/geocode/geo?key=3ff299c6d18db7aff303bde11aae7dc7&address=${encodeURIComponent(address)}`
    const res = await fetch(url)
    const data = await res.json()
    
    if (data.status === '1' && data.geocodes?.length > 0) {
      const [lng, lat] = data.geocodes[0].location.split(',').map(Number)
      return { lat, lng }
    }
  } catch (e) {
    console.error('地理编码失败:', e)
  }
  return null
}

const handleSearch = async () => {
  if (!searchQuery.value.trim()) {
    fetchParkingList()
    return
  }
  
  searching.value = true
  loading.value = true
  
  try {
    const coords = await geocodeAddress(searchQuery.value)
    if (coords && map) {
      map.setView([coords.lat, coords.lng], 15)
      
      if (userMarker) map.removeLayer(userMarker)
      userMarker = L.marker([coords.lat, coords.lng], {
        icon: L.divIcon({
          className: 'search-marker',
          html: '<div style="width:12px;height:12px;border-radius:50%;background:#67c23a;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>',
          iconSize: [12, 12]
        })
      }).addTo(map)
      userMarker.bindPopup(searchQuery.value).openPopup()
    }
    
    await fetchParkingList()
  } finally {
    searching.value = false
    loading.value = false
  }
}

const fetchParkingList = async () => {
  loading.value = true
  try {
    const res: any = await lotApi.fetchLotList({ name: searchQuery.value })
    
    if (res?.records) {
      parkingList.value = res.records.filter((item: any) => item.address)
      
      for (const parking of parkingList.value) {
        const coords = await geocodeAddress(parking.address)
        if (coords) {
          parking.lat = coords.lat
          parking.lng = coords.lng
        }
      }
      
      // 计算距离
      await calculateDistances()
      
      updateMapMarkers()
    }
  } catch (e) {
    console.error('获取停车场失败:', e)
    ElMessage.error('获取停车场数据失败')
  } finally {
    loading.value = false
  }
}

// 计算停车场到用户当前位置的距离
const calculateDistances = async () => {
  if (!navigator.geolocation) return
  
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const { latitude, longitude } = pos.coords
      
      parkingList.value.forEach(parking => {
        if (parking.lat && parking.lng) {
          const distance = calculateHaversineDistance(
            latitude, longitude, parking.lat, parking.lng
          )
          parking.distance = distance.toFixed(2)
        }
      })
    },
    (err) => {
      console.error('定位失败:', err)
    }
  )
}

// 使用 Haversine 公式计算两点之间的距离（单位：公里）
const calculateHaversineDistance = (lat1: number, lon1: number, lat2: number, lon2: number): number => {
  const R = 6371 // 地球半径（公里）
  const dLat = toRad(lat2 - lat1)
  const dLon = toRad(lon2 - lon1)
  const a = 
    Math.sin(dLat/2) * Math.sin(dLat/2) +
    Math.cos(toRad(lat1)) * Math.cos(toRad(lat2)) * 
    Math.sin(dLon/2) * Math.sin(dLon/2)
  const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
  return R * c
}

// 角度转弧度
const toRad = (value: number): number => {
  return value * Math.PI / 180
}

const updateMapMarkers = () => {
  if (!map || typeof L === 'undefined') return
  
  markers.forEach(m => map.removeLayer(m))
  markers = []
  
  parkingList.value.forEach(parking => {
    if (parking.lat && parking.lng) {
      const statusType = getStatusType(parking)
      let color = '#409eff'
      if (statusType === 'success') color = '#67c23a'
      if (statusType === 'danger') color = '#f56c6c'
      
      const marker = L.marker([parking.lat, parking.lng], {
        icon: L.divIcon({
          className: 'parking-marker',
          html: `<div style="width:14px;height:14px;border-radius:50%;background:${color};border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>`,
          iconSize: [14, 14]
        })
      }).addTo(map)
      
      marker.bindPopup(`
        <div style="padding:8px;min-width:150px;">
          <div style="font-weight:600;margin-bottom:4px;">${parking.name}</div>
          <div style="font-size:12px;color:#666;margin-bottom:4px;">${parking.address}</div>
          <div style="font-size:12px;">车位: ${parking.availableSpaceNumber || 0}/${parking.parkingSpaceNumber || 0}</div>
        </div>
      `)
      
      marker.parkingData = parking
      
      markers.push(marker)
    }
  })
  
  if (markers.length > 0) {
    const group = L.featureGroup(markers)
    map.fitBounds(group.getBounds().pad(0.1))
  }
}

const selectParking = async (parking: any) => {
  selectedParking.value = parking
  
  if (!map) return
  
  if (parking.lat && parking.lng) {
    map.setView([parking.lat, parking.lng], 16)
    const marker = markers.find(m => {
      const pos = m.getLatLng()
      return Math.abs(pos.lat - parking.lat) < 0.0001 && Math.abs(pos.lng - parking.lng) < 0.0001
    })
    if (marker) {
      marker.openPopup()
    }
  } else if (parking.address) {
    const coords = await geocodeAddress(parking.address)
    if (coords && map) {
      parking.lat = coords.lat
      parking.lng = coords.lng
      map.setView([coords.lat, coords.lng], 16)
      
      const marker = L.marker([coords.lat, coords.lng], {
        icon: L.divIcon({
          className: 'temp-marker',
          html: `<div style="width:14px;height:14px;border-radius:50%;background:#409eff;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>`,
          iconSize: [14, 14]
        })
      }).addTo(map)
      
      marker.bindPopup(`
        <div style="padding:8px;min-width:150px;">
          <div style="font-weight:600;margin-bottom:4px;">${parking.name}</div>
          <div style="font-size:12px;color:#666;margin-bottom:4px;">${parking.address}</div>
          <div style="font-size:12px;">车位: ${parking.availableSpaceNumber || 0}/${parking.parkingSpaceNumber || 0}</div>
        </div>
      `).openPopup()
      
      markers.push(marker)
    }
  }
}

const getCurrentLocation = () => {
  if (!navigator.geolocation) {
    console.error('浏览器不支持定位')
    return
  }
  
  navigator.geolocation.getCurrentPosition(
    (pos) => {
      const { latitude, longitude } = pos.coords
      if (map) {
        map.setView([latitude, longitude], 15)
        
        if (userMarker) map.removeLayer(userMarker)
        userMarker = L.marker([latitude, longitude], {
          icon: L.divIcon({
            className: 'user-marker',
            html: '<div style="width:12px;height:12px;border-radius:50%;background:#409eff;border:2px solid #fff;box-shadow:0 2px 6px rgba(0,0,0,0.3);"></div>',
            iconSize: [12, 12]
          })
        }).addTo(map)
        userMarker.bindPopup('您的位置').openPopup()
      }
    },
    (err) => {
      console.error('定位失败:', err)
    }
  )
}

onMounted(() => {
  nextTick(() => {
    initMap()
    fetchParkingList()
  })
})

onUnmounted(() => {
  if (map) {
    map.remove()
    map = null
  }
})
</script>

<style scoped lang="scss">
.nearby-parking {
  height: calc(100vh - 120px);
  padding: 16px;
  background: #f5f7fa;
  box-sizing: border-box;
  overflow: hidden;

  .main-content {
    display: flex;
    gap: 16px;
    height: 100%;

    .parking-list {
      width: 320px;
      flex-shrink: 0;
      display: flex;
      flex-direction: column;
      background: #fff;
      border-radius: 8px;
      overflow: hidden;

      .search-bar {
        padding: 12px;
        border-bottom: 1px solid #eee;
        display: flex;
        gap: 8px;

        .search-input {
          flex: 1;
        }

        .search-button {
          padding: 0 16px;
        }
      }

      .list-container {
        flex: 1;
        overflow: hidden;
        padding: 8px;

        .parking-items {
          display: flex;
          flex-direction: column;
          gap: 8px;
        }

        .parking-item {
          padding: 12px;
          border-radius: 8px;
          border: 1px solid #e4e7ed;
          cursor: pointer;
          transition: all 0.2s;

          &:hover {
            border-color: #409eff;
            box-shadow: 0 2px 8px rgba(64, 158, 255, 0.1);
          }

          &.active {
            border-color: #409eff;
            background: #ecf5ff;
          }

          .parking-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 6px;

            .parking-name {
              font-weight: 600;
              font-size: 14px;
              color: #303133;
            }
          }

          .parking-address {
            font-size: 12px;
            color: #909399;
            margin-bottom: 8px;
            line-height: 1.4;
          }

          .parking-info {
            display: flex;
            gap: 16px;
            font-size: 12px;
            color: #606266;

            .info-item {
              display: flex;
              align-items: center;
              gap: 4px;
            }
          }
        }
      }
    }

    .map-container {
      flex: 1;
      min-width: 0;
      position: relative;
      border-radius: 8px;
      overflow: hidden;
      background: #fff;
      height: 100%;

      .map-element {
        position: absolute;
        top: 0;
        left: 0;
        right: 0;
        bottom: 0;
        height: 100%;
        width: 100%;
      }

      .map-legend {
        position: absolute;
        bottom: 20px;
        left: 20px;
        background: #fff;
        padding: 10px 14px;
        border-radius: 8px;
        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
        display: flex;
        gap: 14px;
        z-index: 1000;

        .legend-item {
          display: flex;
          align-items: center;
          gap: 6px;
          font-size: 12px;
          color: #606266;

          .legend-dot {
            width: 8px;
            height: 8px;
            border-radius: 50%;

            &.green { background: #67c23a; }
            &.yellow { background: #e6a23c; }
            &.red { background: #f56c6c; }
          }
        }
      }
    }
  }
}
</style>
