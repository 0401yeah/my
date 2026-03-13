package com.parking.controller;

import com.parking.common.Result;
import com.parking.dto.LoginDTO;
import com.parking.dto.LoginVO;
import com.parking.dto.UserInfoDTO;
import com.parking.entity.User;
import com.parking.service.UserService;
import com.parking.util.JwtUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AuthController {
    
    private final UserService userService;
    private final JwtUtil jwtUtil;
    
    @PostMapping("/auth/login")
    public Result<LoginVO> login(@RequestBody LoginDTO dto) {
        try {
            LoginVO vo = userService.login(dto);
            return Result.success("登录成功", vo);
        } catch (Exception e) {
            return Result.error(401, e.getMessage());
        }
    }
    
    @GetMapping("/user/info")
    public Result<UserInfoDTO> getUserInfo(@RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }
            String token = authorization.replace("Bearer ", "");
            Long userId = jwtUtil.getUserId(token);
            UserInfoDTO dto = userService.getUserInfo(userId);
            return Result.success(dto);
        } catch (Exception e) {
            return Result.error(401, "未授权");
        }
    }

    @GetMapping("/user/list")
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String nickname,
            @RequestParam(required = false) String mobile,
            @RequestParam(required = false) Integer status) {
        try {
            Page<User> page = userService.getUserList(current, size, nickname, mobile, status);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }

    @PutMapping("/user/info")
    public Result<String> updateUserInfo(@RequestBody User user, @RequestHeader(value = "Authorization", required = false) String authorization) {
        try {
            if (authorization == null || !authorization.startsWith("Bearer ")) {
                return Result.error(401, "未授权");
            }
            String token = authorization.replace("Bearer ", "");
            Long userId = jwtUtil.getUserId(token);
            // 确保只能更新自己的信息
            user.setUserId(userId);
            boolean success = userService.updateUserInfo(user);
            return success ? Result.success("更新成功") : Result.error("更新失败");
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
