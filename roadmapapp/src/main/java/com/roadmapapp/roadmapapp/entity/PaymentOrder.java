package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "payment_orders",
        indexes = {
                @Index(
                        name = "idx_payment_order_razorpay_order_id",
                        columnList = "razorpayOrderId",
                        unique = true
                ),
                @Index(
                        name = "idx_payment_order_email",
                        columnList = "email"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(
            nullable = false,
            unique = true
    )
    private String razorpayOrderId;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String plan;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private String status;
}