package com.example.mouad.snake.activities;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.InterstitialAd;

import java.net.URISyntaxException;
import java.util.Calendar;

public class MultiplayerMenu extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public static final String Level = "level";
    public static final String Xp = "xp";

    //InterstitialAd mInterstitialAd;
    public static String name;
    public static Socket socket;
    public static int level, xp;
    public static int round = 1, my_score = 0, his_score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //GET SAVED LEVEL AND XP
        SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        level = sharedPreferences.getInt(Level, 1);
        xp = sharedPreferences.getInt(Xp, 0);

        background();
        back_button();

        try {
            socket = IO.socket("http://10.0.2.2:3000");
            //socket = IO.socket("https://snake1234.herokuapp.com/");
            socket.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }

        ping();
        buttons();

        //ADD INTERSTITIAL
        /*
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build()); */

        xp_bar();
        banner();

    }

    public void back_button() {
        final Button back = new Button(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> {
            Intent intent = new Intent(MultiplayerMenu.this, MainActivity.class);
            startActivity(intent);
//                mInterstitialAd.show();
        });
    }

    public void xp_bar() {
        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        //SET THE LAYOUT TO ALIGN OBJECTS
        final RelativeLayout div = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div, layoutParams);

        //SET LEVEL TEXT VIEW
        final TextView levelTV = new TextView(this);

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV, layoutParams1);

        int blue = Color.parseColor("#1D8189");
        levelTV.setTextColor(blue);
        levelTV.setTypeface(fredoka);
        levelTV.setTextSize(Shared.setX(40));
        levelTV.setY(Shared.setY(1100));
        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(MultiplayerMenu.level));

        final ImageView container, bar;

        container = new ImageView(this);
        bar = new ImageView(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(500), Shared.setY(100));
        addContentView(container, layoutParams2);
        container.setBackgroundResource(R.drawable.container);
        container.setY(Shared.setY(1300));
        container.setX(Shared.setX(290));

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX((500 * MultiplayerMenu.xp) / (MultiplayerMenu.level * 100)), Shared.setY(100));
        addContentView(bar, layoutParams3);
        bar.setBackgroundResource(R.drawable.bar);
        bar.setX(Shared.setX(290));
        bar.setY(Shared.setY(1300));
    }

    private void ping() {
        Calendar rightNow = Calendar.getInstance();
        final long time = rightNow.getTimeInMillis();
        socket.emit("ping");

        MultiplayerMenu.socket.on("pong", args -> runOnUiThread(() -> {
            Calendar rightNow1 = Calendar.getInstance();
            Log.d("ping", String.valueOf(rightNow1.getTimeInMillis() - time));
        }));
    }

    public void buttons() {
        final Button create, join, random;

        create = new Button(this);
        join = new Button(this);
        random = new Button(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(create, layoutParams);
        create.setBackgroundResource(R.drawable.c_lobby_button);
        create.setY(Shared.setY(200));
        create.setX(Shared.setX(400));

        final RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(join, layoutParams1);
        join.setBackgroundResource(R.drawable.j_lobby_button);
        join.setY(Shared.setY(500));
        join.setX(Shared.setX(400));

        final RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(180));
        addContentView(random, layoutParams2);
        random.setBackgroundResource(R.drawable.random_button);
        random.setY(Shared.setY(800));
        random.setX(Shared.setX(400));

        create.setOnClickListener(view -> {
            Intent i = new Intent(MultiplayerMenu.this, Create.class);
            startActivity(i);
        });

        join.setOnClickListener(view -> {
            Intent i = new Intent(MultiplayerMenu.this, Join.class);
            startActivity(i);
        });
        random.setOnClickListener(view -> {
            Intent i = new Intent(MultiplayerMenu.this, Waiting.class);
            i.putExtra(who_key, "random");
            startActivity(i);
        });
    }

    private void background() {
        final RelativeLayout background = new RelativeLayout(this);
        final RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        final int back_color = Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background, params);
    }

    public void banner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(MainActivity.width, MainActivity.height - Shared.statusBarHeight);
        addContentView(layout, layoutParams1);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView, layoutParams);

        //MobileAds.initialize(this,"ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(MultiplayerMenu.this, MainActivity.class);
        startActivity(intent);
        //mInterstitialAd.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        Shared.foreGround = true;
        if (MainActivity.musicBoolean && !MainActivity.isMusicPlaying) {
            MainActivity.music.start();
            MainActivity.music.setLooping(true);
            MainActivity.isMusicPlaying = true;
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
            MainActivity.music.pause();
            MainActivity.isMusicPlaying = false;
        }
    }

}
