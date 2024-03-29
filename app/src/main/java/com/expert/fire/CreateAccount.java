package com.expert.fire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Users;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class CreateAccount extends AppCompatActivity {

    TextView alreadyHaveAccount;
    EditText inputEmail, inputPassword, inputConfirmPassword;
    Button btnSignUp;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressDialog progressDialog;

    FirebaseAuth mAuth;
    FirebaseUser mUser;
    Boolean isLangEng = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        alreadyHaveAccount = findViewById(R.id.alreadyHaveAccount);
        isLangEng = new LanguagePref(CreateAccount.this).getIsEng();

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.inputPassword);
        inputConfirmPassword = findViewById(R.id.inputConfirmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        progressDialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (!isLangEng) {
            inputConfirmPassword.setHint("Konpirmahin Ang Password");
            alreadyHaveAccount.setText("Mayroon nang account?");
        }

        alreadyHaveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CreateAccount.this, MainActivity.class));
            }
        });


        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerformAuth();
            }
        });
    }

    private void PerformAuth() {
        String email = inputEmail.getText().toString();
        String password = inputPassword.getText().toString();
        String confirmPassword = inputConfirmPassword.getText().toString();

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

        } else if (!password.equals(confirmPassword)) {
            if (isLangEng) {
                inputPassword.setError("Passwords Don't Match");
            } else {
                inputConfirmPassword.setError("Hindi Tumutugma ang Password sa Parehong Field");
            }

        } else {
            if (isLangEng) {
                progressDialog.setMessage("Please Wait A While Registration Is In Progress....");
            } else {
                progressDialog.setMessage("Mangyaring Maghintay Habang Nagrerehistro....");
            }
            progressDialog.setTitle("Registration");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        Users users = new Users();
                        users.setPassword(password);
                        users.setEmail(email);
                        new UserPreferences(CreateAccount.this).saveLogin(users);
                        sendUSerToNextActivity();
                        if (isLangEng) {
                            Toast.makeText(CreateAccount.this, "Successfully Registered", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(CreateAccount.this, "Maayos ang pagkarehistro", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        Toast.makeText(CreateAccount.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }


    }

    private void sendUSerToNextActivity() {
        Intent intent = new Intent(CreateAccount.this, Pantry.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}