package udemy.java.uber_clone.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import udemy.java.uber_clone.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button buttonSignIn;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        buttonRegister = binding.buttonRegister;
        buttonSignIn = binding.buttonSignIn;

        buttonRegister.setOnClickListener(v -> {
            // Open Register Activity
            startActivity(new Intent(this, RegisterActivity.class));
        });

        buttonSignIn.setOnClickListener(v -> {
            // Open Sign In Activity
            startActivity(new Intent(this, LoginActivity.class));
        });
    }
}