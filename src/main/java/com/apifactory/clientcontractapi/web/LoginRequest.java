package com.apifactory.clientcontractapi.web;

/**
 * Represents a login request payload.
 */
public class LoginRequest {
    private String username;
    private String password;
    // getters/setters
    public String getUsername() { return username; }
    public void setUsername(String u) { this.username = u; }
    public String getPassword() { return password; }
    public void setPassword(String p) { this.password = p; }
}