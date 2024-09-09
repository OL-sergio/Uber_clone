package udemy.java.uber_clone.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityLoginBinding;
import udemy.java.uber_clone.helpers.UserFirebase;
import udemy.java.uber_clone.model.Users;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    private TextInputEditText editTextEmail;
    private TextInputEditText editTextPassword;
    private Button buttonLogin;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setBackgroundDrawable(ContextCompat.getDrawable(this, R.color.limedSprude_100));
            //actionBar.setTitle(R.string.aceder_a_minha_conta);
        }

        components();

        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validateUserRegister(view);

                }
            }
        );
    }

    private void validateUserRegister(View view) {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        if (!email.isEmpty()) {
            if (!password.isEmpty()) {

                Users user = new Users();
                user.setEmail(email);
                user.setPassword(password);
                loginUser(email, password);

            } else {
                editTextPassword.setError("Preencha a password");
            }
        } else {
            editTextEmail.setError("Preencha o email");
        }
    }

    private void loginUser(String email, String password) {
        auth = FirebaseConfiguration.getFirebaseAuth();
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if ( task.isSuccessful() ){

                            //Verificar o tipo de utilizador
                            // Motorista / Passageiro
                            UserFirebase.rediretUserLoogedIn(LoginActivity.this);

                            Toast.makeText(LoginActivity.this, R.string.login_realizado_com_sucesso, Toast.LENGTH_LONG).show();
                        }else {
                            String exceptionError;
                            try {
                                throw Objects.requireNonNull(task.getException());
                            } catch (FirebaseAuthInvalidUserException e){
                                exceptionError = getString(R.string.utilizador_n_o_registado);
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                exceptionError = getString(R.string.email_ou_password_incorretos);
                            }  catch (Exception e) {
                                exceptionError = getString(R.string.erro_realizar_login_utilizador) + e.getMessage();
                                e.printStackTrace();
                            }

                            Toast.makeText(LoginActivity.this, exceptionError, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
    private void components() {

        editTextEmail = binding.textInputEditTextLoginEmail;
        editTextPassword = binding.textInputEditTextLoginPassword;
        buttonLogin = binding.buttonLogin;

    }
}