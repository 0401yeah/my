package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.SysUser;
import com.parking.service.SysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/sys-user")
@CrossOrigin
public class SysUserController {
    
    @Autowired
    private SysUserService sysUserService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) Integer status) {

        try {
            Page<SysUser> page = new Page<>(current, size);
            QueryWrapper<SysUser> wrapper = new QueryWrapper<>();

            if (username != null && !username.isEmpty()) {
                wrapper.like("username", username);
            }
            if (nickname != null && !nickname.isEmpty()) {
                wrapper.like("nickname", nickname);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.eq("is_deleted", 0);
            wrapper.orderByDesc("gmt_create");

            Page<SysUser> result = sysUserService.page(page, wrapper);

            Map<String, Object> data = new HashMap<>();
            data.put("records", result.getRecords());
            data.put("total", result.getTotal());
            data.put("size", result.getSize());
            data.put("current", result.getCurrent());

            return Result.success(data);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<SysUser> getById(@PathVariable Long id) {
        try {
            SysUser sysUser = sysUserService.getById(id);
            return Result.success(sysUser);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PostMapping
    public Result<String> save(@RequestBody SysUser sysUser) {
        try {
            if (sysUser.getUsername() == null || sysUser.getUsername().isEmpty()) {
                return Result.error("用户名不能为空");
            }
            if (sysUser.getPassword() == null || sysUser.getPassword().isEmpty()) {
                sysUser.setPassword("123456");
            }
            String encryptedPassword = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
            sysUser.setPassword(encryptedPassword);
            if (sysUser.getOrgId() == null) {
                sysUser.setOrgId(1L);
            }
            if (sysUser.getStatus() == null) {
                sysUser.setStatus(1);
            }
            boolean success = sysUserService.save(sysUser);
            return success ? Result.success("保存成功") : Result.error("保存失败");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PutMapping
    public Result<String> update(@RequestBody SysUser sysUser) {
        try {
            if (sysUser.getUserId() == null) {
                return Result.error("用户ID不能为空");
            }
            SysUser existingUser = sysUserService.getById(sysUser.getUserId());
            if (existingUser == null) {
                return Result.error("用户不存在");
            }
            if (sysUser.getPassword() != null && !sysUser.getPassword().isEmpty()) {
                String encryptedPassword = DigestUtils.md5DigestAsHex(sysUser.getPassword().getBytes());
                sysUser.setPassword(encryptedPassword);
            } else {
                sysUser.setPassword(existingUser.getPassword());
            }
            boolean success = sysUserService.updateById(sysUser);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            boolean success = sysUserService.removeById(id);
            return success ? Result.success("删除成功") : Result.error("删除失败");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
