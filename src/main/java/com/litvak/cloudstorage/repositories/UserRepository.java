package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByUserName(String login);
}
