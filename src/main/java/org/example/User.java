package org.example;

public class User {
    private String username;
    private String email;
    private boolean authenticated;

    public User() {}

    public User(String username, String email) {
        this.username = username;
        this.email = email;
        this.authenticated = false;
    }

    // Геттеры и сеттеры
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public boolean isAuthenticated() { return authenticated; }
    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    @Override
    public String toString() {
        return "User{username='" + username + "', email='" + email + "'}";
    }
}
