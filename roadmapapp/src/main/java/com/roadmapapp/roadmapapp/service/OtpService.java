package com.roadmapapp.roadmapapp.service;

import com.roadmapapp.roadmapapp.entity.OtpVerification;
import com.roadmapapp.roadmapapp.repositary.OtpVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
public class OtpService {
    @Autowired
    private OtpVerificationRepository otpRepo;

    @Autowired
    private JavaMailSender mailSender;

//    public boolean sendOtp(String email) {
//
//        try {
//
//            String otp =
//                    String.valueOf(
//                            100000 +
//                                    new Random().nextInt(900000)
//                    );
//
//            OtpVerification otpEntity =
//                    new OtpVerification();
//
//            otpEntity.setEmail(email);
//            otpEntity.setOtp(otp);
//            otpEntity.setVerified(false);
//
//            otpEntity.setExpiryTime(
//                    LocalDateTime.now()
//                            .plusMinutes(5)
//            );
//
//            otpRepo.save(otpEntity);
//
//            SimpleMailMessage message =
//                    new SimpleMailMessage();
//
//            message.setTo(email);
//            message.setSubject(
//                    "Email Verification OTP"
//            );
//
//            message.setText(
//                    "Your OTP is: " + otp +
//                            "\nValid for 5 minutes."
//            );
//
//            mailSender.send(message);
//              System.out.println("OTP Sent to "+email);
//            return true;
//
//        } catch (Exception e) {
//
//            e.printStackTrace();
//            System.out.println("OTP not Sent to "+email);
//            return false;
//        }
//    }
public boolean sendOtp(String email) {

    try {

        String otp = String.valueOf(
                100000 + new Random().nextInt(900000)
        );

        OtpVerification otpEntity =
                new OtpVerification();

        otpEntity.setEmail(email);
        otpEntity.setOtp(otp);
        otpEntity.setVerified(false);

        otpEntity.setExpiryTime(
                LocalDateTime.now().plusMinutes(5)
        );

        otpRepo.save(otpEntity);

        System.out.println("OTP generated: " + otp);
        System.out.println("OTP saved for: " + email);

        SimpleMailMessage message =
                new SimpleMailMessage();

        message.setFrom("onboarding@resend.dev");

        message.setTo(email);

        message.setSubject(
                "Email Verification OTP"
        );

        message.setText(
                "Hello,\n\n" +
                        "Your OTP is: " + otp + "\n\n" +
                        "This OTP is valid for 5 minutes.\n\n" +
                        "If you did not request this verification, " +
                        "please ignore this email."
        );

        System.out.println("Sending OTP email...");

        mailSender.send(message);

        System.out.println(
                "OTP email sent successfully to: " + email
        );

        return true;

    } catch (Exception e) {

        System.out.println(
                "OTP email failed for: " + email
        );

        e.printStackTrace();

        return false;
    }
}

    public boolean verifyOtp(
            String email,
            String otp
    ) {

        var otpOptional =
                otpRepo.findTopByEmailOrderByIdDesc(
                        email
                );

        if (otpOptional.isEmpty()) {
            return false;
        }

        OtpVerification otpEntity =
                otpOptional.get();

        if (otpEntity.isVerified()) {
            return false;
        }

        if (
                LocalDateTime.now()
                        .isAfter(
                                otpEntity.getExpiryTime()
                        )
        ) {

            otpRepo.delete(otpEntity);

            return false;
        }

        if (
                !otpEntity.getOtp().equals(otp)
        ) {
            return false;
        }

        otpEntity.setVerified(true);

        otpRepo.save(otpEntity);

        return true;
    }
}
