package udemy.java.uber_clone.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityMainBinding;
import udemy.java.uber_clone.helpers.Permissions;
import udemy.java.uber_clone.config.UserFirebase;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    private Button buttonSignIn;
    private Button buttonRegister;

   // FirebaseAuth firebaseConfiguration ;


    private  String[] permissions = new String[]{
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
            UserFirebase.rediretUserLoogedIn(this);
            finish();
        }

    }
}