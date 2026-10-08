package com.roadmapapp.roadmapapp.DTO;

import com.roadmapapp.roadmapapp.entity.Tracker;

public class TrackerRequest {

    private Tracker trackerData;
    private String email;
    private String sessionID;

    public Tracker getTrackerData() {
        return trackerData;
    }

    public void setTrackerData(Tracker trackerData) {
        this.trackerData = trackerData;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSessionID() {
        return sessionID;
    }

    public void setSessionID(String sessionId) {
        this.sessionID = sessionId;
    }
}
