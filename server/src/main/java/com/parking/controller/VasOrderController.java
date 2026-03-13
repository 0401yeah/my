package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Order;
import com.parking.entity.Vehicle;
import com.parking.service.OrderService;
import com.parking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 增值服务订单管理 Controller
 */
@RestController
@RequestMapping("/api/vas-order")
public class VasOrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private VehicleService vehicleService;

    /**
     * 获取增值服务订单列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long userId
    ) {
        try {
            // 构建分页参数
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            // 构建查询条件
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            if (orderNo != null && !orderNo.isEmpty()) {
                wrapper.like("order_no", orderNo);
            }
            if (plateNumber != null && !plateNumber.isEmpty()) {
                wrapper.like("plate_number", plateNumber);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            if (userId != null) {
                wrapper.eq("user_id", userId);
            }
            wrapper.orderByDesc("gmt_create");

            // 执行分页查询
            Page<Order> page = new Page<>(current, size);
            Page<Order> result = orderService.page(page, wrapper);

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
            return Result.error("获取增值服务订单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取增值服务订单详情
     */
    @GetMapping("/detail/{id}")
    public Result<Order> detail(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            return Result.success(order);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加增值服务订单
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Order order) {
        try {
            if (order.getOrderNo() == null || order.getOrderNo().isEmpty()) {
                // 生成订单号
                order.setOrderNo(createOrderNo(order.getOrderType()));
            }
            if (order.getGmtCreate() == null) {
                order.setGmtCreate(new Date());
            }
            if (order.getStatus() == null) {
                // 如果订单金额为0，自动设为已支付状态
                if (order.getPayAmount() != null && order.getPayAmount().compareTo(java.math.BigDecimal.ZERO) <= 0) {
                    order.setStatus(1);
                    order.setPayTime(new Date());
                    order.setPayWay(3); // 免费抵扣
                } else {
                    order.setStatus(0);
                }
            }
            boolean success = orderService.save(order);
            if (success) {
                // 如果是月卡服务订单，更新车辆类型为月租车
                if (order.getOrderType() != null && order.getOrderType() == 3) {
                    // 根据车牌号查找车辆
                    com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<com.parking.entity.Vehicle> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                    wrapper.eq("plate_number", order.getPlateNumber());
                    com.parking.entity.Vehicle vehicle = vehicleService.getOne(wrapper);
                    if (vehicle != null) {
                        // 更新车辆类型为月租车
                        vehicle.setType(1); // 1表示月租车
                        // 计算到期时间（根据订单类型设置默认时长）
                        if (order.getOrderType() != null && order.getOrderType() == 3) {
                            // 月卡服务默认设置3个月
                            vehicle.setValidityTime(java.time.LocalDateTime.now().plusMonths(3));
                        }
                        vehicleService.updateById(vehicle);
                    }
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
     * 生成订单号
     * @param orderType 订单类型
     * @return 16位订单号
     */
    private String createOrderNo(Integer orderType) {
        // 1. 确定前缀
        String prefix = (orderType != null && orderType == 1) ? "PK" : "VS";
        
        // 2. 生成 10 位时间戳 (YYMMDDHHmm)
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyMMddHHmm");
        String timestamp = sdf.format(new Date());
        
        // 3. 生成 4 位随机数
        String randomSuffix = String.format("%04d", new java.util.Random().nextInt(10000));
        
        // 4. 拼接成 16 位
        return prefix + timestamp + randomSuffix;
    }

    /**
     * 更新增值服务订单
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Order order) {
        try {
            Order existingOrder = orderService.getById(order.getId());
            if (existingOrder == null) {
                return Result.error("订单不存在");
            }
            boolean success = orderService.updateById(order);
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

    /**
     * 删除增值服务订单
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }
            boolean success = orderService.removeById(id);
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