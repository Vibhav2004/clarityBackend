package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.currentSessionInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface currentSessionRepo extends JpaRepository<currentSessionInfo, Long> {
    currentSessionInfo getBySessionID(String sessionID);

    currentSessionInfo getByEmail(String email);
}
