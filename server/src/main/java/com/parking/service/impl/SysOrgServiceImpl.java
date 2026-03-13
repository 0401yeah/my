package com.parking.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.parking.entity.SysOrg;
import com.parking.mapper.SysOrgMapper;
import com.parking.service.SysOrgService;
import org.springframework.stereotype.Service;

/**
 * 机构管理表 服务实现类
 */
@Service
public class SysOrgServiceImpl extends ServiceImpl<SysOrgMapper, SysOrg> implements SysOrgService {
}
