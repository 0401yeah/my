package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.SysInterface;
import com.parking.mapper.SysInterfaceMapper;
import com.parking.service.SysInterfaceService;
import org.springframework.stereotype.Service;

@Service
public class SysInterfaceServiceImpl extends ServiceImpl<SysInterfaceMapper, SysInterface> implements SysInterfaceService {
}
