package com.expert.fire;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Users;
import com.expert.fire.Service.LocalFireAuth;
import com.google.android.material.textfield.TextInputEditText;

public class Security extends AppCompatActivity {
    TextInputEditText oldPass, newPass;
    Button changePassBtn;
    LocalFireAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        initViews();
        initListeners();
    }

    private void initListeners() {
        changePassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Users users = new UserPreferences(Security.this).getUsers();
                auth.updatePass(users.getEmail(), oldPass.getText().toString(), newPass.getText().toString());
            }
        });
    }

    private void initViews() {
        auth = new LocalFireAuth(new SimpleListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(Security.this, "Successfully Updated Password, Automatically Logging Out ...", Toast.LENGTH_SHORT).show();
                new UserPreferences(Security.this).saveLogin(new Users());
                new UserPreferences(Security.this).clearUsers();
                startActivity(new Intent(Security.this, MainActivity.class));
                finishAffinity();
                finish();
            }

            @Override
            public void onFail() {
                Toast.makeText(Security.this, "Failed To Update Password, Try Again Later.", Toast.LENGTH_SHORT).show();
            }
        });
        oldPass = findViewById(R.id.cPasswordEt);
        newPass = findViewById(R.id.nPasswordEt);
        changePassBtn = findViewById(R.id.changePassBtn);
    }
}