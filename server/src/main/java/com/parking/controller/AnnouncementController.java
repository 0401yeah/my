package com.parking.controller;

import com.parking.common.Result;
import com.parking.entity.Announcement;
import com.parking.entity.vo.AnnouncementVO;
import com.parking.mapper.AnnouncementMapper;
import com.parking.service.AnnouncementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/announcement")
@CrossOrigin
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private AnnouncementMapper announcementMapper;

    @GetMapping("/list")
    public Result<Map<String, Object>> list(
            @RequestParam(defaultValue = "1") Integer current,
            @RequestParam(defaultValue = "20") Integer size,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {

        // 使用自定义查询获取包含创建者姓名的数据
        List<AnnouncementVO> announcements = announcementMapper.selectWithCreatorName();
        
        // 简单的内存分页处理
        int start = (current - 1) * size;
        int end = Math.min(start + size, announcements.size());
        List<AnnouncementVO> pageRecords = announcements.subList(start, end);

        Map<String, Object> data = new HashMap<>();
        data.put("records", pageRecords);
        data.put("total", announcements.size());
        data.put("size", size);
        data.put("current", current);

        return Result.success(data);
    }

    @GetMapping("/{id}")
    public Result<AnnouncementVO> getById(@PathVariable Long id) {
        // 先获取公告信息
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        
        // 转换为VO对象
        AnnouncementVO vo = new AnnouncementVO();
        vo.setId(announcement.getId());
        vo.setTitle(announcement.getTitle());
        vo.setContent(announcement.getContent());
        vo.setType(announcement.getType());
        vo.setStatus(announcement.getStatus());
        vo.setOrgId(announcement.getOrgId());
        vo.setCreatorId(announcement.getCreatorId());
        vo.setGmtCreate(announcement.getGmtCreate());
        
        return Result.success(vo);
    }

    @PostMapping
    public Result<String> save(@RequestBody Announcement announcement) {
        announcement.setGmtCreate(LocalDateTime.now());
        boolean success = announcementService.save(announcement);
        return success ? Result.success("保存成功") : Result.error("保存失败");
    }

    @PutMapping
    public Result<String> update(@RequestBody Announcement announcement) {
        boolean success = announcementService.updateById(announcement);
        return success ? Result.success("更新成功") : Result.error("更新失败");
    }

    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        boolean success = announcementService.removeById(id);
        return success ? Result.success("删除成功") : Result.error("删除失败");
    }
}
