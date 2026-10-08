package com.roadmapapp.roadmapapp.repositary;


import com.roadmapapp.roadmapapp.entity.deletedUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface deletedAccountRepo extends JpaRepository<deletedUser, Long> {
}
