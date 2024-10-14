package udemy.java.uber_clone.activity;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;


import java.util.Objects;

import udemy.java.uber_clone.R;
import udemy.java.uber_clone.config.FirebaseConfiguration;
import udemy.java.uber_clone.databinding.ActivityRegisterBinding;
import udemy.java.uber_clone.config.UserFirebase;
import udemy.java.uber_clone.model.Users;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private TextInputEditText name;
    private TextInputEditText email;
    private TextInputEditText password;
    private Switch switchUserType;
    private Button buttonRegister;

    private FirebaseAuth auth;

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
                        try {

                            FirebaseUser userId = auth.getCurrentUser();
                            assert userId != null;
                            users.setId(userId.getUid());
                            users.saveUser();

                            UserFirebase.updateUserName(users.getName());

                            if (getUserType() == "P" ){
                                startActivity(new Intent(RegisterActivity.this, PassengerActivity.class));
                                finish();
                                Toast.makeText(RegisterActivity.this, R.string.utilizador_passageiro_criado_com_sucesso, Toast.LENGTH_SHORT).show();
                            }else {
                                startActivity(new Intent(RegisterActivity.this, RequestsActivity.class));
                                finish();
                            }

                        } catch (Exception e) {
                            e.printStackTrace();

                            Toast.makeText(RegisterActivity.this, "Utilizadir Motorista criado com sucesso!", Toast.LENGTH_SHORT).show();
                        }

                    }else {

                        String exceptionError;

                        try {
                            throw Objects.requireNonNull(task.getException());
                        } catch (FirebaseAuthWeakPasswordException e){
                            exceptionError = getString(R.string.intreduza_uma_senha_mais_forte);
                        } catch (FirebaseAuthInvalidCredentialsException e) {
                            exceptionError = getString(R.string.intreduza_um_email_v_lido);
                        } catch (FirebaseAuthUserCollisionException e){
                            exceptionError = getString(R.string.este_email_j_est_em_uso);
                        }catch (Exception e) {
                            exceptionError = getString(R.string.erro_ao_criar_utilizador) + e.getMessage();
                            e.printStackTrace();
                        }

                        Toast.makeText(RegisterActivity.this, exceptionError, Toast.LENGTH_SHORT).show();
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