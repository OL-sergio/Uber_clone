package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.adpter.RequestsAdpter;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityPassengerBinding;
import udemy.java.uber_clone.databinding.ActivityRequestsBinding;
import udemy.java.uber_clone.helpers.RecyclerItemClickListener;
import udemy.java.uber_clone.helpers.UserFirebase;
import udemy.java.uber_clone.model.Request;
import udemy.java.uber_clone.model.Users;

public class RequestsActivity extends AppCompatActivity {

    private ActivityRequestsBinding binding;

    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    private RecyclerView recyclerViewRequests;
    private TextView textViewRequests;
    private final List<Request> requestsList = new ArrayList<>();
    private RequestsAdpter requestsAdpter;
    private Users driver;

    private LocationManager locationManager;
    private LocationListener locationListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();
        retriveRequests();
        recoverUserLocation();
    }

    private void retriveRequests() {
        DatabaseReference requestsReference = databaseReference.child("requests");
        Query requestSearch = requestsReference.orderByChild("status").equalTo(Request.STATUS_WAITING);
        requestSearch.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getChildrenCount() > 0) {
                    textViewRequests.setVisibility(View.GONE);
                    recyclerViewRequests.setVisibility(View.VISIBLE);
                } else {
                    textViewRequests.setVisibility(View.VISIBLE);
                    recyclerViewRequests.setVisibility(View.GONE);
                }
                requestsList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Request request = data.getValue(Request.class);
                    requestsList.add(request);
                }
                requestsAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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

    private void recoverUserLocation() {

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                String latitude = String.valueOf(location.getLatitude());
                String longitude = String.valueOf(location.getLongitude());

                if (!latitude.isEmpty() && !longitude.isEmpty()) {
                    driver.setLatitude(latitude);
                    driver.setLongitude(longitude);
                    locationManager.removeUpdates(locationListener);
                    requestsAdpter.notifyDataSetChanged();

                }

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

        };

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        ) {

            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    0,
                    0,
                    locationListener
            );
        }
    }

    private void components() {

        setSupportActionBar(binding.toolbar);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            actionBar.setTitle(R.string.requests);
        }

        driver = UserFirebase.getUserDataLogged();
        auth = FirebaseConfiguration.getFirebaseAuth();
        databaseReference = FirebaseConfiguration.getFirebaseDatabase();

        recyclerViewRequests = binding.recyclerViewPassegersRequests;
        textViewRequests = binding.textViewRequestMessenge;
        requestsAdpter = new RequestsAdpter(requestsList, getApplicationContext(), driver);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewRequests.setLayoutManager(layoutManager);
        recyclerViewRequests.setHasFixedSize(true);
        recyclerViewRequests.setAdapter(requestsAdpter);

        recyclerViewRequests.addOnItemTouchListener(new RecyclerItemClickListener(
                getApplicationContext(),
                recyclerViewRequests,
                new RecyclerItemClickListener.OnItemClickListener() {

                    @Override
                    public void onItemClick(View view, int position) {
                        Request request = requestsList.get(position);
                        Intent intent = new Intent(RequestsActivity.this, DrivingActivity.class);
                        intent.putExtra("idRequest", request.getId());
                        intent.putExtra("driver",  driver);
                        startActivity(intent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {

                    }
                }
            )

        );
    }
}