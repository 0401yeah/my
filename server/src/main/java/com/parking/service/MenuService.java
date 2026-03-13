package com.parking.service;

import java.util.List;
import java.util.Map;

public interface MenuService {
    
    /**
     * 获取菜单列表，构建成前端需要的路由结构
     */
    List<Map<String, Object>> getMenuList();
    
    /**
     * 构建菜单树
     */
    List<Map<String, Object>> buildMenuTree(List<Map<String, Object>> menus, Long parentId);
}
