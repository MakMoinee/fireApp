package com.expert.fire;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Interfaces.LocalFirestoreCallback;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.Users;
import com.expert.fire.Service.LocalFireStore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Pantry extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    EditText mTextTv;
    ImageButton mVoiceBtn, btnUser, btnFavorites, btnUpload;
    private Button btnLogout, btnGenerate;
    private RelativeLayout relGenerate;
    private AlertDialog linearUserDialog;
    String[] siteInfo;
    LocalFireStore db;
    ProgressDialog pdLoading;
    boolean cancelled = false;
    private TextView lblIngredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        initViews();
        initListeners();
    }


    private void initListeners() {
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Pantry.this, Favorites.class);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Pantry.this).toBundle());
            }
        });
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pantry.this, User.class));
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pantry.this, Upload.class));
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextTv.getText().toString().equals("")) {
                    Toast.makeText(Pantry.this, "Mangyaring Huwag Mag-iwan ng Walang laman na mga Fields", Toast.LENGTH_SHORT).show();
                } else {
                    pdLoading.show();
                    String searchKey = mTextTv.getText().toString();
                    String[] searchArr = searchKey.split(",");
                    db.searchIngredient(searchKey, new LocalFirestoreCallback() {
                        @Override
                        public void onSuccess(List<Dishes> dishList) {
                            Dishes filterDish = new Dishes();
                            int validCount = 0;
                            if (dishList != null) {
                                for (Dishes d : dishList) {
                                    validCount = new Integer(0);
                                    Set<String> newSet = new HashSet<>(d.getIngredients());
                                    for (int j = 0; j < newSet.toArray().length; j++) {
                                        String str = newSet.toArray()[j].toString();
                                        for (int i = 0; i < searchArr.length; i++) {
                                            String str2 = searchArr[i].toString();
                                            if (str2.equals(str)) {
                                                validCount++;
                                            }
                                            if (Integer.toString(validCount).equals(Integer.toString(searchArr.length))) {
                                                break;
                                            }

                                        }
                                        if (Integer.toString(validCount).equals(Integer.toString(searchArr.length))) {
                                            break;
                                        }
                                    }

                                    if (Integer.toString(validCount).equals(Integer.toString(searchArr.length))) {
                                        filterDish = d;
                                        break;
                                    }
                                }

                            }
                            pdLoading.hide();
                            if (validCount == searchArr.length) {
                                Intent intent = new Intent(Pantry.this, DishActivity.class);
                                intent.putExtra("dishRaw", new Gson().toJson(filterDish));
                                intent.putExtra("title", filterDish.getDish());
                                intent.putExtra("videoURL", filterDish.getVideoURL());
                                intent.putExtra("instructions", String.join("\n", filterDish.getInstructions()));
                                intent.putExtra("ingredients", String.join("\n", filterDish.getOrigIngredients()));
                                if (filterDish.getDescription().size() > 0) {
                                    intent.putExtra("description", String.join("\n", filterDish.getDescription()));
                                } else {
                                    intent.putExtra("description", "");
                                }


                                startActivity(intent);
                                mTextTv.setText("");
                            } else {
                                Toast.makeText(Pantry.this, "Hindi Natagpuan ang sangkap", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                        }
                    });
//                    List<String> infoList = Arrays.asList(siteInfo);
//                    Log.e("INFOS", Integer.toString(infoList.size()));
//                    if (infoList.indexOf(mTextTv.getText().toString()) > 0) {
//                        int index = Arrays.asList(siteInfo).indexOf(mTextTv.getText().toString());
//                        Intent intent = new Intent(Pantry.this, DishActivity.class);
//                        intent.putExtra("title", infoList.get(index));
//                        startActivity(intent);
//                        mTextTv.setText("");
//                    } else {
//                        List<String> choices = Common.getMap(infoList, mTextTv.getText().toString());
//                        Log.e("INFOS", choices.toString());
//                    }
                }
            }
        });
    }


    private void initDialogView(View mView) {
        btnLogout = mView.findViewById(R.id.btnLogout);
    }

    private void initViews() {
        mTextTv = findViewById(R.id.textTv);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnUser = findViewById(R.id.btnUser);
        siteInfo = getResources().getStringArray(R.array.siteInfo);
        db = new LocalFireStore(Pantry.this);
        pdLoading = new ProgressDialog(Pantry.this);
        pdLoading.setMessage("Nagpapadala ng Kahilingan ...");
        btnFavorites = findViewById(R.id.btnFavorites);
        btnUpload = findViewById(R.id.btnUpload);
        lblIngredients = findViewById(R.id.lblIngredients);
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Kumusta? Sabihin ang iyong mga sangkap");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "" + e, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    mTextTv.setText(result.get(0));
                }
                break;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Users users = new UserPreferences(Pantry.this).getUsers();
        if (users == null) {
            finish();
        }
    }
}
