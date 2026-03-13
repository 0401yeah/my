package com.parking.dto;

import lombok.Data;

@Data
public class LoginVO {
    private String token;
    private String refreshToken;
}
