package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Expense;
import com.parking.entity.ParkingLot;
import com.parking.service.ExpenseService;
import com.parking.service.ParkingLotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/expense")
public class ExpenseController {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private ParkingLotService parkingLotService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer status) {
        try {
            Page<Expense> page = new Page<>(current, size);
            QueryWrapper<Expense> wrapper = new QueryWrapper<>();
            
            if (category != null && !category.isEmpty()) {
                wrapper.eq("category", category);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.orderByDesc("gmt_create");
            
            Page<Expense> result = expenseService.page(page, wrapper);
            
            List<Map<String, Object>> records = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            for (Expense expense : result.getRecords()) {
                Map<String, Object> item = new HashMap<>();
                item.put("id", expense.getId());
                item.put("expenseNo", expense.getExpenseNo());
                item.put("category", expense.getCategory());
                item.put("amount", expense.getAmount());
                item.put("parkingLotId", expense.getParkingLotId());
                item.put("description", expense.getDescription());
                item.put("status", expense.getStatus());
                item.put("gmtCreate", expense.getGmtCreate() != null ? sdf.format(expense.getGmtCreate()) : null);
                
                if (expense.getParkingLotId() != null) {
                    String sql = "SELECT name FROM app_parking_lot WHERE id = ?";
                    String name = jdbcTemplate.queryForObject(sql, String.class, expense.getParkingLotId());
                    item.put("parkingLotName", name);
                } else {
                    item.put("parkingLotName", null);
                }
                
                records.add(item);
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("records", records);
            data.put("total", result.getTotal());
            data.put("current", result.getCurrent());
            data.put("size", result.getSize());
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取支出列表失败: " + e.getMessage());
        }
    }

    @RequestMapping("/{id}")
    public Result<Map<String, Object>> getDetail(@PathVariable Long id) {
        try {
            Expense expense = expenseService.getById(id);
            if (expense == null) {
                return Result.error("支出记录不存在");
            }
            
            Map<String, Object> data = new HashMap<>();
            data.put("id", expense.getId());
            data.put("expenseNo", expense.getExpenseNo());
            data.put("category", expense.getCategory());
            data.put("amount", expense.getAmount());
            data.put("parkingLotId", expense.getParkingLotId());
            data.put("description", expense.getDescription());
            data.put("status", expense.getStatus());
            
            if (expense.getParkingLotId() != null) {
                String sql = "SELECT name FROM app_parking_lot WHERE id = ?";
                String name = jdbcTemplate.queryForObject(sql, String.class, expense.getParkingLotId());
                data.put("parkingLotName", name);
            }
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取支出详情失败: " + e.getMessage());
        }
    }

    @PostMapping
    public Result<String> add(@RequestBody Expense expense) {
        try {
            // 生成支出单号，前缀为EX
            String prefix = "EX";
            // 生成10位时间戳 (YYMMDDHHmm)
            SimpleDateFormat sdf = new SimpleDateFormat("yyMMddHHmm");
            String timestamp = sdf.format(new Date());
            // 生成4位随机数
            String randomSuffix = String.format("%04d", new Random().nextInt(10000));
            // 拼接成16位支出单号
            String expenseNo = prefix + timestamp + randomSuffix;
            expense.setExpenseNo(expenseNo);
            expense.setStatus(0);
            expense.setGmtCreate(new Date());
            
            expenseService.save(expense);
            return Result.success("新增成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("新增失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public Result<String> update(@PathVariable Long id, @RequestBody Expense expense) {
        try {
            Expense existing = expenseService.getById(id);
            if (existing == null) {
                return Result.error("支出记录不存在");
            }
            
            if (existing.getStatus() != 0) {
                return Result.error("只能编辑待审核的记录");
            }
            
            expense.setId(id);
            expenseService.updateById(expense);
            return Result.success("修改成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修改失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            Expense existing = expenseService.getById(id);
            if (existing == null) {
                return Result.error("支出记录不存在");
            }
            
            expenseService.removeById(id);
            return Result.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @PutMapping("/{id}/approve")
    public Result<String> approve(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        try {
            Expense existing = expenseService.getById(id);
            if (existing == null) {
                return Result.error("支出记录不存在");
            }
            
            if (existing.getStatus() != 0) {
                return Result.error("该记录已审批");
            }
            
            Integer status = body.get("status");
            if (status == null || (status != 1 && status != 2)) {
                return Result.error("无效的审批状态");
            }
            
            existing.setStatus(status);
            existing.setApproveTime(new Date());
            expenseService.updateById(existing);
            
            return Result.success(status == 1 ? "审批通过" : "审批拒绝");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("审批失败: " + e.getMessage());
        }
    }
}
