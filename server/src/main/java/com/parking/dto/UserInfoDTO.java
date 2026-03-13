package com.parking.dto;

import lombok.Data;
import java.util.List;

@Data
public class UserInfoDTO {
    private List<String> buttons;
    private List<String> roles;
    private Long userId;
    private String userName;
    private String avatar;
    private Integer userType;
    private String nickname;
    private String mobile;
    private String gmtCreate;
}