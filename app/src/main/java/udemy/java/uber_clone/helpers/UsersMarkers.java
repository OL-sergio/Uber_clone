package udemy.java.uber_clone.helpers;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import udemy.java.uber_clone.R;

public class UsersMarkers {

    private final GoogleMap mMap;
    private Marker markerDestination;
    private Marker markerPassenger;
    private Marker markerDriver;
    private Marker usersMarker;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public UsersMarkers(GoogleMap map) {
        this.mMap = map;
    }

    public void addMarkerPassengerLocation(LatLng location, String title) {

        if (markerPassenger != null) {
            markerPassenger.remove();
        }
            markerPassenger = centerUsersMarker(location, title, R.drawable.img_usuario);

    }

    public void addMarkerDriverLocation(LatLng location, String title) {

        if (markerDriver != null) {
            markerDriver.remove();
        }
            markerDriver = centerUsersMarker(location, title, R.drawable.img_carro );

    }

    public void addMarkerDestination(LatLng location, String title) {

        if (markerPassenger != null) {
            markerPassenger.remove();
        }

        if (markerDestination != null) {
            markerDestination.remove();
        }

        markerDestination = centerUsersMarker(location, title, R.drawable.img_destino );

    }



    public void addMarkerFinalizedTripPassenger(LatLng location, String title) {

        if (markerDriver != null) {
            markerDriver.remove();
        }

        if (markerPassenger != null) {
            markerPassenger.remove();
        }

        markerPassenger = centerUsersMarker(location, title, R.drawable.img_usuario );

    }


    private Marker centerUsersMarker(LatLng location, String title, int resourceIcon) {
        usersMarker =  mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(resourceIcon))
        );
        return usersMarker;
    }


    public void centralizeTwoMarker( LatLng locationA, LatLng locationB, GoogleMap mMap, Context context) {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(@NonNull Location location) {

                if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ) {
                    if (locationA != null && locationB != null) {

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(locationA);
                        builder.include(locationB);
                        LatLngBounds bounds = builder.build();

                        int width = context.getResources().getDisplayMetrics().widthPixels;
                        int height = context.getResources().getDisplayMetrics().heightPixels;
                        int padding = (int) (width * 0.30);

                        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
                        mMap.animateCamera(
                                CameraUpdateFactory.newLatLngBounds( bounds, width, height, padding )
                        );

                    } else {

                        Log.d("Error", "Null location");
                    }
                }
                locationManager.removeUpdates(locationListener);
            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }
        };
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED
            ) {
                locationManager.requestLocationUpdates(
                        LocationManager.GPS_PROVIDER,
                        100000,
                        30,
                        locationListener
                );
        } else {
            Log.d("Error", "Permission denied");
        }
    }

    public void centralizeMarKer(LatLng location) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(location, 18)
        );
    }
}
