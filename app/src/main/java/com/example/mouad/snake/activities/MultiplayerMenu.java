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


import java.net.URISyntaxException;
import java.util.Calendar;

public class MultiplayerMenu extends AppCompatActivity {

    //InterstitialAd mInterstitialAd;
    public static String name;
    public static Socket socket;
    public static int level, xp;
    public static int round = 1, my_score = 0, his_score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());

        setButtons();

        //GET SAVED LEVEL AND XP
        SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        level = sharedPreferences.getInt(Shared.Level, 1);
        xp = sharedPreferences.getInt(Shared.Xp, 0);
        xpBar();

        try {
            socket = IO.socket("http://10.0.2.2:3000");
            //socket = IO.socket("https://snake1234.herokuapp.com/");
            socket.connect();
            ping();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public void xpBar() {
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
        final long time = Calendar.getInstance().getTimeInMillis();
        socket.emit("ping");

        MultiplayerMenu.socket.on("pong", args -> runOnUiThread(() -> Log.d("ping", String.valueOf(Calendar.getInstance().getTimeInMillis() - time))));
    }

    public void setButtons() {
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
            socket.emit("random", (MultiplayerMenu.level / 5) + 1);
            startActivity(i);
        });
    }

    private void onBack(){
        Intent intent = new Intent(MultiplayerMenu.this, MainActivity.class);
        startActivity(intent);
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
