package udemy.java.uber_clone.model;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;

import java.io.Serializable;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.helpers.Constants;

public class Users implements Serializable {
    private String id;
    private String name;
    private String email;
    private String password;
    private String userType;
    private String latitude;
    private String longitude;

    public Users() {
    }

    public void saveUser(){
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference users = databaseReference
                .child( Constants.USERS )
                .child( getId() );
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

    @Exclude
    public String getPassword() {
        return password;
    }
    @Exclude
    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}
