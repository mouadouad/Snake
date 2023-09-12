package com.example.mouad.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class start_game extends AppCompatActivity {

    public final static String SHARED_PREFS="shared_prefs";
    public final static String music_SHAREDPREFS="music_SHAREDPREFS";
    public static int opened=0;
    public static MediaPlayer music;
    public static boolean  musicBoolean;
    public static int width,height;
    Button multi, Normal,Settings;
    ImageView icon;
    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;

        background();
        create_buttons();
        make_icon();

        //ANIMATION FOR SPLASH SCREEN
        Animation from_bottom= AnimationUtils.loadAnimation(this,R.anim.from_bottom);
        Animation from_top= AnimationUtils.loadAnimation(this,R.anim.from_top);

        Normal.setAnimation(from_bottom);
        multi.setAnimation(from_bottom);
        icon.setAnimation(from_top);

        //MUSIC
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        musicBoolean =sharedPreferences.getBoolean(music_SHAREDPREFS,true);

        //SEE IF COMING FROM SETTING OR FROM LEVELS
        if (!start.back_clicked&&!com.example.mouad.snake.normal.back_clicked) {
            music = MediaPlayer.create(this, R.raw.snake_sound);
        }


        //ADD INTERSTITIAL
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        button_onclicks();
    }

    private void button_onclicks() {
        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    Intent intent = new Intent(start_game.this, start.class);
                    startActivity(intent);
                    mInterstitialAd.show();

            }
        });

        Normal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start_game.this, normal.class);
                startActivity(i);
            }
        });

        Settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(start_game.this, settings.class);
                startActivity(i);
            }
        });
    }

    private void make_icon() {
        //MAKING THE ICON
        icon=new ImageView(this);
        icon.setBackgroundResource(R.drawable.icon);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(setx(200),sety(400));
        addContentView(icon,layoutParams1);
        icon.setY(sety(260));
        icon.setX(setx((1080-200)/2));
    }

    private void create_buttons() {
        //CREATE BUTTONS
        multi =new Button(this);
        Normal =new Button(this);
        Settings= new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(multi,layoutParams2);
        multi.setBackgroundResource(R.drawable.multiplayer_button);
        multi.setY(sety(1000));
        multi.setX(setx(390));


        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(Normal,layoutParams3);
        Normal.setBackgroundResource(R.drawable.normal_button);
        Normal.setY(sety(800));
        Normal.setX(setx(390));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(Settings,layoutParams4);
        Settings.setBackgroundResource(R.drawable.settings_button);
        Settings.setX(setx(700));
        Settings.setY(sety(50));
    }

    private void background() {
        //BACKGROUND
        RelativeLayout background=new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color= Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background,backparams);
    }

    public int setx(int x){
        int i;

        i=(x*width)/1080;

        return i;
    }
    public int sety(int x){
        int i;

        i=(x*height)/1770;

        return i;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (musicBoolean){
            music.start();
            music.setLooping(true);
        }
        opened=1;
    }


    @Override
    protected void onStop() {
        super.onStop();
        opened=0;
        app_closed app_closed = new app_closed();
        app_closed.activity_closed();
    }

}
