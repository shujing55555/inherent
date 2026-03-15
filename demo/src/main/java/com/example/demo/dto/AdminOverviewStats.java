package com.example.demo.dto;

import java.util.List;

/**
 * 后台首页数据分析 DTO
 */
public class AdminOverviewStats {

    private long userCount;
    private long projectCount;
    private long activityCount;
    private long newsCount;

    /** 非遗项目按区县分布 */
    private List<LabelCount> projectByRegion;

    /** 活动按区县分布 */
    private List<LabelCount> activityByRegion;

    public long getUserCount() {
        return userCount;
    }

    public void setUserCount(long userCount) {
        this.userCount = userCount;
    }

    public long getProjectCount() {
        return projectCount;
    }

    public void setProjectCount(long projectCount) {
        this.projectCount = projectCount;
    }

    public long getActivityCount() {
        return activityCount;
    }

    public void setActivityCount(long activityCount) {
        this.activityCount = activityCount;
    }

    public long getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(long newsCount) {
        this.newsCount = newsCount;
    }

    public List<LabelCount> getProjectByRegion() {
        return projectByRegion;
    }

    public void setProjectByRegion(List<LabelCount> projectByRegion) {
        this.projectByRegion = projectByRegion;
    }

    public List<LabelCount> getActivityByRegion() {
        return activityByRegion;
    }

    public void setActivityByRegion(List<LabelCount> activityByRegion) {
        this.activityByRegion = activityByRegion;
    }

    public static class LabelCount {
        private String label;
        private long count;

        public LabelCount() {
        }

        public LabelCount(String label, long count) {
            this.label = label;
            this.count = count;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public long getCount() {
            return count;
        }

        public void setCount(long count) {
            this.count = count;
        }
    }
}

