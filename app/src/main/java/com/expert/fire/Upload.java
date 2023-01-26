package com.expert.fire;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.expert.fire.Interfaces.LocalFirestoreCallback;
import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.LocalPreference.LanguagePref;
import com.expert.fire.Models.Dishes;
import com.expert.fire.Service.LocalFireStore;
import com.expert.fire.Service.LocalStorage;

import java.io.IOException;
import java.util.Arrays;

public class Upload extends AppCompatActivity {

    Toolbar toolbar;
    ImageButton btnBrowse;
    private static final int REQUEST_PERMISSIONS = 100;
    ProgressDialog loadingImageDialog, pd;
    private Bitmap bitmap;
    private String filePath;
    Button btnOnUpload;
    EditText editDish, editDesc, editIngredients, editInstructions, editVidUrl;
    LocalFireStore fs;
    LocalStorage storage;
    Boolean isLangEng = false;
    TextView lblPicture;
    ImageButton btnPantry, btnUser, btnFavorites, btnUpload;
    TextView lblUser, lblFavorites;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        boolean permitted = askPermission();
        initViews();
        initListeners();
    }

    private void initListeners() {
        btnPantry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Upload.this, Pantry.class));
                finish();
            }
        });
        btnUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Upload.this, User.class));
                finish();
            }
        });
        btnFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Upload.this, Favorites.class));
                finish();
            }
        });
        btnBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean permitted = askPermission();
                if (!permitted) {
                    Toast.makeText(Upload.this, "Paumanhin ngunit kailangan mo munang payagan ang pag-access sa storage", Toast.LENGTH_SHORT).show();
                } else {
                    browseImage();
                }
            }
        });
        btnOnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editDish.getText().toString().equals("") ||
                        editDesc.getText().toString().equals("") ||
                        editIngredients.getText().toString().equals("") ||
                        editInstructions.getText().toString().equals("") ||
                        editVidUrl.getText().toString().equals("")) {
                    if (isLangEng) {
                        Toast.makeText(Upload.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(Upload.this, "Mangyaring Huwag Mag-iwan ng Walang Lamang Mga Patlang", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (!editVidUrl.getText().toString().contains("https://www.youtube.com/embed")) {
                        if (isLangEng) {
                            Toast.makeText(Upload.this, "Invalid url format of video", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(Upload.this, "Hindi Wasto ang Url ng Video", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        pd.show();
                        Dishes dishes = new Dishes();
                        dishes.setDish(editDish.getText().toString());
                        String desc = editDesc.getText().toString().trim();
                        String[] disc = desc.split(";;");
                        String ing = editIngredients.getText().toString().trim();
                        String[] origIng = ing.split(";;");
                        ing = ing.replaceAll(",", " ");
                        ing = ing.replaceAll(";;", " ");
                        String[] ingredients = ing.split(" ");
                        String inst = editInstructions.getText().toString();
                        String[] sliceInst = inst.split(";;");
                        dishes.setInstructions(Arrays.asList(sliceInst));
                        dishes.setOrigIngredients(Arrays.asList(origIng));
                        dishes.setDescription(Arrays.asList(disc));
                        dishes.setIngredients(Arrays.asList(ingredients));
                        dishes.setVideoURL(editVidUrl.getText().toString());
                        fs.addIngredient(dishes, new LocalFirestoreCallback() {
                            @Override
                            public void onSuccess() {
                                storage.uploadImage(bitmap, editDish.getText().toString() + ".jpg", new SimpleListener() {
                                    @Override
                                    public void onSuccess() {
                                        pd.dismiss();
                                        if (isLangEng) {
                                            Toast.makeText(Upload.this, "Successfully Added Dish and Image Uploaded Successfully", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Upload.this, "Matagumpay na Nagdagdag ng Ulam at Larawan", Toast.LENGTH_SHORT).show();
                                        }
                                        finish();
                                    }

                                    @Override
                                    public void onError(Exception error) {
                                        pd.dismiss();
                                        if (isLangEng) {
                                            Toast.makeText(Upload.this, "Successfully Added Dish But Failed To Upload Image", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(Upload.this, "Matagumpay na Nagdagdag ng Dish Ngunit Nabigong Mag-upload ng Larawan", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onError(Exception e) {
                                pd.dismiss();
                                if (isLangEng) {
                                    Toast.makeText(Upload.this, "Failed to add dish, Please Try Again Later", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(Upload.this, "Nabigong magdagdag ng ulam, Pakisubukang Muli Mamaya", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }

                }
            }
        });
        editVidUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void browseImage() {
        loadingImageDialog.show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        browser.launch(intent);
    }

    private boolean askPermission() {
        boolean permit = false;
        if ((ContextCompat.checkSelfPermission(Upload.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(Upload.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        ) {
            if ((ActivityCompat.shouldShowRequestPermissionRationale(Upload.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) && (ActivityCompat.shouldShowRequestPermissionRationale(Upload.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))) {
                permit = true;
            } else {
                ActivityCompat.requestPermissions(Upload.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                        REQUEST_PERMISSIONS);
                permit = false;
            }
        } else {
            permit = true;
        }
        return permit;
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        storage = new LocalStorage(Upload.this);
        fs = new LocalFireStore(Upload.this);
        btnBrowse = findViewById(R.id.btnBrowse);
        editDesc = findViewById(R.id.editDesc);
        editDish = findViewById(R.id.editDish);
        editIngredients = findViewById(R.id.editIngredients);
        editInstructions = findViewById(R.id.editInstructions);
        loadingImageDialog = new ProgressDialog(Upload.this);
        lblPicture = findViewById(R.id.lblPicture);
        btnOnUpload = findViewById(R.id.btnOnUpload);
        btnPantry = findViewById(R.id.btnPantry);
        btnUser = findViewById(R.id.btnUser);
        btnFavorites = findViewById(R.id.btnFavorites);
        btnUpload = findViewById(R.id.btnUpload);
        lblUser = findViewById(R.id.lblUser);
        lblFavorites = findViewById(R.id.lblFavorites);
        isLangEng = new LanguagePref(Upload.this).getIsEng();
        if (isLangEng) {
            loadingImageDialog.setMessage("Loading Image ...");
            toolbar.setTitle("Upload");
            lblPicture.setText("Picture");
            editDesc.setHint("Description");
            editDish.setHint("Name of Dish");
            editIngredients.setHint("Ingredients");
            editInstructions.setHint("Instructions");
            btnOnUpload.setText("Upload");
            lblUser.setText("User");
            lblFavorites.setText("Favorites");
        } else {
            loadingImageDialog.setMessage("Nilo-load ang Imahe...");
        }

        btnOnUpload = findViewById(R.id.btnOnUpload);
        editDish = findViewById(R.id.editDish);
        editDesc = findViewById(R.id.editDesc);
        editIngredients = findViewById(R.id.editIngredients);
        editInstructions = findViewById(R.id.editInstructions);
        editVidUrl = findViewById(R.id.editVidUrl);
        pd = new ProgressDialog(Upload.this);

        if (isLangEng) {
            pd.setMessage("Sending Request...");
        } else {
            pd.setMessage("Nagpapadala ng Kahilingan...");
        }
        pd.setCancelable(false);
    }

    private ActivityResultLauncher<Intent> browser = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            if (result.getResultCode() == RESULT_OK) {
                Uri picUri = result.getData().getData();
                if (picUri == null) {
                    return;
                }
                filePath = getPath(picUri);
                if (filePath != null) {
                    try {

                        Log.d("filePath", String.valueOf(filePath));
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), picUri);

                        btnBrowse.setImageBitmap(bitmap);
                        loadingImageDialog.dismiss();
                        onResume();
                    } catch (IOException e) {
                        bitmap = null;
                        loadingImageDialog.dismiss();
                        Toast.makeText(Upload.this, "browser -->>> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    bitmap = null;
                    loadingImageDialog.dismiss();
                    if (isLangEng) {
                        Toast.makeText(
                                Upload.this, "No image selected",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(
                                Upload.this, "walang napiling larawan",
                                Toast.LENGTH_LONG).show();
                    }
                }
            }
        }
    });

    public String getPath(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        String document_id = cursor.getString(0);
        document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
        cursor.close();

        cursor = getContentResolver().query(
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                null, MediaStore.Images.Media._ID + " = ? ", new String[]{document_id}, null);
        cursor.moveToFirst();
        @SuppressLint("Range")
        String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        cursor.close();

        return path;
    }
}