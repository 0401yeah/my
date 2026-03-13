package com.parking.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.parking.common.Result;
import com.parking.entity.Feedback;
import com.parking.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin
public class FeedbackController {
    
    @Autowired
    private FeedbackService feedbackService;
    
    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) Integer status) {
        
        Page<Feedback> page = new Page<>(current, size);
        QueryWrapper<Feedback> wrapper = new QueryWrapper<>();
        
        if (username != null && !username.isEmpty()) {
            wrapper.like("username", username);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("gmt_create");
        
        Page<Feedback> result = feedbackService.page(page, wrapper);
        
        List<Map<String, Object>> records = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        
        for (Feedback feedback : result.getRecords()) {
            Map<String, Object> item = new HashMap<>();
            item.put("id", feedback.getId());
            item.put("userId", feedback.getUserId());
            item.put("username", feedback.getUsername());
            item.put("mobile", feedback.getMobile());
            item.put("content", feedback.getContent());
            item.put("replyContent", feedback.getReplyContent());
            item.put("replyUserId", feedback.getReplyUserId());
            item.put("replyTime", feedback.getReplyTime() != null ? sdf.format(feedback.getReplyTime()) : null);
            item.put("type", feedback.getType());
            item.put("status", feedback.getStatus());
            item.put("gmtCreate", feedback.getGmtCreate() != null ? sdf.format(feedback.getGmtCreate()) : null);
            records.add(item);
        }
        
        Map<String, Object> data = new HashMap<>();
        data.put("records", records);
        data.put("total", result.getTotal());
        data.put("size", result.getSize());
        data.put("current", result.getCurrent());
        
        return Result.success(data);
    }
    
    @GetMapping("/{id}")
    public Result<Feedback> getById(@PathVariable Long id) {
        Feedback feedback = feedbackService.getById(id);
        return Result.success(feedback);
    }
    
    @PostMapping
    public Result<String> save(@RequestBody Feedback feedback) {
        boolean success = feedbackService.save(feedback);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }
    
    @PutMapping
    public Result<String> update(@RequestBody Feedback feedback) {
        boolean success = feedbackService.updateById(feedback);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }
    
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = feedbackService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}