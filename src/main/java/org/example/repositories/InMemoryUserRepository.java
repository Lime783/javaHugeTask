package org.example.repositories;

import org.example.domain.users.User;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class InMemoryUserRepository implements UserRepository {
    private final List<User> users;
    private static final HashSet<String> EMAILS_UNIQUE = new HashSet<>();

    public InMemoryUserRepository() {
        users = new ArrayList<>();
    }

    public void addUser(User user) {
        if (EMAILS_UNIQUE.contains(user.getEmail())) {
            throw new IllegalArgumentException("email already exists: " + user.getEmail());
        }
        users.add(user);
        EMAILS_UNIQUE.add(user.getEmail());
    }

    public User findUserByEmail(String email) {
        for (User user : users) {
            if (user.getEmail().equals(email)) {
                return user;
            }
        }
        throw new IllegalArgumentException("User with email " + email + " not found");
    }

    public List<User> getUsers() {
        return users;
    }
}
