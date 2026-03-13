package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.OperationStrategy;
import com.parking.service.OperationStrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/operation-strategy")
@CrossOrigin
public class OperationStrategyController {
    
    @Autowired
    private OperationStrategyService operationStrategyService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String strategyName,
            @RequestParam(required = false) Integer status) {
        
        Page<OperationStrategy> page = new Page<>(current, size);
        QueryWrapper<OperationStrategy> wrapper = new QueryWrapper<>();
        
        if (strategyName != null && !strategyName.isEmpty()) {
            wrapper.like("strategy_name", strategyName);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.eq("is_deleted", 0);
        wrapper.orderByDesc("gmt_create");
        
        Page<OperationStrategy> result = operationStrategyService.page(page, wrapper);
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<OperationStrategy> getById(@PathVariable Long id) {
        OperationStrategy operationStrategy = operationStrategyService.getById(id);
        return Result.success(operationStrategy);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody OperationStrategy operationStrategy) {
        boolean success = operationStrategyService.save(operationStrategy);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody OperationStrategy operationStrategy) {
        boolean success = operationStrategyService.updateById(operationStrategy);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = operationStrategyService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
