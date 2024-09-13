package udemy.java.uber_clone.activity;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.ui.AppBarConfiguration;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityPassengerBinding;
import udemy.java.uber_clone.model.Destination;

public class PassengerActivity extends AppCompatActivity implements OnMapReadyCallback {

    private AppBarConfiguration appBarConfiguration;
    private ActivityPassengerBinding binding;


    private EditText destination;
    private EditText origin;
    private Button requestUber;

    private LocationManager locationManager;
    private LocationListener locationListener;
    private GoogleMap mMap;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPassengerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();

      /*  NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_passenger);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);*/

        requestUber.setOnClickListener(v ->
                requestUber(v)
        );
    }

    private void components() {

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            actionBar.setTitle(R.string.iniciar_uma_viagem);
        }


        origin = binding.editTextTextDestination;
        destination = binding.editTextTextDestination;
        requestUber = binding.buttonCallUber;


        auth = FirebaseConfiguration.getFirebaseAuth();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.passenger_map);
        assert mapFragment != null;
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        recoverUserLocation();

        // Add a marker in Sydney and move the camera
    }

    private void requestUber(View view) {

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
                        .setPositiveButton( "Confirmar",  new DialogInterface.OnClickListener() {
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
                LatLng myLocation = new LatLng(latitude, longitude);

                mMap.addMarker(new MarkerOptions()
                        .position(myLocation)
                        .title("My location")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.usuario))
                );

                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myLocation, 18));

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


    /*
    @Override
   public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_passenger);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }*/
}