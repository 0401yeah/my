package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.CarwashOrder;
import com.parking.mapper.CarwashOrderMapper;
import com.parking.service.CarwashOrderService;
import org.springframework.stereotype.Service;

@Service
public class CarwashOrderServiceImpl extends ServiceImpl<CarwashOrderMapper, CarwashOrder> implements CarwashOrderService {
}
