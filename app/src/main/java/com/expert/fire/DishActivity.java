package com.expert.fire;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.expert.fire.Common.Common;
import com.expert.fire.Common.Constants;
import com.expert.fire.Interfaces.SimpleListener;
import com.expert.fire.LocalPreference.FavoritesPref;
import com.expert.fire.Models.Dishes;
import com.expert.fire.Models.LocalFormats;
import com.expert.fire.Models.Translation;
import com.expert.fire.Service.LocalRequest;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

public class DishActivity extends AppCompatActivity {

    private TextView txtTitle, lblVideo, lblDescription, txtDesc, txtIngredients, txtInstructions;
    private String newTitle = "", origTitle = "", videoURL = "",descriptionTrans = "", description = "", ingredients = "", ingredientsTrans = "", instructionsTrans = "", instructions = "";
    private Toolbar toolbar;
    private PlayerControlView playerView;
    private RelativeLayout relVideo,relDish;
    private ExoPlayer player;
    private SurfaceView surfaceView;
    private ProgressBar pbLoading;
    private ImageButton playButton;
    private Dishes fDish;
    private ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dish);
        initViews();
        initListeners();
    }

    private void initListeners() {
        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_favor:
                        new FavoritesPref(DishActivity.this).storeDish(fDish);
                        Toast.makeText(DishActivity.this, "Matagumpay na Nadagdag sa Mga Paborito", Toast.LENGTH_SHORT).show();
                        break;
                }
                return false;
            }
        });
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                pbLoading.setVisibility(View.VISIBLE);
                playVideo();
            }
        });
    }

    private void initViews() {
        dialog = new ProgressDialog(DishActivity.this);
        dialog.setMessage("Naglo-load ng Mga Pagsasalin ...");
        dialog.setCancelable(false);
        dialog.show();
        relDish = findViewById(R.id.relDish);
        toolbar = findViewById(R.id.toolbar);
        playerView = findViewById(R.id.playerView);
        relVideo = findViewById(R.id.relVideo);
        lblVideo = findViewById(R.id.lblVideo);
        lblVideo.setVisibility(View.GONE);
        relVideo.setVisibility(View.GONE);
        lblDescription = findViewById(R.id.lblDescription);
        txtDesc = findViewById(R.id.txtDesc);
        txtIngredients = findViewById(R.id.txtIngredients);
        txtInstructions = findViewById(R.id.txtInstructions);
        surfaceView = findViewById(R.id.surfaceView);
        pbLoading = findViewById(R.id.progressLoading);
        playButton = findViewById(R.id.playButton);
        player = new ExoPlayer.Builder(DishActivity.this).build();
        playerView.hide();
        String title = getIntent().getStringExtra("title");
        videoURL = getIntent().getStringExtra("videoURL");
        description = getIntent().getStringExtra("description");
        ingredients = getIntent().getStringExtra("ingredients");
        instructions = getIntent().getStringExtra("instructions");
        String rawDish = getIntent().getStringExtra("dishRaw");
        fDish = new Gson().fromJson(rawDish, new TypeToken<Dishes>() {
        }.getType());


        getTranslations(title);


    }

    private void getTranslations(String title) {
        Translation translation = new Translation();
        translation.setTargetLang("tl");
        translation.setSourceLang("auto");
        translation.setText(instructions);
        LocalRequest.getTranslation(DishActivity.this, translation, new SimpleListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.e("data", response);
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("status") == 200) {
                        instructionsTrans = obj.getString("translatedText");
                        txtInstructions.setText(instructionsTrans);
                    } else {

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Log.e("ERR", e.getMessage());
                }
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Log.e("ERR", error.toString());
            }
        });


        Translation translation2 = new Translation();
        translation2.setTargetLang("tl");
        translation2.setSourceLang("auto");
        translation2.setText(ingredients);
        LocalRequest.getTranslation(DishActivity.this, translation2, new SimpleListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.e("data", response);
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("status") == 200) {
                        ingredientsTrans = obj.getString("translatedText");
                        txtIngredients.setText(ingredientsTrans);
                    } else {

                    }

                    dialog.dismiss();
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Log.e("ERR", e.getMessage());
                }
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Log.e("ERR", error.toString());
            }
        });


        Translation translation3 = new Translation();
        translation3.setTargetLang("tl");
        translation3.setSourceLang("auto");
        translation3.setText(description);
        LocalRequest.getTranslation(DishActivity.this, translation3, new SimpleListener() {
            @Override
            public void onSuccess(String response) {
                try {
                    Log.e("data", response);
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("status") == 200) {
                        descriptionTrans = obj.getString("translatedText");
                        txtDesc.setText(descriptionTrans);
                    } else {

                    }
                    relDish.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                } catch (JSONException e) {
                    dialog.dismiss();
                    e.printStackTrace();
                    Log.e("ERR", e.getMessage());
                    relDish.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(VolleyError error) {
                dialog.dismiss();
                Log.e("ERR", error.toString());
                relDish.setVisibility(View.VISIBLE);
            }
        });


        if (title != "") {
            origTitle = title;
            newTitle = title.replace("-", " ");
            newTitle = newTitle.substring(0,1).toUpperCase() +  newTitle.substring(1);
        }

        if (description.equals("")) {
            lblDescription.setVisibility(View.GONE);
            txtDesc.setVisibility(View.GONE);
        } else {
            txtDesc.setText(description);
        }

        if (instructionsTrans != "") {
            txtIngredients.setText(ingredientsTrans);
            txtInstructions.setText(instructionsTrans);
        } else {
            txtIngredients.setText(ingredients);
            txtInstructions.setText(instructions);
        }


        toolbar.setTitle(newTitle);
        loadPureURL();
    }


    private void playVideo() {
        if (!videoURL.equals("")) {
            player = new ExoPlayer.Builder(DishActivity.this).build();
            Uri uri = Uri.parse(videoURL);
            playerView.setKeepScreenOn(true);
            playerView.setShowNextButton(false);
            playerView.setShowPreviousButton(false);
            playerView.setShowTimeoutMs(3000);
            playerView.setPlayer(player);
            DefaultHttpDataSource.Factory dataSource = new DefaultHttpDataSource.Factory();
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSource)
                    .createMediaSource(MediaItem.fromUri(uri));
            player.setMediaSource(mediaSource);
            player.prepare();
            player.setPlayWhenReady(true);
            player.setVideoSurfaceView(surfaceView);
            player.addListener(new Player.Listener() {
                @Override
                public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                    Log.e("statechanged", "statechange");
                    Player.Listener.super.onPlayerStateChanged(playWhenReady, playbackState);
                }

                @Override
                public void onPlaybackStateChanged(int playbackState) {
                    switch (playbackState) {
                        case Player.STATE_READY:
                        case Player.STATE_ENDED:
                            if (!playerView.isVisible()) {
                                playerView.show();
                                pbLoading.setVisibility(View.GONE);
                            }
                            break;
                    }
                    Player.Listener.super.onPlaybackStateChanged(playbackState);
                }

                @Override
                public void onPlayerError(PlaybackException error) {
                    pbLoading.setVisibility(View.VISIBLE);
                    Player.Listener.super.onPlayerError(error);
                }
            });
            playerView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!playerView.isVisible()) {
                        playerView.show();
                    }
                }
            });
            surfaceView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!playerView.isVisible()) {
                        playerView.show();
                    }
                }
            });
        }
    }

    private void releasePlayer() {
        if (player != null) {
            Log.e("release player", "release");
            player.stop();
            player.release();
            player.clearMediaItems();
            player.clearVideoSurfaceView(surfaceView);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        releasePlayer();
    }

    @Override
    protected void onStop() {
        releasePlayer();
        super.onStop();
    }

    @Override
    protected void onResume() {
        releasePlayer();
        super.onResume();
    }

    private void loadPureURL() {
        if (!videoURL.equals("")) {
            String[] sliceStr = videoURL.split("/embed/");
            String watchPath = "/watch?v=";
            Map<String, Object> map = Common.getYoutubeBody(watchPath, sliceStr[1]);
            String finalReq = new Gson().toJson(map);

            StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.youtubeURL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Type mapType = new TypeToken<Map<String, Object>>() {
                    }.getType();
                    Map<String, Object> myMap = new Gson().fromJson(response, mapType);

                    if (myMap.containsKey("streamingData")) {
                        Map<String, Object> streamingMap = (Map<String, Object>) myMap.get("streamingData");
                        if (streamingMap.containsKey("formats")) {
                            String jsonStr = new Gson().toJson(streamingMap.get("formats"));
                            Type typeToken = new TypeToken<List<LocalFormats>>() {
                            }.getType();
                            List<LocalFormats> formats = new Gson().fromJson(jsonStr, typeToken);
                            if (formats.size() > 0) {
                                Log.e("FORMATS", formats.get(0).toString());
                                videoURL = formats.get(0).getUrl();
                                if (videoURL != "") {
                                    lblVideo.setVisibility(View.VISIBLE);
                                    relVideo.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("VOLLEYERR", error.getMessage());
                }
            }) {
                @Override
                public String getBodyContentType() {
                    return Constants.CONTENT_TYPE;
                }

                @Override
                public byte[] getBody() throws AuthFailureError {
                    return finalReq.getBytes();
                }
            };

            RequestQueue queue = Volley.newRequestQueue(DishActivity.this);
            queue.add(stringRequest);
        }
    }
}
