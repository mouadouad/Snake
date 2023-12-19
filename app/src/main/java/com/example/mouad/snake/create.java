package com.example.mouad.snake;

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
import com.github.nkzawa.emitter.Emitter;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.Random;


public class create extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public static int opened=0;

    EditText name_of_lobby;
    Button confirm,generate;
    int width,height;
    Boolean entred=false;
    String generated;

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

        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                generate();
            }
        });
        confirm_click_listener();
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
                Intent intent= new Intent(create.this,start.class);
                startActivity(intent);
            }
        });
    }

    private void confirm_click_listener() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //SAY TO SERVER THE ROOM I WANT TO CREATE
              if (name_of_lobby.getText().toString().contains(" ")||name_of_lobby.getText().toString().isEmpty()||name_of_lobby.getText().toString().length()>50){

                  name_of_lobby.setError("Please choose another lobby");

              }else {
                  start.name = name_of_lobby.getText().toString();
                  start.socket.emit("create", start.name);
              }

                start.socket.on("created", new Emitter.Listener() {
                    @Override
                    public void call(final Object... args) { //SEE IF ROOM IS AVAILABLE
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                entred= (Boolean) args[0];
                                if (entred){ //ROOM AVAILABLE GO TO WAITING
                                    start.name = name_of_lobby.getText().toString();
                                    Intent intent=new Intent(create.this, waiting.class);
                                    intent.putExtra(who_key,"create");
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });

            }
        });
    }

    public void generate(){
        String alphabets="ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        generated = "";

        //RANDOM NAME
        for (int i=0;i<6;i++){

            final Random rand=new Random();
            final int position= rand.nextInt(36);

            generated= String.format("%s%s", generated, alphabets.charAt(position));
        }

        //SAY THAT I WANT THIS ROOM
        start.socket.emit("create", generated);

        start.socket.on("created", new Emitter.Listener() {
            @Override
            public void call(final Object... args) { //SEE IF ROOM IS AVAILABLE
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        entred= (Boolean) args[0];
                        if (entred){ //ROOM AVAILABLE GO TO WAITING
                            start.name=generated;
                            Intent intent=new Intent(create.this,waiting.class);
                            intent.putExtra(who_key,"create");
                            startActivity(intent);
                        }else{
                            generate(); //REPEAT
                        }
                    }
                });
            }
        });

    }

    private void buttons_editText() {
        //CREATE BUTTON AND EDIT TEXT
        name_of_lobby=new EditText(this);
        confirm=new Button(this);
        generate= new Button(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(name_of_lobby,layoutParams2);

        name_of_lobby.setY(sety(200));
        name_of_lobby.setX(setx(400));

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(confirm,layoutParams3);
        confirm.setBackgroundResource(R.drawable.create_button);
        confirm.setY(sety(500));
        confirm.setX(setx(400));

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(generate,layoutParams4);
        generate.setBackgroundResource(R.drawable.generate_button);

        generate.setY(sety(900));
        generate.setX(setx(400));
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
        Intent intent = new Intent(create.this,start.class);
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
