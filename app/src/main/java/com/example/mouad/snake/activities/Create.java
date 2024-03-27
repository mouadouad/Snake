package com.example.mouad.snake.activities;

import android.content.Intent;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

import java.util.Random;


public class Create extends AppCompatActivity {

    EditText nameOfLobby;
    Button confirm, generate;
    Boolean entred = false;
    String generated;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        background();
        buttons_editText();
        back_button();

        generate.setOnClickListener(view -> generate());
        confirm_click_listener();
        banner();
    }

    public void back_button() {
        Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> {
            Intent intent = new Intent(Create.this, MultiplayerMenu.class);
            startActivity(intent);
        });
    }

    private void confirm_click_listener() {
        confirm.setOnClickListener(view -> {

            //SAY TO SERVER THE ROOM I WANT TO CREATE
            if (nameOfLobby.getText().toString().contains(" ") || nameOfLobby.getText().toString().isEmpty() || nameOfLobby.getText().toString().length() > 50) {

                nameOfLobby.setError("Please choose another lobby");

            } else {
                MultiplayerMenu.name = nameOfLobby.getText().toString();
                MultiplayerMenu.socket.emit("create", MultiplayerMenu.name);
            }

            MultiplayerMenu.socket.on("created", args -> { //SEE IF ROOM IS AVAILABLE
                runOnUiThread(() -> {
                    entred = (Boolean) args[0];
                    if (entred) { //ROOM AVAILABLE GO TO WAITING
                        MultiplayerMenu.name = nameOfLobby.getText().toString();
                        Intent intent = new Intent(Create.this, Waiting.class);
                        intent.putExtra(Shared.who_key, "create");
                        startActivity(intent);
                    }
                });
            });

        });
    }

    public void generate() {
        String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        generated = "";

        //RANDOM NAME
        for (int i = 0; i < 6; i++) {

            final Random rand = new Random();
            final int position = rand.nextInt(36);

            generated = String.format("%s%s", generated, alphabets.charAt(position));
        }

        //SAY THAT I WANT THIS ROOM
        MultiplayerMenu.socket.emit("create", generated);

        MultiplayerMenu.socket.on("created", args -> { //SEE IF ROOM IS AVAILABLE
            runOnUiThread(() -> {
                entred = (Boolean) args[0];
                if (entred) { //ROOM AVAILABLE GO TO WAITING
                    MultiplayerMenu.name = generated;
                    Intent intent = new Intent(Create.this, Waiting.class);
                    intent.putExtra(Shared.who_key, "create");
                    startActivity(intent);
                } else {
                    generate(); //REPEAT
                }
            });
        });

    }

    private void buttons_editText() {
        //CREATE BUTTON AND EDIT TEXT
        nameOfLobby = new EditText(this);
        confirm = new Button(this);
        generate = new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(nameOfLobby, layoutParams2);

        nameOfLobby.setY(Shared.setY(200));
        nameOfLobby.setX(Shared.setX(400));

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams3);
        confirm.setBackgroundResource(R.drawable.create_button);
        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(generate, layoutParams4);
        generate.setBackgroundResource(R.drawable.generate_button);

        generate.setY(Shared.setY(900));
        generate.setX(Shared.setX(400));
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Create.this, MultiplayerMenu.class);
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
