package com.example.mouad.snake.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class Join extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public static int opened=0;

    EditText name_of_lobby;
    Button confirm;
    int width,height;
    Boolean entred=false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;

        background();
        buttons_editText();
        back_button();


        //GET MY SIDE FROM SERVER
        MultiplayerMenu.socket.on("side", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Waiting.side = (Integer) args[0];

                    }
                });
            }
        });

        confirm_click_listener();
        banner();
    }

    private void confirm_click_listener() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SAY TO SERVER THE ROOM I WANT TO ENTER TO
                MultiplayerMenu.name=name_of_lobby.getText().toString();
                MultiplayerMenu.socket.emit("join", MultiplayerMenu.name);

                MultiplayerMenu.socket.on("entred1", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) { //SEE IF ROOM EXISTS
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                entred= (Boolean) args[0];
                                if (entred){ //ROOM EXISTS AND IS AVAILABLE GO WAITING
                                    MultiplayerMenu.name=name_of_lobby.getText().toString();
                                    Intent intent=new Intent(Join.this, Waiting.class);
                                    intent.putExtra(who_key,"join");
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });

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
                Intent intent= new Intent(Join.this, MultiplayerMenu.class);
                startActivity(intent);
            }
        });
    }

    private void buttons_editText() {
        //CREATE BUTTON AND EDTI TEXT
        name_of_lobby=new EditText(this);
        confirm=new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(name_of_lobby,layoutParams2);

        name_of_lobby.setY(sety(200));
        name_of_lobby.setX(setx(400));

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(confirm,layoutParams3);
        confirm.setBackgroundResource(R.drawable.join_button);
        confirm.setY(sety(500));
        confirm.setX(setx(400));
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Join.this, MultiplayerMenu.class);
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

        //MobileAds.initialize(this,"ca-app-pub-3922358669029120~3985187056");
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
    public void onResume() {
        super.onResume();
        Shared.foreGround = true;
        if (MainActivity.musicBoolean && !MainActivity.isMusicPlaying){
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
        if(!Shared.foreGround){
            MainActivity.music.pause();
            MainActivity.isMusicPlaying = false;
        }
    }
}
