package com.parking.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.parking.entity.ParkingReservation;

public interface ParkingReservationService extends IService<ParkingReservation> {
    /**
     * 创建预约
     * @param reservation 预约信息
     * @return 是否成功
     */
    boolean createReservation(ParkingReservation reservation);
    
    /**
     * 取消预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean cancelReservation(Long id);
    
    /**
     * 确认预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean confirmReservation(Long id);
    
    /**
     * 完成预约
     * @param id 预约ID
     * @return 是否成功
     */
    boolean completeReservation(Long id);
}