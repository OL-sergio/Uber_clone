package udemy.java.uber_clone.model;

import android.util.Log;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.Constants;

public class RequestActive {

    private String id;
    private String latitude;
    private String longitude;

    public RequestActive() {
    }

    public void saveActiveRequest(  ) {

        String user = UserFirebase.getUserId();
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requestActive = databaseReference
                .child( Constants.REQUESTS_ACTIVE )
                .child( user );
                requestActive.setValue(this);

    }

    public void deleteActiveRequest(  ) {

        String user = UserFirebase.getUserId();
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference
                .child( Constants.REQUESTS_ACTIVE )
                .child( user );

        requests.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // Successfully removed the request
                Log.d("Firebase", "Request removed successfully.");

            } else {
                // Failed to remove the request
                Log.e("Firebase", "Failed to remove request: " + task.getException().getMessage());
            }
        });
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
