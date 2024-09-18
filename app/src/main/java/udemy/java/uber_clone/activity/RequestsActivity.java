package udemy.java.uber_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.adpter.RequestsAdpter;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityPassengerBinding;
import udemy.java.uber_clone.databinding.ActivityRequestsBinding;
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRequestsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        components();

        retriveRequests();

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


    }
}