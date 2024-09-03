package udemy.java.uber_clone.model;

import com.google.firebase.database.DatabaseReference;

import udemy.java.uber_clone.config.FirebaseConfiguration;

public class Users {
    private String id;
    private String name;
    private String email;
    private String password;
    private String userType;

    public Users() {
    }

    public void saveUser(){
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference users = databaseReference
                .child("users")
                .child(getId());
        users.setValue(this);
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}