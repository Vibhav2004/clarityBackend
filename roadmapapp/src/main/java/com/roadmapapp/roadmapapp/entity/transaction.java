package com.roadmapapp.roadmapapp.entity;

import jakarta.persistence.*;
import lombok.*;


import java.time.LocalDateTime;

@Entity
@Table(name = "Transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;
    private String userName;
    private String email;
    private String TransactionId;
    private LocalDateTime transactionDate;
    private String transactionType;
    private String transactionStatus;
    private Long amount;
    private String currency;
    private String planPurchased;
    //new Field
    private String razorpayOrderId;
    private String razorpayPaymentId;
    private String razorpaySignature;

    private String planName;

    private LocalDateTime subscriptionStartDate;
    private LocalDateTime subscriptionEndDate;

    private Integer durationMonths;

    private Boolean activeSubscription;

    private String paymentMethod;


}
