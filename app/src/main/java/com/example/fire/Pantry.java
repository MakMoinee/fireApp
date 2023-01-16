package com.example.fire;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.fire.Interfaces.LocalFirestoreCallback;
import com.example.fire.LocalPreference.UserPreferences;
import com.example.fire.Models.Dishes;
import com.example.fire.Models.Users;
import com.example.fire.Service.LocalFireStore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class Pantry extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    EditText mTextTv;
    ImageButton mVoiceBtn, btnUser;
    private Button btnLogout, btnGenerate;
    private RelativeLayout relGenerate;
    private AlertDialog linearUserDialog;
    String[] siteInfo;
    LocalFireStore db;
    ProgressDialog pdLoading;
    boolean cancelled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pantry);
        initViews();
        initListeners();
    }

    private void initListeners() {
        mVoiceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                speak();
            }
        });

        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pantry.this,User.class));
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextTv.getText().toString().equals("")) {
                    Toast.makeText(Pantry.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
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
                                Toast.makeText(Pantry.this, "Ingredient Not Found", Toast.LENGTH_SHORT).show();
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
        pdLoading.setMessage("Sending Request ...");
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Hi Speak your Ingredients");

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
}
