package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.UserCarBind;
import com.parking.entity.Vehicle;
import com.parking.service.UserCarBindService;
import com.parking.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/family")
public class UserCarBindController {

    @Autowired
    private UserCarBindService userCarBindService;

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false, defaultValue = "1") Integer current,
            @RequestParam(required = false, defaultValue = "20") Integer size,
            @RequestParam(value = "plate_number", required = false) String plateNumber,
            @RequestParam(value = "user_id", required = false) Long userId,
            @RequestParam(value = "status", required = false) Integer status,
            @RequestParam(value = "expire_time_start", required = false) String expireTimeStart,
            @RequestParam(value = "expire_time_end", required = false) String expireTimeEnd
    ) {
        try {
            QueryWrapper<UserCarBind> wrapper = new QueryWrapper<>();
            
            if (plateNumber != null && !plateNumber.isEmpty()) {
                wrapper.like("plate_number", plateNumber);
            }
            if (userId != null) {
                wrapper.eq("user_id", userId);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            if (expireTimeStart != null && !expireTimeStart.isEmpty()) {
                wrapper.ge("expire_time", expireTimeStart);
            }
            if (expireTimeEnd != null && !expireTimeEnd.isEmpty()) {
                wrapper.le("expire_time", expireTimeEnd);
            }
            
            wrapper.orderByDesc("gmt_create");

            Page<UserCarBind> page = new Page<>(current, size);
            Page<UserCarBind> result = userCarBindService.page(page, wrapper);

            Map<Long, UserCarBind> mainAccountMap = new HashMap<>();
            List<UserCarBind> subAccountList = new ArrayList<>();
            
            for (UserCarBind record : result.getRecords()) {
                if (record.getBindType() == 1 && record.getParentId() == null) {
                    mainAccountMap.put(record.getId(), record);
                } else if (record.getBindType() == 2 && record.getParentId() != null) {
                    subAccountList.add(record);
                }
            }

            List<Map<String, Object>> processedRecords = new ArrayList<>();
            
            for (UserCarBind mainAccount : mainAccountMap.values()) {
                Map<String, Object> familyGroup = convertToMap(mainAccount);
                List<Map<String, Object>> subAccounts = new ArrayList<>();
                
                for (UserCarBind subAccount : subAccountList) {
                    if (subAccount.getParentId() != null && subAccount.getParentId().equals(mainAccount.getId())) {
                        subAccounts.add(convertToMap(subAccount));
                    }
                }
                
                familyGroup.put("subAccounts", subAccounts);
                familyGroup.put("subAccountCount", subAccounts.size());
                processedRecords.add(familyGroup);
            }

            for (UserCarBind subAccount : subAccountList) {
                if (!mainAccountMap.containsKey(subAccount.getParentId())) {
                    Map<String, Object> record = convertToMap(subAccount);
                    record.put("subAccounts", new ArrayList<>());
                    record.put("subAccountCount", 0);
                    processedRecords.add(record);
                }
            }

            Map<String, Object> data = new HashMap<>();
            data.put("records", processedRecords);
            data.put("total", result.getTotal());
            data.put("current", result.getCurrent());
            data.put("size", result.getSize());

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取亲情组列表失败: " + e.getMessage());
        }
    }

    private Map<String, Object> convertToMap(UserCarBind bind) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", bind.getId());
        map.put("userId", bind.getUserId());
        map.put("plateNumber", bind.getPlateNumber());
        map.put("bindType", bind.getBindType());
        map.put("parentId", bind.getParentId());
        map.put("isAutoPay", bind.getIsAutoPay());
        map.put("expireTime", bind.getExpireTime());
        map.put("gmtCreate", bind.getGmtCreate());
        map.put("familyName", bind.getFamilyName());
        map.put("status", bind.getStatus());
        
        if (bind.getPlateNumber() != null) {
            QueryWrapper<Vehicle> vehicleWrapper = new QueryWrapper<>();
            vehicleWrapper.eq("plate_number", bind.getPlateNumber());
            Vehicle vehicle = vehicleService.getOne(vehicleWrapper);
            if (vehicle != null) {
                map.put("ownerName", vehicle.getOwnerName());
                map.put("mobile", vehicle.getMobile());
                map.put("vehicleType", vehicle.getType());
            }
        }
        
        return map;
    }

    @GetMapping("/detail/{id}")
    public Result<Map<String, Object>> detail(@PathVariable Long id) {
        try {
            UserCarBind userCarBind = userCarBindService.getById(id);
            if (userCarBind == null) {
                return Result.error("记录不存在");
            }
            
            Map<String, Object> data = convertToMap(userCarBind);
            
            if (userCarBind.getBindType() == 1 && userCarBind.getParentId() == null) {
                QueryWrapper<UserCarBind> subWrapper = new QueryWrapper<>();
                subWrapper.eq("parent_id", id);
                subWrapper.eq("bind_type", 2);
                List<UserCarBind> subAccounts = userCarBindService.list(subWrapper);
                
                List<Map<String, Object>> subAccountMaps = new ArrayList<>();
                for (UserCarBind sub : subAccounts) {
                    subAccountMaps.add(convertToMap(sub));
                }
                data.put("subAccounts", subAccountMaps);
            }
            
            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取记录详情失败: " + e.getMessage());
        }
    }

    @PostMapping("/add")
    public Result<Map<String, Object>> add(@RequestBody Map<String, Object> requestBody) {
        try {
            String plateNumber = (String) requestBody.get("plate_number");
            Integer bindType = requestBody.get("bind_type") != null ? 
                Integer.parseInt(requestBody.get("bind_type").toString()) : 1;
            Long parentId = requestBody.get("parent_id") != null ? 
                Long.parseLong(requestBody.get("parent_id").toString()) : null;
            String familyName = (String) requestBody.get("family_name");
            String expireTimeStr = (String) requestBody.get("expire_time");
            
            Long userId = 1L;
            if (requestBody.get("user_id") != null) {
                userId = Long.parseLong(requestBody.get("user_id").toString());
            } else if (requestBody.get("customer_id") != null) {
                userId = Long.parseLong(requestBody.get("customer_id").toString());
            }

            QueryWrapper<Vehicle> vehicleWrapper = new QueryWrapper<>();
            vehicleWrapper.eq("plate_number", plateNumber);
            Vehicle vehicle = vehicleService.getOne(vehicleWrapper);
            
            if (vehicle == null) {
                return Result.error("车牌【" + plateNumber + "】不存在于车辆档案中，请先添加车辆");
            }
            
            if (bindType == 1) {
                if (vehicle.getType() == null || vehicle.getType() != 1) {
                    return Result.error("车牌【" + plateNumber + "】不是月租车，主车必须是月租车");
                }
            } else if (bindType == 2) {
                if (vehicle.getType() != null && vehicle.getType() == 1) {
                    return Result.error("车牌【" + plateNumber + "】是月租车，副车只能是临时车");
                }
            }

            UserCarBind userCarBind = new UserCarBind();
            userCarBind.setUserId(userId);
            userCarBind.setPlateNumber(plateNumber);
            userCarBind.setBindType(bindType);
            userCarBind.setFamilyName(familyName);
            userCarBind.setStatus(1);
            userCarBind.setIsAutoPay(0);
            
            if (bindType == 2 && parentId == null) {
                QueryWrapper<UserCarBind> mainCarWrapper = new QueryWrapper<>();
                mainCarWrapper.eq("user_id", userId)
                             .eq("bind_type", 1)
                             .isNull("parent_id");
                UserCarBind mainCar = userCarBindService.getOne(mainCarWrapper);
                if (mainCar != null) {
                    parentId = mainCar.getId();
                } else {
                    return Result.error("未找到主车信息，请先创建主车");
                }
            }
            
            userCarBind.setParentId(parentId);
            
            if (expireTimeStr != null && !expireTimeStr.isEmpty()) {
                try {
                    userCarBind.setExpireTime(java.sql.Timestamp.valueOf(expireTimeStr.replace("T", " ").substring(0, 19)));
                } catch (Exception e) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, 1);
                    userCarBind.setExpireTime(new Date(calendar.getTimeInMillis()));
                }
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.YEAR, 1);
                userCarBind.setExpireTime(new Date(calendar.getTimeInMillis()));
            }

            boolean success = userCarBindService.save(userCarBind);
            if (success) {
                Map<String, Object> data = new HashMap<>();
                data.put("id", userCarBind.getId());
                data.put("message", "添加成功");
                return Result.success(data);
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    @GetMapping("/check-vehicle")
    public Result<Map<String, Object>> checkVehicle(@RequestParam("plate_number") String plateNumber) {
        try {
            QueryWrapper<Vehicle> vehicleWrapper = new QueryWrapper<>();
            vehicleWrapper.eq("plate_number", plateNumber);
            Vehicle vehicle = vehicleService.getOne(vehicleWrapper);
            
            Map<String, Object> result = new HashMap<>();
            
            if (vehicle == null) {
                result.put("exists", false);
                result.put("isMonthly", false);
                result.put("message", "车牌不存在于车辆档案中");
                return Result.success(result);
            }
            
            result.put("exists", true);
            result.put("isMonthly", vehicle.getType() != null && vehicle.getType() == 1);
            result.put("vehicleType", vehicle.getType());
            result.put("ownerName", vehicle.getOwnerName());
            
            if (vehicle.getType() == null || vehicle.getType() != 1) {
                String typeDesc = vehicle.getType() == 0 ? "临时车" : (vehicle.getType() == 2 ? "VIP车" : "未知类型");
                result.put("message", "该车牌不是月租车（当前为" + typeDesc + "）");
            } else {
                result.put("message", "校验通过，该车辆为月租车");
            }
            
            return Result.success(result);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("校验失败: " + e.getMessage());
        }
    }

    @DeleteMapping("/delete/{id}")
    public Result<String> delete(@PathVariable Long id) {
        try {
            UserCarBind bind = userCarBindService.getById(id);
            if (bind == null) {
                return Result.error("记录不存在");
            }
            
            if (bind.getBindType() == 1 && bind.getParentId() == null) {
                QueryWrapper<UserCarBind> subWrapper = new QueryWrapper<>();
                subWrapper.eq("parent_id", id);
                userCarBindService.remove(subWrapper);
            }
            
            boolean success = userCarBindService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    @GetMapping("/user-vehicles")
    public Result<List<Map<String, Object>>> getUserVehicles(@RequestParam("user_id") Long userId) {
        try {
            QueryWrapper<UserCarBind> wrapper = new QueryWrapper<>();
            wrapper.eq("user_id", userId);
            List<UserCarBind> userCarBinds = userCarBindService.list(wrapper);
            
            List<Map<String, Object>> vehicles = new ArrayList<>();
            for (UserCarBind bind : userCarBinds) {
                Map<String, Object> vehicleInfo = convertToMap(bind);
                vehicles.add(vehicleInfo);
            }
            
            return Result.success(vehicles);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取用户车辆列表失败: " + e.getMessage());
        }
    }

    @PutMapping("/update")
    public Result<String> update(@RequestBody Map<String, Object> requestBody) {
        try {
            Long id = Long.parseLong(requestBody.get("id").toString());
            UserCarBind userCarBind = userCarBindService.getById(id);
            if (userCarBind == null) {
                return Result.error("记录不存在");
            }
            
            if (requestBody.get("plate_number") != null) {
                String plateNumber = requestBody.get("plate_number").toString();
                Integer bindType = requestBody.get("bind_type") != null ? 
                    Integer.parseInt(requestBody.get("bind_type").toString()) : userCarBind.getBindType();
                
                if (bindType == 1) {
                    QueryWrapper<Vehicle> vehicleWrapper = new QueryWrapper<>();
                    vehicleWrapper.eq("plate_number", plateNumber);
                    Vehicle vehicle = vehicleService.getOne(vehicleWrapper);
                    
                    if (vehicle == null) {
                        return Result.error("车牌【" + plateNumber + "】不存在于车辆档案中，请先添加车辆");
                    }
                }
                userCarBind.setPlateNumber(plateNumber);
                userCarBind.setBindType(bindType);
            }
            
            if (requestBody.get("user_id") != null) {
                userCarBind.setUserId(Long.parseLong(requestBody.get("user_id").toString()));
            } else if (requestBody.get("customer_id") != null) {
                userCarBind.setUserId(Long.parseLong(requestBody.get("customer_id").toString()));
            }
            
            if (requestBody.get("family_name") != null) {
                userCarBind.setFamilyName(requestBody.get("family_name").toString());
            }
            
            if (requestBody.get("parent_id") != null) {
                userCarBind.setParentId(Long.parseLong(requestBody.get("parent_id").toString()));
            } else if (requestBody.containsKey("parent_id")) {
                userCarBind.setParentId(null);
            }
            
            if (requestBody.get("is_auto_pay") != null) {
                userCarBind.setIsAutoPay(Integer.parseInt(requestBody.get("is_auto_pay").toString()));
            }
            
            if (requestBody.get("status") != null) {
                userCarBind.setStatus(Integer.parseInt(requestBody.get("status").toString()));
            }
            
            if (requestBody.get("expire_time") != null) {
                String expireTimeStr = requestBody.get("expire_time").toString();
                try {
                    userCarBind.setExpireTime(java.sql.Timestamp.valueOf(expireTimeStr.replace("T", " ").substring(0, 19)));
                } catch (Exception e) {
                    // 保持原值
                }
            }

            boolean success = userCarBindService.updateById(userCarBind);
            if (success) {
                return Result.success("更新成功");
            } else {
                return Result.error("更新失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("更新失败: " + e.getMessage());
        }
    }

    @PutMapping("/toggle-status/{id}")
    public Result<String> toggleStatus(@PathVariable Long id) {
        try {
            UserCarBind userCarBind = userCarBindService.getById(id);
            if (userCarBind == null) {
                return Result.error("记录不存在");
            }
            
            Integer currentStatus = userCarBind.getStatus();
            userCarBind.setStatus(currentStatus == null || currentStatus == 1 ? 0 : 1);
            
            boolean success = userCarBindService.updateById(userCarBind);
            if (success) {
                return Result.success(userCarBind.getStatus() == 1 ? "启用成功" : "禁用成功");
            } else {
                return Result.error("操作失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("操作失败: " + e.getMessage());
        }
    }
}
