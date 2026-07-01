package org.example.repositories;

import org.example.domain.users.User;

import java.util.List;

public interface UserRepository {
    void addUser(User user);

    User findUserByEmail(String email);

    List<User> getUsers();
}
