package com.roadmapapp.roadmapapp.repositary;



import com.roadmapapp.roadmapapp.entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentOrderRepo
        extends JpaRepository<PaymentOrder, Long> {

    Optional<PaymentOrder> findByRazorpayOrderId(
            String razorpayOrderId
    );

    Optional<PaymentOrder> findByRazorpayOrderIdAndEmail(
            String razorpayOrderId,
            String email
    );
}
