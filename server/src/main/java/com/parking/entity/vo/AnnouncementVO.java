package com.parking.entity.vo;

import com.parking.entity.Announcement;

@SuppressWarnings("serial")
public class AnnouncementVO extends Announcement {
    private String creatorName;

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }
}