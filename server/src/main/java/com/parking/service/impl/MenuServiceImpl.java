package com.parking.service.impl;

import com.parking.entity.Menu;
import com.parking.entity.User;
import com.parking.entity.UserMenu;
import com.parking.mapper.MenuMapper;
import com.parking.mapper.UserMapper;
import com.parking.mapper.UserMenuMapper;
import com.parking.service.MenuService;
import com.parking.util.JwtUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {
    
    private final MenuMapper menuMapper;
    private final UserMapper userMapper;
    private final UserMenuMapper userMenuMapper;
    private final JwtUtil jwtUtil;
    
    @Override
    public List<Map<String, Object>> getMenuList() {
        // 从请求头获取用户信息，判断是否为普通用户
        boolean isCustomer = isCustomerUser();
        
        if (isCustomer) {
            // 普通用户使用 sys_menu_user 表
            return getCustomerMenuList();
        } else {
            // 管理员使用 sys_menu 表
            return getAdminMenuList();
        }
    }
    
    private boolean isCustomerUser() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                String authorization = request.getHeader("Authorization");
                System.out.println("[MenuService] Authorization header: " + authorization);
                if (authorization != null && authorization.startsWith("Bearer ")) {
                    String token = authorization.substring(7);
                    try {
                        Long userId = jwtUtil.getUserId(token);
                        System.out.println("[MenuService] User ID from token: " + userId);
                        
                        // 查询用户的 user_type 字段
                        User user = userMapper.selectById(userId);
                        if (user != null) {
                            Integer userType = user.getUserType();
                            System.out.println("[MenuService] User type: " + userType);
                            // user_type=1 为普通用户
                            boolean isCustomer = userType != null && userType == 1;
                            System.out.println("[MenuService] Is customer user: " + isCustomer);
                            return isCustomer;
                        }
                    } catch (Exception e) {
                        System.out.println("[MenuService] Error parsing token: " + e.getMessage());
                        // token 解析失败，默认为非客户用户
                        return false;
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("[MenuService] Error checking user type: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    
    private List<Map<String, Object>> getAdminMenuList() {
        // 查询所有菜单
        List<Menu> menuList = menuMapper.selectList(new QueryWrapper<Menu>().orderByAsc("order_num"));
        
        // 构建菜单映射，便于快速查找父菜单
        Map<Long, Menu> menuMap = new HashMap<>();
        for (Menu menu : menuList) {
            menuMap.put(menu.getMenuId(), menu);
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> menus = new ArrayList<>();
        for (Menu menu : menuList) {
            Map<String, Object> menuMapObj = new HashMap<>();
            menuMapObj.put("menuId", menu.getMenuId());
            menuMapObj.put("parentId", menu.getParentId());
            menuMapObj.put("path", menu.getUrl());
            menuMapObj.put("name", menu.getName());
            menuMapObj.put("type", menu.getType());
            menuMapObj.put("icon", menu.getIcon());
            menuMapObj.put("orderNum", menu.getOrderNum());
            
            // 构建meta信息
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", menu.getName());
            if (menu.getIcon() != null) {
                meta.put("icon", menu.getIcon());
            }
            menuMapObj.put("meta", meta);
            
            // 构建component信息
            if (menu.getType() == 0) { // 目录
                menuMapObj.put("component", "/index/index");
            } else if (menu.getType() == 1) { // 菜单
                String componentPath = menu.getUrl();
                // 如果 url 已经是完整路径（以 / 开头），直接使用
                if (componentPath != null && componentPath.startsWith("/")) {
                    menuMapObj.put("component", componentPath);
                } else if (menu.getParentId() != 0) {
                    // 否则拼接父菜单路径
                    Menu parentMenu = menuMap.get(menu.getParentId());
                    if (parentMenu != null) {
                        String parentUrl = parentMenu.getUrl();
                        if (parentUrl != null && parentUrl.startsWith("/")) {
                            componentPath = parentUrl + "/" + componentPath;
                        }
                    }
                    menuMapObj.put("component", componentPath);
                } else {
                    menuMapObj.put("component", componentPath);
                }
            }
            
            menus.add(menuMapObj);
        }
        
        // 构建菜单树
        return buildMenuTree(menus, 0L);
    }
    
    private List<Map<String, Object>> getCustomerMenuList() {
        // 查询普通用户菜单
        List<UserMenu> menuList = userMenuMapper.selectList(new QueryWrapper<UserMenu>().orderByAsc("order_num"));
        
        // 构建菜单映射，便于快速查找父菜单
        Map<Long, UserMenu> menuMap = new HashMap<>();
        for (UserMenu menu : menuList) {
            menuMap.put(menu.getMenuId(), menu);
        }
        
        // 转换为前端需要的格式
        List<Map<String, Object>> menus = new ArrayList<>();
        for (UserMenu menu : menuList) {
            Map<String, Object> menuMapObj = new HashMap<>();
            menuMapObj.put("menuId", menu.getMenuId());
            menuMapObj.put("parentId", menu.getParentId());
            menuMapObj.put("path", menu.getUrl());
            menuMapObj.put("name", menu.getName());
            menuMapObj.put("type", menu.getType());
            menuMapObj.put("icon", menu.getIcon());
            menuMapObj.put("orderNum", menu.getOrderNum());
            
            // 构建meta信息
            Map<String, Object> meta = new HashMap<>();
            meta.put("title", menu.getName());
            if (menu.getIcon() != null) {
                meta.put("icon", menu.getIcon());
            }
            menuMapObj.put("meta", meta);
            
            // 构建component信息
            if (menu.getType() == 0) { // 目录
                // 特殊处理首页菜单
                if (menu.getUrl() != null && menu.getUrl().equals("/home")) {
                    menuMapObj.put("component", "/home");
                } else {
                    menuMapObj.put("component", "/index/index");
                }
            } else if (menu.getType() == 1) { // 菜单
                String componentPath = menu.getUrl();
                // 如果 url 已经是完整路径（以 / 开头），直接使用
                if (componentPath != null && componentPath.startsWith("/")) {
                    menuMapObj.put("component", componentPath);
                } else if (menu.getParentId() != 0) {
                    // 否则拼接父菜单路径
                    UserMenu parentMenu = menuMap.get(menu.getParentId());
                    if (parentMenu != null) {
                        String parentUrl = parentMenu.getUrl();
                        if (parentUrl != null && parentUrl.startsWith("/")) {
                            componentPath = parentUrl + "/" + componentPath;
                        }
                    }
                    menuMapObj.put("component", componentPath);
                } else {
                    menuMapObj.put("component", componentPath);
                }
            }
            
            menus.add(menuMapObj);
        }
        
        // 构建菜单树
        return buildMenuTree(menus, 0L);
    }
    
    @Override
    public List<Map<String, Object>> buildMenuTree(List<Map<String, Object>> menus, Long parentId) {
        List<Map<String, Object>> menuTree = new ArrayList<>();
        
        for (Map<String, Object> menu : menus) {
            if (parentId.equals(menu.get("parentId"))) {
                List<Map<String, Object>> children = buildMenuTree(menus, (Long) menu.get("menuId"));
                if (!children.isEmpty()) {
                    menu.put("children", children);
                }
                menuTree.add(menu);
            }
        }
        
        return menuTree;
    }
}