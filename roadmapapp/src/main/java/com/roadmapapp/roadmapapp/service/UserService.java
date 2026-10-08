package com.roadmapapp.roadmapapp.service;

import com.roadmapapp.roadmapapp.DTO.EditPasswordOBJ;
import com.roadmapapp.roadmapapp.DTO.SessionIDUser;
import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.currentSessionInfo;
import com.roadmapapp.roadmapapp.repositary.UserRepo;
import com.roadmapapp.roadmapapp.repositary.deletedAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService {
    @Autowired
    private UserRepo userRepo;
@Autowired
private currentSessionService currentSessionService;
    @Autowired
    private deletedAccountRepo deletedAccountRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    public User registerUser(User user) {
        User existingEmail =
                userRepo.findByEmails(user.getEmail());

        if (existingEmail != null) {
            throw new RuntimeException(
                    "Email already registered"
            );
        }

        User existingUsername =
                userRepo.findByUserName(
                        user.getUserName()
                );

        if (existingUsername != null) {
            throw new RuntimeException(
                    "Username already taken"
            );
        }

        user.setJoinedDate(LocalDateTime.now());
        user.setRoadmaps(0L);
        user.setTrackers(0L);
        user.setCompletedMileStones(0L);
        user.setTotalMileStones(0L);
        user.setTrackerLimit(1L);
        user.setRoadmapLimit(3L);
        user.setPlan("FREE");
        user.setAutoRenew(false);
        user.setSubscriptionActive(false);
        user.setIsLoggedIn(false);
        user.setPassword(
                passwordEncoder.encode(
                        user.getPassword()
                )
        );
        return userRepo.save(user);
    }

    public SessionIDUser loginUser(User user) {
        User existingUser = userRepo.getByEmail(user.getEmail());

        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found");
        }
        boolean valid =
                passwordEncoder.matches(
                        user.getPassword(),
                        existingUser.getPassword()
                );
        if (!valid) {
            throw new RuntimeException("Invalid password"); // you can make a custom exception
        }
        if(existingUser.getIsLoggedIn()==true){
            throw new RuntimeException("User is already logged in");
        }
            setLoggedInfo(existingUser);
        currentSessionInfo newSession=new currentSessionInfo();
        newSession.setUserName(existingUser.getUserName());
        newSession.setEmail(existingUser.getEmail());
        newSession.setJoinedDate(LocalDateTime.now());
        String sessionId=generateSessionId();
        newSession.setSessionID(sessionId);
        newSession.setPlan(existingUser.getPlan());
        SessionIDUser session=new SessionIDUser();
        session.setSessionID(newSession.getSessionID());
        session.setEmail(newSession.getEmail());
        session.setUserName(existingUser.getUserName());
        session.setPlan(existingUser.getPlan());

        currentSessionService.saveSession(newSession);
        return session;
    }

    public static String generateSessionId() {

        final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final String LOWER = "abcdefghijklmnopqrstuvwxyz";
        final String NUMBERS = "0123456789";
        final String SYMBOLS = "!@#$%^&*()-_=+";

        SecureRandom random = new SecureRandom();

        List<Character> characters = new ArrayList<>();

        // Guarantee each type
        characters.add(UPPER.charAt(random.nextInt(UPPER.length())));
        characters.add(LOWER.charAt(random.nextInt(LOWER.length())));
        characters.add(NUMBERS.charAt(random.nextInt(NUMBERS.length())));
        characters.add(SYMBOLS.charAt(random.nextInt(SYMBOLS.length())));

        // Fill remaining 16 characters
        String ALL = UPPER + LOWER + NUMBERS + SYMBOLS;

        for (int i = 4; i < 20; i++) {
            characters.add(
                    ALL.charAt(random.nextInt(ALL.length()))
            );
        }

        // Shuffle so the guaranteed characters aren't predictable in position
        Collections.shuffle(characters, random);

        StringBuilder sessionId = new StringBuilder(20);

        for (char c : characters) {
            sessionId.append(c);
        }

        return sessionId.toString();
    }
  public void setLoggedInfo(User existingUser) {
        User user=userRepo.getByEmail(existingUser.getEmail());
        System.out.println(user);
        if(user==null){
            throw new RuntimeException("User Not Found");
        }
        if(user.getIsLoggedIn()==false){
            user.setIsLoggedIn(true);
            userRepo.save(user);
        }

  }
    public User getProfile(String email) {
        User user=userRepo.getByEmail(email);
        user.setPassword(null);
        return user;
    }

    public User findUser(String email) {
        return userRepo.getByEmail(email);
    }



    public boolean deleteAccount(String email) {
        User user=userRepo.getByEmail(email);
        if(user==null) {
            return false;
        }

        userRepo.delete(user);

        return true;
    }

    public void logOutUser(currentSessionInfo currentSession) {

        User existingUser = userRepo.getByEmail(currentSession.getEmail());

        if (existingUser == null) {
            throw new UsernameNotFoundException("User not found");
        }

        if (existingUser.getIsLoggedIn() == false) {
            throw new RuntimeException("User is already logged out");
        }



        currentSessionService.endSession(currentSession);
        existingUser.setIsLoggedIn(false);

        userRepo.save(existingUser);
    }

    public boolean editPassword(String email, String password) {
        User editUser=userRepo.getByEmail(email);
        if (editUser == null) {
            return false;
        }
        editUser.setPassword(password);
        userRepo.save(editUser);
        return true;
    }
}
