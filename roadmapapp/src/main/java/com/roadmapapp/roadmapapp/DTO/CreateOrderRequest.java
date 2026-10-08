package com.roadmapapp.roadmapapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderRequest {
    private String email;

    private String plan;

    private String sessionID;
}
