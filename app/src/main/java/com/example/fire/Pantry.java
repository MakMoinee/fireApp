package com.example.fire;

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

import com.example.fire.LocalPreference.UserPreferences;
import com.example.fire.Models.Users;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class Pantry extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    EditText mTextTv;
    ImageButton mVoiceBtn;
    private Button btnLogout, btnGenerate;
    private RelativeLayout relGenerate;
    private AlertDialog linearUserDialog;
    private ImageButton imgUser;
    String[] siteInfo;

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

        imgUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Pantry.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_user, null, false);
                mBuilder.setView(mView);
                initDialogView(mView);
                initDialogListeners();
                linearUserDialog = mBuilder.create();
                linearUserDialog.show();
            }
        });

        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextTv.getText().toString().equals("")) {
                    Toast.makeText(Pantry.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("INFOS", mTextTv.getText().toString());
                    List<String> infoList = Arrays.asList(siteInfo);
                    if (infoList.indexOf(mTextTv.getText().toString()) > 0) {
                        int index = Arrays.asList(siteInfo).indexOf(mTextTv.getText().toString());
                        Log.e("INDEXX", Integer.toString(index));
                    } else {
                        Log.e("INFOS", siteInfo.toString());
                    }
                }
            }
        });
    }

    private void initDialogListeners() {
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder lBuilder = new AlertDialog.Builder(Pantry.this);
                DialogInterface.OnClickListener dListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_NEGATIVE:
                                new UserPreferences(Pantry.this).saveLogin(new Users());
                                startActivity(new Intent(Pantry.this, MainActivity.class));
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

    private void initDialogView(View mView) {
        btnLogout = mView.findViewById(R.id.btnLogout);
    }

    private void initViews() {
        mTextTv = findViewById(R.id.textTv);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        btnGenerate = findViewById(R.id.btnGenerate);
        imgUser = findViewById(R.id.imgUser);
        siteInfo = getResources().getStringArray(R.array.siteInfo);
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
