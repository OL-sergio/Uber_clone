package udemy.java.uber_clone.model;

import android.media.MediaPlayer;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.Constants;

public class Request {

    private String id;
    private String status;
    private Users passenger;
    private Users driver;
    private Destination destination;


    public Request() {
    }

    public void saveRequest() {

        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child( Constants.REQUESTS );

        String idRequest = requests.push().getKey();
        setId( idRequest );

        requests.child( getId() ).setValue(this);

    }

    public void updateDriverStatus() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child( Constants.REQUESTS );
        DatabaseReference requestsID = requests.child( getId());

        Map<String, Object> object = new HashMap<>();
        object.put( Constants.DRIVER , getDriver() );
        object.put( Constants.STATUS , getStatus() );

        requestsID.updateChildren( object );

    }

    public void updateDriverLocation() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference
                .child( Constants.REQUESTS );

        DatabaseReference requestsID = requests.child( getId())
                            .child( Constants.DRIVER );

        Map<String, Object> object = new HashMap<>();
        object.put( Constants.LATITUDE , getDriver().getLatitude() );
        object.put( Constants.LONGITUDE , getDriver().getLongitude() );

        requestsID.updateChildren( object );

    }


    public void updateRequest() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child( Constants.REQUESTS );
        DatabaseReference requestsID = requests.child( getId() );

        Map<String, Object> object = new HashMap<>();
        object.put( Constants.STATUS , getStatus());

        requestsID.updateChildren( object );

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users getPassenger() {
        return passenger;
    }

    public void setPassenger(Users passenger) {
        this.passenger = passenger;
    }

    public Users getDriver() {
        return driver;
    }

    public void setDriver(Users driver) {
        this.driver = driver;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }

}
