package com.example.mouad.snake;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.media.MediaPlayer;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class waiting extends AppCompatActivity {


    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public final static String sound_SHAREDPREFS="sound_SHAREDPREFS";
    public final static String SHARED_PREFS="shared_prefs";
    public static int opened=0;

    Boolean joined=false;
    Button play;
    String who="";
    TextView name_lobby;
    public static int side;
    int width,height;
    Boolean sound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;

        background();
        button();
        back_button();
        banner();

        //SOUND
        SharedPreferences sharedPreferences=getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        sound= sharedPreferences.getBoolean(sound_SHAREDPREFS,true);
        final MediaPlayer shine=MediaPlayer.create(this,R.raw.time_start_sound);


        Intent a = getIntent(); // GET WHO ENTRED IF CREATE OR JOIN
        if (a.getStringExtra(who_key).equals("create")){

            name_lobby_tv();

            who="create";
            start.socket.on("entred", new Emitter.Listener() {
                @Override
                public void call(final Object... args) { // SEE IF OTHER PLAYER JOINED SO WE CAN START

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            joined= (Boolean) args[0];
                            if (joined){ //CHANGE THE BACKGROUND OF BUTTON
                                play.setBackgroundResource(R.drawable.play_on_button);
                                if (sound) {
                                    shine.start();
                                }

                            }
                        }
                    });
                }
            });



        }else if (a.getStringExtra(who_key).equals("join")){

            //JOIN ENTRED
            name_lobby_tv();

            who="join";
            joined=true;
            play.setBackgroundResource(R.drawable.play_on_button);

        }else{
            start.socket.emit("random",(start.level/5)+1);

        }

        //GET MY SIDE !! JOIN HAS ALREADY HIS SIDE!!
        start.socket.on("side", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        side = (Integer) args[0];

                    }
                });
            }
        });

        start.socket.on("player_found", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        joined = (Boolean) args[0];
                       if (joined){
                           play.setBackgroundResource(R.drawable.play_on_button);
                           who="create";
                       }

                    }
                });
            }
        });

        start.socket.on("game_found", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         joined = (Boolean) args[0];
                        if (joined){
                            who="join";
                            play.setBackgroundResource(R.drawable.play_on_button);
                        }

                    }
                });
            }
        });

        //IF QUIT
        start.socket.on("quit", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        start.socket.disconnect();
                        start.my_score=0;
                        start.his_score=0;
                        Intent i = new Intent(waiting.this,game_finished.class);
                        startActivity(i);
                    }
                });
            }
        });

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // SEE IF ALL PLAYERS ARE READY AND GO TO MAIN
                    if (joined){
                        Intent intent=new Intent(waiting.this,MainActivity.class);
                        intent.putExtra(who_key,who);
                        startActivity(intent);
                    }
            }
        });



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
                if (!who.equals("join")&&!joined){
                    start.socket.emit("destroy");
                }
                if(joined){
                    start.socket.emit("quit");
                    start.socket.disconnect();

                }

                Intent intent= new Intent(waiting.this,start.class);
                startActivity(intent);
            }
        });
    }

    private void button() {
        //CREATE BUTTON
        play= new Button(this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(play,layoutParams2);
        play.setBackgroundResource(R.drawable.play_off_button);
        play.setY(sety(200));
        play.setX(setx(400));
    }

    private void name_lobby_tv() {

        //SET THE LAYOUT TO ALIGN OBJECTS
        RelativeLayout div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div,layoutParams);

        //SET THE NAME OF THE LOBBY
        name_lobby= new TextView(this);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(600),RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(name_lobby,layoutParams3);
        name_lobby.setTextSize(setx(24));

        String st1=getString(R.string.name_lobby)+ start.name;

        SpannableString ss= new SpannableString(st1);
        ForegroundColorSpan blue=new ForegroundColorSpan(Color.parseColor("#1D8189"));
        ss.setSpan(blue,0,15, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        ForegroundColorSpan yellow=new ForegroundColorSpan(Color.parseColor("#D18D1B"));
        ss.setSpan(yellow,15,15+start.name.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        name_lobby.setText(ss);
        name_lobby.setY(sety(800));

    }

    private void background() {
        //BACKGROUND
        RelativeLayout background=new RelativeLayout(this);
        RelativeLayout.LayoutParams backparams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        int back_color= Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        addContentView(background,backparams);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (!who.equals("join")&&!joined){
            start.socket.emit("destroy");
            start.socket.disconnect();

        }

        if(joined){
            start.socket.emit("quit");
            start.socket.disconnect();

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (!who.equals("join")){
            start.socket.emit("destroy");
        }

        if(joined){
            start.socket.emit("quit");
            start.socket.disconnect();

        }

        Intent intent = new Intent(waiting.this,start.class);
        startActivity(intent);
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
