package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.ParkingSpace;
import com.parking.mapper.ParkingSpaceMapper;
import com.parking.service.ParkingSpaceService;
import org.springframework.stereotype.Service;

@Service
public class ParkingSpaceServiceImpl extends ServiceImpl<ParkingSpaceMapper, ParkingSpace> implements ParkingSpaceService {
}
