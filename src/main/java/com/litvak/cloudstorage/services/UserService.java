package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.User;

import java.util.List;

public interface UserService {
    User getUserByUsername(String username);

    User createNewUser(String login, String password);

    void changePassword(String newPass, User user);

    void removeAccount(User user);

    Long getStorage(String username);

    List<User> getAllUsers();

    User getUserById(Long id);

    void activateAccount(User user);

    void changeSpace(Long space, User user);

    List<User> getAllRemovedUsers();

    List<User> getAllActiveUsers();

    List<User> getAllActiveAdmins();

    List<User> getAllRemovedAdmins();

    void upUser(Long id);

    void downAdmin(Long id);
}
