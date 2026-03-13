package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.SysInterface;
import com.parking.service.SysInterfaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sys-interface")
@CrossOrigin
public class SysInterfaceController {
    
    @Autowired
    private SysInterfaceService sysInterfaceService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        Page<SysInterface> page = new Page<>(current, size);
        QueryWrapper<SysInterface> wrapper = new QueryWrapper<>();
        
        if (type != null && !type.isEmpty()) {
            wrapper.like("type", type);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<SysInterface> result = sysInterfaceService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<SysInterface> getById(@PathVariable Long id) {
        SysInterface sysInterface = sysInterfaceService.getById(id);
        return Result.success(sysInterface);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody SysInterface sysInterface) {
        boolean success = sysInterfaceService.save(sysInterface);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody SysInterface sysInterface) {
        boolean success = sysInterfaceService.updateById(sysInterface);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysInterfaceService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
