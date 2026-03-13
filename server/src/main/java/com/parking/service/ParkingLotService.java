package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.ParkingLot;

/**
 * 停车场管理 Service 接口
 */
public interface ParkingLotService extends IService<ParkingLot> {
    /**
     * 更新停车场车位数量
     * @param parkingLotId 停车场ID
     * @param targetCount 目标车位数量
     */
    void updateParkingSpaces(Long parkingLotId, Integer targetCount);
}