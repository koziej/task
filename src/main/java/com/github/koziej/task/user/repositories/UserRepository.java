package com.github.koziej.task.user.repositories;

import com.github.koziej.task.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

    User findOneById(String id);
}
