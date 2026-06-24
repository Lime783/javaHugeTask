package org.example.domain.users;

public class IndividualUser extends User {

    public IndividualUser(String email, String displayName) {
        super(email, displayName);
    }

    public IndividualUser(String email) {
        super(email, "Anon");
    }
}
