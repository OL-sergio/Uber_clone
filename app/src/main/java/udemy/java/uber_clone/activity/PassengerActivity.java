package udemy.java.uber_clone.activity;

import static android.content.DialogInterface.OnClickListener;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityPassengerBinding;
import udemy.java.uber_clone.helpers.Constants;
import udemy.java.uber_clone.helpers.Locations;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.TripSummaryDialog;
import udemy.java.uber_clone.helpers.UsersMarkers;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.RequestActive;
import udemy.java.uber_clone.model.Users;

public class PassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private ActivityPassengerBinding binding;

    private LinearLayout linearLayoutDestination;
    private EditText destinationText;
    private EditText origin;
    private Button buttonRequestUber;

    private GoogleMap mMap;
    private LatLng driverLocation;
    private LatLng passengerLocation;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private UsersMarkers usersMarkers;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private Destination destination;
    private Request retriveRequest;
    private Users passenger;
    private Users driver;
    private String statusRequest;
    private TripSummaryDialog tripSummaryDialog;

    private boolean uberCancel = false;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPassengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();
        verifyRequestStatus();

        buttonRequestUber.setOnClickListener(v ->
                requestUber()
        );
    }

    private void verifyRequestStatus() {

        Users userPassenger = UserFirebase.getUserDataLogged();

        DatabaseReference requestRef = databaseReference.child("requests");
        Query requestQuery = requestRef.orderByChild("passenger/id")
                .equalTo(userPassenger.getId());

        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataChangeData", "onDataChange: " + snapshot);
                List<Request> requestList = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    requestList.add(ds.getValue(Request.class));
                }

                Collections.reverse(requestList);
                if (requestList != null && requestList.size() > 0) {

                    retriveRequest = requestList.get(0);
                    if (retriveRequest != null) {
                        if (!retriveRequest.getStatus().equals(Request.STATUS_CLOSED)) {

                            passenger = retriveRequest.getPassenger();
                            passengerLocation = new LatLng(
                                    Double.parseDouble(passenger.getLatitude()),
                                    Double.parseDouble(passenger.getLongitude())
                            );

                            statusRequest = retriveRequest.getStatus();
                            destination = retriveRequest.getDestination();

                            if (retriveRequest.getDriver() != null) {
                                driver = retriveRequest.getDriver();
                                driverLocation = new LatLng(
                                        Double.parseDouble(driver.getLatitude()),
                                        Double.parseDouble(driver.getLongitude())
                                );
                            }
                            Log.d("onDataChangeData", "onDataChange: " + statusRequest);
                            changeInterfaceStatusRequest(statusRequest);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeInterfaceStatusRequest(String status) {

        if (status!= null && !status.isEmpty()) {

            uberCancel = false;

            switch (status) {
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
        } else {

            usersMarkers.addMarkerPassengerLocation(passengerLocation, "A sua localização");
            usersMarkers.centralizeMarKer(passengerLocation);

        }
    }


    private void requestWaiting() {
        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.cancelar_uber);
        uberCancel = true;


        usersMarkers.addMarkerPassengerLocation(passengerLocation, passenger.getName());

        usersMarkers.centralizeMarKer(passengerLocation);

    }

    private void requestStart() {

        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.motorista_a_caminho);
        buttonRequestUber.setEnabled(false);


        usersMarkers.addMarkerPassengerLocation(passengerLocation, " Passenger: " + passenger.getName() );

        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName());

        usersMarkers.centralizeTwoMarker(  driverLocation, passengerLocation, mMap , this );

    }

    private void requestStartTrip() {

        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.a_caminho_do_destino);
        buttonRequestUber.setEnabled(false);



        usersMarkers.addMarkerDriverLocation(driverLocation, driver.getName());

        LatLng destinationLocation = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );


        Log.d("onDataChangeData", "destinationLocation: " + destination.getRoad());

        usersMarkers.addMarkerDestination( destinationLocation,
                "Destino: " + destination.getNeighborhood()
                        + " Rua: " + destination.getRoad()
                        + ", Porta nº: " + destination.getNumber()
                        + ", Código postal: " + destination.getPostalCode()
        );

        usersMarkers.centralizeTwoMarker(  driverLocation, destinationLocation, mMap , this );
    }

    private void requestFinalizedTrip() {

        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.chegou_ao_destino);
        buttonRequestUber.setEnabled(false);


        LatLng locationDestination = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );

        LatLng driverLocation = new LatLng(
                Double.parseDouble(driver.getLatitude()),
                Double.parseDouble(driver.getLongitude())
        );

        usersMarkers.addMarkerFinalizedTripPassenger(driverLocation, passenger.getName());


        usersMarkers.centralizeTwoMarker( driverLocation , locationDestination, mMap , this );

        float distance = Locations.calculateDistance(passengerLocation, locationDestination);
        float price = distance * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String result = decimal.format(price);

        buttonRequestUber.setText(
                String.format(
                        "%s%s", getString(R.string.fim_da_viagem), result
                ));


       tripSummaryDialog.dialogTripSummaryClose(result, retriveRequest , this );
    }

    private void requestCanceled() {

        linearLayoutDestination.setVisibility(View.VISIBLE);
        buttonRequestUber.setText(R.string.chamar_uber);
        uberCancel = false;

        RequestActive requestActive = new RequestActive();
        requestActive.deleteActiveRequest();

        LatLng passengerLocation = new LatLng(
                Double.parseDouble(passenger.getLatitude()),
                Double.parseDouble(passenger.getLongitude())
        );

        usersMarkers.addMarkerPassengerLocation(passengerLocation, passenger.getName());
        usersMarkers.centralizeMarKer(passengerLocation);

    }




    private void requestUber() {

        //false -> uber cannot be canceled
        //true -> uber can be canceled
        if (uberCancel) {

            retriveRequest.setStatus(Request.STATUS_CANCEL);
            retriveRequest.updateRequest();

            //String originText = origin.getText().toString();
        } else {

            String destinationString = destinationText.getText().toString();

            if (!destinationString.equals("") && destinationString != null) {

                Address addressDestination = recoverAddress(destinationString);
                if (addressDestination != null) {

                    final Destination destination = new Destination();
                    destination.setCity(addressDestination.getLocality());
                    destination.setPostalCode(addressDestination.getPostalCode());
                    destination.setNeighborhood(addressDestination.getSubLocality());
                    destination.setRoad(addressDestination.getThoroughfare());
                    destination.setNumber(addressDestination.getFeatureName());
                    destination.setLatitude(String.valueOf(addressDestination.getLatitude()));
                    destination.setLongitude(String.valueOf(addressDestination.getLongitude()));

                    StringBuilder message = new StringBuilder();
                    message.append("Cidade: ").append(destination.getCity());
                    message.append("\nRua: ").append(destination.getRoad());
                    message.append("\nNúmero: ").append(destination.getNumber());
                    message.append("\nBairro: ").append(destination.getNeighborhood());
                    message.append("\nCódigo Postal: ").append(destination.getPostalCode());

                    AlertDialog.Builder builder = new AlertDialog.Builder(this)
                            .setTitle("Confirme seu endereço")
                            .setMessage(message)
                            .setPositiveButton("Confirmar", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                    //save request destination
                                    saveRequest(destination);

                                }

                            }).setNegativeButton("Cancelar", new OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                    AlertDialog dialog = builder.create();
                    dialog.show();

                }

            } else {
                Toast.makeText(this, R.string.interduza_o_destino, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void saveRequest(Destination destination) {

        Request request = new Request();
        request.setDestination(destination);


        Users user = UserFirebase.getUserDataLogged();
        user.setLatitude( String.valueOf(passengerLocation.latitude) );
        user.setLongitude( String.valueOf(passengerLocation.longitude) );

        request.setPassenger( user );
        request.setStatus( Request.STATUS_WAITING );
        request.saveRequest();

        RequestActive requestActive = new RequestActive();
        requestActive.setLatitude( String.valueOf(destination.getLatitude() ) );
        requestActive.setLongitude( String.valueOf(destination.getLongitude() ) );

        requestActive.saveActiveRequest( );

        linearLayoutDestination.setVisibility( View.GONE );
        buttonRequestUber.setText(R.string.cancelar_uber);

    }

    private Address recoverAddress(String destinationAddress) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocationName(destinationAddress, 1);
            if ( listAddresses != null && listAddresses.size() > 0 ) {
                Address address = listAddresses.get(0);
                return address;
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    private void recoverUserLocation() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {

                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                passengerLocation = new LatLng(latitude, longitude);

                // Update Geofire
                UserFirebase.updatedDataLocation(latitude, longitude);
                Log.d("onDataChangeData", "onDataChange: " + statusRequest);
                changeInterfaceStatusRequest( statusRequest );

            if ( statusRequest != null && !statusRequest.isEmpty() ){
                if( statusRequest.equals(Request.STATUS_START_TRIP)
                        || statusRequest.equals(Request.STATUS_FINALISED)) {
                    locationManager.removeUpdates(locationListener);
                    } else {
                        if (ActivityCompat.checkSelfPermission(
                                getApplicationContext(),
                                Manifest.permission.ACCESS_FINE_LOCATION)
                                == PackageManager.PERMISSION_GRANTED
                        ) {

                            locationManager.requestLocationUpdates(
                                    LocationManager.GPS_PROVIDER,
                                    10000,
                                    20,
                                    locationListener
                            );
                        }
                    }
                }
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
                    20,
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_user, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menuLogout) {
            auth.signOut();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
        return true;
    }

    private void components() {

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            actionBar.setTitle(R.string.iniciar_uma_viagem);
            //actionBar.setDisplayHomeAsUpEnabled(true);
        }

        tripSummaryDialog = new TripSummaryDialog();

        linearLayoutDestination = binding.linearLayoutDestination;
        origin = binding.editTextTextDestination;
        destinationText = binding.editTextTextDestination;
        buttonRequestUber = binding.buttonCallUber;

        auth = FirebaseConfiguration.getFirebaseAuth();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.passenger_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }
}