package com.expert.fire;

import android.app.ActivityOptions;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.expert.fire.Adapters.RecommendationsAdapter;
import com.expert.fire.Interfaces.LocalFirestoreCallback;
import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.LocalPreference.UserPreferences;
import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.Users;
import com.expert.fire.Service.LocalFireStore;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class Pantry extends AppCompatActivity {

    private static final int REQUEST_CODE_SPEECH_INPUT = 1000;
    EditText mTextTv;
    ImageButton mVoiceBtn, btnPantry, btnUser, btnFavorites, btnUpload;
    ListView lvRecommendations;
    TextView lblTopInst;
    private Button btnLogout, btnGenerate;
    private RelativeLayout relGenerate;
    private AlertDialog linearUserDialog;
    String[] siteInfo;
    LocalFireStore db;
    ProgressDialog pdLoading;
    boolean cancelled = false;
    private TextView lblIngredients, lblFavorites, lblUser;
    boolean isLangEng = false;

    List<Dishes> recommendList = new ArrayList<>();
    AlertDialog recommendDialog;
    RecommendationsAdapter rAdapter;

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
                finish();
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
                finish();
            }
        });
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Pantry.this, Upload.class));
                finish();
            }
        });


        btnGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mTextTv.getText().toString().equals("")) {
                    if (isLangEng) {
                        Toast.makeText(Pantry.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(Pantry.this, "Mangyaring Huwag Mag-iwan ng Walang laman na mga Fields", Toast.LENGTH_SHORT).show();

                    }
                } else {
                    pdLoading.show();
                    String searchKey = mTextTv.getText().toString();
                    String[] searchArr = searchKey.split(",");
                    db.searchIngredient(searchKey, new LocalFirestoreCallback() {
                        @Override
                        public void onSuccess(List<Dishes> dishList) {
                            Dishes filterDish = new Dishes();
                            List<Dishes> filterDishList = new ArrayList<>();
                            Map<String, Boolean> availMap = new HashMap<>();
                            int validCount = 0;
                            int resultCountNeeded = 2;
                            int cycles = 2;
                            if (dishList != null) {
                                while (resultCountNeeded != 0) {
                                    filterDish = null;
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
                                            if (!availMap.containsKey(d.getDish())) {
                                                filterDish = d;
                                                filterDishList.add(d);

                                                availMap.put(filterDish.getDish(), true);
                                            }
                                            resultCountNeeded--;
                                            break;
                                        }
                                    }
                                    cycles--;

                                    if (resultCountNeeded == 2) {
                                        resultCountNeeded = 0;
                                    }

                                    if (cycles == 0 && filterDish == null) {
                                        resultCountNeeded = 0;
                                    } else {
                                        dishList.remove(filterDish);
                                    }

                                }

                            }
                            pdLoading.dismiss();
                            if (validCount == searchArr.length) {
                                recommendList = filterDishList;
                                AlertDialog.Builder mBuilder = new AlertDialog.Builder(Pantry.this);
                                View mView = getLayoutInflater().inflate(R.layout.dialog_show_option, null, false);
                                initRecommendDiagViews(mView);
                                initRecommenDiagListener();
                                mBuilder.setView(mView);
                                recommendDialog = mBuilder.create();
                                recommendDialog.show();

                            } else {
                                Log.e("SEARCHLIST", Integer.toString(filterDishList.size()));
                                if (isLangEng) {
                                    Toast.makeText(Pantry.this, "Ingredient not found", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Pantry.this, "Hindi Natagpuan ang sangkap", Toast.LENGTH_SHORT).show();
                                }

                            }

                        }

                        @Override
                        public void onError(Exception e) {
                            pdLoading.dismiss();
                            if (isLangEng) {
                                Toast.makeText(Pantry.this, "Ingredient not found", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(Pantry.this, "Hindi Natagpuan ang sangkap", Toast.LENGTH_SHORT).show();
                            }

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

    private void initRecommenDiagListener() {
        lvRecommendations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Dishes filterDish = recommendList.get(position);

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
                recommendDialog.dismiss();
            }
        });
    }

    private void initRecommendDiagViews(View mView) {
        lvRecommendations = mView.findViewById(R.id.lvRecommendations);
        TextView txtRec = mView.findViewById(R.id.txtRec);
        if (isLangEng) {
            txtRec.setText("Recommendation Results");
        } else {
            txtRec.setText("Resulta ng Rekomendasyon");
        }
        rAdapter = new RecommendationsAdapter(Pantry.this, recommendList);
        lvRecommendations.setAdapter(rAdapter);
    }


    private void initDialogView(View mView) {
        btnLogout = mView.findViewById(R.id.btnLogout);
    }

    private void initViews() {
        recommendList = new ArrayList<>();
        btnPantry = findViewById(R.id.btnPantry);
        lblTopInst = findViewById(R.id.lblTopInst);
        mTextTv = findViewById(R.id.textTv);
        mVoiceBtn = findViewById(R.id.voiceBtn);
        btnGenerate = findViewById(R.id.btnGenerate);
        btnUser = findViewById(R.id.btnUser);
        lblFavorites = findViewById(R.id.lblFavorites);
        lblUser = findViewById(R.id.lblUser);
        siteInfo = getResources().getStringArray(R.array.siteInfo);
        db = new LocalFireStore(Pantry.this);
        pdLoading = new ProgressDialog(Pantry.this);

        btnFavorites = findViewById(R.id.btnFavorites);
        btnUpload = findViewById(R.id.btnUpload);
        lblIngredients = findViewById(R.id.lblIngredients);

        Boolean isEng = new LanguagePref(Pantry.this).getIsEng();
        if (isEng) {
            isLangEng = true;
            lblIngredients.setText("INGREDIENTS");
            mTextTv.setHint("Enter you ingredients");
            btnGenerate.setText("GENERATE");
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
            pdLoading.setMessage("Sending Request ...");
        } else {
            pdLoading.setMessage("Nagpapadala ng Kahilingan ...");
            lblTopInst.setText("Panuto: Lagyan ng kuwit pagkatapos ng salita at maliit na titik lamang. Halimbawa: baboy, patatas");
        }
    }

    private void speak() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        Boolean isEng = new LanguagePref(Pantry.this).getIsEng();
        if (isEng) {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "How are you? Please state your ingredients");
        } else {
            intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Kumusta? Sabihin ang iyong mga sangkap");
        }


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

        isLangEng = new LanguagePref(Pantry.this).getIsEng();
        if (isLangEng) {
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
            lblTopInst.setText("Instruction: Put comma after the word and small letter only. Example:pork,potato");
        } else {
            lblUser.setText("Gumagamit");
            lblFavorites.setText("Mga Paborito");
            lblTopInst.setText("Panuto: Lagyan ng kuwit pagkatapos ng salita at maliit na titik lamang. Halimbawa: baboy, patatas");
        }
    }
}
