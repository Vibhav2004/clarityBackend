package com.roadmapapp.roadmapapp.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmapapp.roadmapapp.entity.StaticSteps;
import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.generatedRoadmap;
import com.roadmapapp.roadmapapp.repositary.GeneratedRoadmapRepo;
import com.roadmapapp.roadmapapp.repositary.StaticStepRepo;
import com.roadmapapp.roadmapapp.repositary.UserRepo;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class StaticStepService {
    @Autowired
    private StaticStepRepo staticStepRepo;
    @Autowired
    private GeneratedRoadmapRepo  generatedRoadmapRepo;
    @Autowired
    private UserRepo userRepo;
    public List<String> getCategoryNames() {
        return staticStepRepo.findAll()
                .stream()
                .map(StaticSteps::getSubcategory)
                .distinct()
                .toList();
    }

    public List<String> getNames(String category) {

        return staticStepRepo.findAll()
                .stream()
                .peek(step ->
                        System.out.println(
                                "Comparing [" + step.getSubcategory() + "] with [" + category + "]"
                        )
                )
                .filter(step ->
                        step.getSubcategory() != null &&
                                step.getSubcategory().equalsIgnoreCase(category)
                )
                .map(StaticSteps::getName)
                .distinct()
                .toList();
    }

//    public String getRoadMap(
//            String category,
//            String name,
//            String difficulty) {
//
//        StaticSteps step = staticStepRepo
//                .findByNameIgnoreCase(name)
//                .orElseThrow(() ->
//                        new RuntimeException("Roadmap not found: " + name));
//
//        return step.getStepJSONData();
//    }
//public String getRoadMap(
//        String email,
//        String category,
//        String name,
//        String difficulty) {
//
//    User user = (User) userRepo.findByEmail(email)
//            .orElseThrow(() ->
//                    new RuntimeException("User not found"));
//
//    // Check roadmap limit
//    if (user.getRoadmaps() >= 3) {
//        return "ROADMAP_LIMIT_EXCEEDED";
//    }
//
//    // Increment count
//    user.setRoadmaps(
//            user.getRoadmaps() + 1
//    );
//
//    userRepo.save(user);
//
//    StaticSteps step = staticStepRepo
//            .findByNameIgnoreCase(name)
//            .orElseThrow(() ->
//                    new RuntimeException(
//                            "Roadmap not found: " + name
//                    ));
//
//    return step.getStepJSONData();
//}



    public String getRoadMap(
            String email,
            String category,
            String name,
            String difficulty) {

        User user = (User) userRepo.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        StaticSteps step = staticStepRepo
                .findByNameIgnoreCase(name)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Roadmap not found: " + name
                        ));

        boolean alreadyExists =
                generatedRoadmapRepo
                        .existsByEmailAndRoadmapNameAndType(
                                email,
                                name,
                                difficulty
                        );

        if (!alreadyExists) {

            if (user.getRoadmaps() >= user.getRoadmapLimit()) {
                return "ROADMAP_LIMIT_EXCEEDED";
            }

            user.setRoadmaps(
                    user.getRoadmaps() + 1
            );

            userRepo.save(user);

            generatedRoadmap roadmap =
                    generatedRoadmap.builder()
                            .username(user.getUserName())
                            .email(user.getEmail())
                            .roadmapId(UUID.randomUUID().toString())
                            .roadmapName(name)
                            .type("Generated")
                            .jsonData(step.getStepJSONData())
                            .createdAt(LocalDateTime.now())
                            .difficulty(difficulty)
                            .build();

            generatedRoadmapRepo.save(roadmap);
        }

        return step.getStepJSONData();
    }
}
