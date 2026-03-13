package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.SysConfig;
import com.parking.service.SysConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sys-config")
@CrossOrigin
public class SysConfigController {
    
    @Autowired
    private SysConfigService sysConfigService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String configKey,
            @RequestParam(required = false) Integer status) {
        
        Page<SysConfig> page = new Page<>(current, size);
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        
        if (configKey != null && !configKey.isEmpty()) {
            wrapper.like("config_key", configKey);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<SysConfig> result = sysConfigService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<SysConfig> getById(@PathVariable Long id) {
        SysConfig sysConfig = sysConfigService.getById(id);
        return Result.success(sysConfig);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody SysConfig sysConfig) {
        boolean success = sysConfigService.save(sysConfig);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody SysConfig sysConfig) {
        boolean success = sysConfigService.updateById(sysConfig);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysConfigService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
    
    @GetMapping("/invoice-config")
    public Result<Map<String, String>> getInvoiceConfig() {
        QueryWrapper<SysConfig> wrapper = new QueryWrapper<>();
        wrapper.in("config_key", Arrays.asList("invoice_type", "invoice_title", "invoice_content"));
        wrapper.eq("status", 1);
        
        Map<String, String> config = new HashMap<>();
        config.put("invoiceType", "增值税电子普通发票");
        config.put("invoiceTitle", "个人");
        config.put("invoiceContent", "*停车服务*车辆停放费");
        
        for (SysConfig sysConfig : sysConfigService.list(wrapper)) {
            if ("invoice_type".equals(sysConfig.getConfigKey())) {
                config.put("invoiceType", sysConfig.getConfigValue());
            } else if ("invoice_title".equals(sysConfig.getConfigKey())) {
                config.put("invoiceTitle", sysConfig.getConfigValue());
            } else if ("invoice_content".equals(sysConfig.getConfigKey())) {
                config.put("invoiceContent", sysConfig.getConfigValue());
            }
        }
        
        return Result.success(config);
    }
}
