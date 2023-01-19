package com.expert.fire.Service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import androidx.annotation.NonNull;

import com.expert.fire.Common.Constants;
import com.expert.fire.Interfaces.SimpleListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class LocalStorage {
    Context mContext;
    FirebaseStorage fs;

    public LocalStorage(Context mContext) {
        this.mContext = mContext;
        this.fs = FirebaseStorage.getInstance(Constants.storageBucket);
    }


    public void uploadImage(Bitmap bmp, String imageName, SimpleListener listener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference storageRef = fs.getReference().child("images/" + imageName);

        UploadTask uploadTask = storageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e("ERR_UPLOAD_PIC", e.getMessage().toString());
                listener.onError(e);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Log.e("UPLOAD_PIC", "Successfully upload");
                listener.onSuccess();
            }
        });
    }

}
