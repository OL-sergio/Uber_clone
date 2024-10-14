package udemy.java.uber_clone.helpers;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import udemy.java.uber_clone.R;

public class UsersMarkers extends AppCompatActivity {

    private GoogleMap mMap;
    private Marker driverMarker;
    private Marker passengerMarker;
    private Marker destinationMarker;
    private UsersMarkers usersMarkers;


    public UsersMarkers(GoogleMap map) {
        this.mMap = map;
    }


    public void addMarcarPassengerLocation(LatLng location, String title) {

        if (passengerMarker != null) {
            passengerMarker.remove();
        }

        passengerMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_usuario))
        );
    }


    public void centralizePassengerAndDriverLocation(Marker driverMarker, Marker passengerMarker) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(driverMarker.getPosition());
        builder.include(passengerMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.30);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

    }

    public void centralizeMarKer(LatLng location) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(location, 18)
        );
    }

}
