package com.expert.fire;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Users;
import com.google.firebase.auth.FirebaseAuth;

public class User extends AppCompatActivity {

    ImageButton btnBck, btnPersonalInfo, btnLanguage, btnSecurity;
    Button btnLogout;
    private AlertDialog linearUserDialog;
    FirebaseAuth mAuth;
    RelativeLayout relSecurity, relPerson;
    ProgressDialog pdLoading;
    Users nullUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);
        btnLogout = findViewById(R.id.btnLogout);
        btnBck = findViewById(R.id.btnBck);
        btnPersonalInfo = findViewById(R.id.btnPersonalInfo);
        btnLanguage = findViewById(R.id.btnLanguage);
        btnSecurity = findViewById(R.id.btnSecurity);
        relSecurity = findViewById(R.id.relSecurity);
        relPerson = findViewById(R.id.relPerson);
        mAuth = FirebaseAuth.getInstance();

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lBuilder = new AlertDialog.Builder(User.this);
                DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                new UserPreferences(User.this).saveLogin(new Users());
                                startActivity(new Intent(User.this, MainActivity.class));
                                finish();
                                dialog.dismiss();
                                break;
                            default:
                                dialog.cancel();
                        }
                    }
                };
                lBuilder.setMessage("Sigurado Ka bang Gusto Mong Mag-logout?")
                        .setNegativeButton("Yes", dListener)
                        .setPositiveButton("No", dListener)
                        .show();
            }
        });

        relPerson.setOnClickListener(new View.OnClickListener() {
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


        relSecurity.setOnClickListener(new View.OnClickListener() {
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

    private void initDialogViews(View mView) {
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
                                new UserPreferences(User.this).clearUsers();
                                startActivity(new Intent(User.this, MainActivity.class));
                                finish();

                                break;
                            default:
                                dialog.cancel();
                        }
                    }
                };
                lBuilder.setMessage("Sigurado Ka bang Gusto Mong Mag-logout?")
                        .setNegativeButton("Yes", dListener)
                        .setPositiveButton("No", dListener)
                        .show();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        Users users = new UserPreferences(User.this).getUsers();
        if (users == null) {
            finish();
        }
    }
}