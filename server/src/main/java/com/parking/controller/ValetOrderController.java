package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ValetOrder;
import com.parking.service.ValetOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/valet-order")
@CrossOrigin
public class ValetOrderController {
    
    @Autowired
    private ValetOrderService valetOrderService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer status) {
        
        Page<ValetOrder> page = new Page<>(current, size);
        QueryWrapper<ValetOrder> wrapper = new QueryWrapper<>();
        
        if (plateNumber != null && !plateNumber.isEmpty()) {
            wrapper.like("plate_number", plateNumber);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<ValetOrder> result = valetOrderService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<ValetOrder> getById(@PathVariable Long id) {
        ValetOrder valetOrder = valetOrderService.getById(id);
        return Result.success(valetOrder);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody ValetOrder valetOrder) {
        if (valetOrder.getGmtCreate() == null) {
            valetOrder.setGmtCreate(LocalDateTime.now());
        }
        if (valetOrder.getGmtModified() == null) {
            valetOrder.setGmtModified(LocalDateTime.now());
        }
        if (valetOrder.getStatus() == null) {
            valetOrder.setStatus(0);
        }
        boolean success = valetOrderService.save(valetOrder);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody ValetOrder valetOrder) {
        valetOrder.setGmtModified(LocalDateTime.now());
        boolean success = valetOrderService.updateById(valetOrder);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = valetOrderService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
