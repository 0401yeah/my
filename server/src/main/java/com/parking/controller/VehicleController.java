package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Vehicle;
import com.parking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@RestController
@RequestMapping("/api/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) String ownerName,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Integer type
    ) {
        try {
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Vehicle> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            if (plateNumber != null && !plateNumber.isEmpty()) {
                wrapper.like("plate_number", plateNumber);
            }
            if (ownerName != null && !ownerName.isEmpty()) {
                wrapper.like("owner_name", ownerName);
            }
            if (mobile != null && !mobile.isEmpty()) {
                wrapper.like("mobile", mobile);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            if (type != null) {
                wrapper.eq("type", type);
            }
            wrapper.eq("is_deleted", 0);
            wrapper.orderByDesc("gmt_create");

            Page<Vehicle> page = new Page<>(current, size);
            Page<Vehicle> result = vehicleService.page(page, wrapper);

            LocalDate now = LocalDate.now();
            String monthStart = now.withDayOfMonth(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            List<Map<String, Object>> recordsWithStats = new ArrayList<>();
            for (Vehicle vehicle : result.getRecords()) {
                Map<String, Object> vehicleMap = new HashMap<>();
                vehicleMap.put("id", vehicle.getId());
                vehicleMap.put("plateNumber", vehicle.getPlateNumber());
                vehicleMap.put("ownerName", vehicle.getOwnerName());
                vehicleMap.put("mobile", vehicle.getMobile());
                vehicleMap.put("userId", vehicle.getUserId());
                vehicleMap.put("parkingLotId", vehicle.getParkingLotId());
                vehicleMap.put("status", vehicle.getStatus());
                vehicleMap.put("type", vehicle.getType());
                vehicleMap.put("validityTime", vehicle.getValidityTime());
                vehicleMap.put("gmtCreate", vehicle.getGmtCreate());
                vehicleMap.put("gmtModified", vehicle.getGmtModified());

                try {
                    String monthlyCountSql = "SELECT COUNT(*) FROM app_parking_record WHERE plate_number = ? AND gmt_into >= ?";
                    Integer monthlyCount = jdbcTemplate.queryForObject(monthlyCountSql, Integer.class, vehicle.getPlateNumber(), monthStart);
                    vehicleMap.put("monthlyCount", monthlyCount != null ? monthlyCount : 0);
                } catch (Exception e) {
                    vehicleMap.put("monthlyCount", 0);
                }

                try {
                    String totalSpentSql = "SELECT COALESCE(SUM(pay_amount), 0) FROM app_parking_record WHERE plate_number = ? AND status = 1";
                    Double totalSpent = jdbcTemplate.queryForObject(totalSpentSql, Double.class, vehicle.getPlateNumber());
                    vehicleMap.put("totalSpent", totalSpent != null ? totalSpent : 0);
                } catch (Exception e) {
                    vehicleMap.put("totalSpent", 0);
                }

                recordsWithStats.add(vehicleMap);
            }

            Map<String, Object> data = new HashMap<>();
            data.put("records", recordsWithStats);
            data.put("total", result.getTotal());
            data.put("current", result.getCurrent());
            data.put("size", result.getSize());

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取车辆列表失败: " + e.getMessage());
        }
    }

    @RequestMapping("/detail/{id}")
    public Result<Vehicle> detail(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.getById(id);
            if (vehicle == null) {
                return Result.error("车辆不存在");
            }
            return Result.success(vehicle);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取车辆详情失败: " + e.getMessage());
        }
    }

    @RequestMapping("/add")
    public Result<?> add(@RequestBody Vehicle vehicle) {
        try {
            boolean success = vehicleService.save(vehicle);
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

    @RequestMapping("/update")
    public Result<?> update(@RequestBody Vehicle vehicle) {
        try {
            boolean success = vehicleService.updateById(vehicle);
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

    @RequestMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            boolean success = vehicleService.removeById(id);
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
