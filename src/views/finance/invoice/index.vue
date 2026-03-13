<template>
  <div class="invoice-page">
    <el-card class="art-table-card" shadow="never" style="margin-top: 0">
      <div class="card-header flex justify-between items-center mb-4">
        <el-button type="primary" @click="handleAdd">
          <el-icon><Plus /></el-icon>
          添加
        </el-button>
      </div>
      <art-table
        row-key="id"
        :loading="loading"
        :data="data"
        :columns="columns"
        :pagination="pagination"
        @pagination:size-change="handleSizeChange"
        @pagination:current-change="handleCurrentChange"
      >
        <template #status="{ row }">
          <div class="flex items-center space-x-2">
            <el-tag
              :type="row.status === 0 ? 'warning' : row.status === 1 ? 'success' : 'danger'"
              size="small"
            >
              {{ row.status === 0 ? '待开具' : row.status === 1 ? '已开具' : '开具失败' }}
            </el-tag>
            <el-button v-if="row.status === 0" type="primary" size="small" @click.stop="handleIssue(row)">开具</el-button>
            <el-button v-else-if="row.status === 2" type="primary" size="small" @click.stop="handleIssue(row)">重新开具</el-button>
          </div>
        </template>

        <template #operation="{ row }">
          <div class="flex items-center space-x-2">
            <ArtButtonTable v-if="row.status === 1" type="view" @click="handleView(row)" />
            <ArtButtonTable type="edit" @click="handleEdit(row)" />
            <ArtButtonTable type="delete" @click="handleDelete(row)" />
          </div>
        </template>
      </art-table>
    </el-card>

    <!-- 查看电子发票对话框 -->
    <ElDialog v-model="dialogVisible" :title="dialogTitle" width="800px" center>
      <template v-if="dialogType === 'view'">
        <div class="electronic-invoice">
          <!-- 水印图层 -->
          <div class="watermark"></div>
          
          <!-- 发票内容 -->
          <div class="invoice-content">
            <!-- 头部 -->
            <div class="invoice-header">
              <!-- 左侧：税务局红章 -->
              <div class="header-left">
                <div class="tax-seal">
                  <span>国家税务<br/>总局监制</span>
                </div>
                <div class="title-section">
                  <h1>电子发票（普通发票）</h1>
                  <p class="subtitle">Electronic Invoice (General Invoice)</p>
                </div>
              </div>

              <!-- 右侧：发票基本信息 -->
              <div class="header-right">
                <div class="info-block">
                  <p class="info-label">发票号码 INVOICE NUMBER</p>
                  <p class="invoice-number">{{ formData.invoiceNo }}</p>
                </div>
                <div class="info-block">
                  <p class="info-label">开票日期 DATE OF ISSUE</p>
                  <p class="info-date">{{ formatDateChinese(formData.gmtCreate) }}</p>
                </div>
              </div>
            </div>

            <!-- 购买方与销售方卡片 -->
            <div class="party-cards">
              <!-- 购买方 -->
              <div class="party-card">
                <div class="card-header">
                  <div class="red-bar"></div>
                  <span class="card-title">购买方信息 <span class="title-en">Purchaser</span></span>
                </div>
                <div class="party-name">{{ formData.invoiceTitle }}</div>
                <div class="party-info">
                  <span class="info-label">统一社会信用代码:</span>
                  <span class="info-value">{{ formData.taxNumber || '-' }}</span>
                </div>
              </div>

              <!-- 销售方 -->
              <div class="party-card">
                <div class="card-header">
                  <div class="red-bar"></div>
                  <span class="card-title">销售方信息 <span class="title-en">Seller</span></span>
                </div>
                <div class="party-name">智泊云端</div>
                <div class="party-info">
                  <span class="info-label">统一社会信用代码:</span>
                  <span class="info-value">91320100MA1XXXXXXX</span>
                </div>
                <div class="party-info">
                  <span class="info-label">地址、电话:</span>
                  <span class="info-value">南京市玄武区XX路XX号 025-12345678</span>
                </div>
              </div>
            </div>

            <!-- 明细表格 -->
            <div class="invoice-table">
              <table>
                <thead>
                  <tr>
                    <th class="item-name">项目名称 <span class="th-en">Item Name</span></th>
                    <th class="text-center">数量 <span class="th-en">Qty</span></th>
                    <th class="text-center">单价 <span class="th-en">Price</span></th>
                    <th class="text-right">金额 <span class="th-en">Amount</span></th>
                    <th class="text-center">税率 <span class="th-en">Tax Rate</span></th>
                    <th class="text-right">税额 <span class="th-en">Tax</span></th>
                  </tr>
                </thead>
                <tbody>
                  <tr>
                    <td>
                      <p class="item-main">{{ formData.invoiceContent || '*停车服务* 车辆停放费' }}</p>
                      <p class="item-en">Parking Service Fee</p>
                    </td>
                    <td class="text-center">1</td>
                    <td class="text-center">{{ formData.untaxedAmount }}</td>
                    <td class="text-right font-bold">{{ formData.untaxedAmount }}</td>
                    <td class="text-center">6%</td>
                    <td class="text-right">{{ formData.taxAmount }}</td>
                  </tr>
                </tbody>
              </table>
            </div>

            <!-- 底部信息与总价 -->
            <div class="invoice-footer">
              <!-- 备注 -->
              <div class="remarks-card">
                <p class="remarks-label">备注 Remarks</p>
                <div class="remarks-content">
                  <p>订单号: {{ formData.orderNo }}</p>
                  <p>发票类型: {{ formData.invoiceType === 1 ? '企业普票' : formData.invoiceType === 2 ? '企业专票' : '个人普票' }}</p>
                  <p>开票状态: {{ formData.status === 0 ? '待开具' : formData.status === 1 ? '已开具' : '开具失败' }}</p>
                </div>
              </div>

              <!-- 金额统计 -->
              <div class="amount-card">
                <div class="amount-details">
                  <div class="amount-row">
                    <span>合计金额 (不含税) <span class="amount-en">Subtotal</span></span>
                    <span class="amount-value">¥ {{ formData.untaxedAmount }}</span>
                  </div>
                  <div class="amount-row">
                    <span>合计税额 <span class="amount-en">Total Tax</span></span>
                    <span class="amount-value">¥ {{ formData.taxAmount }}</span>
                  </div>
                </div>
                
                <!-- 总价大红框 -->
                <div class="total-box">
                  <div class="total-label">
                    <p class="total-title">价税合计</p>
                    <p class="total-en">Total Amount</p>
                  </div>
                  <div class="total-amount">
                    <span class="currency">¥</span>
                    <span class="number">{{ formData.totalAmount }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </template>
      
      <!-- 编辑发票对话框 -->
      <template v-else>
        <ElForm :model="formData" :rules="rules" ref="formRef" label-position="top">
          <ElRow :gutter="20">
            <ElCol :span="24">
              <ElFormItem label="订单号">
                <ElInput v-model="formData.orderNo" disabled />
              </ElFormItem>
            </ElCol>
            <ElCol :span="24">
              <ElFormItem label="发票抬头" prop="invoiceTitle">
                <ElInput v-model="formData.invoiceTitle" placeholder="请输入发票抬头" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="24">
              <ElFormItem label="开票内容">
                <ElInput v-model="formData.invoiceContent" placeholder="请输入开票内容" />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="金额">
                <ElInput v-model="formData.amount" disabled />
              </ElFormItem>
            </ElCol>
            <ElCol :span="12">
              <ElFormItem label="发票类型" prop="invoiceType">
                <ElSelect v-model="formData.invoiceType" class="w-full">
                  <ElOption label="企业普票" :value="1" />
                  <ElOption label="企业专票" :value="2" />
                  <ElOption label="个人普票" :value="3" />
                </ElSelect>
              </ElFormItem>
            </ElCol>
            <ElCol :span="24">
              <ElFormItem label="状态">
                <ElTag :type="formData.status === 0 ? 'warning' : formData.status === 1 ? 'success' : 'danger'">
                  {{ formData.status === 0 ? '待开具' : formData.status === 1 ? '已开具' : '开具失败' }}
                </ElTag>
              </ElFormItem>
            </ElCol>
          </ElRow>
        </ElForm>
      </template>
      
      <!-- 底部按钮 -->
      <template #footer v-if="dialogType !== 'view'">
        <ElButton @click="dialogVisible = false">取消</ElButton>
        <ElButton type="primary" @click="handleSubmit">确定</ElButton>
      </template>
    </ElDialog>
  </div>
</template>

<script setup lang="ts">
  import { ref, computed } from 'vue'
  import { ElMessageBox, ElMessage } from 'element-plus'
  import { Plus } from '@element-plus/icons-vue'
  import { useTable } from '@/hooks/core/useTable'
  import { invoiceApi } from '@/api/system'

  defineOptions({ name: 'Invoice' })

  const dialogVisible = ref(false)
  const dialogType = ref<'view' | 'edit' | 'add'>('view')
  const formRef = ref()
  const formData = ref<any>({
    id: undefined,
    orderNo: '',
    invoiceTitle: '',
    invoiceContent: '',
    amount: 0,
    invoiceType: 1,
    status: 0
  })

  const dialogTitle = computed(() => {
    switch (dialogType.value) {
      case 'add': return '添加发票'
      case 'view': return '发票详情'
      case 'edit': return '编辑发票'
      default: return '发票'
    }
  })

  const rules = {
    invoiceTitle: [{ required: true, message: '请输入发票抬头', trigger: 'blur' }],
    invoiceType: [{ required: true, message: '请选择发票类型', trigger: 'change' }]
  }

  const { data, columns, loading, pagination, handleSizeChange, handleCurrentChange, refreshData } = useTable({
    core: {
      apiFn: invoiceApi.fetchList,
      apiParams: {
        current: 1,
        size: 20,
        invoiceTitle: '',
        status: ''
      },
      columnsFactory: () => [
        {
          type: 'globalIndex',
          width: 60,
          label: '序号'
        },
        {  
          prop: 'orderNo',
          label: '订单号',
          minWidth: 200
        },
        {
          prop: 'invoiceTitle',
          label: '发票抬头',
          minWidth: 180
        },
        {
          prop: 'amount',
          label: '金额',
          minWidth: 100,
          formatter: (row: any) => `¥${row.amount?.toFixed(2) || '0.00'}`
        },
        {
          prop: 'invoiceType',
          label: '发票类型',
          minWidth: 100,
          formatter: (row: any) =>
            row.invoiceType === 1 ? '企业普票' : row.invoiceType === 2 ? '企业专票' : '个人普票'
        },
        {
          prop: 'invoiceContent',
          label: '开票内容',
          minWidth: 180
        },
        {
          prop: 'status',
          label: '状态',
          minWidth: 180,
          useSlot: true
        },
        {
          prop: 'gmtCreate',
          label: '申请时间',
          minWidth: 160,
          formatter: (row: any) => formatTime(row.gmtCreate)
        },
        {
          prop: 'operation',
          label: '操作',
          width: 280,
          useSlot: true,
          fixed: 'right'
        }
      ]
    }
  })

  const formatTime = (time: string | Date | null | undefined, format: string = 'YYYY-MM-DD HH:mm:ss'): string => {
    if (!time) return '-'
    
    let date: Date
    
    if (typeof time === 'string') {
      const timeStr = time.replace('T', ' ')
      date = new Date(timeStr)
    } else {
      date = time
    }
    
    if (isNaN(date.getTime())) return '-'
    
    const year = date.getFullYear()
    const month = String(date.getMonth() + 1).padStart(2, '0')
    const day = String(date.getDate()).padStart(2, '0')
    const hours = String(date.getHours()).padStart(2, '0')
    const minutes = String(date.getMinutes()).padStart(2, '0')
    const seconds = String(date.getSeconds()).padStart(2, '0')
    
    return format
      .replace('YYYY', String(year))
      .replace('MM', month)
      .replace('DD', day)
      .replace('HH', hours)
      .replace('mm', minutes)
      .replace('ss', seconds)
  }
  
  const formatDateChinese = (time: string | Date | null | undefined): string => {
    if (!time) return '-'
    
    let date: Date
    
    if (typeof time === 'string') {
      const timeStr = time.replace('T', ' ')
      date = new Date(timeStr)
    } else {
      date = time
    }
    
    if (isNaN(date.getTime())) return '-'
    
    const year = date.getFullYear()
    const month = date.getMonth() + 1
    const day = date.getDate()
    
    return `${year}年${month.toString().padStart(2, '0')}月${day.toString().padStart(2, '0')}日`
  }
  
  const generateInvoiceNo = () => {
    const timestamp = Date.now().toString().slice(-10)
    const random = Math.floor(Math.random() * 1000000).toString().padStart(6, '0')
    return `${timestamp}${random}`
  }
  
  const calculateTax = (amount: number) => {
    const taxRate = 0.06
    const tax = amount * taxRate
    const untaxedAmount = amount - tax
    return {
      untaxedAmount: untaxedAmount.toFixed(2),
      tax: tax.toFixed(2),
      total: amount.toFixed(2)
    }
  }
  
  const handleView = (row: any) => {
    dialogType.value = 'view'
    const taxData = calculateTax(row.amount || 0)
    formData.value = { 
      ...row,
      invoiceNo: generateInvoiceNo(),
      untaxedAmount: taxData.untaxedAmount,
      taxAmount: taxData.tax,
      totalAmount: taxData.total,
      gmtCreate: formatTime(row.gmtCreate)
    }
    dialogVisible.value = true
  }

  const handleEdit = (row: any) => {
    dialogType.value = 'edit'
    formData.value = { ...row }
    dialogVisible.value = true
  }

  const handleSubmit = async () => {
    await formRef.value.validate()
    try {
      if (dialogType.value === 'add') {
        await invoiceApi.add(formData.value)
        ElMessage.success('添加成功')
      } else if (dialogType.value === 'edit') {
        await invoiceApi.update(formData.value)
        ElMessage.success('修改成功')
      }
      dialogVisible.value = false
      refreshData()
    } catch (error) {
      ElMessage.error('操作失败，请重试')
    }
  }

  const handleDelete = (row: any) => {
    ElMessageBox.confirm('确定要删除该发票记录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(async () => {
      await invoiceApi.delete(row.id)
      ElMessage.success('删除成功')
      refreshData()
    })
  }

  const handleAdd = () => {
    dialogType.value = 'add'
    formData.value = {
      orderNo: '',
      amount: 0,
      invoiceType: 1,
      invoiceTitle: '',
      invoiceContent: '',
      taxNumber: '',
      email: '',
      status: 0
    }
    dialogVisible.value = true
  }

  const handleIssue = async (row: any) => {
    try {
      await invoiceApi.approve(row.id)
      ElMessage.success('发票开具成功')
      refreshData()
    } catch (error) {
      ElMessage.error('发票开具失败，请重试')
    }
  }
