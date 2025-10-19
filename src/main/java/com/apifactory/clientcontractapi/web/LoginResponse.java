package com.apifactory.clientcontractapi.web;

/**
 * Represents a login response containing the JWT token.
 */
public class LoginResponse {
    private String token;
    private String type;

    public LoginResponse(String token, String type) {
        this.token = token;
        this.type = type;
    }
    // getters
    public String getToken() { return token; }
    public String getType() { return type; }
}