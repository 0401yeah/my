package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.EquipmentMaintain;
import com.parking.entity.vo.EquipmentMaintainVO;
import com.parking.service.EquipmentMaintainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/equipment-maintain")
@CrossOrigin
public class EquipmentMaintainController {
    
    @Autowired
    private EquipmentMaintainService equipmentMaintainService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Integer status) {
        
        // 获取所有带有设备信息的维修记录
        List<EquipmentMaintainVO> allRecords = equipmentMaintainService.getWithEquipment();
        
        // 计算分页
        int total = allRecords.size();
        int start = (current - 1) * size;
        int end = Math.min(start + size, total);
        List<EquipmentMaintainVO> pageRecords = allRecords.subList(start, end);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", pageRecords);
        data.put("total", total);
        data.put("size", size);
        data.put("current", current);
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<EquipmentMaintain> getById(@PathVariable Long id) {
        EquipmentMaintain equipmentMaintain = equipmentMaintainService.getById(id);
        return Result.success(equipmentMaintain);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody EquipmentMaintain equipmentMaintain) {
        boolean success = equipmentMaintainService.save(equipmentMaintain);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody EquipmentMaintain equipmentMaintain) {
        boolean success = equipmentMaintainService.updateById(equipmentMaintain);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = equipmentMaintainService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
