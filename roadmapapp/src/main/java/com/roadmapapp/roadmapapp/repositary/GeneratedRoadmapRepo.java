package com.roadmapapp.roadmapapp.repositary;


import com.roadmapapp.roadmapapp.entity.generatedRoadmap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GeneratedRoadmapRepo extends JpaRepository<generatedRoadmap, Long> {



    @Query("""
       SELECT g
       FROM generatedRoadmap g
       WHERE g.email = :email
       """)
    List<generatedRoadmap> findByUserEmail(
            @Param("email") String email
    );

    boolean existsByEmailAndRoadmapNameAndType(
            String email,
            String roadmapName,
            String type
    );

    Optional<generatedRoadmap>
    findByEmailAndRoadmapName(
            String email,
            String roadmapName
    );


}
