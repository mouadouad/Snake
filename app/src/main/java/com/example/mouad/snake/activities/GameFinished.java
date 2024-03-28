package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;

public class GameFinished extends AppCompatActivity {

    TextView result;
    TextView levelTV;
    RelativeLayout div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        makeTextViews();

        //SEE HOW MANY XP I GOT
        if (MultiplayerMenu.my_score - MultiplayerMenu.his_score == 2) {
            MultiplayerMenu.xp += 100;
            result.setText(R.string.won);
        } else if (MultiplayerMenu.my_score - MultiplayerMenu.his_score == 1) {
            MultiplayerMenu.xp += 70;
            result.setText(R.string.won);
        } else if (MultiplayerMenu.my_score - MultiplayerMenu.his_score == -1) {
            MultiplayerMenu.xp += 25;
            result.setText(R.string.lost);
        } else if (MultiplayerMenu.my_score - MultiplayerMenu.his_score == -2) {
            MultiplayerMenu.xp += 15;
            result.setText(R.string.lost);
        } else if (MultiplayerMenu.my_score == MultiplayerMenu.his_score) {
            MultiplayerMenu.xp += 40;
            result.setText(R.string.draw);
        }

        if (MultiplayerMenu.my_score == 0 && MultiplayerMenu.his_score == 0) {
            MultiplayerMenu.xp += 100;
            result.setText(R.string.won);
        } else {
            result.append("\n" + MultiplayerMenu.my_score + "/" + MultiplayerMenu.his_score);
        }

        //UPDATE XP AND LEVEL
        if (MultiplayerMenu.xp >= MultiplayerMenu.level * 100) {
            MultiplayerMenu.xp -= MultiplayerMenu.level * 100;
            MultiplayerMenu.level++;
        }
        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(MultiplayerMenu.level));

        save();
        xpBar();
        quitButton();
    }

    private void quitButton() {
        //QUIT BUTTON
        final Button quit = new Button(this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(quit, layoutParams2);
        quit.setBackgroundResource(R.drawable.quit_button);
        quit.setY(Shared.setY(1200));
        quit.setX(Shared.setX(390));

        quit.setOnClickListener(view -> {

            Intent i = new Intent(GameFinished.this, MultiplayerMenu.class);
            MultiplayerMenu.round = 1;
            startActivity(i);

        });
    }

    public void save() {
        //SAVE NEW XP AND LEVEL
        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Shared.Level, MultiplayerMenu.level);
        editor.putInt(Shared.Xp, MultiplayerMenu.xp);

        editor.apply();
    }

    public void xpBar() {
        final ImageView container, bar;

        container = new ImageView(this);
        bar = new ImageView(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(500), Shared.setY(100));
        addContentView(container, layoutParams);
        container.setBackgroundResource(R.drawable.container);
        container.setY(Shared.setY(800));
        container.setX(Shared.setX(290));

        final RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX((500 * MultiplayerMenu.xp) / (MultiplayerMenu.level * 100)), Shared.setY(100));
        addContentView(bar, layoutParams1);
        bar.setBackgroundResource(R.drawable.bar);
        bar.setX(Shared.setX(290));
        bar.setY(Shared.setY(800));
    }

    public void makeTextViews() {

        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        //SET THE LAYOUT TO ALIGN OBJECTS
        div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div, layoutParams);

        //SET RESULT TEXT VIEW
        result = new TextView(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(result, layoutParams1);

        result.setTextSize(Shared.setX(40));
        int yellow = Color.parseColor("#D18D1B");
        result.setTextColor(yellow);
        result.setTypeface(fredoka);
        result.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        result.setY(Shared.setY(200));

        //SET LEVEL TEXT VIEW
        levelTV = new TextView(this);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV, layoutParams3);

        int blue = Color.parseColor("#1D8189");
        levelTV.setTextColor(blue);
        levelTV.setTypeface(fredoka);
        levelTV.setTextSize(Shared.setX(40));
        levelTV.setY(Shared.setY(600));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(GameFinished.this, MultiplayerMenu.class);
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
