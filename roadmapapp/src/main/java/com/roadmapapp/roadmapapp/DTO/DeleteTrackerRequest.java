package com.roadmapapp.roadmapapp.DTO;

import java.util.List;

public class DeleteTrackerRequest {

    private List<Long> trackerIds;
    private String email;
    private String sessionID;

    public List<Long> getTrackerIds() {
        return trackerIds;
    }

    public void setTrackerIds(List<Long> trackerIds) {
        this.trackerIds = trackerIds;
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
