package com.parking.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.CarTag;
import com.parking.service.CarTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 车辆分组与黑白名单管理 Controller
 */
@RestController
@RequestMapping("/api/blacklist")
public class CarTagController {

    @Autowired
    private CarTagService carTagService;

    /**
     * 获取黑白名单列表
     */
    @RequestMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(required = false) Integer current,
            @RequestParam(required = false) Integer size,
            @RequestParam(required = false) String plate_number,
            @RequestParam(required = false) Integer tag_type,
            @RequestParam(required = false) String expire_time_start,
            @RequestParam(required = false) String expire_time_end
    ) {
        try {
            // 构建分页参数
            current = current != null && current > 0 ? current : 1;
            size = size != null && size > 0 ? size : 20;

            // 构建查询条件
            com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<CarTag> wrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
            if (plate_number != null && !plate_number.isEmpty()) {
                wrapper.like("plate_number", plate_number);
            }
            if (tag_type != null) {
                wrapper.eq("tag_type", tag_type);
            } else {
                // 默认只查询黑白名单
                wrapper.in("tag_type", 1, 2);
            }
            if (expire_time_start != null) {
                wrapper.ge("expire_time", expire_time_start);
            }
            if (expire_time_end != null) {
                wrapper.le("expire_time", expire_time_end);
            }

            // 执行分页查询
            Page<CarTag> page = new Page<>(current, size);
            Page<CarTag> result = carTagService.page(page, wrapper);

            // 构建返回数据
            Map<String, Object> data = Map.of(
                    "records", result.getRecords(),
                    "total", result.getTotal(),
                    "current", result.getCurrent(),
                    "size", result.getSize()
            );

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取黑白名单列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取黑白名单详情
     */
    @RequestMapping("/detail/{id}")
    public Result<CarTag> detail(@PathVariable Long id) {
        try {
            CarTag carTag = carTagService.getById(id);
            if (carTag == null) {
                return Result.error("记录不存在");
            }
            return Result.success(carTag);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取记录详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加黑白名单
     */
    @RequestMapping("/add")
    public Result<?> add(@RequestParam String plate_number,
                         @RequestParam Integer tag_type,
                         @RequestParam(required = false) String tag_name,
                         @RequestParam(required = false) String reason,
                         @RequestParam(required = false) String remark,
                         @RequestParam(required = false) String expire_time) {
        try {
            CarTag carTag = new CarTag();
            carTag.setPlateNumber(plate_number);
            carTag.setTagType(tag_type);
            carTag.setTagName(tag_name);
            carTag.setReason(reason);
            carTag.setRemark(remark);
            carTag.setParkingLotId(1L); // 默认停车场ID
            carTag.setColor(tag_type == 1 ? "green" : "red"); // 白名单绿色，黑名单红色
            if (expire_time != null) {
                carTag.setExpireTime(java.sql.Timestamp.valueOf(expire_time));
            }

            boolean success = carTagService.save(carTag);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 删除黑白名单
     */
    @RequestMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            boolean success = carTagService.removeById(id);
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
}
