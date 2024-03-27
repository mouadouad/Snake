package com.example.mouad.snake.activities;

import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class Join extends AppCompatActivity {
    EditText name_of_lobby;
    Button confirm;
    Boolean entred = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        background();
        buttons_editText();
        back_button();

        //GET MY SIDE FROM SERVER
        MultiplayerMenu.socket.on("side", args -> runOnUiThread(() -> Waiting.side = (Integer) args[0]));

        confirm_click_listener();
        banner();
    }

    private void confirm_click_listener() {
        confirm.setOnClickListener(view -> {

            //SAY TO SERVER THE ROOM I WANT TO ENTER TO
            MultiplayerMenu.name = name_of_lobby.getText().toString();
            MultiplayerMenu.socket.emit("join", MultiplayerMenu.name);

            MultiplayerMenu.socket.on("entred1", args -> { //SEE IF ROOM EXISTS
                runOnUiThread(() -> {
                    entred = (Boolean) args[0];
                    if (entred) { //ROOM EXISTS AND IS AVAILABLE GO WAITING
                        MultiplayerMenu.name = name_of_lobby.getText().toString();
                        Intent intent = new Intent(Join.this, Waiting.class);
                        intent.putExtra(Shared.who_key, "join");
                        startActivity(intent);
                    }
                });
            });

        });
    }

    public void back_button() {
        final Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> this.finish());
    }

    private void buttons_editText() {
        //CREATE BUTTON AND EDIT TEXT
        name_of_lobby = new EditText(this);
        confirm = new Button(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(name_of_lobby, layoutParams);

        name_of_lobby.setY(Shared.setY(200));
        name_of_lobby.setX(Shared.setX(400));

        final RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams1);
        confirm.setBackgroundResource(R.drawable.join_button);
        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));
    }

    private void background() {
        //BACKGROUND
        RelativeLayout background = new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color = Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background, backparams);
    }

    public void banner() {
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.width, Shared.height - Shared.statusBarHeight);
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
