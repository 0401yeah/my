package com.parking.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingLot;
import com.parking.entity.ParkingSpace;
import com.parking.mapper.ParkingLotMapper;
import com.parking.service.ParkingLotService;
import com.parking.service.ParkingSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 停车场管理 Service 实现类
 */
@Service
public class ParkingLotServiceImpl extends ServiceImpl<ParkingLotMapper, ParkingLot> implements ParkingLotService {

    @Autowired
    private ParkingSpaceService parkingSpaceService;

    @Override
    @Transactional
    public void updateParkingSpaces(Long parkingLotId, Integer targetCount) {
        // 获取当前车位
        QueryWrapper<ParkingSpace> wrapper = new QueryWrapper<>();
        wrapper.eq("parking_lot_id", parkingLotId);
        List<ParkingSpace> existingSpaces = parkingSpaceService.list(wrapper);
        
        // 只有当车位数量不一致时才重新生成
        if (existingSpaces.size() != targetCount) {
            // 先删除所有现有车位
            if (!existingSpaces.isEmpty()) {
                List<Long> ids = existingSpaces.stream().map(ParkingSpace::getId).collect(Collectors.toList());
                parkingSpaceService.removeByIds(ids);
            }
            
            // 重新生成所有车位，确保从1开始连续编号
            List<ParkingSpace> newSpaces = new ArrayList<>();
            for (int i = 1; i <= targetCount; i++) {
                ParkingSpace space = new ParkingSpace();
                space.setParkingLotId(parkingLotId);
                space.setSpaceNo(String.format("%03d", i)); // 纯数字格式，3位补零
                space.setStatus(0); // 初始状态为空闲
                newSpaces.add(space);
            }
            
            if (!newSpaces.isEmpty()) {
                parkingSpaceService.saveBatch(newSpaces);
            }
        }
        
        // 更新停车场表中的总车位数
        ParkingLot parkingLot = getById(parkingLotId);
        if (parkingLot != null) {
            parkingLot.setParkingSpaceNumber(targetCount);
            updateById(parkingLot);
        }
    }
}