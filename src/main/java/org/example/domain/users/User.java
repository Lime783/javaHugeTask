package org.example.domain.users;

import java.util.HashSet;
import java.util.Objects;
import java.util.regex.Pattern;

public class User {
    private String email;
    private String displayName;
    private static final HashSet<String> emailsUnique = new HashSet<>();
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");

    public User(String email, String displayName) {
        if (!(EMAIL_PATTERN.matcher(email).matches())){
            throw new IllegalArgumentException("Invalid email: " + email);
        }
        if (emailsUnique.contains(email)) {
            throw new IllegalArgumentException("email already exists: " +  email);
        }

        this.email = email;
        this.displayName = Objects.requireNonNull(displayName, "displayName cannot be null");
        emailsUnique.add(email);
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
