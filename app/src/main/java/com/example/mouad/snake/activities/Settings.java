package com.example.mouad.snake.activities;


import android.content.SharedPreferences;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;

public class Settings extends AppCompatActivity {

    Switch sound_switch, music_switch;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        back_button();

        //GET THE PREVIOUS VALUE OF THE SWITCH
        sound_switch = new Switch(this);
        music_switch = new Switch(this);

        make_switch();
        make_tv();

        sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);

        music_switch.setChecked(sharedPreferences.getBoolean(Shared.MUSIC_SHARED_PREFS, true));
        sound_switch.setChecked(sharedPreferences.getBoolean(Shared.SOUND_SHARED_PREFS, true));

        music_switch.setOnClickListener(view -> {
            if (music_switch.isChecked()) {
                MainActivity.music.start();
                MainActivity.music.setLooping(true);
            } else {
                MainActivity.music.pause();
            }
            MainActivity.musicBoolean = music_switch.isChecked();
            save();
        });
        sound_switch.setOnClickListener(view -> save());

    }

    private void make_tv() {
        TextView settings_tv, music_tv, sound_tv;
        settings_tv = new TextView(this);
        music_tv = new TextView(this);
        sound_tv = new TextView(this);

        settings_tv.setText(R.string.settings);
        music_tv.setText(R.string.music);
        sound_tv.setText(R.string.sound);

        settings_tv.setTextSize(Shared.setX(20));
        music_tv.setTextSize(Shared.setX(20));
        sound_tv.setTextSize(Shared.setX(20));

        int sett_color = Color.parseColor("#D18D1B");
        int tv_color = Color.parseColor("#1D8189");
        settings_tv.setTextColor(sett_color);
        music_tv.setTextColor(tv_color);
        sound_tv.setTextColor(tv_color);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");
        settings_tv.setTypeface(face);
        music_tv.setTypeface(face);
        sound_tv.setTypeface(face);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(settings_tv, layoutParams);
        settings_tv.setY(Shared.setY(100));
        settings_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        //settings_tv.setX((width-settings_tv.getWidth())/2);


        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(music_tv, layoutParams1);
        music_tv.setY(Shared.setY(300));
        music_tv.setX(Shared.setX(200));

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(sound_tv, layoutParams2);
        sound_tv.setY(Shared.setY(500));
        sound_tv.setX(Shared.setX(200));
    }

    private void make_switch() {
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(150), Shared.setY(100));
        addContentView(sound_switch, layoutParams);
        sound_switch.setY(Shared.setY(500));
        sound_switch.setX(Shared.setX(600));

        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(150), Shared.setY(100));
        addContentView(music_switch, layoutParams1);
        music_switch.setY(Shared.setY(300));
        music_switch.setX(Shared.setX(600));
    }

    public void back_button() {
        final Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> finish());
    }

    public void save() {

        SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(Shared.MUSIC_SHARED_PREFS, music_switch.isChecked());
        editor.putBoolean(Shared.SOUND_SHARED_PREFS, sound_switch.isChecked());

        editor.apply();

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