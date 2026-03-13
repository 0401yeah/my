package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.Vehicle;
import com.parking.mapper.VehicleMapper;
import com.parking.service.VehicleService;
import org.springframework.stereotype.Service;

/**
 * 车辆管理 Service 实现
 */
@Service
public class VehicleServiceImpl extends ServiceImpl<VehicleMapper, Vehicle> implements VehicleService {
}
