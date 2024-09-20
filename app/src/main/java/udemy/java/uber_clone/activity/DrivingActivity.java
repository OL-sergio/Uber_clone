package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityDrivingBinding;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class DrivingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDrivingBinding binding;

    private Button buttonAcceptTrip;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LatLng passengerLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker driverMarker;
    private Marker passengerMarker;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private Users driver;
    private Users passenger;
    private String idRequest;
    private Request requests;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDrivingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        compoments();

        if (getIntent().getExtras().containsKey("idRequest")
                && getIntent().getExtras().containsKey("driver")
        ) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                driver = (Users) extras.getSerializable("driver");
                idRequest = extras.getString("idRequest");
                verifyStatusRequest(driver, idRequest);
                
            }
        }

        buttonAcceptTrip.setOnClickListener(v -> {
            acceptTrip();
        });


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Handle the case where the fragment is not found
            Log.e("RequestsActivity", "Map fragment not found");
        }
    }

    private void verifyStatusRequest(Users driver, String idRequest) {
        DatabaseReference requestsRef = databaseReference.child("requests")
                .child(idRequest);
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              requests = snapshot.getValue(Request.class);

                assert requests != null;
                passenger = requests.getPassenger();
                passengerLocation = new LatLng(
                        Double.parseDouble(passenger.getLatitude()),
                        Double.parseDouble(passenger.getLongitude())
                );

              if( requests.getStatus() == Request.STATUS_ON_MY_AWAY ){
                  requestStart();

              }
              if ( requests.getStatus() == Request.STATUS_WAITING ){
                  requestWaiting();
              }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void requestStart() {
        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);
        
        addMarcarDriverLocation(driverLocation, driver.getName());
        
        addMarcarPassengerLocation(passengerLocation, passenger.getName());

        centralizePassengerAndDriverLocation(driverMarker, passengerMarker);
    }

    private void centralizePassengerAndDriverLocation(Marker driverMarker, Marker passengerMarker) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(driverMarker.getPosition());
        builder.include(passengerMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.20);

        //mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));
       mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding));

    }

    private void addMarcarPassengerLocation(LatLng location, String title) {
        if (passengerMarker != null) {
            passengerMarker.remove();
        }

        passengerMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
        );

    }

    private void addMarcarDriverLocation(LatLng location, String title) {

        if (driverMarker != null) {
            driverMarker.remove();
        }

       driverMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro))
        );


    }

    private void requestWaiting() {
        buttonAcceptTrip.setText(R.string.aceitar_viagem);


    }

    private void acceptTrip() {

        requests = new Request();
        requests.setId(idRequest);
        requests.setDriver(driver);
        requests.setStatus(Request.STATUS_ON_MY_AWAY);
        requests.updateStatus();

    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        recoverUserLocation();
    }

    private void recoverUserLocation() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                driverLocation = new LatLng(latitude, longitude);

                mMap.clear();
                mMap.addMarker(new MarkerOptions()
                        .position(driverLocation)
                        .title("My location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.carro))
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(driverLocation, 18));

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

        };

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        ) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    10000,
                    10,
                    locationListener
            );
        }
    }



    private void compoments() {

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            actionBar.setTitle("Viagem iniciada - Motorista");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        auth = FirebaseConfiguration.getFirebaseAuth();

        buttonAcceptTrip = binding.buttonAcceptTrip;

    }
}