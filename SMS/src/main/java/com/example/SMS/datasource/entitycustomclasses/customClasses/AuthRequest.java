package com.example.SMS.datasource.entitycustomclasses.customClasses;

/**
 * Data Transfer Object (DTO) for unified login request body.
 */
public class AuthRequest {
    private String email;
    private String password;
    private String role; // Expecting "STUDENT", "STAFF", or "STUFF"

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getRoleStu() { return this.role.equals("STUDENT") ? role : null; }
    public String getRoleSta() { return this.role.equals("STAFF") ? role : null; }

    public void setRoleStu(String role) { this.role = role; }
    public void setRoleSta(String role) { this.role = role; }
}
