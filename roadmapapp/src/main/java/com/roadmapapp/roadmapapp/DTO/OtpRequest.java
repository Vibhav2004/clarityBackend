package com.roadmapapp.roadmapapp.DTO;

public class OtpRequest {
    private String email;
    private String otp;
    private String sessionID;
    public String getSessionId() {
        return sessionID;
    }
    public void setSessionId(String sessionId) {
        this.sessionID = sessionId;
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
