package com.example.mouad.snake;


import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;

public class settings extends AppCompatActivity {

    public final static String SHARED_PREFS="shared_prefs";
    public final static String music_SHAREDPREFS="music_SHAREDPREFS";
    public final static String sound_SHAREDPREFS="sound_SHAREDPREFS";

    public static int opened=0;

    Switch sound_switch, music_switch;
    SharedPreferences sharedPreferences;
    float width,height;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager(). getDefaultDisplay();
        Point size = new Point();
        display. getSize(size);
        width = size. x;
        height = size. y;

        background();
        back_button();


        //GET THE PREVIOUS VALUE OF THE SWITCH
        sound_switch = new Switch(this);
        music_switch =new Switch(this);

        make_switch();
        make_tv();

        sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);

        music_switch.setChecked(sharedPreferences.getBoolean(music_SHAREDPREFS,true));
        sound_switch.setChecked(sharedPreferences.getBoolean(sound_SHAREDPREFS,true));

        music_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (music_switch.isChecked()){
                    start_game.music.start();
                    start_game.music.setLooping(true);
                }else{
                    start_game.music.pause();
                }
                start_game.musicBoolean= music_switch.isChecked();
                save();
            }
        });
        sound_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { save(); }});



    }

    private void make_tv(){
        TextView settings_tv, music_tv , sound_tv;
        settings_tv= new TextView(this);
        music_tv= new TextView(this);
        sound_tv= new TextView(this);

        settings_tv.setText(R.string.settings);
        music_tv.setText(R.string.music);
        sound_tv.setText(R.string.sound);

        settings_tv.setTextSize(setx(20));
        music_tv.setTextSize(setx(20));
        sound_tv.setTextSize(setx(20));

        int sett_color= Color.parseColor("#D18D1B");
        int tv_color= Color.parseColor("#1D8189");
        settings_tv.setTextColor(sett_color);
        music_tv.setTextColor(tv_color);
        sound_tv.setTextColor(tv_color);

        Typeface face = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");
        settings_tv.setTypeface(face);
        music_tv.setTypeface(face);
        sound_tv.setTypeface(face);

        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(settings_tv,layoutParams);
        settings_tv.setY(sety(100));
        settings_tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
       //settings_tv.setX((width-settings_tv.getWidth())/2);


        RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(music_tv,layoutParams1);
        music_tv.setY(sety(300));
        music_tv.setX(setx(200));

        RelativeLayout.LayoutParams layoutParams2=new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        addContentView(sound_tv,layoutParams2);
        sound_tv.setY(sety(500));
        sound_tv.setX(setx(200));
    }

    private void make_switch() {
        RelativeLayout.LayoutParams layoutParams=new RelativeLayout.LayoutParams(setx(150),sety(100));
        addContentView(sound_switch,layoutParams);
        sound_switch.setY(sety(500));
        sound_switch.setX(setx(600));

        RelativeLayout.LayoutParams layoutParams1=new RelativeLayout.LayoutParams(setx(150),sety(100));
        addContentView(music_switch,layoutParams1);
        music_switch.setY(sety(300));
        music_switch.setX(setx(600));
    }

    private void background() {
        //BACKGROUND
        RelativeLayout background=new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color= Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background,backparams);
    }

    public void back_button(){
        Button back =new Button(this );
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(setx(100),sety(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back,layoutParams);
        back.setY(sety(50));
        back.setX(setx(50));

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void save(){

        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        editor.putBoolean(music_SHAREDPREFS, music_switch.isChecked());
        editor.putBoolean(sound_SHAREDPREFS, sound_switch.isChecked());

        editor.apply();

    }

    public int setx(float x){
        int i;

        i= (int) ((x*width)/1080);

        return i;
    }

    public int sety(float x){
        int i;

        i= (int) ((x*height)/1770);

        return i;
    }

    @Override
    protected void onStop() {
        super.onStop();
        opened=0;
        app_closed app_closed = new app_closed();
        app_closed.activity_closed();
    }

    @Override
    public void onResume() {
        super.onResume();

        opened=1;

        if (start_game.musicBoolean){
            start_game.music.start();
            start_game.music.setLooping(true);

        }
    }

}