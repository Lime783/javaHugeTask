package org.example.domain;

import java.util.LinkedHashSet;
import java.util.Objects;

public class User {
    static private final LinkedHashSet<String> emails = new LinkedHashSet<>();
    private String email;
    private String displayName;

    public User(String email, String displayName) {
        Objects.requireNonNull(email, "email cannot be null");
        Objects.requireNonNull(displayName, "displayName cannot be null");
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!(email.matches(emailRegex))){
            throw new IllegalArgumentException("Invalid email address: " + email);
        }
        if (emails.contains(email)) {
            throw new IllegalArgumentException("email already exists: " + email);
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

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", displayName='" + displayName + '\'' +
                '}';
    }
}
