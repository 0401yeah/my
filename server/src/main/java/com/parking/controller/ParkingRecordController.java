package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ParkingRecord;
import com.parking.entity.UserCarBind;
import com.parking.entity.dto.VehicleEntryDTO;
import com.parking.service.ParkingRecordService;
import com.parking.service.UserCarBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * 停车记录管理 Controller
 */
@RestController
@RequestMapping("/api/record")
public class ParkingRecordController {

    @Autowired
    private ParkingRecordService parkingRecordService;

    @Autowired
    private UserCarBindService userCarBindService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取停车记录列表
     */
    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) Long userId
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
            if (status != null) {
                wrapper.eq("status", status);
            }
            if (orderNo != null && !orderNo.isEmpty()) {
                wrapper.eq("order_no", orderNo);
            }

            // 如果提供了用户ID，查询该用户的停车记录
            if (userId != null) {
                // 直接查询user_id字段匹配的停车记录
                wrapper.eq("user_id", userId);
            }

            // 执行分页查询
            Page<ParkingRecord> page = new Page<>(current, size);
            Page<ParkingRecord> result = parkingRecordService.page(page, wrapper);

            // 补充停车场名称和车位信息
            List<Map<String, Object>> recordsWithInfo = result.getRecords().stream().map(record -> {
                Map<String, Object> recordMap = new HashMap<>();
                recordMap.put("id", record.getId());
                recordMap.put("plateNumber", record.getPlateNumber());
                recordMap.put("parkingLotId", record.getParkingLotId());
                recordMap.put("gmtInto", record.getGmtInto());
                recordMap.put("gmtOut", record.getGmtOut());
                recordMap.put("stayMinutes", record.getStayMinutes());
                recordMap.put("payAmount", record.getPayAmount());
                recordMap.put("originalAmount", record.getOriginalAmount());
                recordMap.put("discountAmount", record.getDiscountAmount());
                recordMap.put("feeStandardId", record.getFeeStandardId());
                recordMap.put("vehicleType", record.getVehicleType());
                recordMap.put("orderNo", record.getOrderNo());
                recordMap.put("paymentStatus", record.getPaymentStatus());
                recordMap.put("payWay", record.getPayWay());
                recordMap.put("userId", record.getUserId());
                recordMap.put("type", record.getType());
                recordMap.put("status", record.getStatus());
                recordMap.put("spaceNo", record.getSpaceNo());
                recordMap.put("payStatus", record.getPaymentStatus());
                recordMap.put("cost", record.getPayAmount() != null ? record.getPayAmount().toString() : "0.00");

                // 查询停车场名称
                if (record.getParkingLotId() != null) {
                    try {
                        String lotNameSql = "SELECT name FROM app_parking_lot WHERE id = ?";
                        String lotName = jdbcTemplate.queryForObject(lotNameSql, String.class, record.getParkingLotId());
                        recordMap.put("parkManageName", lotName);
                    } catch (Exception e) {
                        recordMap.put("parkManageName", "未知停车场");
                    }
                } else {
                    recordMap.put("parkManageName", "未知停车场");
                }

                // 车位号直接使用spaceNo
                recordMap.put("parkingLot", record.getSpaceNo() != null ? record.getSpaceNo() : "未分配");

                return recordMap;
            }).collect(Collectors.toList());

            // 构建返回数据
            Map<String, Object> data = Map.of(
                    "records", recordsWithInfo,
                    "total", result.getTotal(),
                    "current", result.getCurrent(),
                    "size", result.getSize()
            );

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取停车记录详情
     */
    @RequestMapping("/detail/{id}")
    public Result<ParkingRecord> detail(@PathVariable Long id) {
        try {
            ParkingRecord record = parkingRecordService.getById(id);
            if (record == null) {
                return Result.error("停车记录不存在");
            }
            return Result.success(record);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 车辆入场
     * 新逻辑：车位有状态，入场时从可用池里捞一个号码牌给用户，出场时号码牌回池子
     */
    @RequestMapping("/entry")
    public Result<Map<String, Object>> vehicleEntry(@RequestBody VehicleEntryDTO dto) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            String plateNumber = dto.getPlateNumber();
            Long parkingLotId = dto.getParkingLotId();
            
            // 1. 检查停车场是否存在
            String checkParkSql = "SELECT name FROM app_parking_lot WHERE id = ?";
            try {
                jdbcTemplate.queryForObject(checkParkSql, String.class, parkingLotId);
            } catch (Exception e) {
                return Result.error("停车场不存在");
            }
            
            // 2. 检查是否已有未出场的停车记录
            String checkRecordSql = "SELECT id, space_no FROM app_parking_record WHERE plate_number = ? AND parking_lot_id = ? AND status = 0";
            try {
                List<Map<String, Object>> records = jdbcTemplate.queryForList(checkRecordSql, plateNumber, parkingLotId);
                if (!records.isEmpty()) {
                    // 车辆已在场内，自动执行出场操作
                    Map<String, Object> recordData = records.get(0);
                    Long recordId = ((Number) recordData.get("id")).longValue();
                    String spaceNo = (String) recordData.get("space_no");
                    
                    // 更新停车记录
                    ParkingRecord parkingRecord = parkingRecordService.getById(recordId);
                    parkingRecord.setGmtOut(new java.util.Date());
                    parkingRecord.setStatus(1);
                    
                    // 计算停留时间（分钟）
                    long stayMinutes = (parkingRecord.getGmtOut().getTime() - parkingRecord.getGmtInto().getTime()) / (1000 * 60);
                    parkingRecord.setStayMinutes((int) stayMinutes);
                    
                    parkingRecordService.updateById(parkingRecord);
                    
                    // 更新停车场剩余车位数
                    String updateAvailableSql = "UPDATE app_parking_lot SET available_space_number = available_space_number + 1 WHERE id = ?";
                    jdbcTemplate.update(updateAvailableSql, parkingLotId);
                    
                    // 更新车位状态为空闲
                    String updateSpaceStatusSql = "UPDATE app_parking_space SET status = 0 WHERE parking_lot_id = ? AND space_no = ?";
                    jdbcTemplate.update(updateSpaceStatusSql, parkingLotId, spaceNo);
                    
                    Map<String, Object> exitResult = new HashMap<>();
                    exitResult.put("message", "车辆出场成功");
                    exitResult.put("stayMinutes", stayMinutes);
                    exitResult.put("releasedSpace", spaceNo);
                    
                    return Result.success(exitResult);
                }
            } catch (Exception e) {
                // 没有记录，继续
            }
            
            // 3. 获取可用车位数量
            String getAvailableSql = "SELECT available_space_number FROM app_parking_lot WHERE id = ?";
            Integer availableSpaces = null;
            try {
                availableSpaces = jdbcTemplate.queryForObject(getAvailableSql, Integer.class, parkingLotId);
            } catch (Exception e) {
                return Result.error("获取可用车位失败");
            }
            
            if (availableSpaces == null || availableSpaces <= 0) {
                return Result.error("停车场已满，无空闲车位");
            }
            
            // 4. 从可用 di池中随机分配一个车位号
            String assignedSpace = null;
            String prefix = getSpacePrefix(parkingLotId);
            
            // 获取停车场总车位数
            String getTotalSql = "SELECT parking_space_number FROM app_parking_lot WHERE id = ?";
            Integer totalSpaces = null;
            try {
                totalSpaces = jdbcTemplate.queryForObject(getTotalSql, Integer.class, parkingLotId);
            } catch (Exception e) {
                return Result.error("获取停车场信息失败");
            }
            
            if (totalSpaces == null || totalSpaces <= 0) {
                return Result.error("停车场信息异常");
            }
            
            // 生成随机起始位置
            int randomStart = new java.util.Random().nextInt(totalSpaces) + 1;
            System.out.println("随机起始车位: " + randomStart);
            
            // 从随机位置开始查找可用车位
            int i = randomStart;
            int count = 0;
            while (count < totalSpaces) {
                String spaceNo = prefix + String.format("%03d", i);
                
                // 检查该车位是否被占用
                String checkSpaceSql = "SELECT id FROM app_parking_record WHERE space_no = ? AND parking_lot_id = ? AND status = 0";
                try {
                    Long spaceId = jdbcTemplate.queryForObject(checkSpaceSql, Long.class, spaceNo, parkingLotId);
                    if (spaceId == null) {
                        // 找到可用车位
                        assignedSpace = spaceNo;
                        break;
                    }
                } catch (Exception e) {
                    // 没有记录，找到可用车位
                    assignedSpace = spaceNo;
                    break;
                }
                
                // 循环查找
                i++;
                if (i > totalSpaces) {
                    i = 1; // 回到开始
                }
                count++;
            }
            
            if (assignedSpace == null) {
                return Result.error("无法分配车位");
            }
            
            // 5. 创建停车记录
            ParkingRecord record = new ParkingRecord();
            record.setPlateNumber(plateNumber);
            record.setParkingLotId(parkingLotId);
            record.setGmtInto(new java.util.Date());
            record.setStatus(0); // 0: 停车中
            record.setSpaceNo(assignedSpace);
            
            // 设置车辆类型快照
            try {
                String vehicleTypeSql = "SELECT type FROM app_vehicle WHERE plate_number = ?";
                List<Map<String, Object>> vehicleTypeRecords = jdbcTemplate.queryForList(vehicleTypeSql, plateNumber);
                if (!vehicleTypeRecords.isEmpty()) {
                    Integer type = ((Number) vehicleTypeRecords.get(0).get("type")).intValue();
                    record.setType(type);
                }
            } catch (Exception e) {
                // 车辆类型查询失败不影响入场流程，只记录日志
                e.printStackTrace();
            }
            
            // 生成临时订单号
            String orderNo = "PK" + new java.text.SimpleDateFormat("yyMMddHHmm").format(new java.util.Date()) + String.format("%04d", new java.util.Random().nextInt(10000));
            record.setOrderNo(orderNo);
            
            // 查询该车牌绑定的用户
            try {
                System.out.println("开始查询用户ID，车牌号: " + plateNumber);
                
                // 先从app_user_car_bind表查询
                String userSql = "SELECT user_id FROM app_user_car_bind WHERE plate_number = ?";
                List<Map<String, Object>> userRecords = jdbcTemplate.queryForList(userSql, plateNumber);
                System.out.println("app_user_car_bind查询结果: " + userRecords.size() + " 条记录");
                
                if (!userRecords.isEmpty()) {
                    Long userId = ((Number) userRecords.get(0).get("user_id")).longValue();
                    record.setUserId(userId);
                    System.out.println("从app_user_car_bind获取到用户ID: " + userId);
                } else {
                    // 如果app_user_car_bind表中没有，从app_vehicle表查询
                    String vehicleSql = "SELECT user_id FROM app_vehicle WHERE plate_number = ?";
                    List<Map<String, Object>> vehicleRecords = jdbcTemplate.queryForList(vehicleSql, plateNumber);
                    System.out.println("app_vehicle查询结果: " + vehicleRecords.size() + " 条记录");
                    
                    if (!vehicleRecords.isEmpty()) {
                        Long userId = ((Number) vehicleRecords.get(0).get("user_id")).longValue();
                        record.setUserId(userId);
                        System.out.println("从app_vehicle获取到用户ID: " + userId);
                    } else {
                        System.out.println("未找到该车牌的用户绑定记录");
                    }
                }
            } catch (Exception e) {
                // 用户查询失败不影响入场流程，只记录日志
                System.out.println("查询用户ID时出现异常: " + e.getMessage());
                e.printStackTrace();
            }
            
            // 输出最终设置的用户ID
            System.out.println("最终设置的用户ID: " + record.getUserId());
            
            boolean saved = parkingRecordService.save(record);
            
            if (!saved) {
                return Result.error("保存停车记录失败");
            }
            
            // 6. 更新停车场剩余车位数
            String updateAvailableSql = "UPDATE app_parking_lot SET available_space_number = available_space_number - 1 WHERE id = ?";
            jdbcTemplate.update(updateAvailableSql, parkingLotId);
            
            // 7. 更新车位状态为占用
            // 移除车位号前缀，只保留数字部分
            String spaceNoWithoutPrefix = assignedSpace.replaceAll("^[A-E]-?", "");
            String updateSpaceStatusSql = "UPDATE app_parking_space SET status = 1 WHERE parking_lot_id = ? AND space_no = ?";
            jdbcTemplate.update(updateSpaceStatusSql, parkingLotId, spaceNoWithoutPrefix);
            
            // 7. 返回结果
            result.put("spaceNo", assignedSpace);
            result.put("message", "车辆入场成功，分配车位: " + assignedSpace);
            result.put("recordId", record.getId());
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("车辆入场失败: " + e.getMessage());
        }
    }
    
    private String getSpacePrefix(Long parkingLotId) {
        if (parkingLotId == null) return "P-";
        switch (parkingLotId.intValue()) {
            case 1: return "A-";
            case 2: return "B-";
            case 3: return "C-";
            case 4: return "D-";
            case 5: return "E-";
            default: return "P-";
        }
    }

    /**
     * 车辆出场
     * 方案一：释放车位号，更新停车记录
     */
    @RequestMapping("/exit")
    public Result<Map<String, Object>> vehicleExit(
            @RequestParam String plateNumber,
            @RequestParam Long parkingLotId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            // 1. 查找车辆的停车记录
            String findRecordSql = "SELECT id, space_no FROM app_parking_record WHERE plate_number = ? AND parking_lot_id = ? AND status = 0 LIMIT 1";
            List<Map<String, Object>> records = jdbcTemplate.queryForList(findRecordSql, plateNumber, parkingLotId);
            
            if (records.isEmpty()) {
                return Result.error("未找到该车辆的停车记录");
            }
            
            // 2. 更新停车记录
            Map<String, Object> recordData = records.get(0);
            Long recordId = ((Number) recordData.get("id")).longValue();
            String spaceNo = (String) recordData.get("space_no");
            
            ParkingRecord parkingRecord = parkingRecordService.getById(recordId);
            parkingRecord.setGmtOut(new java.util.Date());
            parkingRecord.setStatus(1);
            
            // 计算停留时间（分钟）
            long stayMinutes = (parkingRecord.getGmtOut().getTime() - parkingRecord.getGmtInto().getTime()) / (1000 * 60);
            parkingRecord.setStayMinutes((int) stayMinutes);
            
            parkingRecordService.updateById(parkingRecord);
            
            // 3. 更新停车场剩余车位数
            String updateAvailableSql = "UPDATE app_parking_lot SET available_space_number = available_space_number + 1 WHERE id = ?";
            jdbcTemplate.update(updateAvailableSql, parkingLotId);
            
            // 4. 更新车位状态为空闲
            // 移除车位号前缀，只保留数字部分
            String spaceNoWithoutPrefix = spaceNo.replaceAll("^[A-E]-?", "");
            String updateSpaceStatusSql = "UPDATE app_parking_space SET status = 0 WHERE parking_lot_id = ? AND space_no = ?";
            jdbcTemplate.update(updateSpaceStatusSql, parkingLotId, spaceNoWithoutPrefix);
            
            result.put("message", "车辆出场成功");
            result.put("stayMinutes", stayMinutes);
            result.put("releasedSpace", spaceNo);
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("车辆出场失败: " + e.getMessage());
        }
    }

    /**
     * 删除停车记录
     */
    @RequestMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            boolean success = parkingRecordService.removeById(id);
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

    /**
     * 更新停车记录支付状态
     */
    @RequestMapping("/updatePaymentStatus")
    public Result<?> updatePaymentStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            ParkingRecord record = parkingRecordService.getById(id);
            if (record == null) {
                return Result.error("停车记录不存在");
            }

            record.setPaymentStatus(status);
            boolean success = parkingRecordService.updateById(record);
            if (success) {
                return Result.success("支付状态更新成功");
            } else {
                return Result.error("支付状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("支付状态更新失败: " + e.getMessage());
        }
    }

}
