<template>
  <ElDialog
    v-model="dialogVisible"
    title="编辑资料"
    width="440px"
    :align-center="true"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
    class="qq-style-dialog"
  >
    <div class="user-profile-dialog">
      <div class="avatar-section text-center mt-[-30px] mb-6">
        <div class="avatar-wrapper" @click="handleAvatarUpload">
          <img
            class="w-20 h-20 object-cover rounded-full mx-auto shadow-md"
            :src="form.avatar || defaultAvatar"
          />
          <div class="avatar-mask">更换</div>
        </div>
      </div>

      <ElForm
        :model="form"
        class="profile-form"
        label-width="70px"
      >
        <ElFormItem label="账号">
          <ElInput v-model="form.userName" placeholder="请输入账号" />
        </ElFormItem>

        <ElFormItem label="昵称">
          <ElInput v-model="form.nickname" placeholder="请输入昵称" />
        </ElFormItem>

        <ElFormItem label="手机">
          <ElInput v-model="form.mobile" placeholder="请输入手机" />
        </ElFormItem>
      </ElForm>
    </div>

    <template #footer>
      <div class="dialog-footer">
        <ElButton class="btn-cancel" @click="dialogVisible = false">取消</ElButton>
        <ElButton class="btn-save" type="primary" @click="saveProfile">保存</ElButton>
      </div>
    </template>
  </ElDialog>
</template>

<script setup lang="ts">
  import { ref, reactive, computed, watch } from 'vue'
  import { useUserStore } from '@/store/modules/user'
  import { ElMessage } from 'element-plus'
  import defaultAvatarImg from '@imgs/user/avatar.webp'

  defineOptions({ name: 'ArtUserProfileDialog' })

  const props = defineProps<{
    visible: boolean
  }>()

  const emit = defineEmits<{
    (e: 'update:visible', value: boolean): void
  }>()

  const dialogVisible = computed({
    get: () => props.visible,
    set: (value) => emit('update:visible', value)
  })

  const userStore = useUserStore()
  const userInfo = computed(() => userStore.getUserInfo)
  const defaultAvatar = defaultAvatarImg

  const form = reactive({
    // 用户表字段
    userName: '',
    nickname: '',
    mobile: '',
    avatar: ''
  })

  const handleAvatarUpload = () => {
    // 这里可以实现头像上传功能
    ElMessage.info('头像上传功能待实现')
  }

  const saveProfile = async () => {
    try {
      // 调用更新用户信息的API
      const { fetchUpdateUserInfo } = await import('@/api/auth')
      await fetchUpdateUserInfo({
        userName: form.userName,
        nickname: form.nickname,
        mobile: form.mobile,
        avatar: form.avatar
      })
      // 更新本地用户信息
      userStore.setUserInfo({
        ...userInfo.value,
        userName: form.userName,
        nickname: form.nickname,
        mobile: form.mobile,
        avatar: form.avatar,
        buttons: userInfo.value?.buttons || [],
        roles: userInfo.value?.roles || [],
        userId: userInfo.value?.userId,
        userType: userInfo.value?.userType,
        gmtCreate: userInfo.value?.gmtCreate
      } as Api.Auth.UserInfo)
      ElMessage.success('保存成功')
      dialogVisible.value = false
    } catch (error) {
      ElMessage.error('保存失败')
    }
  }

  watch(
    () => userInfo.value,
    (newVal) => {
      if (newVal) {
        // 公共字段
        form.avatar = newVal.avatar || ''
        // 统一字段
        form.userName = newVal.userName || ''
        form.nickname = newVal.nickname || ''
        form.mobile = newVal.mobile || ''
      }
    },
    { immediate: true }
  )
</script>

<style scoped lang="scss">
/* 1. 弹窗整体去线与圆角 */
:deep(.el-dialog) {
  border-radius: 16px;
  overflow: hidden;
}

:deep(.el-dialog__header) {
  padding: 20px 20px 0; /* 减小标题底部内边距 */
  border-bottom: none;   /* 移除标题下划线 */
  text-align: center;
  margin-right: 0;
  .el-dialog__title {
    font-size: 16px;
    font-weight: 600;
  }
}

:deep(.el-dialog__body) {
  padding: 10px 30px; /* 侧边留白多一点，视觉更集中 */
}

/* 2. 头像交互效果 */
.avatar-section {
  .avatar-wrapper {
    position: relative;
    display: inline-block;
    cursor: pointer;
    &:hover .avatar-mask {
      opacity: 1;
    }
    .avatar-mask {
      position: absolute;
      inset: 0;
      background: rgba(0, 0, 0, 0.3);
      border-radius: 50%;
      display: flex;
      align-items: center;
      justify-content: center;
      color: #fff;
      font-size: 12px;
      opacity: 0;
      transition: opacity 0.2s;
    }
  }
}

/* 3. 表单项上移后的紧凑布局 */
.profile-form {
  :deep(.el-form-item) {
    margin-bottom: 18px; 
    align-items: center;
  }
  
  :deep(.el-input__wrapper) {
    background-color: #f5f6f7; /* 浅灰填充色 */
    box-shadow: none !important; /* 去边框 */
    border-radius: 8px;
    padding: 4px 12px;
    &.is-focus {
      background-color: #fff;
      box-shadow: 0 0 0 1px var(--el-color-primary) inset !important;
    }
  }
}

/* 4. 底部按钮上移感 */
:deep(.el-dialog__footer) {
  border-top: none; /* 移除底部上划线 */
  padding: 0 30px 24px;
}

.dialog-footer {
  display: flex;
  justify-content: center; /* 按钮居中，更符合QQ审美 */
  gap: 12px;

  .el-button {
    width: 100px;
    height: 36px;
    border-radius: 8px;
    font-weight: 500;
    border: none;
  }
  
  .btn-cancel {
    background-color: #f0f0f0;
    color: #666;
    &:hover { background-color: #e5e5e5; }
  }

  .btn-save {
    background-color: #4e88ff;
    &:hover { background-color: #3b77ff; }
  }
}
</style>