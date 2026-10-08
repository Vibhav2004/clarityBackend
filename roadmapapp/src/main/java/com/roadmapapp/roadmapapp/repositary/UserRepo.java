package com.roadmapapp.roadmapapp.repositary;

import com.roadmapapp.roadmapapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {
    User save(User user);

    User getByEmail(String email);

    Optional<Object> findByEmail(String email);

    void save(Optional<User> users);

    @Query(
            value = """
        SELECT *
        FROM users
        WHERE email = :email
        """,
            nativeQuery = true
    )
    User findByEmails(
            @Param("email") String email
    );

    User findByUserName(String userName);
}
