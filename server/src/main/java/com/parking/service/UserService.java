package com.parking.service;

import com.parking.dto.LoginDTO;
import com.parking.dto.LoginVO;
import com.parking.dto.UserInfoDTO;
import com.parking.entity.User;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

public interface UserService {
    LoginVO login(LoginDTO dto);
    UserInfoDTO getUserInfo(Long userId);
    User getByUserName(String userName);
    Page<User> getUserList(Integer current, Integer size);
    Page<User> getUserList(Integer current, Integer size, String nickname, String mobile, Integer status);
    boolean updateUserInfo(User user);
}
