package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.example.mouad.snake.enums.States;

public class Waiting extends AppCompatActivity {

    Boolean joined = false;
    Button play;
    States who;
    public static int side;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        playButton();
        back_button();

        //SOUND
        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        final boolean sound = sharedPreferences.getBoolean(Shared.SOUND_SHARED_PREFS, true);
        final MediaPlayer shine = MediaPlayer.create(this, R.raw.time_start_sound);

        Intent a = getIntent(); // GET WHO ENTERED IF CREATE OR JOIN
        if (a.getSerializableExtra(Shared.who_key) == States.CREATE) {

            name_lobby_tv();
            who = States.CREATE;
            MultiplayerMenu.socket.on("entred", args -> { // SEE IF OTHER PLAYER JOINED SO WE CAN START
                runOnUiThread(() -> {
                    joined = (Boolean) args[0];
                    if (joined) { //CHANGE THE BACKGROUND OF BUTTON
                        play.setBackgroundResource(R.drawable.play_on_button);
                        if (sound) {
                            shine.start();
                        }
                    }
                });
            });
        } else if (a.getSerializableExtra(Shared.who_key) == States.JOIN) {

            //JOIN ENTERED
            name_lobby_tv();

            who = States.JOIN;
            joined = true;
            play.setBackgroundResource(R.drawable.play_on_button);

        } else {
            MultiplayerMenu.socket.emit("random", (MultiplayerMenu.level / 5) + 1);

        }

        //GET MY SIDE !! JOIN HAS ALREADY HIS SIDE!!
        MultiplayerMenu.socket.on("side", args -> runOnUiThread(() -> side = (Integer) args[0]));

        MultiplayerMenu.socket.on("player_found", args -> runOnUiThread(() -> {
            joined = (Boolean) args[0];
            if (joined) {
                play.setBackgroundResource(R.drawable.play_on_button);
                who = States.CREATE;
            }

        }));

        MultiplayerMenu.socket.on("game_found", args -> runOnUiThread(() -> {
            joined = (Boolean) args[0];
            if (joined) {
                who = States.JOIN;
                play.setBackgroundResource(R.drawable.play_on_button);
            }
        }));

        //IF QUIT
        MultiplayerMenu.socket.on("quit", args -> runOnUiThread(() -> {

            MultiplayerMenu.socket.disconnect();
            MultiplayerMenu.my_score = 0;
            MultiplayerMenu.his_score = 0;
            Intent i = new Intent(Waiting.this, GameFinished.class);
            startActivity(i);
        }));


    }

    public void back_button() {
        final Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> {
            if (who != States.JOIN && !joined) {
                MultiplayerMenu.socket.emit("destroy");
            }
            if (joined) {
                MultiplayerMenu.socket.emit("quit");
                MultiplayerMenu.socket.disconnect();
            }
            Intent intent = new Intent(Waiting.this, MultiplayerMenu.class);
            startActivity(intent);
        });
    }

    private void playButton() {
        play = new Button(this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(play, layoutParams2);
        play.setBackgroundResource(R.drawable.play_off_button);
        play.setY(Shared.setY(200));
        play.setX(Shared.setX(400));

        play.setOnClickListener(view -> {
            // SEE IF ALL PLAYERS ARE READY AND GO TO MAIN
            if (joined) {
                Intent intent = new Intent(Waiting.this, Multiplayer.class);
                intent.putExtra(Shared.who_key, who);
                startActivity(intent);
            }
        });
    }

    private void name_lobby_tv() {

        //SET THE LAYOUT TO ALIGN OBJECTS
        RelativeLayout div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div, layoutParams);

        //SET THE NAME OF THE LOBBY
        final TextView name_lobby = new TextView(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(600), RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(name_lobby, layoutParams1);
        name_lobby.setTextSize(Shared.setX(24));

        final String st1 = getString(R.string.name_lobby) + MultiplayerMenu.name;

        final SpannableString ss = new SpannableString(st1);
        final ForegroundColorSpan blue = new ForegroundColorSpan(Color.parseColor("#1D8189"));
        ss.setSpan(blue, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        final ForegroundColorSpan yellow = new ForegroundColorSpan(Color.parseColor("#D18D1B"));
        ss.setSpan(yellow, 15, 15 + MultiplayerMenu.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        name_lobby.setText(ss);
        name_lobby.setY(Shared.setY(800));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (who != States.JOIN && !joined) {
            MultiplayerMenu.socket.emit("destroy");
            MultiplayerMenu.socket.disconnect();
        }

        if (joined) {
            MultiplayerMenu.socket.emit("quit");
            MultiplayerMenu.socket.disconnect();
        }
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (who != States.JOIN) {
            MultiplayerMenu.socket.emit("destroy");
        }

        if (joined) {
            MultiplayerMenu.socket.emit("quit");
            MultiplayerMenu.socket.disconnect();

        }
        Intent intent = new Intent(Waiting.this, MultiplayerMenu.class);
        startActivity(intent);
    }
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
