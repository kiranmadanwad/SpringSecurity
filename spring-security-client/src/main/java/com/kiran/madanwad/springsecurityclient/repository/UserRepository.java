package com.kiran.madanwad.springsecurityclient.repository;

import com.kiran.madanwad.springsecurityclient.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}
