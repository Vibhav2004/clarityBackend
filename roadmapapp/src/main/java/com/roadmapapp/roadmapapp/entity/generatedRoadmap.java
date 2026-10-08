package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "generated Roadmap")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class generatedRoadmap {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String username;

    private String email;

    private String roadmapId;

    private String roadmapName;
    private String difficulty;
    private String type;
//    @Column(columnDefinition = "LONGTEXT")
    private String jsonData;
    private LocalDateTime createdAt;
}
