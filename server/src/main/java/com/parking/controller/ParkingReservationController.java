package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ParkingReservation;
import com.parking.service.ParkingReservationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/reservation")
@CrossOrigin
public class ParkingReservationController {

    @Autowired
    private ParkingReservationService parkingReservationService;

    /**
     * 获取预约列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) Long parkingLotId,
            @RequestParam(required = false) Integer status) {
        try {
            Page<ParkingReservation> page = new Page<>(current, size);
            QueryWrapper<ParkingReservation> wrapper = new QueryWrapper<>();
            
            if (parkingLotId != null) {
                wrapper.eq("parking_lot_id", parkingLotId);
            }
            
            if (status != null) {
                wrapper.eq("status", status);
            }
            
            wrapper.orderByDesc("gmt_create");
            
            Page<ParkingReservation> result = parkingReservationService.page(page, wrapper);
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", result.getRecords());
            data.put("total", result.getTotal());
            data.put("size", result.getSize());
            data.put("current", result.getCurrent());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取预约列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取预约详情
     */
    @GetMapping("/{id}")
    public Result<ParkingReservation> getById(@PathVariable Long id) {
        try {
            ParkingReservation reservation = parkingReservationService.getById(id);
            if (reservation == null) {
                return Result.error("预约不存在");
            }
            return Result.success(reservation);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取预约详情失败: " + e.getMessage());
        }
    }

    /**
     * 创建预约
     */
    @PostMapping
    public Result<String> create(@RequestBody ParkingReservation reservation) {
        try {
            boolean success = parkingReservationService.createReservation(reservation);
            if (success) {
                return Result.success("预约成功");
            } else {
                return Result.error("预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("预约失败: " + e.getMessage());
        }
    }

    /**
     * 取消预约
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable Long id) {
        try {
            boolean success = parkingReservationService.cancelReservation(id);
            if (success) {
                return Result.success("取消预约成功");
            } else {
                return Result.error("取消预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("取消预约失败: " + e.getMessage());
        }
    }

    /**
     * 确认预约
     */
    @PutMapping("/confirm/{id}")
    public Result<String> confirm(@PathVariable Long id) {
        try {
            boolean success = parkingReservationService.confirmReservation(id);
            if (success) {
                return Result.success("确认预约成功");
            } else {
                return Result.error("确认预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("确认预约失败: " + e.getMessage());
        }
    }

    /**
     * 完成预约
     */
    @PutMapping("/complete/{id}")
    public Result<String> complete(@PathVariable Long id) {
        try {
            boolean success = parkingReservationService.completeReservation(id);
            if (success) {
                return Result.success("完成预约成功");
            } else {
                return Result.error("完成预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("完成预约失败: " + e.getMessage());
        }
    }

    /**
     * 删除预约
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = parkingReservationService.removeById(id);
            if (success) {
                return Result.success("删除预约成功");
            } else {
                return Result.error("删除预约失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除预约失败: " + e.getMessage());
        }
    }
}