</script>

<style scoped>
  .invoice-page {
    padding: 20px;
    overflow-x: hidden;
  }
  
  /* 电子发票样式 */
  .electronic-invoice {
    font-family: 'Microsoft YaHei', 'Noto Sans SC', sans-serif;
    background: #ffffff;
    padding: 40px 50px;
    border-radius: 8px;
    box-shadow: 0 10px 25px -5px rgba(0, 0, 0, 0.1), 0 8px 10px -6px rgba(0, 0, 0, 0.1);
    position: relative;
    overflow: hidden;
  }
  
  /* 水印 */
  .watermark {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    z-index: 0;
    pointer-events: none;
    background-image: url("data:image/svg+xml;utf8,<svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 400 200' width='400' height='200'><text x='50%' y='50%' dominant-baseline='middle' text-anchor='middle' transform='rotate(-25 200 100)' fill='rgba(220, 220, 220, 0.3)' font-size='22' font-family='sans-serif' font-weight='bold'>电子发票 ELECTRONIC INVOICE</text></svg>");
    background-repeat: repeat;
  }
  
  .invoice-content {
    position: relative;
    z-index: 1;
  }
  
  /* 头部 */
  .invoice-header {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    margin-bottom: 40px;
    padding-bottom: 24px;
    border-bottom: 1px solid #f0f0f0;
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 16px;
  }
  
  .tax-seal {
    width: 80px;
    height: 80px;
    border-radius: 50%;
    border: 4px solid #db2828;
    display: flex;
    align-items: center;
    justify-content: center;
    text-align: center;
    line-height: 1.2;
    font-size: 14px;
    font-weight: bold;
    color: #db2828;
    background: #fff;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  }
  
  .title-section h1 {
    font-size: 28px;
    font-weight: bold;
    color: #333;
    margin: 0;
    letter-spacing: 2px;
  }
  
  .subtitle {
    font-size: 11px;
    color: #999;
    margin: 4px 0 0 0;
    letter-spacing: 1px;
    text-transform: uppercase;
  }
  
  .header-right {
    text-align: right;
    display: flex;
    flex-direction: column;
    justify-content: center;
  }
  
  .info-block {
    margin-bottom: 8px;
  }
  
  .info-block:last-child {
    margin-bottom: 0;
  }
  
  .info-label {
    font-size: 11px;
    color: #999;
    letter-spacing: 1px;
    margin: 0 0 4px 0;
    text-transform: uppercase;
  }
  
  .invoice-number {
    font-size: 24px;
    font-family: 'Courier New', monospace;
    color: #333;
    margin: 0;
    letter-spacing: 2px;
  }
  
  .info-date {
    font-size: 16px;
    color: #333;
    margin: 0;
    letter-spacing: 1px;
  }
  
  /* 购买方与销售方卡片 */
  .party-cards {
    display: grid;
    grid-template-columns: 1fr 1fr;
    gap: 24px;
    margin-bottom: 32px;
  }
  
  .party-card {
    background: #fafafa;
    border-radius: 8px;
    padding: 20px;
    border: 1px solid #f0f0f0;
  }
  
  .card-header {
    display: flex;
    align-items: center;
    margin-bottom: 16px;
  }
  
  .red-bar {
    width: 4px;
    height: 14px;
    background: #db2828;
    margin-right: 8px;
  }
  
  .card-title {
    font-size: 14px;
    font-weight: bold;
    color: #db2828;
  }
  
  .title-en {
    font-size: 11px;
    font-weight: normal;
    margin-left: 4px;
    text-transform: uppercase;
  }
  
  .party-name {
    font-size: 18px;
    font-weight: bold;
    color: #333;
    margin-bottom: 12px;
    letter-spacing: 1px;
  }
  
  .party-info {
    display: flex;
    font-size: 14px;
    color: #666;
    margin-bottom: 4px;
  }
  
  .party-info .info-label {
    width: 140px;
    color: #666;
    text-transform: none;
    font-size: 14px;
  }
  
  .party-info .info-value {
    flex: 1;
    color: #333;
  }
  
  /* 明细表格 */
  .invoice-table {
    margin-bottom: 32px;
  }
  
  .invoice-table table {
    width: 100%;
    border-collapse: collapse;
  }
  
  .invoice-table th {
    padding: 12px 8px;
    text-align: left;
    font-size: 14px;
    font-weight: bold;
    color: #db2828;
    border-bottom: 2px solid rgba(219, 40, 40, 0.2);
  }
  
  .invoice-table th.item-name {
    width: 33%;
  }
  
  .th-en {
    font-size: 11px;
    font-weight: normal;
    color: #999;
    margin-left: 4px;
  }
  
  .invoice-table td {
    padding: 16px 8px;
    font-size: 14px;
    color: #333;
    border-bottom: 1px dashed #f0f0f0;
  }
  
  .item-main {
    margin: 0 0 2px 0;
    color: #333;
  }
  
  .item-en {
    margin: 0;
    font-size: 11px;
    color: #999;
  }
  
  .text-center {
    text-align: center;
  }
  
  .text-right {
    text-align: right;
  }
  
  .font-bold {
    font-weight: bold;
  }
  
  /* 底部信息与总价 */
  .invoice-footer {
    display: flex;
    justify-content: space-between;
    gap: 32px;
    margin-top: 48px;
  }
  
  .remarks-card {
    flex: 1;
    background: #fafafa;
    border-radius: 8px;
    padding: 20px;
    border: 1px solid #f0f0f0;
  }
  
  .remarks-label {
    font-size: 11px;
    color: #999;
    margin: 0 0 12px 0;
    letter-spacing: 1px;
    text-transform: uppercase;
  }
  
  .remarks-content {
    font-size: 14px;
    color: #666;
    font-family: 'Courier New', monospace;
  }
  
  .remarks-content p {
    margin: 0 0 6px 0;
  }
  
  .remarks-content p:last-child {
    margin-bottom: 0;
  }
  
  .amount-card {
    width: 320px;
    display: flex;
    flex-direction: column;
    align-items: flex-end;
  }
  
  .amount-details {
    width: 100%;
    margin-bottom: 16px;
    padding: 0 8px;
  }
  
  .amount-row {
    display: flex;
    justify-content: space-between;
    font-size: 14px;
    color: #666;
    margin-bottom: 8px;
  }
  
  .amount-row:last-child {
    margin-bottom: 0;
  }
  
  .amount-en {
    font-size: 11px;
    color: #999;
    margin-left: 4px;
  }
  
  .amount-value {
    font-family: 'Courier New', monospace;
    color: #333;
  }
  
  /* 总价大红框 */
  .total-box {
    background: #db2828;
    color: #fff;
    width: 100%;
    border-radius: 8px;
    padding: 20px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    box-shadow: 0 4px 12px rgba(219, 40, 40, 0.3);
  }
  
  .total-title {
    font-size: 16px;
    font-weight: bold;
    margin: 0;
    letter-spacing: 2px;
  }
  
  .total-en {
    font-size: 11px;
    color: rgba(255, 255, 255, 0.7);
    margin: 4px 0 0 0;
    text-transform: uppercase;
    letter-spacing: 1px;
  }
  
  .total-amount {
    display: flex;
    align-items: baseline;
    gap: 4px;
  }
  
  .currency {
    font-size: 20px;
    font-weight: bold;
  }
  
  .number {
    font-size: 32px;
    font-weight: bold;
    font-family: 'Courier New', monospace;
    letter-spacing: 2px;
  }
  
  /* 打印样式 */
  @media print {
    .invoice-page {
      padding: 0;
    }
    
    .electronic-invoice {
      box-shadow: none;
      border: 1px solid #000;
    }
    
    .invoice-footer .el-button {
      display: none;
    }
    
    * {
      -webkit-print-color-adjust: exact !important;
      print-color-adjust: exact !important;
    }
  }
</style>
