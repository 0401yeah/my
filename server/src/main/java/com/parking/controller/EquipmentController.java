package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Equipment;
import com.parking.service.EquipmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment")
@CrossOrigin
public class EquipmentController {
    
    @Autowired
    private EquipmentService equipmentService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status) {
        
        Page<Equipment> page = new Page<>(current, size);
        QueryWrapper<Equipment> wrapper = new QueryWrapper<>();
        
        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (code != null && !code.isEmpty()) {
            wrapper.like("code", code);
        }
        if (type != null && !type.isEmpty()) {
            wrapper.eq("type", type);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.eq("is_deleted", 0);
        wrapper.orderByDesc("gmt_create");
        
        Page<Equipment> result = equipmentService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<Equipment> getById(@PathVariable Long id) {
        Equipment equipment = equipmentService.getById(id);
        return Result.success(equipment);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Equipment equipment) {
        boolean success = equipmentService.save(equipment);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Equipment equipment) {
        boolean success = equipmentService.updateById(equipment);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = equipmentService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
