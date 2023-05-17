package edu.cuhk.csci3310.cufood.model;


public class User {
    private String userId;
    private String username;
    private String email;
    private String password;

    public User() {}

    public User(String username, String email, String password) {

        this.username = username;
        this.email = email;
        this.password = password;
    }

   // public String getUserId() {
      //  return userId;
    //}

   // public void setUserId(String userId) {
     //   this.userId = userId;
    //}

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

