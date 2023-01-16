package com.example.fire;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.fire.LocalPreference.UserPreferences;
import com.example.fire.Models.Users;
import com.google.android.material.textfield.TextInputEditText;

public class PersonalInformation extends AppCompatActivity {

    TextInputEditText editName, editEmail, editPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        initViews();
    }

    private void initViews() {
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        Users users = new UserPreferences(PersonalInformation.this).getUsers();
        editEmail.setText(users.getEmail());
    }
}