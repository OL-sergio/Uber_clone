package udemy.java.uber_clone.model;

import com.google.firebase.database.DatabaseReference;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.config.UserFirebase;

public class RequestActive {

    private String idUser;
    private String latitude;
    private String longitude;

    public RequestActive() {
    }

    public void saveActiveRequest() {

        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child("requests_active");

        String userId = UserFirebase.getUserId();
        requests.child(userId)
                .child("destination")
                .setValue(this);
    }

    public void deleteActiveRequest() {

        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child("requests_active");
        requests.removeValue();
    }

    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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
