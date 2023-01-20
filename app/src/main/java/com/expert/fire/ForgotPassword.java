package com.expert.fire;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.expert.fire.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity {
    ActivityForgotPasswordBinding binding;
    String usernamePattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    TextView lblForgotPass;
    ProgressDialog dialog;
    FirebaseAuth mAuth;
    Boolean isLangEng = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        lblForgotPass = findViewById(R.id.lblForgotPass);
        if (isLangEng) {
            lblForgotPass.setText("Forgot Password");
            binding.btnForgotpass.setText("Change Password");
        }

        mAuth = FirebaseAuth.getInstance();

        dialog = new ProgressDialog(ForgotPassword.this);
        dialog.setCancelable(false);
        dialog.setMessage("Loading....");

        binding.btnForgotpass.setOnClickListener(view -> {
            forgotPassword();
        });
    }

    private Boolean validateEmail() {
        String val = binding.txtEmail.getText().toString();

        if (val.isEmpty()) {
            if (isLangEng) {
                binding.txtEmail.setError("This field can't be empty");
            } else {
                binding.txtEmail.setError("Hindi maaaring walang laman ang field na ito ");
            }
            return false;
        } else if (!val.matches(usernamePattern)) {
            if (isLangEng) {
                binding.txtEmail.setError("wrong email address");
            } else {
                binding.txtEmail.setError("maling email address");
            }
            return false;
        } else {
            binding.txtEmail.setError(null);
            return true;
        }
    }

    private void forgotPassword() {
        if (!validateEmail()) {
            return;
        }

        dialog.show();

        mAuth.sendPasswordResetEmail(binding.txtEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                dialog.dismiss();
                if (task.isSuccessful()) {
                    startActivity(new Intent(ForgotPassword.this, com.expert.fire.MainActivity.class));
                    finish();
                } else {
                    if (isLangEng) {
                        Toast.makeText(ForgotPassword.this, "Enter Correct Email", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ForgotPassword.this, "Ipasok ang Tamang Email", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ForgotPassword.this, e.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}