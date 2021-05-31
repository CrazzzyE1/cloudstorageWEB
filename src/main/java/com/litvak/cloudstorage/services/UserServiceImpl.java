package com.litvak.cloudstorage.services;
import com.litvak.cloudstorage.entities.DirApp;
import com.litvak.cloudstorage.entities.Role;
import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.repositories.DirAppRepository;
import com.litvak.cloudstorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
        user.setSpace(209715200L);
        user.setRoles(Collections.singleton(new Role(1L, "ROLE_USER")));
        dir.setName(login);
        dir.setUser(user);
        recycle.setUser(user);
        recycle.setName(login.concat("_recycle"));
        userRepository.save(user);
        dirAppRepository.save(dir);
        dirAppRepository.save(recycle);
        return user;
    }

    @Override
    @Transactional
    public void changePassword(String newPass, User user) {
        user.setPassword(newPass);
    }

    @Override
    @Transactional
    public void removeAccount(User user) {
        user.setEnabled(false);
    }

    @Override
    public Long getStorage(String username) {
       return userRepository.findUserByUserName(username).get().getSpace();
    }
}
