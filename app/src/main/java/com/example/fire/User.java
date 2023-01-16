package com.example.fire;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.fire.LocalPreference.UserPreferences;
import com.example.fire.Models.Users;
import com.google.firebase.auth.FirebaseAuth;

public class User extends AppCompatActivity {

    ImageButton btnBck, btnPersonalInfo, btnLanguage, btnSecurity;
    Button btnLogout;
    private AlertDialog linearUserDialog;
    FirebaseAuth mAuth;
    ProgressDialog pdLoading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        btnLogout = findViewById(R.id.btnLogout);
        btnBck = findViewById(R.id.btnBck);
        btnPersonalInfo = findViewById(R.id.btnPersonalInfo);
        btnLanguage = findViewById(R.id.btnLanguage);
        btnSecurity = findViewById(R.id.btnSecurity);
        mAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(User.this);
                initDialogListeners();
                linearUserDialog = mBuilder.create();
                linearUserDialog.show();
            }
        });

        btnPersonalInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, PersonalInformation.class));
            }
        });

        btnLanguage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, Language.class));
            }
        });

        btnSecurity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, Security.class));
            }
        });

        btnBck.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(User.this, Pantry.class));
            }
        }));
    }
    private void initDialogListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lBuilder = new AlertDialog.Builder(User.this);
                DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                linearUserDialog.dismiss();
                                new UserPreferences(User.this).saveLogin(new Users());
                                startActivity(new Intent(User.this, MainActivity.class));
                                finish();

                                break;
                            default:
                                dialog.cancel();
                        }
                    }
                };
                lBuilder.setMessage("Are You Sure You Want To Logout?")
                        .setNegativeButton("Yes", dListener)
                        .setPositiveButton("No", dListener)
                        .show();
            }
        });
    }
}