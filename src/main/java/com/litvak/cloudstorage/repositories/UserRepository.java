package com.litvak.cloudstorage.repositories;

import com.litvak.cloudstorage.entities.Role;
import com.litvak.cloudstorage.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
   Optional<User> findUserByUserName(String login);

   // TODO: 10.06.2021 Test it
//   List<User> findAllByRoles(Role role);
   List<User> findAllByEnabledAndRoles(Boolean enabled, Role role);
}
