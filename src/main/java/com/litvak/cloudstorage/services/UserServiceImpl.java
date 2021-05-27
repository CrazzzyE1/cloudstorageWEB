package com.litvak.cloudstorage.services;
import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.Principal;

@Service
public class UserServiceImpl implements UserService{

    private UserRepository userRepository;
    private DirAppRepository dirAppRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Autowired
    public void setDirAppRepository(DirAppRepository dirAppRepository) {
        this.dirAppRepository = dirAppRepository;
    }
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findUserByUserName(username).get();
    }

    @Override
    public User createNewUser(String login, String password) {
        DirApp dir = new DirApp();
        DirApp recycle = new DirApp();
        User user = new User();
        user.setUserName(login);
        user.setEnabled(true);
        user.setPassword(password);
        dir.setName(login);
        dir.setUser(user);
        recycle.setUser(user);
        recycle.setName(login.concat("_recycle"));
        userRepository.save(user);
        dirAppRepository.save(dir);
        dirAppRepository.save(recycle);
        return user;
    }

    // TODO: 27.05.2021 FIX IT
    @Override
    public User changePassword(String oldPass, String newPass, Principal principal) {
        return new User();
    }

    @Override
    public void removeAccount(Principal principal) {
        User user = userRepository.findUserByUserName(principal.getName()).get();
        user.setEnabled(false);
    }
}
