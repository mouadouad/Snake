package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
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
        switch (MultiplayerMenu.my_score - MultiplayerMenu.his_score) {
            case 2:
                MultiplayerMenu.xp += 100;
                result.setText(R.string.won);
                break;
            case 1:
                MultiplayerMenu.xp += 70;
                result.setText(R.string.won);
                break;
            case -1:
                MultiplayerMenu.xp += 25;
                result.setText(R.string.lost);
                break;
            case -2:
                MultiplayerMenu.xp += 15;
                result.setText(R.string.lost);
                break;
            case 0:
                MultiplayerMenu.xp += 40;
                result.setText(R.string.draw);
                break;
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
        final Button quit = new Button(this);
        Shared.addElement(this, quit, 300, 150, 390, 1200);
        quit.setBackgroundResource(R.drawable.quit_button);

        quit.setOnClickListener(view -> {
            Intent i = new Intent(GameFinished.this, MultiplayerMenu.class);
            MultiplayerMenu.round = 1;
            startActivity(i);
        });
    }

    public void save() {
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

        Shared.addElement(this, container, 500, 100, 290, 800);
        container.setBackgroundResource(R.drawable.container);

        final int xpBarLength = (500 * MultiplayerMenu.xp) / (MultiplayerMenu.level * 100);
        Shared.addElement(this, bar, xpBarLength, 100, 290, 800);
        bar.setBackgroundResource(R.drawable.bar);
    }

    public void makeTextViews() {

        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div, layoutParams);

        result = new TextView(this);
        RelativeLayout.LayoutParams textViewParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(result, textViewParams);

        result.setTextSize(Shared.setX(40));
        result.setTextColor(Shared.YELLOW);
        result.setTypeface(fredoka);
        result.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        result.setY(Shared.setY(200));

        levelTV = new TextView(this);

        RelativeLayout.LayoutParams levelParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        levelParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV, levelParams);

        levelTV.setTextColor(Shared.BLUE);
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
