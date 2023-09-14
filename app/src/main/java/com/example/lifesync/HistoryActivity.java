package com.example.lifesync;

import java.util.List;

public class HistoryActivity {
    final private int timeStamp;
    final private List<ActivityTask> activityTaskList;

    public HistoryActivity(int timeStamp, List<ActivityTask> activityTaskList) {
        this.timeStamp = timeStamp;
        this.activityTaskList = activityTaskList;
    }

    public int getTimeStamp() {
        return timeStamp;
    }

    public List<ActivityTask> getActivityTaskList() {
        return activityTaskList;
    }
}
