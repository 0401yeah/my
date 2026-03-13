package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.ParkingLot;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 停车场管理 Controller
 */
@RestController
@RequestMapping("/api/lot")
public class ParkingLotController {

    @Autowired
    private ParkingLotService parkingLotService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取停车场列表
     */
    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Integer status
    ) {
        try {
            // 构建分页参数
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            // 构建查询条件
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ParkingLot> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            if (name != null && !name.isEmpty()) {
                wrapper.like("name", name);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.eq("is_deleted", 0);

            // 执行分页查询
            Page<ParkingLot> page = new Page<>(current, size);
            Page<ParkingLot> result = parkingLotService.page(page, wrapper);

            // 构建返回数据
            Map<String, Object> data = Map.of(
                    "records", result.getRecords(),
                    "total", result.getTotal(),
                    "current", result.getCurrent(),
                    "size", result.getSize()
            );

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取停车场详情
     */
    @RequestMapping("/detail/{id}")
    public Result<ParkingLot> detail(@PathVariable Long id) {
        try {
            ParkingLot parkingLot = parkingLotService.getById(id);
            if (parkingLot == null) {
                return Result.error("停车场不存在");
            }
            return Result.success(parkingLot);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取停车场详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加停车场
     */
    @RequestMapping("/add")
    public Result<?> add(@org.springframework.web.bind.annotation.RequestBody ParkingLot parkingLot) {
        try {
            boolean success = parkingLotService.save(parkingLot);
            if (success) {
                // 如果设置了车位数量，自动创建车位
                if (parkingLot.getParkingSpaceNumber() != null && parkingLot.getParkingSpaceNumber() > 0) {
                    parkingLotService.updateParkingSpaces(parkingLot.getId(), parkingLot.getParkingSpaceNumber());
                }
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 更新停车场
     */
    @RequestMapping("/update")
    public Result<?> update(@org.springframework.web.bind.annotation.RequestBody ParkingLot parkingLot) {
        try {
            // 获取原停车场信息
            ParkingLot originalParkingLot = parkingLotService.getById(parkingLot.getId());
            if (originalParkingLot == null) {
                return Result.error("停车场不存在");
            }
            
            // 更新停车场信息
            boolean success = parkingLotService.updateById(parkingLot);
            if (success) {
                // 检查车位数量是否发生变化
                if (parkingLot.getParkingSpaceNumber() != null && 
                    !parkingLot.getParkingSpaceNumber().equals(originalParkingLot.getParkingSpaceNumber())) {
                    // 车位数量发生变化，自动更新车位数据
                    parkingLotService.updateParkingSpaces(parkingLot.getId(), parkingLot.getParkingSpaceNumber());
                }
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除停车场
     */
    @RequestMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            // 软删除
            ParkingLot parkingLot = new ParkingLot();
            parkingLot.setId(id);
            parkingLot.setIsDeleted(1);
            boolean success = parkingLotService.updateById(parkingLot);
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
     * 生成停车位
     * @param parkingLotId 停车场ID
     * @param count 车位数量
     * @return 操作结果
     */
    @RequestMapping("/generate-spaces")
    public Result<?> generateSpaces(@RequestParam Long parkingLotId, @RequestParam Integer count) {
        try {
            ParkingLot parkingLot = parkingLotService.getById(parkingLotId);
            if (parkingLot == null) {
                return Result.error("停车场不存在");
            }
            
            parkingLotService.updateParkingSpaces(parkingLotId, count);
            
            // 更新停车场的车位数量
            parkingLot.setParkingSpaceNumber(count);
            parkingLotService.updateById(parkingLot);
            
            return Result.success("生成停车位成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("生成停车位失败: " + e.getMessage());
        }
    }

    /**
     * 批量生成所有停车场的停车位
     * @return 操作结果
     */
    @RequestMapping("/generate-all-spaces")
    public Result<?> generateAllSpaces() {
        try {
            // 获取所有停车场
            List<ParkingLot> parkingLots = parkingLotService.list();
            int successCount = 0;
            
            for (ParkingLot parkingLot : parkingLots) {
                // 跳过已删除的停车场
                if (parkingLot.getIsDeleted() != null && parkingLot.getIsDeleted() == 1) {
                    continue;
                }
                
                // 检查停车位数量是否一致
                if (parkingLot.getParkingSpaceNumber() != null && parkingLot.getParkingSpaceNumber() > 0) {
                    parkingLotService.updateParkingSpaces(parkingLot.getId(), parkingLot.getParkingSpaceNumber());
                    successCount++;
                }
            }
            
            return Result.success("批量生成停车位成功，共处理 " + successCount + " 个停车场");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("批量生成停车位失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步所有停车场的可用车位数
     * @return 操作结果
     */
    @RequestMapping("/sync-available-spaces")
    public Result<?> syncAvailableSpaces() {
        try {
            // 获取所有停车场
            List<ParkingLot> parkingLots = parkingLotService.list();
            int successCount = 0;
            
            for (ParkingLot parkingLot : parkingLots) {
                // 跳过已删除的停车场
                if (parkingLot.getIsDeleted() != null && parkingLot.getIsDeleted() == 1) {
                    continue;
                }
                
                Long lotId = parkingLot.getId();
                Integer totalSpaces = parkingLot.getParkingSpaceNumber();
                
                if (totalSpaces == null || totalSpaces <= 0) {
                    continue;
                }
                
                // 查询当前正在停车中的车辆数量
                String occupiedSql = "SELECT COUNT(*) FROM app_parking_record WHERE parking_lot_id = ? AND status = 0";
                Integer occupiedSpaces = jdbcTemplate.queryForObject(occupiedSql, Integer.class, lotId);
                
                if (occupiedSpaces == null) {
                    occupiedSpaces = 0;
                }
                
                // 计算可用车位数
                int availableSpaces = totalSpaces - occupiedSpaces;
                
                // 更新可用车位数
                String updateSql = "UPDATE app_parking_lot SET available_space_number = ? WHERE id = ?";
                jdbcTemplate.update(updateSql, availableSpaces, lotId);
                
                successCount++;
            }
            
            return Result.success("同步可用车位数成功，共处理 " + successCount + " 个停车场");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("同步可用车位数失败: " + e.getMessage());
        }
    }
    
    /**
     * 同步指定停车场的可用车位数
     * @param lotId 停车场ID
     * @return 操作结果
     */
    @RequestMapping("/sync-available-spaces/{lotId}")
    public Result<?> syncAvailableSpaces(@PathVariable Long lotId) {
        try {
            // 获取停车场信息
            ParkingLot parkingLot = parkingLotService.getById(lotId);
            if (parkingLot == null) {
                return Result.error("停车场不存在");
            }
            
            // 跳过已删除的停车场
            if (parkingLot.getIsDeleted() != null && parkingLot.getIsDeleted() == 1) {
                return Result.error("停车场已删除");
            }
            
            Integer totalSpaces = parkingLot.getParkingSpaceNumber();
            if (totalSpaces == null || totalSpaces <= 0) {
                return Result.error("停车场车位数量未设置");
            }
            
            // 查询当前正在停车中的车辆数量
            String occupiedSql = "SELECT COUNT(*) FROM app_parking_record WHERE parking_lot_id = ? AND status = 0";
            Integer occupiedSpaces = jdbcTemplate.queryForObject(occupiedSql, Integer.class, lotId);
            
            if (occupiedSpaces == null) {
                occupiedSpaces = 0;
            }
            
            // 计算可用车位数
            int availableSpaces = totalSpaces - occupiedSpaces;
            
            // 更新可用车位数
            String updateSql = "UPDATE app_parking_lot SET available_space_number = ? WHERE id = ?";
            jdbcTemplate.update(updateSql, availableSpaces, lotId);
            
            return Result.success("同步可用车位数成功，总车位: " + totalSpaces + "，占用: " + occupiedSpaces + "，可用: " + availableSpaces);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("同步可用车位数失败: " + e.getMessage());
        }
    }
}