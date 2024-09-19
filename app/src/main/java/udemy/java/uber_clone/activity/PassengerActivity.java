package udemy.java.uber_clone.activity;

import static android.content.DialogInterface.*;

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

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityPassengerBinding;
import udemy.java.uber_clone.helpers.UserFirebase;
import udemy.java.uber_clone.model.Destination;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class PassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPassengerBinding binding;

    private LinearLayout linearLayoutDestination;
    private EditText destination;
    private EditText origin;
    private Button buttonRequestUber;


    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private LatLng passegerLocation;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;
    private Request request;

    private boolean uberRequest = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPassengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();
        verifyRequestStatus();

      /*  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_passenger);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

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
                    request = requestList.get(0);

                    if (request.getStatus().equals(Request.STATUS_WAITING)) {

                        linearLayoutDestination.setVisibility(View.GONE);
                        buttonRequestUber.setText(R.string.cancelar_uber);
                        uberRequest = true;

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        recoverUserLocation();

        // Add a marker in Sydney and move the camera
    }

    private void requestUber() {

      if (!uberRequest ){

          //String originText = origin.getText().toString();
          String destinationText = destination.getText().toString();

          if (!(destinationText.isEmpty() && (destinationText != null))) {

              Address addressDestination = recoverAddress( destinationText );
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
                                  uberRequest = true;
                              }

                          }) .setNegativeButton("Cancelar", new OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int which) {
                                  uberRequest = false;


                              }
                          });

                  AlertDialog dialog = builder.create();
                  dialog.show();

              }

          }else {
              Toast.makeText(this, R.string.interduza_o_destino, Toast.LENGTH_SHORT).show();
          }

      }else {
         uberRequest = false;
      }


    }


    private void saveRequest(Destination destination) {
        Request request = new Request();
        request.setDestination(destination);

        Users user = UserFirebase.getUserDataLogged();
        user.setLatitude( String.valueOf(passegerLocation.latitude) );
        user.setLongitude( String.valueOf(passegerLocation.longitude) );

        //is not geting user name and saving
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
                passegerLocation = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(passegerLocation)
                        .title("My location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(passegerLocation, 18));

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
        }

        linearLayoutDestination = binding.linearLayoutDestination;
        origin = binding.editTextTextDestination;
        destination = binding.editTextTextDestination;
        buttonRequestUber = binding.buttonCallUber;

        auth = FirebaseConfiguration.getFirebaseAuth();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.passenger_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }
}