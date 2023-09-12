package com.example.mouad.snake;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

import java.net.URISyntaxException;
import java.util.Calendar;

public class start extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public static final String SHAREDPREFS="sharedprefs";
    public static final  String Level="level";
    public static final  String Xp="xp";

    public static Boolean back_clicked=false;
    public static int opened=0;


    ImageView container,bar;
    InterstitialAd mInterstitialAd;
    Button create, join, random;
    public static String name;
    int width,height;
    public static Socket socket;
    public static int level,xp;
    public static int round=1,my_score=0,his_score=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;

        background();
        back_button();


            //CONNECT TO SERVER
        try {
          // socket = IO.socket("http://10.0.2.2:3000");

          socket = IO.socket("https://snake1234.herokuapp.com/");
            //create connection
             socket.connect();


        } catch (URISyntaxException e) {
            e.printStackTrace();

        }

        ping();
        buttons();

        //GET SAVED LEVEL AND XP
        SharedPreferences sharedPreferences= getSharedPreferences(SHAREDPREFS,MODE_PRIVATE);
        level=sharedPreferences.getInt(Level,1);
        xp=sharedPreferences.getInt(Xp,0);

        //ADD INTERSTITIAL
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        mInterstitialAd.loadAd(new AdRequest.Builder().build());

        xp_bar();
        banner();

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
                Intent intent= new Intent(start.this,start_game.class);
                startActivity(intent);
                back_clicked=true;
                mInterstitialAd.show();
            }
        });
    }

    public void xp_bar(){

        Typeface fredoka= Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        //SET THE LAYOUT TO ALIGN OBJECTS
        RelativeLayout div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div,layoutParams);

        //SET LEVEL TEXT VIEW
        TextView levelTV= new TextView(this);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV,layoutParams3);

        int blue= Color.parseColor("#1D8189");
        levelTV.setTextColor(blue);
        levelTV.setTypeface(fredoka);
        levelTV.setTextSize(setx(40));
        levelTV.setY(sety(1100));
        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(start.level));

        //SET THE XP BAR
        container= new ImageView(this);
        bar= new ImageView(this);


        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(setx(500),sety(100));
        addContentView(container,layoutParams5);
        container.setBackgroundResource(R.drawable.container);
        container.setY(sety(1300));
        container.setX(setx(290));

        RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(setx((500*start.xp)/(start.level*100)),sety(100));
        addContentView(bar,layoutParams6);
        bar.setBackgroundResource(R.drawable.bar);
        bar.setX(setx(290));
        bar.setY(sety(1300));
    }

    private void ping() {
        //GET LAG
        Calendar rightNow = Calendar.getInstance();
        final long time =rightNow.getTimeInMillis();
        socket.emit("ping");

        start.socket.on("pong", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        Calendar rightNow = Calendar.getInstance();
                        Log.d("LAAAAAAAG", String.valueOf(rightNow.getTimeInMillis()-time));

                    }
                });
            }
        });
    }

    public void buttons(){
        //CREATE BUTTONS
        create =new Button(this);
        join =new Button(this);
        random =new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(200));
        addContentView(create,layoutParams2);
        create.setBackgroundResource(R.drawable.c_lobby_button);
        create.setY(sety(200));
        create.setX(setx(400));


        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(300),sety(200));
        addContentView(join,layoutParams3);
        join.setBackgroundResource(R.drawable.j_lobby_button);
        join.setY(sety(500));
        join.setX(setx(400));


        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(setx(300),sety(180));
        addContentView(random,layoutParams4);
        random.setBackgroundResource(R.drawable.random_button);
        random.setY(sety(800));
        random.setX(setx(400));


        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GO TO CREATE
                Intent i =new Intent(start.this,create.class);
                startActivity(i);

            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GO TO JOIN
                Intent i =new Intent(start.this,join.class);
                startActivity(i);

            }
        });
        random.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //GO TO WAITING
                Intent i =new Intent(start.this,waiting.class);
                i.putExtra(who_key,"random");
                startActivity(i);

            }
        });

    }

    private void background() {
        //BACKGROUND
        RelativeLayout background=new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color= Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background,backparams);
    }

    public void banner(){
        AdView adView = new AdView(this);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout=new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams( width,  height-getStatusBarHeight());
        addContentView(layout,layoutParams1);


        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView,layoutParams);

        MobileAds.initialize(this,"ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);


    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(start.this,start_game.class);
        startActivity(intent);
        back_clicked=true;
        mInterstitialAd.show();
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
