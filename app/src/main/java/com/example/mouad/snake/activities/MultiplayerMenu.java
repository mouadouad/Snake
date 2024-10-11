package com.example.mouad.snake.activities;


import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.shared.PlayerInfo;



import java.net.URISyntaxException;
import java.util.Calendar;
import java.util.UUID;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MultiplayerMenu extends AppCompatActivity {

    //InterstitialAd mInterstitialAd;
    public static Socket socket;
    private static final String TOKEN_KEY = "device_token";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());
        getLifecycle().addObserver(MusicObserver.getInstance());

        setButtons();

        //GET SAVED LEVEL AND XP
        SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        PlayerInfo.level = sharedPreferences.getInt(Shared.Level, 1);
        PlayerInfo.xp = sharedPreferences.getInt(Shared.Xp, 0);
        PlayerInfo.name = null;
        xpBar();

        try {
            if(socket==null || !socket.connected()){
                String token = getDeviceToken();
                IO.Options opts = new IO.Options();
                opts.query = "token=" + token;
//              socket= IO.socket("http://10.0.2.2:3000", opts); // https://snake1234.herokuapp.com/
                socket = IO.socket("http://192.168.1.13:80", opts);
                socket.connect();
                ping();
            }
        } catch (URISyntaxException e) {
            Log.e("TAG", String.valueOf(e));
        }
    }


    public String getDeviceToken() {
        SharedPreferences prefs = this.getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        String token = prefs.getString(TOKEN_KEY, null);

        if (token == null) {
            token = UUID.randomUUID().toString();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString(TOKEN_KEY, token);
            editor.apply();
        }

        return token;
    }

    public void xpBar() {
        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        final RelativeLayout div = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div, layoutParams);

        final TextView levelTV = new TextView(this);
        final RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV, textViewParams);
        levelTV.setY(Shared.setY(1100));

        levelTV.setTextColor(Shared.BLUE);
        levelTV.setTypeface(fredoka);
        levelTV.setTextSize(Shared.setX(40));
        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(PlayerInfo.level));

        final ImageView container, bar;

        container = new ImageView(this);
        bar = new ImageView(this);

        Shared.addElement(this, container, 500, 100, 290, 1300);
        container.setBackgroundResource(R.drawable.container);

        final int xpBarLength = (500 * PlayerInfo.xp) / (PlayerInfo.level * 100);
        Shared.addElement(this, bar, xpBarLength, 100, 290, 1300);
        bar.setBackgroundResource(R.drawable.bar);

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

        Shared.addElement(this, create, 300, 200, 400, 200);
        create.setBackgroundResource(R.drawable.c_lobby_button);

        Shared.addElement(this, join, 300, 200, 400, 500);
        join.setBackgroundResource(R.drawable.j_lobby_button);

        Shared.addElement(this, random, 300, 180, 400, 800);
        random.setBackgroundResource(R.drawable.random_button);

        create.setOnClickListener(view -> {
            Intent i = new Intent(MultiplayerMenu.this, Create.class);
            startActivity(i);
        });
        join.setOnClickListener(view -> {
            Intent i = new Intent(MultiplayerMenu.this, Join.class);
            startActivity(i);
        });
        random.setOnClickListener(view -> {
            random.setEnabled(false);
            Intent i = new Intent(MultiplayerMenu.this, Waiting.class);
            socket.emit("random", (PlayerInfo.level / 5) + 1);
            startActivity(i);
            random.setEnabled(true);

        });
    }

    private void onBack(){
        Intent intent = new Intent(MultiplayerMenu.this, MainActivity.class);
        startActivity(intent);
    }

}
