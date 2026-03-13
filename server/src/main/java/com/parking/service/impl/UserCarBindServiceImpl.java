package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.UserCarBind;
import com.parking.mapper.UserCarBindMapper;
import com.parking.service.UserCarBindService;
import org.springframework.stereotype.Service;

/**
 * 车主与车辆绑定关系表(亲情组) Service 实现
 */
@Service
public class UserCarBindServiceImpl extends ServiceImpl<UserCarBindMapper, UserCarBind> implements UserCarBindService {
}
