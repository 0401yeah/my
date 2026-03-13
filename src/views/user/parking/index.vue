<template>
  <div class="smart-parking">
    <ElCard>
      <template #header>
        <div class="card-header">
          <span>智慧停车</span>
        </div>
      </template>
      <div class="parking-info">
        <ElRow :gutter="20">
          <ElCol :span="8">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <ElIcon class="icon"><Location /></ElIcon>
                <div class="content">
                  <div class="label">附近停车场</div>
                  <div class="value">5个</div>
                </div>
              </div>
            </ElCard>
          </ElCol>
          <ElCol :span="8">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <ElIcon class="icon"><Van /></ElIcon>
                <div class="content">
                  <div class="label">空余车位</div>
                  <div class="value">128个</div>
                </div>
              </div>
            </ElCard>
          </ElCol>
          <ElCol :span="8">
            <ElCard shadow="hover" class="info-card">
              <div class="info-item">
                <ElIcon class="icon"><Timer /></ElIcon>
                <div class="content">
                  <div class="label">预计费用</div>
                  <div class="value">¥10/小时</div>
                </div>
              </div>
            </ElCard>
          </ElCol>
        </ElRow>
      </div>
      
      <div class="parking-lot-list">
        <h3>附近停车场</h3>
        <ElRow :gutter="20">
          <ElCol :span="12" v-for="lot in parkingLots" :key="lot.id">
            <ElCard shadow="hover" class="lot-card">
              <div class="lot-info">
                <div class="lot-name">{{ lot.name }}</div>
                <div class="lot-address">{{ lot.address }}</div>
                <div class="lot-status">
                  <ElTag :type="lot.available > 50 ? 'success' : lot.available > 20 ? 'warning' : 'danger'">
                    空位: {{ lot.available }}/{{ lot.total }}
                  </ElTag>
                  <span class="distance">{{ lot.distance }}</span>
                </div>
                <div class="lot-fee">¥{{ lot.fee }}/小时</div>
              </div>
              <div class="lot-action">
                <ElButton type="primary" size="small">导航</ElButton>
                <ElButton type="success" size="small">预约</ElButton>
              </div>
            </ElCard>
          </ElCol>
        </ElRow>
      </div>
    </ElCard>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { Location, Timer, Van } from '@element-plus/icons-vue'

const parkingLots = ref([
  {
    id: 1,
    name: '万达广场停车场',
    address: '泰州市海陵区万达广场B1层',
    available: 85,
    total: 200,
    distance: '500m',
    fee: 5
  },
  {
    id: 2,
    name: '中医院停车场',
    address: '泰州市海陵区中医院',
    available: 15,
    total: 100,
    distance: '800m',
    fee: 3
  },
  {
    id: 3,
    name: '坡子街停车场',
    address: '泰州市海陵区坡子街',
    available: 60,
    total: 150,
    distance: '1.2km',
    fee: 4
  },
  {
    id: 4,
    name: '凤城河老街停车场',
    address: '泰州市海陵区老街',
    available: 30,
    total: 80,
    distance: '1.5km',
    fee: 5
  }
])
</script>

<style scoped lang="scss">
.smart-parking {
  padding: 20px;

  .card-header {
    font-size: 18px;
    font-weight: bold;
  }

  .parking-info {
    margin-bottom: 30px;

    .info-card {
      .info-item {
        display: flex;
        align-items: center;
        gap: 15px;

        .icon {
          font-size: 40px;
          color: var(--el-color-primary);
        }

        .content {
          .label {
            font-size: 14px;
            color: #666;
          }

          .value {
            font-size: 24px;
            font-weight: bold;
            color: #333;
          }
        }
      }
    }
  }

  .parking-lot-list {
    h3 {
      margin-bottom: 20px;
      font-size: 16px;
      font-weight: bold;
    }

    .lot-card {
      margin-bottom: 20px;

      .lot-info {
        .lot-name {
          font-size: 16px;
          font-weight: bold;
          margin-bottom: 8px;
        }

        .lot-address {
          font-size: 14px;
          color: #666;
          margin-bottom: 8px;
        }

        .lot-status {
          display: flex;
          justify-content: space-between;
          align-items: center;
          margin-bottom: 8px;

          .distance {
            font-size: 14px;
            color: #999;
          }
        }

        .lot-fee {
          font-size: 16px;
          color: var(--el-color-danger);
          font-weight: bold;
        }
      }

      .lot-action {
        margin-top: 15px;
        display: flex;
        gap: 10px;
      }
    }
  }
}
</style>
