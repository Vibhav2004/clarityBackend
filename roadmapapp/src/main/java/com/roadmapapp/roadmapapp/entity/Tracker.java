package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Tracker")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tracker {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String Username;
//    private String UserEmail;
//
//    private String RoadmapName;
//    private String type;
//
//    @Lob
//    @Column(columnDefinition = "TEXT")
//    private String jsonData;
//    private LocalDateTime createdAt;
//    private LocalDateTime updatedAt;
//    private String status;
//    private Long completedSteps;
//    private Long totalSteps;
//    private Long completedpercentage;
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;

    private String username;
    private String userEmail;
    private String roadmapName;

    @Column(name="json_data")
    private String jsonData;

    private String type;
    private String status;

    private Long completedSteps;
    private Long totalSteps;
    private Long completedpercentage;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
