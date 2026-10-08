package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface transactionRepo
        extends JpaRepository<transaction, Long> {

    Optional<transaction> findByRazorpayOrderId(
            String razorpayOrderId
    );

    Optional<transaction> findByRazorpayPaymentId(
            String razorpayPaymentId
    );

    Optional<transaction> findByRazorpayOrderIdAndEmail(
            String razorpayOrderId,
            String email
    );
}