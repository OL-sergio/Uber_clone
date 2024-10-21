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
    private Marker driverMarker;
    private Marker passengerMarker;
    private Marker destinationMarker;
    private Marker userMarker;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public UsersMarkers(GoogleMap map ) {
        this.mMap = map;
    }

    public void addMarcarPassengerLocation(LatLng location, String title) {

        if (passengerMarker != null) {
            passengerMarker.remove();
        }
            passengerMarker = centerUsersMarker(location, title, R.drawable.img_usuario);

    }

    public void addMarcarDriverLocation(LatLng location, String title) {

        if (driverMarker != null) {
            driverMarker.remove();
        }
            driverMarker = centerUsersMarker(location, title, R.drawable.img_carro);

    }

    public void addMarcarDestino(LatLng location, String title) {

        if (passengerMarker != null) {
            passengerMarker.remove();
        }

        if (destinationMarker != null) {
            destinationMarker.remove();
        }

        destinationMarker = centerUsersMarker(location, title, R.drawable.img_destino);

    }

    private Marker centerUsersMarker(LatLng location, String title, int resourceIcon) {
        userMarker =  mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(resourceIcon))
        );
        return userMarker;
    }


    public void centralizePassengerAndDriverLocation( Context context, LatLng driverLocation, LatLng passengerLocation, GoogleMap mMap) {

        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {


            @Override
            public void onLocationChanged(@NonNull Location location) {

                if ( ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED ) {
                    if (driverLocation != null && passengerLocation != null) {

                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        builder.include(driverLocation);
                        builder.include(passengerLocation);
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
