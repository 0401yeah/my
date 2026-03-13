package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ParkingSpace;
import com.parking.service.ParkingSpaceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/parking-space")
@CrossOrigin
public class ParkingSpaceController {
    
    @Autowired
    private ParkingSpaceService parkingSpaceService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "100") Integer size,
            @RequestParam(required = false) String parkingLotId,
            @RequestParam(required = false) String spaceNo,
            @RequestParam(required = false) Integer status) {
        
        try {
            Page<ParkingSpace> page = new Page<>(current, size);
            QueryWrapper<ParkingSpace> wrapper = new QueryWrapper<>();
            
            if (parkingLotId != null && !parkingLotId.isEmpty()) {
                wrapper.eq("parking_lot_id", Long.parseLong(parkingLotId));
            }
            
            if (spaceNo != null && !spaceNo.isEmpty()) {
                wrapper.like("space_no", spaceNo);
            }
            
            if (status != null) {
                wrapper.eq("status", status);
            }
            
            // 按照车位编号排序
            wrapper.orderByAsc("space_no");
            
            Page<ParkingSpace> result = parkingSpaceService.page(page, wrapper);
            
            // 如果指定了停车场ID，查询实时停车记录并更新车位状态
            if (parkingLotId != null && !parkingLotId.isEmpty()) {
                Long lotId = Long.parseLong(parkingLotId);
                // 查询正在停车的记录
                String occupiedSql = "SELECT space_no FROM app_parking_record WHERE parking_lot_id = ? AND status = 0";
                List<Map<String, Object>> occupiedRecords = jdbcTemplate.queryForList(occupiedSql, lotId);
                
                // 构建已占用车位号集合（移除前缀）
                java.util.Set<String> occupiedSpaceNos = new java.util.HashSet<>();
                for (Map<String, Object> record : occupiedRecords) {
                    String spaceNoWithPrefix = (String) record.get("space_no");
                    if (spaceNoWithPrefix != null) {
                        // 移除车位号前缀，只保留数字部分
                        String spaceNoWithoutPrefix = spaceNoWithPrefix.replaceAll("^[A-E]-?", "");
                        occupiedSpaceNos.add(spaceNoWithoutPrefix);
                    }
                }
                
                // 更新车位状态
                for (ParkingSpace space : result.getRecords()) {
                    if (occupiedSpaceNos.contains(space.getSpaceNo())) {
                        space.setStatus(1); // 设置为占用状态
                    }
                }
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", result.getRecords());
            data.put("total", result.getTotal());
            data.put("size", result.getSize());
            data.put("current", result.getCurrent());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取车位列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/{id}")
    public Result<ParkingSpace> getById(@PathVariable Long id) {
        try {
            ParkingSpace parkingSpace = parkingSpaceService.getById(id);
            if (parkingSpace == null) {
                return Result.error("车位不存在");
            }
            return Result.success(parkingSpace);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取车位详情失败: " + e.getMessage());
        }
    }
    
    @PostMapping
    public Result<String> save(@RequestBody ParkingSpace parkingSpace) {
        try {
            boolean success = parkingSpaceService.save(parkingSpace);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    @PutMapping
    public Result<String> update(@RequestBody ParkingSpace parkingSpace) {
        try {
            boolean success = parkingSpaceService.updateById(parkingSpace);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = parkingSpaceService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}
