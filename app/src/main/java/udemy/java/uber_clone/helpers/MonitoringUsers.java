package udemy.java.uber_clone.helpers;

import android.graphics.Color;
import android.util.Log;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.Objects;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class MonitoringUsers {

    public void startMonitoringDriving(final Users uOrigem, LatLng location, final String statusRequest, GoogleMap mMap, Request retriveRequest) {

        DatabaseReference userLocation = FirebaseConfiguration.getFirebaseDatabase()
                .child("location_user");
        GeoFire geoFire = new GeoFire(userLocation);

        final Circle circle = mMap.addCircle(
                new CircleOptions()
                        .center(location)
                        .radius(30)// meters
                        .fillColor( Color.argb(90, 255, 153, 0) )
                        .strokeColor( Color.argb(190, 255, 153, 0) )

        );

       final GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(
                        location.latitude,
                        location.longitude
                ), 0.030 //  meters
        );

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if( key.equals( uOrigem.getId() ) ){
                    Log.d("statusRequest", "location: " + key + " passenger it's on location: " + location);
                    Log.d("statusRequest", "statusRequest: " + key + "statusRequest: " + statusRequest);

                    retriveRequest.setStatus(statusRequest);
                    retriveRequest.updateRequest();
                    geoQuery.removeAllListeners();
                    circle.remove();
                }

            }

            @Override
            public void onKeyExited(String key) {
                Log.d("onKeyExited", "onKeyExited: " + key );
            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {
                Log.d("onKeyMoved", "onKeyMoved: " + key + " location: " + location);
            }

            @Override
            public void onGeoQueryReady() {
                Log.d("onGeoQueryReady", "onGeoQueryReady: " );
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Log.d("onGeoQueryError", "onGeoQueryError: " + error );
            }
        });
    }
}
