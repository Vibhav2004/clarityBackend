package com.roadmapapp.roadmapapp.DTO;



import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaymentFailedRequest {

    private String razorpayOrderId;

    private String email;

    private String sessionID;
}
