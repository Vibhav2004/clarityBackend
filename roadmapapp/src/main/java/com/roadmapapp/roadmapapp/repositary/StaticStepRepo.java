package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.StaticSteps;
import com.roadmapapp.roadmapapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StaticStepRepo extends JpaRepository<StaticSteps, Long> {
    List<StaticSteps> findByCategory(String category);

    Object findByName(String name);

    Optional<StaticSteps> findByCategoryAndName(
            String category,
            String name
    );


    Optional<StaticSteps> findByNameIgnoreCase(String name);
}
