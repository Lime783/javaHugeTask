package org.example.domain.users;

import java.util.Objects;
import java.util.regex.Pattern;

public abstract class User {
    private String email;
    private String displayName;
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public User(String email, String displayName) {
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(displayName, "displayName cannot be null");

        if (!(EMAIL_PATTERN.matcher(email).matches())) {
            throw new IllegalArgumentException("Invalid email: " + email);
        }

        this.email = email;
        this.displayName = displayName;
    }

    public User(String email) {
        this(email, "Anon");
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
