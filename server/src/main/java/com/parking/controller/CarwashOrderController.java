package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.CarwashOrder;
import com.parking.service.CarwashOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/carwash-order")
@CrossOrigin
public class CarwashOrderController {
    
    @Autowired
    private CarwashOrderService carwashOrderService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer status) {
        
        Page<CarwashOrder> page = new Page<>(current, size);
        QueryWrapper<CarwashOrder> wrapper = new QueryWrapper<>();
        
        if (plateNumber != null && !plateNumber.isEmpty()) {
            wrapper.like("plate_number", plateNumber);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<CarwashOrder> result = carwashOrderService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<CarwashOrder> getById(@PathVariable Long id) {
        CarwashOrder carwashOrder = carwashOrderService.getById(id);
        return Result.success(carwashOrder);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody CarwashOrder carwashOrder) {
        if (carwashOrder.getGmtCreate() == null) {
            carwashOrder.setGmtCreate(LocalDateTime.now());
        }
        if (carwashOrder.getGmtModified() == null) {
            carwashOrder.setGmtModified(LocalDateTime.now());
        }
        if (carwashOrder.getStatus() == null) {
            carwashOrder.setStatus(0);
        }
        if (carwashOrder.getWashType() == null) {
            carwashOrder.setWashType(1);
        }
        boolean success = carwashOrderService.save(carwashOrder);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody CarwashOrder carwashOrder) {
        carwashOrder.setGmtModified(LocalDateTime.now());
        boolean success = carwashOrderService.updateById(carwashOrder);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = carwashOrderService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
