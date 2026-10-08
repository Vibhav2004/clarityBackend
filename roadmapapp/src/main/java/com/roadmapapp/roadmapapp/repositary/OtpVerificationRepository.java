package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.OtpVerification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {
    Optional<OtpVerification> findTopByEmailOrderByIdDesc(
            String email
    );
}
