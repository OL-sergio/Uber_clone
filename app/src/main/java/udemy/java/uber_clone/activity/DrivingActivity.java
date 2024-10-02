package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.net.UriCompat;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityDrivingBinding;
import udemy.java.uber_clone.helpers.UserFirebase;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class DrivingActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDrivingBinding binding;

    private Button buttonAcceptTrip;
    private FloatingActionButton fabRoute;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LatLng passengerLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Marker driverMarker;
    private Marker passengerMarker;
    private Marker destinationMarker;

    private DatabaseReference databaseReference;
    private FirebaseAuth auth;

    private Users driver;
    private Users passenger;
    private Destination destination;
    private String idRequest;
    private Request retriveRequest;
    private String statusRequest;
    private boolean requestAccepted;

    /**
     *
     * Lat/lon destino:-23.556407, -46.662365 (Av. Paulista, 2439)
     * Lat/lon passageiro: -23.562791, -46.654668
     * Lat/lon Motorista (a caminho):
     *  inicial: -23.563196, -46.650607
     *  intermediaria: -23.564801, -46.652196
     *  final: -23.562801, -46.654660 (esse local foi corrigido com relação ao vídeo)
     *  Rua Augusta, 1611, São Paulo, 01310-928, Brasil
     **/


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
                driverLocation = new LatLng(
                        Double.parseDouble(driver.getLatitude()),
                        Double.parseDouble(driver.getLongitude())
                );


                idRequest = extras.getString("idRequest");
                requestAccepted = extras.getBoolean("requestAccepted");

                verifyStatusRequest();
                
            }
        }

        buttonAcceptTrip.setOnClickListener(v -> {
            acceptTrip();
        });

        fabRoute.setOnClickListener(v -> {
            String status = statusRequest;
            if (status != null && !status.isEmpty()) {

                String lat = "";
                String lon = "";

                if (retriveRequest.equals(Request.STATUS_ON_MY_AWAY)) {

                    lat = String.valueOf(passengerLocation.latitude);
                    lon = String.valueOf(passengerLocation.longitude);

                }   else if (retriveRequest.equals(Request.STATUS_START_TRIP)) {

                    lat = destination.getLatitude();
                    lon = destination.getLongitude();

                }
                //Open Google Maps
                String latLon = lat + "," + lon;
                Uri uri = Uri.parse("google.navigation:q="+latLon+"&mode=d");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }
        });
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
                    destination = retriveRequest.getDestination();
                    changeInterfaceStatusRequest(statusRequest);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

private void changeInterfaceStatusRequest(String request) {
        switch (request) {
            case Request.STATUS_WAITING:
                requestWaiting();
                break;
            case Request.STATUS_ON_MY_AWAY:
                requestStart();
                break;
            case Request.STATUS_START_TRIP:
                requestStratTrip();
                break;
            case Request.STATUS_FINALISED:
                requestFinalizedTrip();
                break;
        }
    }

    private void requestFinalizedTrip() {

        fabRoute.setVisibility(View.GONE);

        if (driverMarker != null) {
            driverMarker.remove();
        }

        if (destinationMarker != null) {
            destinationMarker.remove();
        }

        LatLng locationDestination = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );

        addMarcarDestino(locationDestination, "Destino"  + destination.getRoad() );

        centralizeMarcar(locationDestination);

        buttonAcceptTrip.setText("Viagem finalizada"+ " R$ 20 " );

    }

    private void requestWaiting() {

        buttonAcceptTrip.setText(R.string.aceitar_viagem);

        addMarcarDriverLocation(driverLocation, driver.getName());

        centralizeMarcar(driverLocation);

    }


    private void requestStart() {

        buttonAcceptTrip.setText(R.string.a_camino_do_passageiro);
        fabRoute.setVisibility( FloatingActionButton.VISIBLE );

        addMarcarDriverLocation(driverLocation, driver.getName());

        addMarcarPassengerLocation(passengerLocation, passenger.getName());

        centralizePassengerAndDriverLocation(driverMarker, passengerMarker);

        startMonitoringDriving(passenger, driver);

    }

    private void requestStratTrip() {

        fabRoute.setVisibility(View.VISIBLE);
        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);

        addMarcarDriverLocation(driverLocation, driver.getName());

        LatLng destinationLocation = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );

        addMarcarDestino( destinationLocation, "Destino");

        centralizePassengerAndDriverLocation(driverMarker, destinationMarker);

    }

    private void centralizeMarcar(LatLng location) {
        mMap.moveCamera(
                CameraUpdateFactory.newLatLngZoom(location, 18)
        );
    }


    private void startMonitoringDriving( Users uPassenger, Users uDriver ) {

        DatabaseReference userLocation = FirebaseConfiguration.getFirebaseDatabase()
                .child("location_user");

        GeoFire geoFire = new GeoFire(userLocation);
        Circle passengerCircle = mMap.addCircle(
                new CircleOptions()
                        .center(passengerLocation)
                        .radius(20)// meters
                        .fillColor( Color.argb(90, 255, 153, 0) )
                        .strokeColor( Color.argb(190, 255, 153, 0) )

        );

        GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(
                        passengerLocation.latitude,
                        passengerLocation.longitude
                ), 0.020 //  meters
        );

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if( key.equals( driver.getId() ) ){
                    Log.d("onKeyEntered", "onKeyEntered: " + key + " passenger it's on location: " + location);

                    retriveRequest.setStatus(Request.STATUS_START_TRIP);
                    retriveRequest.updateRequest();

                    geoQuery.removeAllListeners();
                    passengerCircle.remove();

                }else if (key.equals( uDriver.getId() ) ){
                    Log.d("onKeyEntered", "onKeyEntered: " + key + " driver it's on location: " + location);
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

    private void centralizePassengerAndDriverLocation(Marker driverMarker, Marker passengerMarker) {

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

    private void addMarcarPassengerLocation(LatLng location, String title) {

        if (passengerMarker != null) {
            passengerMarker.remove();
        }

        passengerMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_usuario))
        );
    }

    private void addMarcarDriverLocation(LatLng location, String title) {

        if (driverMarker != null) {
            driverMarker.remove();
        }

       driverMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_carro))
        );
    }

    private void addMarcarDestino(LatLng location, String title) {

        if (passengerMarker != null) {
            passengerMarker.remove();
        }

        if (destinationMarker != null) {
            destinationMarker.remove();
        }

        destinationMarker = mMap.addMarker(new MarkerOptions()
                .position(location)
                .title(title)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.img_destino))
        );
    }


    private void acceptTrip() {

        retriveRequest = new Request();
        retriveRequest.setId(idRequest);
        retriveRequest.setDriver(driver);
        retriveRequest.setStatus(Request.STATUS_ON_MY_AWAY);
        retriveRequest.updateDriverStatus();

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

                UserFirebase.updatedDataLocation(latitude, longitude);

                changeInterfaceStatusRequest(statusRequest);


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

        fabRoute = binding.floatingActionButtonRoute;
        buttonAcceptTrip = binding.buttonAcceptTrip;

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();
        auth = FirebaseConfiguration.getFirebaseAuth();



        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.driver_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        } else {
            // Handle the case where the fragment is not found
            Log.e("RequestsActivity", "Map fragment not found");
        }
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