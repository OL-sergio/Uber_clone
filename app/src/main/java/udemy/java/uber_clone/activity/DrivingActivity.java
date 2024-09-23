package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

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
    private Request retriveRequest;
    private String statusRequest;
    private boolean requestAccepted;

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
                driverLocation = new LatLng(
                        Double.parseDouble(driver.getLatitude()),
                        Double.parseDouble(driver.getLongitude())
                );
                requestAccepted = extras.getBoolean("requestAccepted");
                verifyStatusRequest();
                
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

    private void verifyStatusRequest() {
        DatabaseReference requestsRef = databaseReference.child("requests")
                .child(idRequest);
        requestsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                retriveRequest = snapshot.getValue(Request.class);
                if (retriveRequest != null) {
                    passenger = retriveRequest.getPassenger();
                    passengerLocation = new LatLng(
                            Double.parseDouble(passenger.getLatitude()),
                            Double.parseDouble(passenger.getLongitude())
                    );
                    statusRequest = retriveRequest.getStatus();
                    changeInterfaceStatusRequest(statusRequest);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

private void changeInterfaceStatusRequest(String request) {
        if (request.equals(Request.STATUS_WAITING)) {
            requestWaiting();
        } else if (request.equals(Request.STATUS_ON_MY_AWAY)) {
            requestStart();
        }
    }

    private void requestWaiting() {

        buttonAcceptTrip.setText(R.string.aceitar_viagem);
    }

    private void requestStart() {
        addMarcarDriverLocation(driverLocation, driver.getName());

        addMarcarPassengerLocation(passengerLocation, passenger.getName());

        centralizePassengerAndDriverLocation(driverMarker, passengerMarker);

        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);
    }

    private void centralizePassengerAndDriverLocation(Marker driverMarker, Marker passengerMarker) {

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(driverMarker.getPosition());
        builder.include(passengerMarker.getPosition());

        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.40);

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

    private void acceptTrip() {

        retriveRequest = new Request();
        retriveRequest.setId(idRequest);
        retriveRequest.setDriver(driver);
        retriveRequest.setStatus(Request.STATUS_ON_MY_AWAY);
        retriveRequest.updateStatus();

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

    @Override
    public boolean onSupportNavigateUp() {
        if (requestAccepted) {
            Toast.makeText(this, "Tem de cancelar o Uber!", Toast.LENGTH_SHORT).show();
        }else {
            Intent intent = new Intent(this, RequestsActivity.class);
            startActivity(intent);
        }
        return false;
    }
}