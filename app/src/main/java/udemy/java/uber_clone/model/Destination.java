package udemy.java.uber_clone.model;

import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.helpers.Constants;

public class Destination {

    private String road;
    private String number;
    private String city;
    private String neighborhood;
    private String postalCode;

    private String latitude;
    private String longitude;

    public Destination() {
    }

    public void updatePassengerSelectedLocation( String idRequest ) {


        DatabaseReference databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        DatabaseReference requests = databaseReference
                .child( Constants.REQUESTS );
        DatabaseReference requestsID = requests.child( idRequest )
                .child( Constants.PASSENGER );


        Map<String, Object> object = new HashMap<>();
        object.put( Constants.LATITUDE , getLatitude() );
        object.put( Constants.LONGITUDE , getLongitude() );
        object.put( Constants.NEIGHBORHOOD, getNeighborhood() );
        object.put( Constants.NUMBER , getNumber() );
        object.put( Constants.POSTAL_CODE , getPostalCode() );
        object.put( Constants.ROAD ,  getRoad() );

        requestsID.updateChildren( object );
    }


    public String getRoad() {
        return road;
    }

    public void setRoad(String road) {
        this.road = road;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
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
