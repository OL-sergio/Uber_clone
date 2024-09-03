package udemy.java.uber_clone.activity;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.auth.User;

import java.util.Objects;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityLoginBinding;
import udemy.java.uber_clone.databinding.ActivityMainBinding;
import udemy.java.uber_clone.databinding.ActivityRegisterBinding;
import udemy.java.uber_clone.model.Users;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText password;
    private Switch switchUserType;
    private Button buttonRegister;

    private FirebaseAuth auth;
    @SuppressLint("RestrictedApi")

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            //actionBar.setTitle(R.string.criar_uma_conta);
        }

        components();

        buttonRegister.setOnClickListener(view -> {
                    validateUserRegister(view);
                }
        );

    }

    private void validateUserRegister(View view) {
        String textName = Objects.requireNonNull(name.getText()).toString();
        String textEmail = Objects.requireNonNull(email.getText()).toString();
        String textPassword = Objects.requireNonNull(password.getText()).toString();
        if ( !textName.isEmpty() ){
            if ( !textEmail.isEmpty() ){
                if ( !textPassword.isEmpty() ){

                    Users users = new Users();
                    users.setName(textName);
                    users.setEmail(textEmail);
                    users.setPassword(textPassword);
                    users.setUserType(getUserType());
                        
                    createUser(users);

                }else {
                    Toast.makeText(this, "Interduza uma password!", Toast.LENGTH_SHORT).show();
                }
            }else {
                Toast.makeText(this, "Interduza um Email!", Toast.LENGTH_SHORT).show();
            }
        }else {
            Toast.makeText(this, "Interduza um nome!", Toast.LENGTH_SHORT).show();
        }
    }

    private void createUser(Users users) {

            auth = FirebaseConfiguration.getFirebaseAuth();
            auth.createUserWithEmailAndPassword(
                                    users.getEmail(),
                                    users.getPassword()
                            ) .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()){

                            FirebaseUser userId = auth.getCurrentUser();
                            assert userId != null;
                            users.setId(userId.getUid());
                            users.saveUser();

                        Toast.makeText(RegisterActivity.this, "Utilizador criado com sucesso!", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(RegisterActivity.this, "Erro ao criar utilizador!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }


    public String getUserType(){

        //return switchUserType.isChecked() ? "true" : "false";
        //D = driver - P = passenger
        return switchUserType.isChecked() ? "D" : "P";

    }


    private void components() {

            name = binding.textInputEditTextRegisterName;
            email = binding.textInputEditTextRegisterEmail;
            password = binding.textInputEditTextRegisterPassword;
            switchUserType = binding.switchSelectTypeUser;
            buttonRegister = binding.buttonRegisterUser;


    }

}