package com.roadmapapp.roadmapapp.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EditPasswordOBJ {
    private String password;
    private String email;
    private String sessionID;
}
