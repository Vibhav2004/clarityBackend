package com.roadmapapp.roadmapapp.service;

import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.deletedUser;
import com.roadmapapp.roadmapapp.repositary.UserRepo;
import com.roadmapapp.roadmapapp.repositary.deletedAccountRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class deletedAccountService {
    @Autowired
    private deletedAccountRepo deletedAccountRepo;
    @Autowired
    private UserRepo userRepo;

    public void logDeletedUser(String email) {
        deletedUser du = new deletedUser();
        User user = userRepo.getByEmail(email);
        System.out.println(user);
//        du.setId(user.getId());
        du.setAccountDeleted(true);
        du.setEmail(user.getEmail());
        du.setUserName(user.getUserName());


         deletedAccountRepo.save(du);

    }
}
