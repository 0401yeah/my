package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Notice;
import com.parking.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Date;

/**
 * 通知与公告控制器
 */
@RestController
@RequestMapping("/api/notice")
@CrossOrigin
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    /**
     * 获取通知与公告列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer status
    ) {
        try {
            Page<Notice> page = new Page<>(current, size);
            QueryWrapper<Notice> wrapper = new QueryWrapper<>();

            if (title != null && !title.isEmpty()) {
                wrapper.like("title", title);
            }
            if (type != null) {
                wrapper.eq("type", type);
            }
            if (status != null) {
                wrapper.eq("status", status);
            }
            wrapper.orderByDesc("gmt_create");

            Page<Notice> result = noticeService.page(page, wrapper);

            Map<String, Object> data = Map.of(
                    "records", result.getRecords(),
                    "total", result.getTotal(),
                    "current", result.getCurrent(),
                    "size", result.getSize()
            );

            return Result.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取通知与公告列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取通知与公告详情
     */
    @GetMapping("/detail/{id}")
    public Result<Notice> detail(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getById(id);
            if (notice == null) {
                return Result.error("通知与公告不存在");
            }
            return Result.success(notice);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取通知与公告详情失败: " + e.getMessage());
        }
    }

    /**
     * 添加通知与公告
     */
    @PostMapping("/add")
    public Result<?> add(@RequestBody Notice notice) {
        try {
            notice.setGmtCreate(new Date());
            boolean success = noticeService.save(notice);
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
     * 更新通知与公告
     */
    @PutMapping("/update")
    public Result<?> update(@RequestBody Notice notice) {
        try {
            Notice existingNotice = noticeService.getById(notice.getId());
            if (existingNotice == null) {
                return Result.error("通知与公告不存在");
            }
            
            boolean success = noticeService.updateById(notice);
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

    /**
     * 删除通知与公告
     */
    @DeleteMapping("/delete/{id}")
    public Result<?> delete(@PathVariable Long id) {
        try {
            Notice notice = noticeService.getById(id);
            if (notice == null) {
                return Result.error("通知与公告不存在");
            }

            boolean success = noticeService.removeById(id);
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
