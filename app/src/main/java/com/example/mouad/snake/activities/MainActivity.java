package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;

//import com.google.android.gms.ads.InterstitialAd;


public class MainActivity extends AppCompatActivity {

    public static MediaPlayer music;
    public static boolean musicBoolean, isMusicPlaying = false;
    public static int width, height;
    //InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;

        Shared.height = height;
        Shared.width = width;

        Shared.background(this, this);
        createButtons();
        makeIcon();

        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        musicBoolean = sharedPreferences.getBoolean(Shared.MUSIC_SHARED_PREFS, true);

        music = MediaPlayer.create(this, R.raw.snake_sound);


       /*mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());*/
    }

    private void makeIcon() {
        final Animation from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);
        final ImageView icon = new ImageView(this);
        Shared.addElement(this, icon, 200, 400, 440, 260);
        icon.setBackgroundResource(R.drawable.icon);
        icon.setAnimation(from_top);
    }
    private void createButtons() {
        final Button multi, Normal, Settings;
        multi = new Button(this);
        Normal = new Button(this);
        Settings = new Button(this);

        Shared.addElement(this, multi, 300, 150, 390, 1000);
        multi.setBackgroundResource(R.drawable.multiplayer_button);

        Shared.addElement(this, Normal, 300, 150, 390, 800);
        Normal.setBackgroundResource(R.drawable.normal_button);

        Shared.addElement(this, Settings, 300, 150, 700, 50);
        Settings.setBackgroundResource(R.drawable.settings_button);

        multi.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MultiplayerMenu.class);
            startActivity(intent);
            // mInterstitialAd.show();
        });

        Normal.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, Normal.class);
            startActivity(i);
        });

        Settings.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, com.example.mouad.snake.activities.Settings.class);
            startActivity(i);
        });

        final Animation from_bottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom);
        Normal.setAnimation(from_bottom);
        multi.setAnimation(from_bottom);

    }
    @Override
    public void onResume() {
        super.onResume();
        Shared.foreGround = true;
        if (musicBoolean && !isMusicPlaying) {
            music.start();
            music.setLooping(true);
            isMusicPlaying = true;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Shared.foreGround = false;
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (!Shared.foreGround) {
            music.pause();
            isMusicPlaying = false;
        }
    }

}
