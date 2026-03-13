package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ParkingRecord;
import com.parking.service.ParkingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 僵尸车管理 Controller
 */
@RestController
@RequestMapping("/api/zombie")
public class ZombieCarController {

    @Autowired
    private ParkingRecordService parkingRecordService;

    /**
     * 获取僵尸车列表
     */
    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer stayDurationMin,
            @RequestParam(required = false) Integer stayDurationMax
    ) {
        try {
            // 构建分页参数
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            // 构建查询条件
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ParkingRecord> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            if (plateNumber != null && !plateNumber.isEmpty()) {
                wrapper.like("plate_number", plateNumber);
            }
            // 不限制status，查询所有记录

            // 执行分页查询
            Page<ParkingRecord> page = new Page<>(current, size);
            Page<ParkingRecord> result = parkingRecordService.page(page, wrapper);

            // 计算停留天数并构建返回数据
            List<Map<String, Object>> zombieCars = new ArrayList<>();
            
            // 处理数据库中的记录
            for (ParkingRecord record : result.getRecords()) {
                // 实时计算停留天数（基于当前时间和入场时间）
                long stayMinutes = 0;
                if (record.getGmtInto() != null) {
                    long currentTime = System.currentTimeMillis();
                    long intoTime = record.getGmtInto().getTime();
                    stayMinutes = (currentTime - intoTime) / (1000 * 60);
                }
                int stayDays = (int) Math.ceil(stayMinutes / (24.0 * 60.0));
                
                // 调试信息
                System.out.println("Plate: " + record.getPlateNumber() + ", Stay minutes: " + stayMinutes + ", Stay days: " + stayDays + ", Status: " + record.getStatus());
                
                // 只有停留天数大于等于1的才认为是僵尸车
                if (stayDays >= 1) {
                    // 检查停留天数范围
                    if ((stayDurationMin == null || stayDays >= stayDurationMin) &&
                        (stayDurationMax == null || stayDays <= stayDurationMax)) {
                        Map<String, Object> car = Map.of(
                                "id", record.getId(),
                                "licensePlate", record.getPlateNumber(),
                                "parkingLot", record.getParkingLotId(),
                                "lastMoveTime", record.getGmtInto(),
                                "stayDuration", stayDays,
                                "level", getZombieLevel(stayDays),
                                "position", record.getSpaceNo() != null ? record.getSpaceNo() : ""
                        );
                        zombieCars.add(car);
                    }
                }
            }

            // 构建返回数据
            Map<String, Object> data = Map.of(
                    "records", zombieCars,
                    "total", zombieCars.size(),
                    "current", result.getCurrent(),
                    "size", result.getSize()
            );

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取僵尸车列表失败: " + e.getMessage());
        }
    }

    /**
     * 根据停留天数计算僵尸车等级
     * @param stayDays 停留天数
     * @return 等级 1-轻微 2-中等 3-严重
     */
    private int getZombieLevel(int stayDays) {
        if (stayDays < 7) {
            return 1;
        } else if (stayDays < 30) {
            return 2;
        } else {
            return 3;
        }
    }

    /**
     * 更新僵尸车信息
     */
    @RequestMapping(value = "/update", method = org.springframework.web.bind.annotation.RequestMethod.PUT)
    public Result<?> update(@org.springframework.web.bind.annotation.RequestBody Map<String, Object> data) {
        try {
            // 获取id
            Long id = ((Number) data.get("id")).longValue();
            if (id == null) {
                return Result.error("ID不能为空");
            }

            // 查询记录
            ParkingRecord record = parkingRecordService.getById(id);
            if (record == null) {
                return Result.error("记录不存在");
            }

            // 更新字段
            if (data.containsKey("licensePlate")) {
                record.setPlateNumber((String) data.get("licensePlate"));
            }
            if (data.containsKey("parkingLot")) {
                record.setParkingLotId(((Number) data.get("parkingLot")).longValue());
            }
            if (data.containsKey("lastMoveTime")) {
                // 这里可以根据实际需要处理时间格式
                // 暂时不更新，因为lastMoveTime对应gmtInto，通常不应该手动修改
            }
            if (data.containsKey("position")) {
                record.setSpaceNo((String) data.get("position"));
            }

            // 保存更新
            parkingRecordService.updateById(record);

            return Result.success("更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }
}
