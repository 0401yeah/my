package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.CarTag;
import com.parking.mapper.CarTagMapper;
import com.parking.service.CarTagService;
import org.springframework.stereotype.Service;

/**
 * 车辆分组与黑白名单 Service 实现
 */
@Service
public class CarTagServiceImpl extends ServiceImpl<CarTagMapper, CarTag> implements CarTagService {
}
