package udemy.java.uber_clone.model;

import com.google.firebase.database.DatabaseReference;
import java.util.HashMap;
import java.util.Map;

import udemy.java.uber_clone.config.FirebaseConfiguration;

public class Request {

    private String id;
    private String status;
    private Users passenger;
    private Users driver;
    private Destination destination;

    public static final String STATUS_WAITING = "WAITING";
    public static final String STATUS_ON_MY_AWAY = "ONMYWAY";
    public static final String STATUS_START = "START";
    public static final String STATUS_FINALISED = "FINALISED";

    public Request() {
    }

    public void saveRequest() {

        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child("requests");

        String idRequest = requests.push().getKey();
        setId( idRequest );

        requests.child(getId()).setValue(this);

    }

    public void updateStatus() {
        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference.child("requests");
        DatabaseReference requestsID = requests.child(getId());

        Map object = new HashMap();
        object.put("driver", getDriver());
        object.put("status", getStatus());
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
