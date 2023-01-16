package com.example.fire.Service;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fire.Interfaces.SimpleListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LocalFireAuth {
    FirebaseUser user;
    SimpleListener listener;

    public LocalFireAuth(SimpleListener l) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        this.listener = l;
    }

    public void updatePass(String email, String oldPass, String newPass) {
        AuthCredential credential = EmailAuthProvider.getCredential(email, oldPass);
        user.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                listener.onSuccess();
                            } else {
                                listener.onFail();
                            }
                        }
                    });
                }else{
                    listener.onFail();
                }
            }
        });
    }
}
