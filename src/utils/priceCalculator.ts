import { feeStandardApi, operationStrategyApi } from '@/api/business'

// 服务类型定义
export type ServiceType = 'valet' | 'carwash'

// 价格计算结果接口
export interface PriceResult {
  basePrice: number        // 基础价格
  strategyDiscount: number  // 运营策略折扣
  finalPrice: number        // 最终价格
  appliedStrategy?: string  // 应用的运营策略名称
}

/**
 * 价格计算服务
 */
export class PriceCalculator {
  /**
   * 计算服务价格
   * @param serviceType 服务类型
   * @param parkingLotId 停车场ID
   * @returns 价格计算结果
   */
  static async calculatePrice(serviceType: ServiceType, parkingLotId: number): Promise<PriceResult> {
    try {
      // 1. 获取基础价格
      const basePrice = this.getBasePrice(serviceType)
      
      // 2. 获取适用的运营策略
      const strategy = await this.getApplicableStrategy(parkingLotId, serviceType)
      
      // 3. 计算最终价格
      let finalPrice = basePrice
      let strategyDiscount = 0
      let appliedStrategy = ''
      
      if (strategy) {
        // 应用运营策略折扣
        const discount = parseFloat(strategy.ruleCondition) || 0
        strategyDiscount = basePrice * discount
        finalPrice = basePrice - strategyDiscount
        appliedStrategy = strategy.strategyName
      }
      
      return {
        basePrice,
        strategyDiscount,
        finalPrice: Math.max(0, finalPrice), // 确保价格不为负数
        appliedStrategy
      }
    } catch (error) {
      console.error('价格计算失败:', error)
      // 失败时返回基础价格
      const basePrice = this.getBasePrice(serviceType)
      return {
        basePrice,
        strategyDiscount: 0,
        finalPrice: basePrice
      }
    }
  }
  
  /**
   * 获取服务基础价格
   * @param serviceType 服务类型
   * @returns 基础价格
   */
  private static getBasePrice(serviceType: ServiceType): number {
    switch (serviceType) {
      case 'valet':
        return 15 // 代泊服务基础价格
      case 'carwash':
        return 20 // 洗车服务基础价格
      default:
        return 0
    }
  }
  
  /**
   * 获取适用的运营策略
   * @param parkingLotId 停车场ID
   * @param serviceType 服务类型
   * @returns 适用的运营策略
   */
  private static async getApplicableStrategy(parkingLotId: number, serviceType: ServiceType) {
    try {
      // 获取所有启用的运营策略
      const response = await operationStrategyApi.fetchOperationStrategyList({ status: 1 })
      const strategies = response?.records || []
      
      // 筛选适用于当前停车场和服务类型的策略
      for (const strategy of strategies) {
        // 检查停车场ID是否匹配
        if (strategy.parkingLotId && strategy.parkingLotId !== parkingLotId) {
          continue
        }
        
        // 检查服务类型是否匹配
        const applyVehicleTypes = strategy.applyVehicleTypes || ''
        if (applyVehicleTypes && !applyVehicleTypes.includes(serviceType)) {
          continue
        }
        
        // 检查策略是否可叠加
        if (strategy.isStackable) {
          // 可叠加策略，返回第一个匹配的
          return strategy
        } else {
          // 不可叠加策略，返回第一个匹配的
          return strategy
        }
      }
      
      return null
    } catch (error) {
      console.error('获取运营策略失败:', error)
      return null
    }
  }
  
  /**
   * 根据收费标准计算停车费用
   * @param parkingLotId 停车场ID
   * @param durationMinutes 停车时长（分钟）
   * @returns 停车费用
   */
  static async calculateParkingFee(parkingLotId: number, durationMinutes: number): Promise<number> {
    try {
      // 获取停车场的收费标准
      const response = await feeStandardApi.fetchFeeStandardList({ parkingLotId })
      const feeStandards = response?.records || []
      
      // 找到启用的收费标准
      const activeStandard = feeStandards.find(standard => standard.status === 1)
      
      if (!activeStandard) {
        return 0 // 没有收费标准，返回0
      }
      
      // 计算停车费用
      const freeMinutes = activeStandard.freeMinutes || 0
      const firstHourFee = Number(activeStandard.firstHourFee) || 0
      const afterFirstHourFee = Number(activeStandard.afterFirstHourFee) || 0
      const dayMaxFee = Number(activeStandard.dayMaxFee) || 0
      
      // 减去免费时间
      const payableMinutes = Math.max(0, durationMinutes - freeMinutes)
      
      if (payableMinutes <= 0) {
        return 0
      }
      
      // 计算费用
      let fee = 0
      
      if (payableMinutes <= 60) {
        // 不足1小时，按首时费计算
        fee = firstHourFee
      } else {
        // 超过1小时，首时费 + 续时费
        const hours = Math.ceil(payableMinutes / 60)
        fee = firstHourFee + (hours - 1) * afterFirstHourFee
      }
      
      // 应用日封顶
      if (dayMaxFee > 0) {
        fee = Math.min(fee, dayMaxFee)
      }
      
      return fee
    } catch (error) {
      console.error('计算停车费用失败:', error)
      return 0
    }
  }
}