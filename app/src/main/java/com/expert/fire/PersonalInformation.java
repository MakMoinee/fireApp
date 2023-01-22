package com.expert.fire;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Interfaces.LocalFirestoreCallback;
import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.LocalPreference.PersonalInfoPref;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.PersonalInfo;
import com.expert.fire.Models.Users;
import com.expert.fire.Service.LocalFireStore;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PersonalInformation extends AppCompatActivity {

    TextInputEditText editName, editEmail, editPhoneNumber;
    Button btnUpdate;
    LocalFireStore fs;
    String tmpPass = "";
    ProgressDialog dialog;
    String docID = "";
    boolean isLangEng = false;
    TextView lblPersonalInfo;
    TextInputLayout layoutName, layoutEmail, layoutNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_information);
        initViews();
        initListeners();
    }

    private void initListeners() {
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editName.getText().toString().equals("") || editEmail.getText().toString().equals("") || editPhoneNumber.getText().toString().equals("")) {
                    if (isLangEng) {
                        Toast.makeText(PersonalInformation.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(PersonalInformation.this, "Mangyaring Huwag Mag-iwan ng Walang laman na mga Fields", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    dialog.show();
                    PersonalInfo info = new PersonalInfo();
                    if (docID != "") {
                        info.setDocID(docID);
                    }

                    info.setName(editName.getText().toString());
                    info.setEmail(editEmail.getText().toString());
                    info.setPhoneNum(editPhoneNumber.getText().toString());
                    info.setPassword(tmpPass);


                    fs.addPersonalInfo(info, new LocalFirestoreCallback() {
                        @Override
                        public void onSuccess() {

                            fs.getPersonalInfo(info.getEmail(), new LocalFirestoreCallback() {

                                @Override
                                public void onSuccess(PersonalInfo infos) {
                                    dialog.dismiss();
                                    new PersonalInfoPref(PersonalInformation.this).storeInfo(infos);
                                    if (isLangEng) {
                                        Toast.makeText(PersonalInformation.this, "Successfully update personal information", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PersonalInformation.this, "Matagumpay na Na-update ang Personal na Impormasyon", Toast.LENGTH_SHORT).show();
                                    }

                                    finish();
                                }

                                @Override
                                public void onError(Exception e) {
                                    dialog.dismiss();
                                    Log.e("ERROR_ADDING_PERSONAL_INFO", e.getMessage());
                                    if (isLangEng) {
                                        Toast.makeText(PersonalInformation.this, "Failed to update personal information, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(PersonalInformation.this, "Nabigong magdagdag ng personal na impormasyon, Pakisubukang Muli Mamaya", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                        @Override
                        public void onError(Exception e) {
                            dialog.dismiss();
                            Log.e("ERROR_ADDING_PERSONAL_INFO", e.getMessage());
                            if (isLangEng) {
                                Toast.makeText(PersonalInformation.this, "Failed to update personal information, Please Try Again Later", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(PersonalInformation.this, "Nabigong magdagdag ng personal na impormasyon, Pakisubukang Muli Mamaya", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });
    }

    private void initViews() {
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        btnUpdate = findViewById(R.id.btnUpdate);
        editPhoneNumber = findViewById(R.id.editPhoneNumber);
        layoutEmail = findViewById(R.id.layoutEmail2);
        layoutName = findViewById(R.id.layoutName);
        layoutNumber = findViewById(R.id.layoutNumber);
        lblPersonalInfo = findViewById(R.id.lblPersonalInfo);
        isLangEng = new LanguagePref(PersonalInformation.this).getIsEng();
        dialog = new ProgressDialog(PersonalInformation.this);
        if (isLangEng) {
            layoutName.setHint("Name");
            layoutNumber.setHint("Phone Number");
            lblPersonalInfo.setText("Personal Information");
            dialog.setMessage("Sending Request ...");
        } else {
            dialog.setMessage("Nagpapadala ng Kahilingan ...");
        }

        dialog.setCancelable(false);

        Users users = new UserPreferences(PersonalInformation.this).getUsers();
        tmpPass = users.getPassword();
        editEmail.setText(users.getEmail());

        fs = new LocalFireStore(PersonalInformation.this);

        PersonalInfo info = new PersonalInfoPref(PersonalInformation.this).getInfo();
        if (info != null) {
            editEmail.setText(info.getEmail());
            editName.setText(info.getName());
            editPhoneNumber.setText(info.getPhoneNum());
            docID = info.getDocID();
        } else {
            if (isLangEng) {
                dialog.setMessage("Loading Infos ...");
            } else {
                dialog.setMessage("Naglo-load ng Infos ...");
            }
            dialog.show();
            loadInfoData(users.getEmail());
        }

    }

    private void loadInfoData(String email) {
        fs.getPersonalInfo(email, new LocalFirestoreCallback() {

            @Override
            public void onSuccess(PersonalInfo infos) {
                dialog.dismiss();
                if (isLangEng) {
                    dialog.setMessage("Sending Request ...");
                } else {
                    dialog.setMessage("Nagpapadala ng Kahilingan ...");
                }
                new PersonalInfoPref(PersonalInformation.this).storeInfo(infos);
                editEmail.setText(infos.getEmail());
                editName.setText(infos.getName());
                editPhoneNumber.setText(infos.getPhoneNum());
                docID = infos.getDocID();
            }

            @Override
            public void onError(Exception e) {
                dialog.dismiss();
                if (isLangEng) {
                    dialog.setMessage("Sending Request ...");
                } else {
                    dialog.setMessage("Nagpapadala ng Kahilingan ...");
                }
                Users users = new UserPreferences(PersonalInformation.this).getUsers();
                tmpPass = users.getPassword();
                editEmail.setText(users.getEmail());
            }
        });
    }
}