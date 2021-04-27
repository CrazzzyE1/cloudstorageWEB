package com.litvak.cloudstorage.services;

import com.litvak.cloudstorage.entities.User;
import com.litvak.cloudstorage.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AppService {

    private UserRepository userRepository;

    @Autowired
    public void setUserRepository(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User getUserById(Long id) {
        Optional<User> optional = userRepository.findById(id);
        if (optional.isPresent()) {
            return optional.get();
        }
        return null;
    }

    @Transactional
    public void saveOrUpdate(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void removeUserById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional
    public void removeUser(User user) {
        userRepository.delete(user);
    }
}
