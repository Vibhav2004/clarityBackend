package com.roadmapapp.roadmapapp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadmapapp.roadmapapp.entity.Tracker;

import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.repositary.GeneratedRoadmapRepo;
import com.roadmapapp.roadmapapp.repositary.TrackerRepo;

import com.roadmapapp.roadmapapp.repositary.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class TrackerService {

    @Autowired
    private TrackerRepo trackerRepository;

    @Autowired
    private GeneratedRoadmapRepo generatedRoadmapRepo;
    @Autowired
    private UserRepo userRepo;


//    public String saveTracker(
//            Map<String, Object> trackerData
//    ) throws JsonProcessingException {
//
//        String userEmail =
//                trackerData.get("UserEmail")
//                        .toString();
//
//        String roadmapName =
//                trackerData.get("RoadmapName")
//                        .toString();
//
//        ObjectMapper objectMapper = new ObjectMapper();
//
//        String jsonData =
//                objectMapper.writeValueAsString(
//                        trackerData.get("jsonData")
//                );
//        System.out.println("jsonData Vibhav: " + jsonData);
//
//        boolean exists =
//                trackerRepository
//                        .existsByRoadmapNameAndUserEmail(
//                                roadmapName,
//                                userEmail
//                        );
//
//        if (exists) {
//            return "TRACKER_ALREADY_EXISTS";
//        }
//        System.out.println("JSON DATA:");
//        System.out.println(trackerData.get("jsonData"));
//        System.out.println("TYPE: " + trackerData.get("jsonData").getClass());
//        Long completedSteps =
//                trackerData.get("completedSteps") == null
//                        ? 0L
//                        : Long.parseLong(
//                        trackerData.get("completedSteps")
//                                .toString()
//                );
//
//        Long totalSteps =
//                trackerData.get("totalSteps") == null
//                        ? 0L
//                        : Long.parseLong(
//                        trackerData.get("totalSteps")
//                                .toString()
//                );
//
//        Long completedPercentage =
//                trackerData.get("completedpercentage") == null
//                        ? 0L
//                        : Long.parseLong(
//                        trackerData.get("completedpercentage")
//                                .toString()
//                );
//
//        Tracker tracker =
//                Tracker.builder()
//                        .username(
//                                trackerData.get("Username")
//                                        .toString()
//                        )
//                        .userEmail(userEmail)
//                        .roadmapName(roadmapName)
//                        .type(
//                                trackerData.get("type")
//                                        .toString()
//                        )
//                        .jsonData(jsonData)
//                        .status(
//                                trackerData.get("status")
//                                        .toString()
//                        )
//                        .completedSteps(
//                                completedSteps
//                        )
//                        .totalSteps(
//                                totalSteps
//                        )
//                        .completedpercentage(
//                                completedPercentage
//                        )
//                        .createdAt(
//                                LocalDateTime.now()
//                        )
//                        .updatedAt(
//                                LocalDateTime.now()
//                        )
//                        .build();
//
//        trackerRepository.save(tracker);
//
//        return "TRACKER_SAVED";
//    }
//public String saveTracker(
//        Map<String, Object> trackerData
//) throws JsonProcessingException {
//
//    String userEmail =
//            trackerData.get("UserEmail")
//                    .toString();
//
//    String roadmapName =
//            trackerData.get("RoadmapName")
//                    .toString();
//
//    User user =
//            (User) userRepo.findByEmail(userEmail)
//                    .orElseThrow(() ->
//                            new RuntimeException(
//                                    "User not found"
//                            ));
//
//    ObjectMapper objectMapper =
//            new ObjectMapper();
//
//    String jsonData =
//            objectMapper.writeValueAsString(
//                    trackerData.get("jsonData")
//            );
//
//    boolean exists =
//            trackerRepository
//                    .existsByRoadmapNameAndUserEmail(
//                            roadmapName,
//                            userEmail
//                    );
//
//    if (exists) {
//        return "TRACKER_ALREADY_EXISTS";
//    }
//
//    // =========================
//    // TRACKER LIMIT = 1
//    // =========================
//    if (user.getTrackers() >= user.getTrackerLimit()) {
//        return "TRACKER_LIMIT_EXCEEDED";
//    }
//
//    Long completedSteps =
//            trackerData.get("completedSteps") == null
//                    ? 0L
//                    : Long.parseLong(
//                    trackerData.get("completedSteps")
//                            .toString()
//            );
//
//    Long totalSteps =
//            trackerData.get("totalSteps") == null
//                    ? 0L
//                    : Long.parseLong(
//                    trackerData.get("totalSteps")
//                            .toString()
//            );
//
//    Long completedPercentage =
//            trackerData.get("completedpercentage") == null
//                    ? 0L
//                    : Long.parseLong(
//                    trackerData.get("completedpercentage")
//                            .toString()
//            );
//
//    Tracker tracker =
//            Tracker.builder()
//                    .username(
//                            trackerData.get("Username")
//                                    .toString()
//                    )
//                    .userEmail(userEmail)
//                    .roadmapName(roadmapName)
//                    .type(
//                            trackerData.get("type")
//                                    .toString()
//                    )
//                    .jsonData(jsonData)
//                    .status(
//                            trackerData.get("status")
//                                    .toString()
//                    )
//                    .completedSteps(completedSteps)
//                    .totalSteps(totalSteps)
//                    .completedpercentage(
//                            completedPercentage
//                    )
//                    .createdAt(
//                            LocalDateTime.now()
//                    )
//                    .updatedAt(
//                            LocalDateTime.now()
//                    )
//                    .build();
//
//    trackerRepository.save(tracker);
//
//    // Increment after successful save
//    user.setTrackers(
//            user.getTrackers() + 1
//    );
//
//    userRepo.save(user);
//
//    return "TRACKER_SAVED";
//}

    public String saveTracker(
            Map<String, Object> trackerData
    ) throws JsonProcessingException {
        System.out.println("========== TRACKER DATA ==========");
        trackerData.forEach((key, value) ->
                System.out.println(key + " = " + value)
        );
        System.out.println("==================================");
        // =========================================================
        // 1. Validate required tracker fields
        // =========================================================

        Object userEmailValue = trackerData.get("userEmail");
        Object roadmapNameValue = trackerData.get("roadmapName");
        Object usernameValue = trackerData.get("username");
        Object typeValue = trackerData.get("type");
        Object statusValue = trackerData.get("status");

        if (userEmailValue == null ||
                roadmapNameValue == null ||
                usernameValue == null ||
                typeValue == null ||
                statusValue == null) {

            throw new IllegalArgumentException(
                    "Required tracker fields are missing."
            );
        }

        // =========================================================
        // 2. Convert required values to String
        // =========================================================

        String userEmail = userEmailValue.toString().trim();
        String roadmapName = roadmapNameValue.toString().trim();
        String username = usernameValue.toString().trim();
        String type = typeValue.toString().trim();
        String status = statusValue.toString().trim();

        if (userEmail.isEmpty() ||
                roadmapName.isEmpty() ||
                username.isEmpty() ||
                type.isEmpty() ||
                status.isEmpty()) {

            throw new IllegalArgumentException(
                    "Required tracker fields cannot be empty."
            );
        }

        // =========================================================
        // 3. Find user
        // =========================================================

        User user =
                (User) userRepo.findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                ));

        // =========================================================
        // 4. Convert jsonData
        // =========================================================

        Object jsonDataValue = trackerData.get("jsonData");

        if (jsonDataValue == null) {
            throw new IllegalArgumentException(
                    "jsonData is required."
            );
        }

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonData =
                objectMapper.writeValueAsString(
                        jsonDataValue
                );

        // =========================================================
        // 5. Check if roadmap is already being tracked
        // =========================================================

        boolean exists =
                trackerRepository
                        .existsByRoadmapNameAndUserEmail(
                                roadmapName,
                                userEmail
                        );

        if (exists) {
            return "TRACKER_ALREADY_EXISTS";
        }

        // =========================================================
        // 6. Check tracker limit
        // =========================================================

        if (user.getTrackers() >= user.getTrackerLimit()) {
            return "TRACKER_LIMIT_EXCEEDED";
        }

        // =========================================================
        // 7. Optional numeric fields
        // =========================================================

        Long completedSteps =
                trackerData.get("completedSteps") == null
                        ? 0L
                        : Long.parseLong(
                        trackerData.get("completedSteps")
                                .toString()
                );

        Long totalSteps =
                trackerData.get("totalSteps") == null
                        ? 0L
                        : Long.parseLong(
                        trackerData.get("totalSteps")
                                .toString()
                );

        Long completedPercentage =
                trackerData.get("completedpercentage") == null
                        ? 0L
                        : Long.parseLong(
                        trackerData.get("completedpercentage")
                                .toString()
                );

        // =========================================================
        // 8. Validate numeric ranges
        // =========================================================

        if (completedSteps < 0) {
            throw new IllegalArgumentException(
                    "completedSteps cannot be negative."
            );
        }

        if (totalSteps < 0) {
            throw new IllegalArgumentException(
                    "totalSteps cannot be negative."
            );
        }

        if (completedSteps > totalSteps) {
            throw new IllegalArgumentException(
                    "completedSteps cannot be greater than totalSteps."
            );
        }

        if (completedPercentage < 0 ||
                completedPercentage > 100) {

            throw new IllegalArgumentException(
                    "completedpercentage must be between 0 and 100."
            );
        }

        // =========================================================
        // 9. Create Tracker
        // =========================================================

        LocalDateTime now = LocalDateTime.now();

        Tracker tracker =
                Tracker.builder()
                        .username(username)
                        .userEmail(userEmail)
                        .roadmapName(roadmapName)
                        .type(type)
                        .jsonData(jsonData)
                        .status(status)
                        .completedSteps(completedSteps)
                        .totalSteps(totalSteps)
                        .completedpercentage(
                                completedPercentage
                        )
                        .createdAt(now)
                        .updatedAt(now)
                        .build();

        // =========================================================
        // 10. Save tracker
        // =========================================================

        trackerRepository.save(tracker);

        // =========================================================
        // 11. Increment user's tracker count
        // =========================================================

        user.setTrackers(
                user.getTrackers() + 1
        );

        userRepo.save(user);

        // =========================================================
        // 12. Success
        // =========================================================

        return "TRACKER_SAVED";
    }


    public List<Tracker> getTrackersByEmail(String email) {
        return trackerRepository.findByUserEmail(email);
    }

    public String saveCustomTracker(
            Map<String, Object> trackerData
    ) throws JsonProcessingException {

        String userEmail =
                trackerData.get("userEmail")
                        .toString();

        String roadmapName =
                trackerData.get("roadmapName")
                        .toString();

        User user =
                (User) userRepo.findByEmail(userEmail)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "User not found"
                                )
                        );

        boolean exists =
                trackerRepository
                        .existsByRoadmapNameAndUserEmail(
                                roadmapName,
                                userEmail
                        );

        if (exists) {
            return "TRACKER_ALREADY_EXISTS";
        }

        // LIMIT CHECK
        if (user.getTrackers() >= user.getTrackerLimit()) {
            return "TRACKER_LIMIT_EXCEEDED";
        }

        ObjectMapper objectMapper =
                new ObjectMapper();

        String jsonData =
                objectMapper.writeValueAsString(
                        trackerData.get("jsonData")
                );

        Long completedSteps =
                Long.parseLong(
                        trackerData.get(
                                "completedSteps"
                        ).toString()
                );

        Long totalSteps =
                Long.parseLong(
                        trackerData.get(
                                "totalSteps"
                        ).toString()
                );

        Long completedPercentage =
                Long.parseLong(
                        trackerData.get(
                                "completedpercentage"
                        ).toString()
                );

        Tracker tracker =
                Tracker.builder()
                        .username(
                                trackerData.get(
                                        "username"
                                ).toString()
                        )
                        .userEmail(
                                userEmail
                        )
                        .roadmapName(
                                roadmapName
                        )
                        .type(
                                "CustomTracker"
                        )
                        .jsonData(
                                jsonData
                        )
                        .status(
                                trackerData.get(
                                        "status"
                                ).toString()
                        )
                        .completedSteps(
                                completedSteps
                        )
                        .totalSteps(
                                totalSteps
                        )
                        .completedpercentage(
                                completedPercentage
                        )
                        .createdAt(
                                LocalDateTime.now()
                        )
                        .updatedAt(
                                LocalDateTime.now()
                        )
                        .build();

        trackerRepository.save(
                tracker
        );

        // ==========================
        // INCREMENT USER TRACKERS
        // ==========================

        user.setTrackers(
                user.getTrackers() + 1
        );

        userRepo.save(
                user
        );

        return "TRACKER_SAVED";
    }



    public void deleteTrackers(List<Long> ids, String email) {
        User users = userRepo.findByEmails(email);
        if (users == null) {
            throw new RuntimeException(
                    "User not found"
            );
        }
        Long currentTrackers =
                users.getTrackers();

        if (currentTrackers == null) {
            currentTrackers = 0L;
        }

        long updatedTrackers =
                Math.max(
                        0,
                        currentTrackers - ids.size()
                );

        users.setTrackers(updatedTrackers);


        userRepo.save(users);
        trackerRepository.deleteAllById(ids);
    }

        public void updateTracker (Tracker tracker, String authenticatedEmail){

            if (tracker == null) {
                throw new IllegalArgumentException("Tracker data is required.");
            }

            if (tracker.getId() == null) {
                throw new IllegalArgumentException("Tracker ID is required.");
            }

            if (authenticatedEmail == null || authenticatedEmail.isBlank()) {
                throw new SecurityException("Authenticated email is required.");
            }

            System.out.println("========== UPDATE TRACKER ==========");
            System.out.println("Tracker ID = " + tracker.getId());
            System.out.println("Authenticated Email = " + authenticatedEmail);
            System.out.println("Completed Steps = " + tracker.getCompletedSteps());
            System.out.println("Total Steps = " + tracker.getTotalSteps());
            System.out.println("Status = " + tracker.getStatus());
            System.out.println("====================================");

            // 1. Find tracker
            Tracker existingTracker =
                    trackerRepository
                            .findById(tracker.getId())
                            .orElseThrow(() ->
                                    new RuntimeException("Tracker not found")
                            );

            // 2. Authorization check
            if (existingTracker.getUserEmail() == null ||
                    !existingTracker.getUserEmail()
                            .equalsIgnoreCase(authenticatedEmail)) {

                throw new SecurityException(
                        "You are not authorized to modify this tracker."
                );
            }

            // 3. Find authenticated user
            User user = userRepo.findByEmails(authenticatedEmail);

            if (user == null) {
                throw new RuntimeException("User not found.");
            }

            // 4. Update only allowed fields
            existingTracker.setCompletedSteps(
                    tracker.getCompletedSteps()
            );

            existingTracker.setTotalSteps(
                    tracker.getTotalSteps()
            );

            existingTracker.setStatus(
                    tracker.getStatus()
            );

            existingTracker.setUpdatedAt(
                    LocalDateTime.now()
            );

            // 5. Save tracker
            trackerRepository.save(existingTracker);

            // 6. Update user's milestone totals
            user.setCompletedMileStones(
                    tracker.getCompletedSteps()
            );

            user.setTotalMileStones(
                    tracker.getTotalSteps()
            );

            userRepo.save(user);
        }
    }