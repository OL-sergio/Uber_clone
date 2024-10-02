package udemy.java.uber_clone.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.adapter.RequestsAdapter;
import udemy.java.uber_clone.config.FirebaseConfiguration;
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
    private RequestsAdapter requestsAdapter;
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

    private void addEventClickRecyclerView() {



        recyclerViewRequests.addOnItemTouchListener(new RecyclerItemClickListener(
                        getApplicationContext(),
                        recyclerViewRequests,
                        new RecyclerItemClickListener.OnItemClickListener() {

                            @Override
                            public void onItemClick(View view, int position) {

                                Request request = requestsList.get(position);
                                changeActivity(request.getId(), driver, false );

                            }

                            @Override
                            public void onLongItemClick(View view, int position) {

                            }
                        }
                )
        );
    }

    private void verifyStatusRequest() {

        Users userLogged = UserFirebase.getUserDataLogged();
        DatabaseReference requestReference = databaseReference.child("requests");

        Query requestSearch = requestReference.orderByChild("driver/id")
                .equalTo(userLogged.getId());

        requestSearch.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot data : snapshot.getChildren()) {
                    Request request = data.getValue(Request.class);

                    assert request != null;
                    if (request.getStatus().equals(Request.STATUS_ON_MY_AWAY)
                            || request.getStatus().equals(Request.STATUS_START_TRIP)
                            || request.getStatus().equals(Request.STATUS_FINALISED)
                    ) {
                        driver = request.getDriver();
                        changeActivity( request.getId(), driver, true);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void changeActivity(String idRequest, Users driver, Boolean requestAccepted) {

        Intent intent = new Intent(RequestsActivity.this, DrivingActivity.class);
        intent.putExtra("idRequest", idRequest);
        intent.putExtra("driver", driver);
        intent.putExtra("requestAccepted", requestAccepted);
        startActivity(intent);

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
                requestsAdapter.notifyDataSetChanged();
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

                UserFirebase.updatedDataLocation(
                        location.getLatitude(),
                        location.getLongitude()
                );

                if (!latitude.isEmpty() && !longitude.isEmpty()) {

                    driver.setLatitude(latitude);
                    driver.setLongitude(longitude);

                    addEventClickRecyclerView();
                    locationManager.removeUpdates(locationListener);
                    requestsAdapter.notifyDataSetChanged();

                }

            }

            @Override
            public void onProviderEnabled(@NonNull String provider) {
                LocationListener.super.onProviderEnabled(provider);
            }

        };

        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION  )
                == PackageManager.PERMISSION_GRANTED
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

        requestsAdapter = new RequestsAdapter(requestsList, getApplicationContext(), driver);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());

        recyclerViewRequests.setLayoutManager(layoutManager);
        recyclerViewRequests.setHasFixedSize(true);
        recyclerViewRequests.setAdapter(requestsAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        verifyStatusRequest();
    }
}