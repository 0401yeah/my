package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.AppCustomer;
import com.parking.mapper.AppCustomerMapper;
import com.parking.service.AppCustomerService;
import org.springframework.stereotype.Service;

@Service
public class AppCustomerServiceImpl extends ServiceImpl<AppCustomerMapper, AppCustomer> implements AppCustomerService {
}
