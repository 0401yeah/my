package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.SysOrg;
import com.parking.service.SysOrgService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sys-org")
@CrossOrigin
public class SysOrgController {

    @Autowired
    private SysOrgService sysOrgService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String code,
            @RequestParam(required = false) Integer status) {

        Page<SysOrg> page = new Page<>(current, size);
        QueryWrapper<SysOrg> wrapper = new QueryWrapper<>();

        if (name != null && !name.isEmpty()) {
            wrapper.like("name", name);
        }
        if (code != null && !code.isEmpty()) {
            wrapper.like("code", code);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.eq("is_deleted", 0);
        wrapper.orderByAsc("order_num");
        wrapper.orderByDesc("gmt_create");

        Page<SysOrg> result = sysOrgService.page(page, wrapper);

        Map<String, Object> data = new HashMap<>();
        data.put("records", result.getRecords());
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());

        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<SysOrg> getById(@PathVariable Long id) {
        SysOrg sysOrg = sysOrgService.getById(id);
        return Result.success(sysOrg);
    }

    @PostMapping
    public Result<String> save(@RequestBody SysOrg sysOrg) {
        boolean success = sysOrgService.save(sysOrg);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }

    @PutMapping
    public Result<String> update(@RequestBody SysOrg sysOrg) {
        boolean success = sysOrgService.updateById(sysOrg);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = sysOrgService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
