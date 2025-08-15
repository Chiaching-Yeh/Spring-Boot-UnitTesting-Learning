package com.self.demo.service;

import com.self.demo.dao.UserInterface;
import com.self.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    public  UserInterface userDao;

    public User findById(String id) {
        Optional<User> userOpt = userDao.findById(id);
        if (userOpt.isEmpty()) {
            throw new NoSuchElementException("User not found: " + id);
        }
        return userOpt.get();
    }


}
