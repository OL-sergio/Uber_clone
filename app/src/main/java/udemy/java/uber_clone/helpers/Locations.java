package udemy.java.uber_clone.helpers;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.text.DecimalFormat;

public class Locations {

    public static float calculateDistance(LatLng start, LatLng end) {

        Location inicialLocation = new android.location.Location("Incical Location");
        inicialLocation.setLatitude(start.latitude);
        inicialLocation.setLongitude(start.longitude);

        Location finalLocation = new android.location.Location("Final Location");
        finalLocation.setLatitude(end.latitude);
        finalLocation.setLongitude(end.longitude);

        // Calculate the distance in meters
        // Calculate the distance between two points
        float distanceMeters = inicialLocation.distanceTo(finalLocation) / 1000;


        return distanceMeters;
    }


    public static String formatDistance(float distance) {

        String distanceForamt;
        if (distance < 1) {

            distance = distance * 1000;
            distanceForamt = Math.round( distance ) + " M de distância";

        } else {

            DecimalFormat decimal = new DecimalFormat("0.0");
            distanceForamt = decimal.format( distance ) + " Km de distância";

        }
        return distanceForamt;
    }
}
