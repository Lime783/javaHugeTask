package org.example.repositories;

import org.example.domain.users.User;

import java.util.List;

public class InMemoryUserRepository implements UserRepository {
    private List<User> users;

    public InMemoryUserRepository(List<User> users) {
        this.users = users;
    }

    public void addUser(User user) {
        users.add(user);
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
