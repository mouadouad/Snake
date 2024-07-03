package com.example.mouad.snake.activities;


import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;

public class Settings extends AppCompatActivity {

    SwitchCompat soundSwitch, musicSwitch;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.backButton(this, this,  v -> onBack());
        getLifecycle().addObserver(MusicObserver.getInstance());

        makeSwitch();
        makeTextView();

        sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);

        musicSwitch.setChecked(sharedPreferences.getBoolean(Shared.MUSIC_SHARED_PREFS, true));
        soundSwitch.setChecked(sharedPreferences.getBoolean(Shared.SOUND_SHARED_PREFS, true));

        musicSwitch.setOnClickListener(view -> {
            MusicObserver.setMusic(musicSwitch.isChecked());
            save();
        });

        soundSwitch.setOnClickListener(view -> save());

    }

    private void makeTextView() {
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

        settings_tv.setTextColor(Shared.YELLOW);
        music_tv.setTextColor(Shared.BLUE);
        sound_tv.setTextColor(Shared.BLUE);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");
        settings_tv.setTypeface(face);
        music_tv.setTypeface(face);
        sound_tv.setTypeface(face);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(settings_tv, layoutParams);
        settings_tv.setY(Shared.setY(100));
        settings_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        Shared.addElement(this, music_tv, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 200, 300);

        Shared.addElement(this, sound_tv, RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT, 200, 500);

    }
    private void makeSwitch() {
        soundSwitch = new SwitchCompat(this);
        musicSwitch = new SwitchCompat(this);
        Shared.addElement(this, soundSwitch, 150, 100, 600, 500);
        Shared.addElement(this, musicSwitch, 150, 100, 600, 300);
    }
    public void save() {
        final SharedPreferences sharedPreferences = getSharedPreferences(Shared.SHARED_PREFS, MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(Shared.MUSIC_SHARED_PREFS, musicSwitch.isChecked());
        editor.putBoolean(Shared.SOUND_SHARED_PREFS, soundSwitch.isChecked());

        editor.apply();
    }
    private void onBack() {
        finish();
    }

}