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
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.shared.PlayerInfo;


public class Waiting extends AppCompatActivity {

    private Boolean canPlay = false, willPlay = false;
    private Button play;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());
        playButton();
        nameOfLobbyTv();

        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        final boolean sound = sharedPreferences.getBoolean(Shared.SOUND_SHARED_PREFS, true);
        final MediaPlayer shine = MediaPlayer.create(this, R.raw.time_start_sound);

        MultiplayerMenu.socket.emit("enterLobby");

        MultiplayerMenu.socket.on("canPlay", args -> runOnUiThread(() -> {
            canPlay = (boolean) args[0];
            if (canPlay) {
                play.setBackgroundResource(R.drawable.play_on_button);
                if (sound) {
                    shine.start();
                }
            } else {
                play.setBackgroundResource(R.drawable.play_off_button);
            }
        }));

        MultiplayerMenu.socket.on("side", args -> runOnUiThread(() -> PlayerInfo.side = (Integer) args[0]));

    }
    private void playButton() {
        play = new Button(this);
        Shared.addElement(this, play, 300, 150, 400, 200);
        play.setBackgroundResource(R.drawable.play_off_button);

        play.setOnClickListener(view -> {
            if (canPlay) {
                willPlay = true;
                finish();
                Intent intent = new Intent(Waiting.this, Multiplayer.class);
                startActivity(intent);
            }
        });
    }
    private void nameOfLobbyTv() {
        if (PlayerInfo.name != null) {

            final RelativeLayout div = new RelativeLayout(this);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            addContentView(div, layoutParams);

            final TextView nameOfLobbyTV = new TextView(this);
            RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(Shared.setX(600), RelativeLayout.LayoutParams.WRAP_CONTENT);
            textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
            div.addView(nameOfLobbyTV, textViewParams);
            nameOfLobbyTV.setTextSize(Shared.setX(24));

            final String nameOfLobbyText = getString(R.string.name_lobby) + PlayerInfo.name;

            final SpannableString styledText = new SpannableString(nameOfLobbyText);
            final ForegroundColorSpan blue = new ForegroundColorSpan(Color.parseColor("#1D8189"));
            styledText.setSpan(blue, 0, 15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            final ForegroundColorSpan yellow = new ForegroundColorSpan(Color.parseColor("#D18D1B"));
            styledText.setSpan(yellow, 15, 15 + PlayerInfo.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            nameOfLobbyTV.setText(styledText);
            nameOfLobbyTV.setY(Shared.setY(800));
        }
    }
    private void onBack() {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!willPlay) {
            MultiplayerMenu.socket.emit("quitWaiting");
        }
    }
}
