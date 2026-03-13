package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.dto.OrderDetailDTO;
import com.parking.entity.Order;
import com.parking.entity.ParkingRecord;
import com.parking.service.OrderService;
import com.parking.service.ParkingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ParkingRecordService parkingRecordService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/detail-list")
    public Result<Map<String, Object>> detailList(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status
    ) {
        try {
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            List<OrderDetailDTO> allRecords = new ArrayList<>();
            
            String orderSql = "SELECT o.*, pl.name AS parkingLotName FROM app_order o LEFT JOIN app_parking_lot pl ON o.parking_lot_id = pl.id WHERE 1=1";
            StringBuilder orderWhereClause = new StringBuilder();
            List<Object> orderParams = new ArrayList<>();
            
            if (orderNo != null && !orderNo.isEmpty()) {
                orderWhereClause.append(" AND o.order_no LIKE ?");
                orderParams.add("%" + orderNo + "%");
            }
            if (plateNumber != null && !plateNumber.isEmpty()) {
                orderWhereClause.append(" AND o.plate_number LIKE ?");
                orderParams.add("%" + plateNumber + "%");
            }
            if (orderType != null && orderType != 0) {
                orderWhereClause.append(" AND o.order_type = ?");
                orderParams.add(orderType);
            }
            if (status != null) {
                orderWhereClause.append(" AND o.status = ?");
                orderParams.add(status);
            }
            orderWhereClause.append(" ORDER BY o.gmt_create DESC");
            
            List<OrderDetailDTO> orderDtos = jdbcTemplate.query(orderSql + orderWhereClause.toString(), (rs, rowNum) -> {
                OrderDetailDTO dto = new OrderDetailDTO();
                dto.setId(rs.getLong("id"));
                dto.setOrderNo(rs.getString("order_no"));
                dto.setTransactionId(rs.getString("transaction_id"));
                
                dto.setOrderType(rs.getInt("order_type"));
                dto.setBody(rs.getString("body"));
                
                dto.setPayAmount(rs.getBigDecimal("pay_amount"));
                dto.setOriginalAmount(rs.getBigDecimal("original_amount"));
                dto.setDiscountAmount(rs.getBigDecimal("discount_amount"));
                dto.setPlateNumber(rs.getString("plate_number"));
                dto.setParkingLotId(rs.getLong("parking_lot_id"));
                dto.setParkingLotName(rs.getString("parkingLotName"));
                dto.setGmtInto(rs.getTimestamp("gmt_into"));
                dto.setGmtOut(rs.getTimestamp("gmt_out"));
                dto.setStatus(rs.getInt("status"));
                dto.setPayWay(rs.getInt("pay_way"));
                Long userId = rs.getLong("user_id");
                dto.setCustomerId(rs.wasNull() ? null : userId);
                dto.setPayTime(rs.getTimestamp("pay_time"));
                dto.setRemark(rs.getString("remark"));
                dto.setGmtCreate(rs.getTimestamp("gmt_create"));
                dto.setSource("order");
                return dto;
            }, orderParams.toArray());
            
            allRecords.addAll(orderDtos);
            
            if (orderType == null || orderType == 0) {
                com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<ParkingRecord> recordWrapper = 
                    new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
                
                if (orderNo != null && !orderNo.isEmpty()) {
                    recordWrapper.like("order_no", orderNo);
                }
                if (plateNumber != null && !plateNumber.isEmpty()) {
                    recordWrapper.like("plate_number", plateNumber);
                }
                if (status != null) {
                    recordWrapper.eq("payment_status", status);
                }
                recordWrapper.orderByDesc("gmt_out");
                
                List<ParkingRecord> records = parkingRecordService.list(recordWrapper);
                for (ParkingRecord record : records) {
                    OrderDetailDTO dto = new OrderDetailDTO();
                    dto.setId(record.getId());
                    dto.setOrderNo(record.getOrderNo());
                    dto.setOrderType(0);
                    dto.setBody("临停缴费");
                    dto.setPayAmount(record.getPayAmount());
                    dto.setOriginalAmount(record.getOriginalAmount());
                    dto.setDiscountAmount(record.getDiscountAmount());
                    dto.setPlateNumber(record.getPlateNumber());
                    dto.setParkingLotId(record.getParkingLotId());
                    
                    // 查询停车场名称
                    if (record.getParkingLotId() != null && record.getParkingLotId() > 0) {
                        try {
                            String lotNameSql = "SELECT name FROM app_parking_lot WHERE id = ?";
                            String lotName = jdbcTemplate.queryForObject(lotNameSql, String.class, record.getParkingLotId());
                            dto.setParkingLotName(lotName);
                        } catch (Exception e) {
                            // 停车场不存在，名称设为空
                        }
                    }
                    
                    dto.setGmtInto(record.getGmtInto());
                    dto.setGmtOut(record.getGmtOut());
                    dto.setStayMinutes(record.getStayMinutes());
                    dto.setOriginalAmount(record.getOriginalAmount());
                    dto.setDiscountAmount(record.getDiscountAmount());
                    dto.setFeeStandardId(record.getFeeStandardId());
                    dto.setVehicleType(record.getVehicleType());
                    dto.setPaymentStatus(record.getPaymentStatus());
                    dto.setStatus(record.getPaymentStatus());
                    dto.setPayWay(record.getPayWay());
                    dto.setCustomerId(record.getUserId());
                    // 使用入场时间作为创建时间，如果没有入场时间则使用出场时间
                    dto.setGmtCreate(record.getGmtInto() != null ? record.getGmtInto() : record.getGmtOut());
                    dto.setSource("parking_record");
                    allRecords.add(dto);
                }
            }
            
            allRecords.sort((a, b) -> {
                Date timeA = a.getGmtCreate() != null ? a.getGmtCreate() : new Date(0);
                Date timeB = b.getGmtCreate() != null ? b.getGmtCreate() : new Date(0);
                return timeB.compareTo(timeA);
            });
            
            int total = allRecords.size();
            int fromIndex = (current - 1) * size;
            int toIndex = Math.min(fromIndex + size, total);
            List<OrderDetailDTO> pageRecords = fromIndex < total ? allRecords.subList(fromIndex, toIndex) : new ArrayList<>();

            Map<String, Object> data = new HashMap<>();
            data.put("records", pageRecords);
            data.put("total", total);
            data.put("current", current);
            data.put("size", size);

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }

    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String plateNumber,
            @RequestParam(required = false) Integer orderType,
            @RequestParam(required = false) Integer status
    ) {
        try {
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Order> wrapper = 
                new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            
            if (orderNo != null && !orderNo.isEmpty()) {
                wrapper.like("order_no", orderNo);
            }
            if (plateNumber != null && !plateNumber.isEmpty()) {
                wrapper.like("plate_number", plateNumber);
            }
            if (orderType != null) {
                wrapper.eq("order_type", orderType);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.orderByDesc("gmt_create");

            Page<Order> page = new Page<>(current, size);
            Page<Order> resultPage = orderService.page(page, wrapper);

            Map<String, Object> data = new HashMap<>();
            data.put("records", resultPage.getRecords());
            data.put("total", resultPage.getTotal());
            data.put("current", resultPage.getCurrent());
            data.put("size", resultPage.getSize());

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取订单列表失败: " + e.getMessage());
        }
    }

    @RequestMapping("/detail/{id}")
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

    @RequestMapping("/updateStatus")
    public Result<?> updateStatus(@RequestParam Long id, @RequestParam Integer status) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            order.setStatus(status);
            boolean success = orderService.updateById(order);
            if (success) {
                return Result.success("状态更新成功");
            } else {
                return Result.error("状态更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("状态更新失败: " + e.getMessage());
        }
    }

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
                return Result.success("订单创建成功");
            } else {
                return Result.error("订单创建失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("订单创建失败: " + e.getMessage());
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

    @PutMapping("/update")
    public Result<?> update(@RequestBody Order order) {
        try {
            if (order.getId() == null) {
                return Result.error("订单ID不能为空");
            }
            
            Order existingOrder = orderService.getById(order.getId());
            if (existingOrder == null) {
                return Result.error("订单不存在");
            }

            boolean success = orderService.updateById(order);
            if (success) {
                return Result.success("订单更新成功");
            } else {
                return Result.error("订单更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("订单更新失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            Order order = orderService.getById(id);
            if (order == null) {
                return Result.error("订单不存在");
            }

            boolean success = orderService.removeById(id);
            if (success) {
                return Result.success("订单删除成功");
            } else {
                return Result.error("订单删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("订单删除失败: " + e.getMessage());
        }
    }
}
