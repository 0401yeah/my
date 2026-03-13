package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.parking.common.Result;
import com.parking.service.SysOrgService;
import com.parking.service.ParkingLotService;
import com.parking.service.VehicleService;
import com.parking.service.ParkingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 数据看板 Controller
 */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    @Autowired
    private SysOrgService sysOrgService;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private ParkingRecordService parkingRecordService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取顶部统计卡片数据
     */
    @RequestMapping("/stats")
    public Result<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = new HashMap<>();

            // 合作单位数量
            long orgCount = sysOrgService.count();
            stats.put("orgCount", orgCount);

            // 停车场数量
            long lotCount = parkingLotService.count();
            stats.put("lotCount", lotCount);

            // 车辆数量
            long vehicleCount = vehicleService.count();
            stats.put("vehicleCount", vehicleCount);

            // 收益总额
            // 这里可以根据实际情况查询订单表计算总收益
            stats.put("totalRevenue", 9530.0);

            return Result.success(stats);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取统计数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取营收与车流量分析数据
     */
    @RequestMapping("/revenue-traffic")
    public Result<Map<String, Object>> getRevenueTraffic() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 近7天日期
            data.put("dates", new String[]{"02-14", "02-15", "02-16", "02-17", "02-18", "02-19", "02-20"});
            data.put("revenue", new double[]{1200, 1500, 1300, 1800, 1600, 1900, 1700});
            data.put("traffic", new int[]{800, 950, 850, 1100, 1000, 1150, 1050});

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取营收与车流量数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取停车场停车热度分析数据
     */
    @RequestMapping("/parking-heat")
    public Result<Map<String, Object>> getParkingHeat() {
        try {
            Map<String, Object> data = new HashMap<>();

            // 获取所有停车场
            List<String> parkingLots = parkingLotService.list().stream()
                    .map(parkingLot -> parkingLot.getName())
                    .collect(Collectors.toList());

            // 生成对应的热度数据（实际项目中应该根据真实数据计算）
            int[] heatData = new int[parkingLots.size()];
            for (int i = 0; i < parkingLots.size(); i++) {
                heatData[i] = 60 + (int)(Math.random() * 40); // 生成60-100的随机热度值
            }

            data.put("parkingLots", parkingLots.toArray(new String[0]));
            data.put("heatData", heatData);

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场热度数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取车场实况监控数据
     */
    @RequestMapping("/monitor")
    public Result<Map<String, Object>> getMonitorData(@RequestParam(required = false) Long lotId) {
        try {
            Map<String, Object> data = new HashMap<>();

            String lotSql = "SELECT * FROM app_parking_lot WHERE is_deleted = 0";
            if (lotId != null) {
                lotSql += " AND id = " + lotId;
            }

            List<Map<String, Object>> parkingLots = jdbcTemplate.queryForList(lotSql);

            if (parkingLots.isEmpty()) {
                data.put("totalSpaces", 0);
                data.put("availableSpaces", 0);
                data.put("occupiedSpaces", 0);
                data.put("parkingSpots", new ArrayList<>());
                return Result.success(data);
            }

            Map<String, Object> parkingLot = parkingLots.get(0);
            Long currentLotId = ((Number) parkingLot.get("id")).longValue();
            int totalSpaces = ((Number) parkingLot.getOrDefault("parking_space_number", 50)).intValue();

            // 查询正在停车中的车辆及其车位号
            String occupiedSql = "SELECT plate_number, space_no FROM app_parking_record WHERE parking_lot_id = ? AND status = 0";
            List<Map<String, Object>> occupiedRecords = jdbcTemplate.queryForList(occupiedSql, currentLotId);
            int occupiedSpaces = occupiedRecords.size();
            int availableSpaces = totalSpaces - occupiedSpaces;

            data.put("totalSpaces", totalSpaces);
            data.put("availableSpaces", availableSpaces);
            data.put("occupiedSpaces", occupiedSpaces);

            // 构建车位数据
            List<Map<String, Object>> parkingSpots = generateParkingSpotsFromRecords(totalSpaces, occupiedRecords, currentLotId);
            data.put("parkingSpots", parkingSpots);

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("totalSpaces", 0);
            errorData.put("availableSpaces", 0);
            errorData.put("occupiedSpaces", 0);
            errorData.put("parkingSpots", new ArrayList<>());
            return Result.success(errorData);
        }
    }

    /**
     * 根据停车记录生成车位数据
     */
    private List<Map<String, Object>> generateParkingSpotsFromRecords(int totalSpaces, List<Map<String, Object>> occupiedRecords, Long lotId) {
        List<Map<String, Object>> parkingSpots = new ArrayList<>();
        
        // 获取车位前缀
        String prefix = getSpacePrefix(lotId);
        
        // 构建已占用车位的映射
        Map<String, String> occupiedMap = new HashMap<>();
        for (Map<String, Object> record : occupiedRecords) {
            String spaceNo = (String) record.get("space_no");
            String plateNumber = (String) record.get("plate_number");
            if (spaceNo != null) {
                occupiedMap.put(spaceNo, plateNumber);
            }
        }
        
        // 生成所有车位
        // 计算充电桩数量（前10%）
        int chargingCount = (int) Math.floor(totalSpaces * 0.1);
        for (int i = 1; i <= totalSpaces; i++) {
            Map<String, Object> spot = new HashMap<>();
            // 生成纯数字格式的车位号
            String spaceNo = String.format("%03d", i);
            // 生成带前缀的车位号（用于数据库匹配）
            String spaceNoWithPrefix = prefix + spaceNo;
            spot.put("id", spaceNo);
            spot.put("spaceNo", spaceNo);
            
            if (occupiedMap.containsKey(spaceNoWithPrefix)) {
                spot.put("status", "occupied");
                spot.put("plateNumber", occupiedMap.get(spaceNoWithPrefix));
            } else {
                spot.put("status", "free");
                spot.put("plateNumber", null);
            }
            
            spot.put("isCharging", i <= chargingCount);
            parkingSpots.add(spot);
        }
        return parkingSpots;
    }

    /**
     * 获取车位号前缀
     */
    private String getSpacePrefix(Long lotId) {
        if (lotId == null) return "P-";
        switch (lotId.intValue()) {
            case 1: return "A-";
            case 2: return "B-";
            case 3: return "C-";
            case 4: return "D-";
            case 5: return "E-";
            default: return "P-";
        }
    }

    /**
     * 生成模拟车位数据
     */
    private List<Map<String, Object>> generateMockParkingSpots(int totalSpaces, int occupiedSpaces) {
        List<Map<String, Object>> parkingSpots = new ArrayList<>();
        // 计算充电桩数量（前10%）
        int chargingCount = (int) Math.floor(totalSpaces * 0.1);
        for (int i = 1; i <= totalSpaces; i++) {
            Map<String, Object> spot = new HashMap<>();
            // 生成纯数字格式的车位号
            String spaceNo = String.format("%03d", i);
            spot.put("id", spaceNo);
            spot.put("spaceNo", spaceNo);
            spot.put("status", i <= occupiedSpaces ? "occupied" : "free");
            spot.put("plateNumber", i <= occupiedSpaces ? "苏M" + (10000 + i) : null);
            spot.put("isCharging", i <= chargingCount); // 前10%的车位为充电桩
            parkingSpots.add(spot);
        }
        return parkingSpots;
    }
}
