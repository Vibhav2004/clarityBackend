package com.roadmapapp.roadmapapp.service;

import com.roadmapapp.roadmapapp.entity.User;
import com.roadmapapp.roadmapapp.entity.currentSessionInfo;
import com.roadmapapp.roadmapapp.repositary.currentSessionRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class currentSessionService {
   @Autowired
   private   currentSessionRepo currentSessionRepo;

//    public currentSessionService(currentSessionRepo currentSessionRepo) {
//        this.currentSessionRepo = currentSessionRepo;
//    }


    public  boolean verifySession(String email, String sessionId) throws Exception {
        currentSessionInfo session=currentSessionRepo.getByEmail(email  );
        if(session==null){
            return false;
        }
        if(session.getSessionID().equals(sessionId)&& session.getEmail().equals(email)){
            return true;
        }
        return false;

    }

    public void saveSession(currentSessionInfo newSession) {


        currentSessionInfo newSessionCheck=currentSessionRepo.getBySessionID(newSession.getSessionID());
        if(newSessionCheck!=null) {
           throw new RuntimeException("Session already exists");
        }

        currentSessionRepo.save(newSession);
    }

    public void endSession(currentSessionInfo currentSession) {
        currentSessionInfo session=currentSessionRepo.getByEmail(currentSession.getEmail());
        if(session==null) {
            throw new RuntimeException("Session doesnt exist exists");
        }
        if(!currentSession.getSessionID().equals(session.getSessionID() ) && !currentSession.getEmail().equals(session.getEmail()) ) {
            throw new RuntimeException("Current Session ID and Email doesnt match");
        }
        currentSessionRepo.delete(session);
    }
}
