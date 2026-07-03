package org.example.repositories;

import org.example.domain.users.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    void addUser(User user);
    Optional<User> findUserByEmail(String email);
    List<User> findAllUsers();
}
