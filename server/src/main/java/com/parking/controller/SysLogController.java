package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.SysLog;
import com.parking.service.SysLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sys-log")
@CrossOrigin
public class SysLogController {
    
    @Autowired
    private SysLogService sysLogService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String operation) {
        
        Page<SysLog> page = new Page<>(current, size);
        QueryWrapper<SysLog> wrapper = new QueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (operation != null && !operation.isEmpty()) {
            wrapper.like("operation", operation);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<SysLog> result = sysLogService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<SysLog> getById(@PathVariable Long id) {
        SysLog sysLog = sysLogService.getById(id);
        return Result.success(sysLog);
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysLogService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
