package com.roadmapapp.roadmapapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentVerificationRequest {
    private String razorpayOrderId;

    private String razorpayPaymentId;

    private String razorpaySignature;

    private String email;

//   private String plan;

    private String sessionID;
}
