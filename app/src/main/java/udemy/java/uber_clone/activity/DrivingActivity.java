package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityDrivingBinding;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class DrivingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDrivingBinding binding;

    private Button buttonAcceptTrip;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private Users driver;
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
              if( requests.getStatus() == Request.STATUS_ON_MY_AWAY ){
                  requestwaiting();
              }
              if ( requests.getStatus() == Request.STATUS_START ){
                  requestStart();
              }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    private void requestStart() {
        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);
    }

    private void requestwaiting() {
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