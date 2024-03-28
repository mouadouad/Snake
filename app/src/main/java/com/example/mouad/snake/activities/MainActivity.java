package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.Display;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

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

        Shared.statusBarHeight = getStatusBarHeight();
        Shared.height = height;
        Shared.width = width;

        background();
        create_buttons();
        make_icon();

        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        musicBoolean = sharedPreferences.getBoolean(Shared.MUSIC_SHARED_PREFS, true);

        music = MediaPlayer.create(this, R.raw.snake_sound);


//        mInterstitialAd = new InterstitialAd(this);
//        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
//        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    private void make_icon() {
        final Animation from_top = AnimationUtils.loadAnimation(this, R.anim.from_top);
        final ImageView icon = new ImageView(this);
        icon.setBackgroundResource(R.drawable.icon);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(200), Shared.setY(400));
        addContentView(icon, layoutParams1);
        icon.setY(Shared.setY(260));
        icon.setX(Shared.setX((1080 - 200) / 2));
        icon.setAnimation(from_top);
    }

    private void create_buttons() {
        final Button multi, Normal, Settings;
        multi = new Button(this);
        Normal = new Button(this);
        Settings = new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(multi, layoutParams2);
        multi.setBackgroundResource(R.drawable.multiplayer_button);
        multi.setY(Shared.setY(1000));
        multi.setX(Shared.setX(390));

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(Normal, layoutParams3);
        Normal.setBackgroundResource(R.drawable.normal_button);
        Normal.setY(Shared.setY(800));
        Normal.setX(Shared.setX(390));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(Settings, layoutParams4);
        Settings.setBackgroundResource(R.drawable.settings_button);
        Settings.setX(Shared.setX(700));
        Settings.setY(Shared.setY(50));

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

    private void background() {
        //BACKGROUND
        RelativeLayout background = new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color = Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background, backparams);
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
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
