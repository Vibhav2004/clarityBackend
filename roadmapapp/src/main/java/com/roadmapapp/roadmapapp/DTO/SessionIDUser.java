package com.roadmapapp.roadmapapp.DTO;

public class SessionIDUser {
    private String email;
    private String sessionID;
    private String userName;
    private String plan;
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getSessionID() {
        return sessionID;
    }
    public void setSessionID(String sessionId) {
        this.sessionID = sessionId;
    }
    public String getPlan() {
        return plan;
    }
    public void setPlan(String plan) {
        this.plan = plan;
    }
}
