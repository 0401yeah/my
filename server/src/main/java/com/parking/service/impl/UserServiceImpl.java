package com.parking.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.dto.LoginDTO;
import com.parking.dto.LoginVO;
import com.parking.dto.UserInfoDTO;
import com.parking.entity.User;
import com.parking.mapper.UserMapper;
import com.parking.service.UserService;
import com.parking.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    
    @Override
    public LoginVO login(LoginDTO dto) {
        // 在user表中查找用户
        User user = getByUserName(dto.getUserName());
        
        if (user != null) {
            // 验证用户的密码
            String md5Password = DigestUtil.md5Hex(dto.getPassword());
            if (!md5Password.equals(user.getPassword())) {
                throw new RuntimeException("密码错误");
            }
            
            if (user.getStatus() != 1) {
                throw new RuntimeException("账号已被禁用");
            }
            
            String token = jwtUtil.generateToken(user.getUserId(), user.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(user.getUserId());
            
            LoginVO vo = new LoginVO();
            vo.setToken("Bearer " + token);
            vo.setRefreshToken(refreshToken);
            return vo;
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
    
    @Override
    public UserInfoDTO getUserInfo(Long userId) {
        // 在user表中查找用户
        User user = userMapper.selectById(userId);
        
        if (user != null) {
            List<String> roles = userMapper.selectRoleSignsByUserId(userId);
            
            // 如果用户是普通用户（user_type=1），添加normal角色
            if (user.getUserType() == 1 && !roles.contains("normal")) {
                roles.add("normal");
            }
            
            UserInfoDTO dto = new UserInfoDTO();
            dto.setUserId(user.getUserId());
            dto.setUserName(user.getUsername());
            dto.setAvatar(user.getAvatar());
            dto.setRoles(roles);
            dto.setUserType(user.getUserType());
            dto.setNickname(user.getNickname());
            dto.setMobile(user.getMobile());
            
            // 设置创建时间
            if (user.getGmtCreate() != null) {
                dto.setGmtCreate(user.getGmtCreate().toString());
            }
            
            // 根据用户类型设置不同的按钮权限
            if (user.getUserType() == 0) {
                // 管理员权限
                dto.setButtons(Arrays.asList("add", "edit", "delete", "view", "export", "import"));
            } else {
                // 普通用户权限
                dto.setButtons(Arrays.asList("view", "add", "edit"));
            }
            
            return dto;
        } else {
            throw new RuntimeException("用户不存在");
        }
    }
    
    @Override
    public User getByUserName(String userName) {
        return userMapper.selectOne(
            new LambdaQueryWrapper<User>()
                .eq(User::getUsername, userName)
                .or()
                .eq(User::getMobile, userName)
        );
    }

    @Override
    public Page<User> getUserList(Integer current, Integer size) {
        Page<User> page = new Page<>(current, size);
        return userMapper.selectPage(page, null);
    }

    @Override
    public Page<User> getUserList(Integer current, Integer size, String nickname, String mobile, Integer status) {
        Page<User> page = new Page<>(current, size);
        return userMapper.selectPage(page, new LambdaQueryWrapper<User>()
            .like(nickname != null && !nickname.trim().isEmpty(), User::getNickname, nickname)
            .like(mobile != null && !mobile.trim().isEmpty(), User::getMobile, mobile)
            .eq(status != null, User::getStatus, status)
        );
    }

    @Override
    public boolean updateUserInfo(User user) {
        return userMapper.updateById(user) > 0;
    }
}