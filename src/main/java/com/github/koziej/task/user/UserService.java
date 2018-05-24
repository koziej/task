package com.github.koziej.task.user;

import com.github.koziej.task.user.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public User addUser(User user) {
        return repository.save(user);
    }
}
