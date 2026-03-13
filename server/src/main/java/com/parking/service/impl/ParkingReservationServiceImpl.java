package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingReservation;
import com.parking.entity.ParkingSpace;
import com.parking.mapper.ParkingReservationMapper;
import com.parking.service.ParkingLotService;
import com.parking.service.ParkingReservationService;
import com.parking.service.ParkingSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 停车预约服务实现类
 */
@Service
public class ParkingReservationServiceImpl extends ServiceImpl<ParkingReservationMapper, ParkingReservation> implements ParkingReservationService {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @Autowired
    private ParkingLotService parkingLotService;

    /**
     * 更新停车场可用车位数量
     */
    private void updateParkingLotAvailableSpaces(Long parkingLotId) {
        // 查询该停车场空闲车位的数量
        QueryWrapper<ParkingSpace> spaceWrapper = new QueryWrapper<>();
        spaceWrapper.eq("parking_lot_id", parkingLotId);
        spaceWrapper.eq("status", 0); // 0: 空闲
        long availableCount = parkingSpaceService.count(spaceWrapper);
        
        // 更新停车场的可用车位数量
        ParkingLot parkingLot = parkingLotService.getById(parkingLotId);
        if (parkingLot != null) {
            parkingLot.setAvailableSpaceNumber((int) availableCount);
            parkingLotService.updateById(parkingLot);
        }
    }

    @Override
    @Transactional
    public boolean createReservation(ParkingReservation reservation) {
        // 验证预约时间
        if (reservation.getExpireTime() == null) {
            throw new RuntimeException("预约过期时间不能为空");
        }
        
        Date now = new Date();
        if (reservation.getExpireTime().before(now)) {
            throw new RuntimeException("预约过期时间不能早于当前时间");
        }
        
        // 设置预约时间为当前时间
        reservation.setReservationTime(now);
        // 设置初始状态为预约中
        reservation.setStatus(0);
        
        // 保存预约信息
        boolean success = save(reservation);
        
        if (success) {
            // 更新车位状态为已预约
            ParkingSpace parkingSpace = parkingSpaceService.getById(reservation.getParkingSpaceId());
            if (parkingSpace != null) {
                parkingSpace.setStatus(2); // 2: 已预约
                parkingSpaceService.updateById(parkingSpace);
                
                // 更新停车场可用车位数量
                updateParkingLotAvailableSpaces(parkingSpace.getParkingLotId());
            }
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean cancelReservation(Long id) {
        ParkingReservation reservation = getById(id);
        if (reservation == null) {
            return false;
        }
        
        // 更新预约状态为已取消
        reservation.setStatus(2);
        boolean success = updateById(reservation);
        
        if (success) {
            // 更新车位状态为空闲
            ParkingSpace parkingSpace = parkingSpaceService.getById(reservation.getParkingSpaceId());
            if (parkingSpace != null) {
                parkingSpace.setStatus(0); // 0: 空闲
                parkingSpaceService.updateById(parkingSpace);
                
                // 更新停车场可用车位数量
                updateParkingLotAvailableSpaces(parkingSpace.getParkingLotId());
            }
        }
        
        return success;
    }

    @Override
    @Transactional
    public boolean confirmReservation(Long id) {
        ParkingReservation reservation = getById(id);
        if (reservation == null) {
            return false;
        }
        
        // 更新预约状态为已履约
        reservation.setStatus(1);
        return updateById(reservation);
    }

    @Override
    @Transactional
    public boolean completeReservation(Long id) {
        ParkingReservation reservation = getById(id);
        if (reservation == null) {
            return false;
        }
        
        // 更新预约状态为已履约
        reservation.setStatus(1);
        boolean success = updateById(reservation);
        
        if (success) {
            // 更新车位状态为空闲
            ParkingSpace parkingSpace = parkingSpaceService.getById(reservation.getParkingSpaceId());
            if (parkingSpace != null) {
                parkingSpace.setStatus(0); // 0: 空闲
                parkingSpaceService.updateById(parkingSpace);
                
                // 更新停车场可用车位数量
                updateParkingLotAvailableSpaces(parkingSpace.getParkingLotId());
            }
        }
        
        return success;
    }
}