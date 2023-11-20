package com.example.pinventory;

public class User {

    public String uid, userName, email, role;


    public User(){

    }

    public User(String userName, String email, String role){
        this.userName = userName;
        this.email = email;
        this.role = role;
    }
    // Method to change the role
    public void changeRole(String newRole) {
        this.role = newRole;
    }

    // Getter method for UID
    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

}
