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
import udemy.java.uber_clone.helpers.Locations;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.helpers.UsersMarkers;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
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
    private Marker driverMarker;
    private Marker passengerMarker;
    private Marker destinationMarker;
    private UsersMarkers usersMarkers;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private Destination destination;
    private Request retriveRequest;
    private Users passenger;
    private Users driver;
    private String statusRequest;
    
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

        Users userPasseger = UserFirebase.getUserDataLogged();

        DatabaseReference requestReferece = databaseReference.child("requests");
        Query requestQuery = requestReferece.orderByChild("passenger/id")
                .equalTo( userPasseger.getId() );

        requestQuery.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Log.d("onDataChangeData", "onDataChange: " + snapshot.toString());
                List<Request> requestList = new ArrayList<>();
                for (DataSnapshot ds: snapshot.getChildren()) {
                    requestList.add( ds.getValue(Request.class) );
                }

                Collections.reverse( requestList );
                if ( requestList != null && requestList.size() > 0 ){

                    retriveRequest = requestList.get(0);
                    if (retriveRequest != null) {
                        if ( !retriveRequest.getStatus().equals(Request.STATUS_CLOSED ) ) {

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

    private void changeInterfaceStatusRequest(String satatus) {

        if (satatus != null) {

            uberCancel = false;

            switch (satatus) {
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
        }else {

            usersMarkers.addMarcarPassengerLocation( passengerLocation, "A sua localização" );
            usersMarkers.centralizeMarKer(passengerLocation);

            Log.e("changeInterfaceStatusRequest", " Status request is empty! " );
        }
    }



    private void requestWaiting() {
        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.cancelar_uber);
        uberCancel = true;

        usersMarkers.addMarcarPassengerLocation( passengerLocation, passenger.getName() );

        usersMarkers.centralizeMarKer(passengerLocation);



    }

    private void requestStart() {
        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.motorista_a_caminho);
        buttonRequestUber.setEnabled(false);


        usersMarkers.addMarcarPassengerLocation( passengerLocation, passenger.getName() );

        addMarcarDriverLocation( driverLocation, driver.getName() );

        centralizePassengerAndDriverLocation( driverMarker, passengerMarker );

    }

    private void requestStratTrip() {

        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.a_caminho_do_destino);
        buttonRequestUber.setEnabled(false);

        addMarcarDriverLocation( driverLocation, driver.getName() );

        LatLng destinationLocation = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );
        Destination destinationRoad = new Destination();
        addMarcarDestino( destinationLocation, "Destino: " + destinationRoad.getRoad() );

        centralizePassengerAndDriverLocation( driverMarker, passengerMarker );

    }

    private void requestFinalizedTrip() {

        linearLayoutDestination.setVisibility(View.GONE);
        buttonRequestUber.setText(R.string.chegou_ao_destino);
        buttonRequestUber.setEnabled(false);

        LatLng locationDestination = new LatLng(
                Double.parseDouble(destination.getLatitude()),
                Double.parseDouble(destination.getLongitude())
        );
        Destination destinationRoad = new Destination();
        addMarcarDestino( locationDestination, "Destino: " + destinationRoad.getRoad() );
        usersMarkers.centralizeMarKer(locationDestination);


        float distance = Locations.calculateDistance(passengerLocation, locationDestination);
        float price = distance * 8;
        DecimalFormat decimal = new DecimalFormat("0.00");
        String result = decimal.format(price);

        buttonRequestUber.setText(
                String.format(
                        "%s%s",
                        getString(R.string.fim_da_viagem) + result
                ));

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("Total da viagem")
                .setMessage("Total da viagem á pagar: € " + result)
                .setCancelable(false)
                .setNegativeButton("Encerrar viagem", new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        retriveRequest.setStatus(Request.STATUS_CLOSED);
                        retriveRequest.updateRequest();
                        finish();
                        startActivity(new Intent(getIntent()));
                    }
                });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void requestCanceled() {

        linearLayoutDestination.setVisibility(View.VISIBLE);
        buttonRequestUber.setText(R.string.chamar_uber);
        uberCancel = false;

        usersMarkers.addMarcarPassengerLocation(passengerLocation, passenger.getName());
        usersMarkers.centralizeMarKer(passengerLocation);

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


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);

        usersMarkers = new UsersMarkers(mMap);

        recoverUserLocation();
    }

    private void requestUber() {

        //false -> uber cannot be canceled
        //true -> uber can be canceled
      if ( uberCancel ){

          retriveRequest.setStatus(Request.STATUS_CANCEL);
          retriveRequest.updateRequest();


          //String originText = origin.getText().toString();
      }else {

          String destinationString = destinationText.getText().toString();

          if (!destinationString.equals("") &&  destinationString != null) {

              Address addressDestination = recoverAddress( destinationString );
              if ( addressDestination != null ) {

                  Destination destination = new Destination();
                  destination.setCity( addressDestination.getLocality() );
                  destination.setPostalCode( addressDestination.getPostalCode() );
                  destination.setNeighborhood( addressDestination.getSubLocality() );
                  destination.setRoad( addressDestination.getThoroughfare());
                  destination.setNumber( addressDestination.getFeatureName());
                  destination.setLatitude( String.valueOf( addressDestination.getLatitude() ) );
                  destination.setLongitude( String.valueOf( addressDestination.getLongitude() ) );

                  StringBuilder message = new StringBuilder();
                  message.append("Cidade: ").append(destination.getCity());
                  message.append("\nRua: ").append(destination.getRoad());
                  message.append("\nNúmero: ").append(destination.getNumber());
                  message.append("\nBairro: ").append(destination.getNeighborhood());
                  message.append("\nCódigo Postal: ").append(destination.getPostalCode());

                  AlertDialog.Builder builder = new AlertDialog.Builder(this)
                          .setTitle("Confirme seu endereço")
                          .setMessage(message)
                          .setPositiveButton( "Confirmar",  new OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {

                                  //save request destination
                                  saveRequest(destination);

                              }

                          }) .setNegativeButton("Cancelar", new OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {

                              }
                          });

                  AlertDialog dialog = builder.create();
                  dialog.show();

              }

          }else {
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


        linearLayoutDestination.setVisibility( View.GONE );
        buttonRequestUber.setText(R.string.cancelar_uber);

    }

    private Address recoverAddress(String destinationText) {

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> listAddresses = geocoder.getFromLocationName(destinationText, 1);
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

                        Log.e("changeInterfaceStatusRequest", " Status request is empty! " );
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