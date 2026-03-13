package com.parking.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.parking.entity.Announcement;
import com.parking.entity.vo.AnnouncementVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface AnnouncementMapper extends BaseMapper<Announcement> {

    @Select("SELECT a.*, u.nickname AS creator_name FROM app_announcement a LEFT JOIN sys_user u ON a.creator_id = u.user_id ORDER BY a.gmt_create DESC")
    List<AnnouncementVO> selectWithCreatorName();
}
