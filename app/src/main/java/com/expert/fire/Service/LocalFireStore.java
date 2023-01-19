package com.expert.fire.Service;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.expert.fire.Common.Common;
import com.expert.fire.Interfaces.LocalFirestoreCallback;
import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.PersonalInfo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class LocalFireStore {
    Context mContext;
    FirebaseFirestore db;

    public LocalFireStore(Context c) {
        this.mContext = c;
        db = FirebaseFirestore.getInstance();
    }

    public void searchIngredient(String searchKey, LocalFirestoreCallback callback) {
        Log.e("TRIGGERED", "YES");
        try {
            CollectionReference ref = db.collection("dishes");
            ref.whereArrayContainsAny("ingredients", Arrays.asList(searchKey.split(",")))
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (queryDocumentSnapshots.isEmpty()) {
                                callback.onError(new Exception("empty"));
                            } else {
                                List<Dishes> dishesList = new ArrayList<>();
                                for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                    if (documentSnapshot.exists()) {
//                                        Log.e("SUCCESS", "onSuccess: DOCUMENT" + documentSnapshot.getId() + " ; " + documentSnapshot.getData());
//                                        Log.e("HEY",documentSnapshot.getData().get("dish").toString());
//                                        String dishKey =documentSnapshot.getData().get("dish").toString();
//                                        if (dishKey.equals("ampalaya-chicken-feet")) {
//                                            Log.e("FEET", "YES");
//                                        }
                                        Dishes dishes = documentSnapshot.toObject(Dishes.class);
//                                        if (dishKey.equals("ampalaya-chicken-feet")) {
//                                            Log.e("FEETdishes", dishes.toString());
//                                            dishesList.add(dishes);
//                                        }
                                        if (dishes.toString() != null) {
                                            dishesList.add(dishes);
                                        }

                                    } else {
                                        callback.onError(new Exception("empty"));
                                    }
                                }
                                callback.onSuccess(dishesList);

                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError(e);
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onError(e);
        }

    }

    public void addIngredient(Dishes dishes, LocalFirestoreCallback callback) {
        try {
            Map<String, Object> map = Common.convertDishToMap(dishes);
            db.collection("dishes")
                    .document()
                    .set(map)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError(e);
                        }
                    });
        } catch (Exception e) {
            callback.onError(e);
        }
    }


    public void addPersonalInfo(PersonalInfo info, LocalFirestoreCallback callback) {
        Map<String, Object> finalMap = Common.convertPersonalInfoToMap(info);
        if (info.getDocID() == null) {
            db.collection("personalInfos")
                    .document()
                    .set(finalMap)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError(e);
                        }
                    });
        } else {
            db.collection("personalInfos")
                    .document(info.getDocID())
                    .set(finalMap, SetOptions.merge())
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            callback.onSuccess();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            callback.onError(e);
                        }
                    });
        }

    }

    public void getPersonalInfo(String email, LocalFirestoreCallback callback) {
        db.collection("personalInfos")
                .whereEqualTo("email", email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (queryDocumentSnapshots.isEmpty()) {
                            callback.onError(new Exception("Wrong Username or Password"));
                        } else {
                            for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                if (documentSnapshot.exists()) {
                                    Log.e("SUCCESS", "onSuccess: DOCUMENT" + documentSnapshot.getId() + " ; " + documentSnapshot.getData());

                                    PersonalInfo info = documentSnapshot.toObject(PersonalInfo.class);
                                    info.setDocID(documentSnapshot.getId());
                                    callback.onSuccess(info);
                                } else {
                                    callback.onError(new Exception("Wrong Username or Password"));
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

}
