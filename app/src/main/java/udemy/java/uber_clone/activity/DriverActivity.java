package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
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

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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
import udemy.java.uber_clone.helpers.Constants;
import udemy.java.uber_clone.helpers.Locations;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.MonitoringUsers;
import udemy.java.uber_clone.helpers.TripSummaryDialog;
import udemy.java.uber_clone.helpers.UsersMarkers;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.RequestActive;
import udemy.java.uber_clone.model.Users;

public class DriverActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityDriverBinding binding;

    private Button buttonAcceptTrip;
    private FloatingActionButton fabRoute;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LatLng passengerLocation;
    private LatLng destinationLocation;
    private LatLng destinationLocationActive;
    private LocationManager locationManager;
    private LocationListener locationListener;

    private DatabaseReference databaseReference;

    private Users driver;
    private Users passenger;
    private String passengerId;
    private Destination destination;
    private RequestActive requestActive;
    private String requestId;
    private Request retriveRequest;
    private String statusRequest;
    private boolean requestAccepted;
    private UsersMarkers usersMarkers;
    private MonitoringUsers monitoringUsers;
    private TripSummaryDialog tripSummaryDialog;


    /// Lat/lon destino:-23.556407, -46.662365 ( Av. Paulista, 2064 )
    /// Lat/lon passageiro: -23.562791, -46.654668 ( Av. Paulista, 2439 )
    /// Lat/lon Motorista (a caminho):
    ///  inicial: -23.563196, -46.650607
    ///  intermediaria: -23.564801, -46.652196
    ///  final: -23.562801, -46.654660 (esse local foi corrigido com rela&ccedil;&atilde;o ao v&iacute;deo)
    ///  Rua Augusta, 1611, S&atilde;o Paulo, 01310-928, Brasil
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDriverBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        components();

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

                requestId = extras.getString("idRequest");
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

                if ( retriveRequest.equals(Constants.STATUS_ON_MY_AWAY ) ) {

                    lat = String.valueOf(passengerLocation.latitude);
                    lon = String.valueOf(passengerLocation.longitude);

                }   else if (retriveRequest.equals(Constants.STATUS_START_TRIP)) {

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

    private void verifyStatusRequest( ) {

        DatabaseReference requestsRef = databaseReference.child("requests")
                .child(requestId);
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


                    destination = retriveRequest.getDestination();
                    destinationLocation = new LatLng(
                            Double.parseDouble(destination.getLatitude()),
                            Double.parseDouble(destination.getLongitude())
                    );

                    destination.getPostalCode();
                    destination.getNeighborhood();
                    destination.getNumber();
                    destination.getCity();

                    Log.d("passenger", String.valueOf(passengerLocation));


                    passengerId = passenger.getId();
                    Log.d("destination", String.valueOf(destinationLocation));
                    statusRequest = retriveRequest.getStatus();

                    changeInterfaceStatusRequest(statusRequest);

                    requestActive(passengerId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void requestActive(String passengerId) {

        DatabaseReference activeRequestRef = databaseReference.child( Constants.REQUESTS_ACTIVE )
                .child(passengerId)
                .child( Constants.DESTINATION );

        activeRequestRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                requestActive = snapshot.getValue(RequestActive.class);
                if (requestActive != null) {

                    destinationLocationActive = new LatLng(
                            Double.parseDouble(requestActive.getLatitude()),
                            Double.parseDouble(requestActive.getLongitude())
                    );

                }

                Log.d("firebase", "requestActive: " + requestActive + " Location: " + destinationLocationActive );
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    private void requestWaiting() {

        buttonAcceptTrip.setText(R.string.aceitar_viagem);

        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName());

        usersMarkers.centralizeMarKer(driverLocation);

    }


    private void requestStart() {

        buttonAcceptTrip.setText(R.string.a_caminho_do_passageiro);
        fabRoute.setVisibility( FloatingActionButton.VISIBLE );
        buttonAcceptTrip.setEnabled(false);

        usersMarkers.addMarkerDriverLocation( driverLocation, driver.getName() );

        usersMarkers.addMarkerPassengerLocation( passengerLocation, passenger.getName() );

        usersMarkers.centralizeTwoMarker(  driverLocation, passengerLocation, mMap, this );

        monitoringUsers.startMonitoringDriving( driver, passengerLocation, Constants.STATUS_START_TRIP, mMap, retriveRequest );

    }

    private void requestStartTrip() {

        fabRoute.setVisibility(View.GONE);
        buttonAcceptTrip.setText(R.string.a_caminho_do_destino);
        buttonAcceptTrip.setEnabled(false);


        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName() );

        Log.d("requestStartTrip", "requestStartTrip: " + destinationLocationActive);

        if (  requestActive != null ) {
            LatLng destinationLocationActive = new LatLng(
                    Double.parseDouble(requestActive.getLatitude()),
                    Double.parseDouble(requestActive.getLongitude())
            );

            usersMarkers.addMarkerDestination(destinationLocationActive,
                    "Destino: " +  destination.getNeighborhood()
                            + " Rua: " + destination.getRoad()
                            + " nº: " + destination.getNumber()
                            + " CP: " + destination.getPostalCode()
            );

            usersMarkers.centralizeTwoMarker(  driverLocation, destinationLocationActive, mMap , this);

            monitoringUsers.startMonitoringDriving( driver, destinationLocationActive , Constants.STATUS_FINALISED, mMap, retriveRequest );

        }
    }


    private void requestFinalizedTrip() {

        fabRoute.setVisibility(View.GONE);
        buttonAcceptTrip.setVisibility(View.GONE);
        requestAccepted = false;


        if (requestActive != null) {
            LatLng locationDestination = new LatLng(
                    Double.parseDouble(requestActive.getLatitude()),
                    Double.parseDouble(requestActive.getLongitude())
            );

            LatLng driverLocation = new LatLng(
                    Double.parseDouble(driver.getLatitude()),
                    Double.parseDouble(driver.getLongitude())
            );

            usersMarkers.addMarkerDriverLocation(driverLocation , driver.getName() );
            usersMarkers.centralizeMarKer(driverLocation);

            float distance = Locations.calculateDistance(passengerLocation, locationDestination);
            float price = distance * 8;
            DecimalFormat decimal = new DecimalFormat("0.00");
            String result = decimal.format(price);

            buttonAcceptTrip.setText(
                    String.format("%s%s", getString(R.string.fim_da_viagem), result ));

            tripSummaryDialog.dialogTripSummary(result , this);

        }
    }

    private void requestCanceled() {

        Toast.makeText(this, "Viagem cancelada pelo passageiro!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, RequestsActivity.class);
        startActivity(intent);

    }

    private void acceptTrip() {

        retriveRequest = new Request();
        retriveRequest.setId(requestId);
        retriveRequest.setDriver(driver);
        retriveRequest.setStatus(Constants.STATUS_ON_MY_AWAY);
        retriveRequest.updateDriverStatus();

    }

    private void changeInterfaceStatusRequest(String request) {
        switch (request) {
            case Constants.STATUS_WAITING:
                requestWaiting();
                break;
            case Constants.STATUS_ON_MY_AWAY:
                requestStart();
                break;
            case Constants.STATUS_START_TRIP:
                requestStartTrip();
                break;
            case Constants.STATUS_FINALISED:
                requestFinalizedTrip();
                break;
            case Constants.STATUS_CANCEL:
                requestCanceled();
                break;
        }
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

                driver.setLatitude(String.valueOf(latitude));
                driver.setLongitude(String.valueOf(longitude));
                retriveRequest.setDriver(driver);
                retriveRequest.updateDriverLocation();

                changeInterfaceStatusRequest(statusRequest);

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

        };

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION)
                 == PackageManager.PERMISSION_GRANTED
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
        monitoringUsers = new MonitoringUsers();

        recoverUserLocation();
    }

    private void components() {

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(this, R.color.limedSpruce_100)) );
            actionBar.setTitle("Viagem iniciada - Motorista");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fabRoute = binding.floatingActionButtonRoute;
        buttonAcceptTrip = binding.buttonAcceptTrip;

        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        tripSummaryDialog = new TripSummaryDialog();

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
            retriveRequest.setStatus(Constants.STATUS_CLOSED);
            retriveRequest.updateRequest();
        }

        return false;
    }
}