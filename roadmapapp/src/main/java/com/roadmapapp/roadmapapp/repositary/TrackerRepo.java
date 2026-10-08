package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.Tracker;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TrackerRepo extends CrudRepository<Tracker, Long> {
    boolean existsByRoadmapNameAndUserEmail(
            String roadmapName,
            String userEmail
    );

    Optional<Tracker> findByRoadmapNameAndUserEmail(
            String roadmapName,
            String userEmail
    );

    List<Tracker> findByUserEmail(
            String userEmail
    );
}
