package com.roadmapapp.roadmapapp.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.generatedRoadmap;
import com.roadmapapp.roadmapapp.repositary.GeneratedRoadmapRepo;
import com.roadmapapp.roadmapapp.repositary.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class GeneratedRoadmapService {

    @Autowired
    private  GeneratedRoadmapRepo generatedRoadmapRepo;
   @Autowired
   UserRepo userRepo;

    public List<generatedRoadmap> getAllRoadMaps(String email) {

        return generatedRoadmapRepo
                .findByUserEmail(email);
    }



    public GeneratedRoadmapService(
            GeneratedRoadmapRepo generatedRoadmapRepo
    ) {
        this.generatedRoadmapRepo = generatedRoadmapRepo;
    }

    public String saveCustomRoadmap(
            Map<String, Object> roadmapData
    ) {

        String roadmapName =
                (String) roadmapData.get("name");

        String email =
                (String) roadmapData.get("userEmail");

        String username =
                (String) roadmapData.get("username");

        String difficulty =
                (String) roadmapData.get("difficulty");


        User user =
                (User) userRepo.findByEmail(email)
                        .orElseThrow(() ->
                                new RuntimeException("User not found"));

        String jsonData;

        try {

            ObjectMapper objectMapper =
                    new ObjectMapper();

            jsonData =
                    objectMapper.writeValueAsString(
                            roadmapData.get("jsonData")
                    );

        } catch (Exception e) {

            throw new RuntimeException(
                    "Failed to serialize roadmap",
                    e
            );
        }

        boolean exists =
                generatedRoadmapRepo
                        .existsByEmailAndRoadmapNameAndType(
                                email,
                                roadmapName,
                                "CustomRoadmap"
                        );

        // If roadmap already exists, don't count it again
        if (exists) {
            return "ROADMAP_ALREADY_EXISTS";
        }

        // Check roadmap limit
        if (user.getRoadmaps() >= user.getRoadmapLimit()) {
            return "ROADMAP_LIMIT_EXCEEDED";
        }

        // Increment only when saving a new roadmap
        user.setRoadmaps(
                user.getRoadmaps() + 1
        );

        userRepo.save(user);

        generatedRoadmap roadmap =
                generatedRoadmap.builder()
                        .roadmapId(
                                UUID.randomUUID().toString()
                        )
                        .username(username)
                        .roadmapName(roadmapName)
                        .email(email)
                        .type("CustomRoadmap")
                        .difficulty(difficulty)
                        .jsonData(jsonData)
                        .createdAt(LocalDateTime.now())
                        .build();

        generatedRoadmapRepo.save(roadmap);

        return "ROADMAP_SAVED";
    }
    }

