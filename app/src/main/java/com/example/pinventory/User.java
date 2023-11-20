package com.example.pinventory;

public class User {

    public String uid, userName, email, role;
    public boolean status; // true for enabled, false for disabled

    public User() {
        // Default constructor required for Firebase
    }

    public User(String userName, String email, String role, boolean status) {
        this.userName = userName;
        this.email = email;
        this.role = role;
        this.status = status;
    }


    // Method to change the role
    public void changeRole(String newRole) {
        this.role = newRole;
    }

    // Getter and setter methods for UID
    public String getUid() {
        return uid;
    }

    public String getRole() {
        return role;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    // Getter and setter methods for status
    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    // Other getter and setter methods for userName, email, and role
    // ...

}

