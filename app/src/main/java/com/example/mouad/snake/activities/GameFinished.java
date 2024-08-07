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
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.shared.PlayerInfo;

public class GameFinished extends AppCompatActivity {

    TextView result;
    TextView levelTV;
    RelativeLayout div;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        getLifecycle().addObserver(MusicObserver.getInstance());
        makeTextViews();

        final int myScore = getIntent().getIntExtra("myScore", 0);
        final int hisScore = getIntent().getIntExtra("hisScore", 0);

        switch (myScore - hisScore) {
            case 2:
                PlayerInfo.xp += 100;
                result.setText(R.string.won);
                break;
            case 1:
                PlayerInfo.xp += 70;
                result.setText(R.string.won);
                break;
            case -1:
                PlayerInfo.xp += 25;
                result.setText(R.string.lost);
                break;
            case -2:
                PlayerInfo.xp += 15;
                result.setText(R.string.lost);
                break;
            case 0:
                PlayerInfo.xp += 40;
                result.setText(R.string.draw);
                break;
        }

        if (myScore == 0 && hisScore == 0) {
            PlayerInfo.xp += 100;
            result.setText(R.string.won);
        } else {
            result.append("\n" + myScore + "/" + hisScore);
        }

        //UPDATE XP AND LEVEL
        if (PlayerInfo.xp >= PlayerInfo.level * 100) {
            PlayerInfo.xp -= PlayerInfo.level * 100;
            PlayerInfo.level++;
        }
        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(PlayerInfo.level));

        save();
        xpBar();
        quitButton();
    }

    private void quitButton() {
        final Button quit = new Button(this);
        Shared.addElement(this, quit, 300, 150, 390, 1200);
        quit.setBackgroundResource(R.drawable.quit_button);

        quit.setOnClickListener(view -> {
            finish();
            Intent i = new Intent(GameFinished.this, MultiplayerMenu.class);
            startActivity(i);
        });
    }

    public void save() {
        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Shared.Level, PlayerInfo.level);
        editor.putInt(Shared.Xp, PlayerInfo.xp);
        editor.apply();
    }

    public void xpBar() {
        final ImageView container, bar;

        container = new ImageView(this);
        bar = new ImageView(this);

        Shared.addElement(this, container, 500, 100, 290, 800);
        container.setBackgroundResource(R.drawable.container);

        final int xpBarLength = (500 * PlayerInfo.xp) / (PlayerInfo.level * 100);
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
}
