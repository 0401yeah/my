<template>
  <div class="user-profile-page">
    <div class="profile-container">


      <!-- 个人信息卡片 -->
      <div class="user-info-card">
        <div class="card-header">
          <h2 class="card-title">基本信息</h2>
          <div class="header-actions">
            <button class="edit-button" @click="dialogVisible = true">
              <i class="icon ri-edit-2-line"></i>
              编辑资料
            </button>
            <button class="password-button" @click="showPasswordDialog = true">
              <i class="icon ri-lock-password-line"></i>
              修改密码
            </button>
          </div>
        </div>
        <div class="card-body">
          <div class="user-avatar-section">
            <div class="avatar-container">
              <img :src="userInfo.avatar || defaultAvatar" class="user-avatar" />
              <div class="avatar-edit-icon" @click="dialogVisible = true">
                <i class="icon ri-camera-line"></i>
              </div>
            </div>
            <div class="user-basic-info">
              <h3 class="user-name">{{ userInfo.nickname || '用户' }}</h3>
              <p class="user-mobile">{{ userInfo.mobile || '未绑定手机' }}</p>
              <div class="user-id">ID: {{ (userInfo as any).id || '000000' }}</div>
            </div>
          </div>



          <div class="info-grid">
            <div class="info-item">
              <span class="info-label">账号</span>
              <span class="info-value">{{ userInfo.userName || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">昵称</span>
              <span class="info-value">{{ userInfo.nickname || '未设置' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">手机号</span>
              <span class="info-value">{{ userInfo.mobile || '未绑定' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">邮箱</span>
              <span class="info-value">{{ (userInfo as any).email || '未绑定' }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">注册时间</span>
              <span class="info-value">{{ formatTime(userInfo.gmtCreate || '') }}</span>
            </div>
            <div class="info-item">
              <span class="info-label">会员天数</span>
              <span class="info-value">{{ memberDays }} 天</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 编辑资料弹窗 -->
    <ArtUserProfileDialog v-model:visible="dialogVisible" />
    
    <!-- 修改密码弹窗 -->
    <div v-if="showPasswordDialog" class="modal-mask" @click="showPasswordDialog = false">
      <div class="modal-wrapper" @click.stop>
        <div class="modal-header">
          <h3>修改密码</h3>
          <button class="close-icon" @click="showPasswordDialog = false">
            <i class="icon ri-close-line"></i>
          </button>
        </div>
        <div class="modal-body">
          <div class="form-field">
            <label>当前密码</label>
            <input type="password" v-model="pwdForm.password" placeholder="请输入当前密码" />
          </div>
          <div class="form-field">
            <label>新密码</label>
            <input type="password" v-model="pwdForm.newPassword" placeholder="请输入新密码" />
          </div>
          <div class="form-field">
            <label>确认密码</label>
            <input type="password" v-model="pwdForm.confirmPassword" placeholder="请再次输入新密码" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn-cancel" @click="showPasswordDialog = false">取消</button>
          <button class="btn-confirm" @click="savePassword">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { ElMessage } from 'element-plus'
import defaultAvatarImg from '@imgs/user/avatar.webp'
import ArtUserProfileDialog from '@/components/user/ArtUserProfileDialog.vue'

defineOptions({ name: 'UserProfile' })

const userStore = useUserStore()
const userInfo = computed(() => userStore.getUserInfo)
const defaultAvatar = defaultAvatarImg

const memberDays = ref(0)
const dialogVisible = ref(false)
const showPasswordDialog = ref(false)

const pwdForm = ref({
  password: '',
  newPassword: '',
  confirmPassword: ''
})

const formatTime = (time: string) => {
  if (!time) return '未知'
  return new Date(time).toLocaleDateString()
}

const maskMobile = (mobile: string) => {
  if (!mobile || mobile.length !== 11) return mobile
  return mobile.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const calculateMemberDays = (gmtCreate: string) => {
  if (!gmtCreate) return 0
  const create = new Date(gmtCreate)
  const now = new Date()
  const diff = now.getTime() - create.getTime()
  return Math.floor(diff / (1000 * 60 * 60 * 24))
}

const savePassword = () => {
  if (!pwdForm.value.password || !pwdForm.value.newPassword || !pwdForm.value.confirmPassword) {
    ElMessage.warning('请填写完整的密码信息')
    return
  }
  if (pwdForm.value.newPassword !== pwdForm.value.confirmPassword) {
    ElMessage.error('两次输入的新密码不一致')
    return
  }
  ElMessage.success('密码修改成功')
  pwdForm.value.password = ''
  pwdForm.value.newPassword = ''
  pwdForm.value.confirmPassword = ''
  showPasswordDialog.value = false
}

watch(
  () => userInfo.value,
  (newVal) => {
    if (newVal) {
      memberDays.value = calculateMemberDays(newVal.gmtCreate || '')
    }
  },
  { immediate: true }
)

onMounted(() => {
  // 初始化数据
})
</script>

<style scoped>
.user-profile-page {
  padding: 20px;
}

.profile-container {
  max-width: 900px;
  margin: 0 auto;
  display: flex;
  flex-direction: column;
  gap: 24px;
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.page-header {
  text-align: center;
  margin-bottom: 16px;
}

.page-title {
  font-size: 32px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 8px 0;
}

.page-subtitle {
  font-size: 16px;
  color: #718096;
  margin: 0;
}

/* 卡片通用样式 */
.user-info-card,
.security-card {
  background: white;
  border-radius: 24px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.06);
  overflow: hidden;
  transition: all 0.3s ease;
}

.user-info-card:hover,
.security-card:hover {
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
}

.card-header {
  padding: 24px 32px;
  border-bottom: 1px solid #edf2f7;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.header-actions {
  display: flex;
  gap: 12px;
  align-items: center;
}

.card-title {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.card-body {
  padding: 32px;
}

.edit-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #4CAF50;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.edit-button:hover {
  background: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

.password-button {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 20px;
  background: #2196F3;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.password-button:hover {
  background: #0b7dda;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(33, 150, 243, 0.4);
}

/* 用户头像部分 */
.user-avatar-section {
  display: flex;
  align-items: center;
  gap: 24px;
  margin-bottom: 32px;
  padding-bottom: 24px;
  border-bottom: 1px solid #edf2f7;
}

.avatar-container {
  position: relative;
  flex-shrink: 0;
}

.user-avatar {
  width: 100px;
  height: 100px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #e2e8f0;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.avatar-edit-icon {
  position: absolute;
  bottom: 0;
  right: 0;
  width: 32px;
  height: 32px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  border: 2px solid white;
}

.avatar-edit-icon:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.user-basic-info {
  flex: 1;
}

.user-name {
  font-size: 24px;
  font-weight: 700;
  color: #2d3748;
  margin: 0 0 8px 0;
}

.user-mobile {
  font-size: 16px;
  color: #718096;
  margin: 0 0 4px 0;
}

.user-id {
  font-size: 14px;
  color: #a0aec0;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 24px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.info-label {
  font-size: 13px;
  color: #a0aec0;
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.info-value {
  font-size: 16px;
  color: #2d3748;
  font-weight: 500;
}

/* 安全列表 */
.security-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.security-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px;
  background: #f7fafc;
  border-radius: 16px;
  transition: all 0.3s ease;
}

.security-item:hover {
  background: #edf2f7;
}

.security-left {
  display: flex;
  align-items: center;
  gap: 16px;
  flex: 1;
}

.security-icon {
  width: 48px;
  height: 48px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  color: white;
  flex-shrink: 0;
}

.security-content {
  flex: 1;
}

.security-content h4 {
  font-size: 16px;
  font-weight: 600;
  color: #2d3748;
  margin: 0 0 4px 0;
}

.security-content p {
  font-size: 14px;
  color: #718096;
  margin: 0;
}

.action-button {
  padding: 10px 24px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  color: #667eea;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
  flex-shrink: 0;
}

.action-button:hover {
  background: #667eea;
  border-color: #667eea;
  color: white;
}



.form-field {
  margin-bottom: 16px;
}

.form-field label {
  display: block;
  font-size: 14px;
  font-weight: 500;
  color: #2d3748;
  margin-bottom: 8px;
}

.form-field input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.3s ease;
  background: white;
  color: #2d3748;
}

.form-field input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
}

.form-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 20px;
}

.btn-cancel {
  padding: 10px 20px;
  background: white;
  border: 2px solid #e2e8f0;
  border-radius: 12px;
  font-size: 14px;
  color: #718096;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-cancel:hover {
  background: #f7fafc;
  border-color: #cbd5e0;
}

.btn-confirm {
  padding: 10px 20px;
  background: #4CAF50;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  color: white;
  cursor: pointer;
  transition: all 0.3s ease;
  font-weight: 500;
}

.btn-confirm:hover {
  background: #45a049;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 175, 80, 0.4);
}

/* 弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.3s ease;
  backdrop-filter: blur(4px);
}

.modal-wrapper {
  background: white;
  border-radius: 24px;
  width: 90%;
  max-width: 520px;
  overflow: hidden;
  animation: slideUp 0.3s ease;
  box-shadow: 0 24px 80px rgba(0, 0, 0, 0.3);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.modal-header {
  padding: 24px 32px;
  border-bottom: 1px solid #edf2f7;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.modal-header h3 {
  font-size: 20px;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
}

.close-icon {
  background: none;
  border: none;
  font-size: 24px;
  color: #a0aec0;
  cursor: pointer;
  padding: 4px;
  border-radius: 8px;
  transition: all 0.3s ease;
}

.close-icon:hover {
  background: #f7fafc;
  color: #2d3748;
}

.modal-body {
  padding: 32px;
}

.modal-footer {
  padding: 24px 32px;
  border-top: 1px solid #edf2f7;
  display: flex;
  gap: 16px;
  justify-content: flex-end;
}

.modal-footer .btn-confirm {
  padding: 12px 28px;
  font-size: 15px;
}

.modal-footer .btn-cancel {
  padding: 12px 28px;
  font-size: 15px;
}

/* 图标样式 */
.icon {
  font-family: 'remixicon';
  font-style: normal;
  font-weight: normal;
  font-variant: normal;
  text-transform: none;
  line-height: 1;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-profile-page {
    padding: 20px 16px;
  }
  
  .page-title {
    font-size: 28px;
  }
  
  .user-avatar-section {
    flex-direction: column;
    text-align: center;
    gap: 16px;
  }
  
  .info-grid {
    grid-template-columns: 1fr;
  }
  
  .card-body {
    padding: 24px;
  }
  
  .header-actions {
    flex-direction: column;
    align-items: stretch;
    gap: 8px;
  }
  
  .edit-button,
  .password-button {
    width: 100%;
    justify-content: center;
  }
}
</style>
