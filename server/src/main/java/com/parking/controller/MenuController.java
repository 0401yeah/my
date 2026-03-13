package com.parking.controller;

import com.parking.common.Result;
import com.parking.service.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v3/system")
@RequiredArgsConstructor
public class MenuController {
    
    private final MenuService menuService;
    
    @GetMapping("/menus")
    public Result<List<Map<String, Object>>> getMenuList() {
        try {
            List<Map<String, Object>> menuList = menuService.getMenuList();
            return Result.success(menuList);
        } catch (Exception e) {
            return Result.error(500, e.getMessage());
        }
    }
}
