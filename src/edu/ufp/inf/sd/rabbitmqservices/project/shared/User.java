package edu.ufp.inf.sd.rabbitmqservices.project.shared;

public class User {
    private int passwordHash;
    private String token;

    public User(int passwordHash, String token) {
        this.passwordHash = passwordHash;
        this.token = token;
    }

    public int getPasswordHash() {
        return passwordHash;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
