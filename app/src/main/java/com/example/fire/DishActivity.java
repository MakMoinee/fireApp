package com.example.fire;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.fire.Common.Common;
import com.example.fire.Common.Constants;
import com.example.fire.Models.LocalFormats;
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

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DishActivity extends AppCompatActivity {

    private TextView txtTitle, lblVideo, lblDescription, txtDesc, txtIngredients, txtInstructions;
    private String newTitle = "", origTitle = "", videoURL = "", description = "", ingredients = "", instructions = "";
    private Toolbar toolbar;
    private PlayerControlView playerView;
    private RelativeLayout relVideo;
    private ExoPlayer player;
    private SurfaceView surfaceView;
    private ProgressBar pbLoading;
    private ImageButton playButton;


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
        if (title != "") {
            origTitle = title;
            newTitle = title.replace("-", " ");
        }

        if (description.equals("")) {
            lblDescription.setVisibility(View.GONE);
            txtDesc.setVisibility(View.GONE);
        } else {
            txtDesc.setText(description);
        }

        txtIngredients.setText(ingredients);
        txtInstructions.setText(instructions);

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
