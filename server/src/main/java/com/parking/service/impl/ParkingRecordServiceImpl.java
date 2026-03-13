package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingRecord;
import com.parking.mapper.ParkingRecordMapper;
import com.parking.service.ParkingRecordService;
import org.springframework.stereotype.Service;

/**
 * 停车记录 Service 实现
 */
@Service
public class ParkingRecordServiceImpl extends ServiceImpl<ParkingRecordMapper, ParkingRecord> implements ParkingRecordService {
}
