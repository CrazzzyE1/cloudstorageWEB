package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.User;

public interface UserService {
    User getUserByUsername(String username);

    User createNewUser(String login, String password);

    void changePassword(String newPass, User user);

    void removeAccount(User user);

    Long getStorage(String username);

}
