package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
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

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityDriverBinding;
import udemy.java.uber_clone.helpers.Locations;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.UsersMarkers;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDriverBinding binding;

    private Button buttonAcceptTrip;
    private FloatingActionButton fabRoute;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LatLng passengerLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference databaseReference;

    private Users driver;
    private Users passenger;
    private Destination destination;
    private String idRequest;
    private Request retriveRequest;
    private String statusRequest;
    private boolean requestAccepted;
    private UsersMarkers usersMarkers;

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

        binding = ActivityDriverBinding.inflate(getLayoutInflater());
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

                if ( retriveRequest.equals(Request.STATUS_ON_MY_AWAY ) ) {

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
            case Request.STATUS_CANCEL:
                requestCanceled();
                break;

        }
    }


    private void requestWaiting() {

        buttonAcceptTrip.setText(R.string.aceitar_viagem);

        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName());

        usersMarkers.centralizeMarKer(driverLocation);

    }


    private void requestStart() {

        buttonAcceptTrip.setText(R.string.a_caminho_do_passageiro);
        fabRoute.setVisibility( FloatingActionButton.VISIBLE );

        usersMarkers.addMarkerDriverLocation( driverLocation, driver.getName() );

        usersMarkers.addMarkerPassengerLocation( passengerLocation, passenger.getName() );

        usersMarkers.centralizePassengerAndDriverLocation( this ,driverLocation , passengerLocation, mMap );

        startMonitoringDriving( driver, passengerLocation , Request.STATUS_START_TRIP );

    }

    private void requestStratTrip() {

        fabRoute.setVisibility(View.VISIBLE);
        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);

       /*
        DatabaseReference requests = databaseReference.child("requests");
        String idRequest = requests.getKey();
        Log.d("firebase", idRequest);

        databaseReference.child(idRequest).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {

                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {

                    Log.d("firebase", String.valueOf(task.getResult().getValue()));



                }
            }
        });*/


        LatLng destinationLocation = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );

        Log.d("requestStratTrip", "requestStratTrip: " + destinationLocation);

        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName());

        usersMarkers.addMarkerDestino( destinationLocation, "Destino");

        usersMarkers.centralizePassengerAndDriverLocation( this, driverLocation, destinationLocation, mMap );

        startMonitoringDriving(driver, destinationLocation , Request.STATUS_START_TRIP );

    }

    private void requestFinalizedTrip() {

        fabRoute.setVisibility(View.GONE);
        requestAccepted = false;


        LatLng locationDestination = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );

        usersMarkers.addMarkerDestino(locationDestination, "Destino"  + destination.getRoad() );

        usersMarkers.centralizeMarKer(locationDestination);

        float distance = Locations.calculateDistance(passengerLocation, locationDestination);
        float price = distance * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String result = decimal.format(price);

        buttonAcceptTrip.setText(
                String.format("%s%s",
                        getString(R.string.fim_da_viagem) + result)
        );

    }

    private void requestCanceled() {

        Toast.makeText(this, "Viagem cancelada pelo passageiro!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RequestsActivity.class);
        startActivity(intent);
    }


    private void startMonitoringDriving( final Users uOrigem, LatLng locationDistaniton,final String status) {

        DatabaseReference userLocation = FirebaseConfiguration.getFirebaseDatabase()
                .child("location_user");

        GeoFire geoFire = new GeoFire(userLocation);
        Circle passengerCircle = mMap.addCircle(
                new CircleOptions()
                        .center(locationDistaniton)
                        .radius(20)// meters
                        .fillColor( Color.argb(90, 255, 153, 0) )
                        .strokeColor( Color.argb(190, 255, 153, 0) )

        );

        GeoQuery geoQuery = geoFire.queryAtLocation(
                new GeoLocation(
                        locationDistaniton.latitude,
                        locationDistaniton.longitude
                ), 0.020 //  meters
        );

        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {

                if( key.equals( uOrigem.getId() ) ){
                    Log.d("onKeyEntered", "onKeyEntered: " + key + " passenger it's on location: " + location);

                    retriveRequest.setStatus(status);
                    retriveRequest.updateRequest();

                    geoQuery.removeAllListeners();
                    passengerCircle.remove();

                }else if (key.equals( uOrigem.getId() ) ){
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

    private void acceptTrip() {

        retriveRequest = new Request();
        retriveRequest.setId(idRequest);
        retriveRequest.setDriver(driver);
        retriveRequest.setStatus(Request.STATUS_ON_MY_AWAY);
        retriveRequest.updateDriverStatus();

    }


    private void recoverUserLocation() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull android.location.Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                driverLocation = new LatLng(latitude, longitude);

                UserFirebase.updatedDataLocation(latitude, longitude);

                driver.setLatitude(String.valueOf(latitude));
                driver.setLongitude(String.valueOf(longitude));
                retriveRequest.setDriver(driver);
                retriveRequest.upadateDriverLocation();

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

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        usersMarkers = new UsersMarkers(mMap);

        recoverUserLocation();
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

        if (statusRequest != null && !statusRequest.isEmpty()) {
            retriveRequest.setStatus(Request.STATUS_CLOSED);
            retriveRequest.updateRequest();
        }

        return false;
    }
}