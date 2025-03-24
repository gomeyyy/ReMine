package com.razinrahimi.remine.data;

//Class to store user data for each register user
public class Users {

    private String userId;
    private String email;
    private String username;

    public Users(String userID) {}

    public Users(String userID, String email, String username) {
        this.userId = userID;
        this.email = email;
        this.username = username;
    }

    //Getter and setter required for firestore
    public String getUserID() {return userId;}
    public void setUserID(String userID) {this.userId = userID;}
    public String getEmail() {return email;}
    public void setEmail(String email) {this.email = email;}
    public String getUsername() {return username;}
    public void setUsername(String username) {this.username = username;}

}
