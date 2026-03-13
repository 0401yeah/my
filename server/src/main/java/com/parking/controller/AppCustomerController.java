package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.AppCustomer;
import com.parking.service.AppCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/customer")
@CrossOrigin
public class AppCustomerController {

    @Autowired
    private AppCustomerService appCustomerService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer status) {

        Page<AppCustomer> page = new Page<>(current, size);
        QueryWrapper<AppCustomer> wrapper = new QueryWrapper<>();

        if (mobile != null && !mobile.isEmpty()) {
            wrapper.like("mobile", mobile);
        }
        if (nickname != null && !nickname.isEmpty()) {
            wrapper.like("nickname", nickname);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.eq("is_deleted", 0);
        wrapper.orderByDesc("gmt_create");

        Page<AppCustomer> result = appCustomerService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());

        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<AppCustomer> getById(@PathVariable Long id) {
        AppCustomer customer = appCustomerService.getById(id);
        return Result.success(customer);
    }

    @PutMapping
    public Result<String> update(@RequestBody AppCustomer customer) {
        if (customer.getId() == null) {
            return Result.error("ID不能为空");
        }
        AppCustomer existingCustomer = appCustomerService.getById(customer.getId());
        if (existingCustomer == null) {
            return Result.error("用户不存在");
        }
        customer.setPassword(existingCustomer.getPassword());
        boolean success = appCustomerService.updateById(customer);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = appCustomerService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }

    @PutMapping("/status/{id}")
    public Result<String> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        AppCustomer customer = appCustomerService.getById(id);
        if (customer == null) {
            return Result.error("用户不存在");
        }
        customer.setStatus(status);
        boolean success = appCustomerService.updateById(customer);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
}
