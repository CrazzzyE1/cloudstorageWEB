package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
