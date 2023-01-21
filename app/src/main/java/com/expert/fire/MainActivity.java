package com.expert.fire;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    TextView createNewAccount, forgotPassword;

    EditText inputEmail, inputPassword, inputConfirmPassword;
    Button btnLogin;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;
    Boolean isLangEng = false;

    FirebaseAuth mAuth;
    FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Users users = new UserPreferences(MainActivity.this).getUsers();
        if (users != null) {
            if (users.getEmail() != "") {
                Log.e("users", users.toString());
                sendUSerToNextActivity();
            }


        }
        setContentView(R.layout.activity_main);
        createNewAccount = findViewById(R.id.createNewAccount);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        btnLogin = findViewById(R.id.btnLogin);
        forgotPassword = findViewById(R.id.forgotPassword);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        Boolean isEng = new LanguagePref(MainActivity.this).getIsEng();
        isLangEng = isEng;
        if (!isEng) {
            btnLogin.setText("Login");
            inputEmail.setHint("Email");
            inputPassword.setHint("Password");
            forgotPassword.setText("Nakalimutan ang password?");
            createNewAccount.setText("Lumikha ng bagong account?");
        }

        createNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CreateAccount.class));
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ForgotPassword.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogIn();

            }
        });
    }

    private void performLogIn() {

        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();

        if (!email.matches(emailPattern)) {
            if (isLangEng) {
                inputEmail.setError("Enter Correct Email");
            } else {
                inputEmail.setError("Ipasok ang Tamang Email");
            }
        } else if (password.isEmpty() || password.length() < 10) {
            if (isLangEng) {
                inputPassword.setError("Enter Correct Password");
            } else {
                inputPassword.setError("Ipasok ang Wastong Password");
            }
        } else {
            if (isLangEng) {
                progressDialog.setMessage("Please Wait While Logging in....");
            } else {
                progressDialog.setMessage("Mangyaring Maghintay Habang Nag-log in....");
            }
            progressDialog.setTitle("Login");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Users users = new Users();
                        users.setPassword(inputPassword.getText().toString());
                        users.setEmail(inputEmail.getText().toString());
                        new UserPreferences(MainActivity.this).saveLogin(users);
                        new LanguagePref(MainActivity.this).storeIsEng(true);
                        sendUSerToNextActivity();
                        if (isLangEng) {
                            Toast.makeText(MainActivity.this, "Login Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Matagumpay na Mag-login", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(MainActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

    }

    private void sendUSerToNextActivity() {
        Intent intent = new Intent(MainActivity.this, Pantry.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}