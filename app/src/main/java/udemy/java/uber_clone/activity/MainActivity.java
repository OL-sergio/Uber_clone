package udemy.java.uber_clone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityMainBinding;
import udemy.java.uber_clone.helpers.Permissions;
import udemy.java.uber_clone.config.UserFirebase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button buttonSignIn;
    private Button buttonRegister;


    private final String[] permissions = new String[]{
            android.Manifest.permission.ACCESS_FINE_LOCATION

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Objects.requireNonNull(getSupportActionBar()).hide();

        //firebaseConfiguration = FirebaseConfiguration.getFirebaseAuth();
        //firebaseConfiguration.signOut();

        Permissions.validatePermissions(permissions, this, 1);

        ImageView imageView = findViewById(R.id.image_logo);
        Bitmap originalBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.img_logo_);

        // Set your desired width and height
        int newWidth = 200; // Desired width in pixels
        int newHeight = 100; // Desired height in pixels

        // Create a resized bitmap
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(originalBitmap, newWidth, newHeight, false);

        // Create a LayerDrawable
        LayerDrawable layerDrawable = new LayerDrawable(new Drawable[]{

                new ColorDrawable(Color.WHITE), // Solid color background
                new BitmapDrawable(getResources(), resizedBitmap),// Resized bitmap
        });

        // Set the LayerDrawable as the background
        imageView.setBackground(layerDrawable);


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


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        boolean validatePermission = true;
        for (int result : grantResults) {
            if ( result != PackageManager.PERMISSION_GRANTED ) {
                validatePermission = false;
                break;
            }
        }

        if (!validatePermission) {
            alertValidatePermission();
        }
    }

    private void alertValidatePermission() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Negadas");
        builder.setMessage("Para utilizar o aplicação é necessário aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = FirebaseConfiguration.getFirebaseAuth().getCurrentUser();
        if (user != null){
            UserFirebase.redirectUserLoggedIn(this);
            finish();
        }

    }
}