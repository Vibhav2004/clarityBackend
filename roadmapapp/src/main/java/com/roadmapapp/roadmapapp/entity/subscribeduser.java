package com.roadmapapp.roadmapapp.entity;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "subscribedUser")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class subscribeduser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String planType;
    private Long amountPaid;
    private LocalDateTime subscriptionDateStart;
    private LocalDateTime subscriptionDateEnd;
    private Boolean isActive;
}
