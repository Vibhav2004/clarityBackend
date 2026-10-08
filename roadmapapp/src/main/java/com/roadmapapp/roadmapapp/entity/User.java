package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String userName;
    private String email;
    private String password;
    private Long roadmaps;
    private Long trackers=0L;
    private Long totalMileStones;
    private Long completedMileStones;
    private LocalDateTime joinedDate;
    private String plan;

    //new Field
    private LocalDateTime planStartDate;

    private LocalDateTime planExpiryDate;

    private Boolean subscriptionActive = false;

    private Long roadmapLimit = 3L;//Free

    private Long trackerLimit = 1L;//Free

    private String lastTransactionId;

    private Boolean autoRenew = false;
    private Boolean isLoggedIn=false;


}
