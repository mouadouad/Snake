package com.example.mouad.snake.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.AppClosed;
import com.example.mouad.snake.R;

public class GameFinished extends AppCompatActivity {

    public static final String SHAREDPREFS="sharedprefs";
    public static final  String Level="level";
    public static final  String Xp="xp";
    public static int opened=0;

    TextView result;
    TextView levelTV;
    RelativeLayout div;
    ImageView container,bar;
    Button quit;
    Typeface fredoka;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fredoka=Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        background();
        make_textViews();


        //SEE HOW MANY XP I GOT
        if (MultiplayerMenu.my_score- MultiplayerMenu.his_score==2){
            MultiplayerMenu.xp+=100;
            result.setText(R.string.won);
        }else if (MultiplayerMenu.my_score- MultiplayerMenu.his_score==1){
            MultiplayerMenu.xp+=70;
            result.setText(R.string.won);
        }else if(MultiplayerMenu.my_score- MultiplayerMenu.his_score==-1){
            MultiplayerMenu.xp+=25;
            result.setText(R.string.lost);
        }else if (MultiplayerMenu.my_score- MultiplayerMenu.his_score==-2){
            MultiplayerMenu.xp+=15;
            result.setText(R.string.lost);
        }else if (MultiplayerMenu.my_score== MultiplayerMenu.his_score){
            MultiplayerMenu.xp+=40;
            result.setText(R.string.draw);
        }

        if (MultiplayerMenu.my_score==0&& MultiplayerMenu.his_score==0){
            MultiplayerMenu.xp+=100;
            result.setText(R.string.won);
        }else {
            result.append("\n" + MultiplayerMenu.my_score + "/" + MultiplayerMenu.his_score);
        }

        //UPDATE XP AND LEVEL
        if (MultiplayerMenu.xp>= MultiplayerMenu.level*100){
            MultiplayerMenu.xp-= MultiplayerMenu.level*100;
            MultiplayerMenu.level++;

        }

        levelTV.setText(R.string.level);
        levelTV.append(String.valueOf(MultiplayerMenu.level));

        save();
        xp_bar();
        quit_button();


    }

    private void quit_button() {
        //QUIT BUTTON
        quit= new Button(this);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        addContentView(quit,layoutParams2);
        quit.setBackgroundResource(R.drawable.quit_button);
        quit.setY(sety(1200));
        quit.setX(setx(390));

        quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(GameFinished.this, MultiplayerMenu.class);
                MultiplayerMenu.round=1;
                startActivity(i);

            }
        });
    }

    public void save(){
        //SAVE NEW XP AND LEVEL
        SharedPreferences sharedPreferences= getSharedPreferences(SHAREDPREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(Level, MultiplayerMenu.level);
        editor.putInt(Xp, MultiplayerMenu.xp);

        editor.apply();
    }

    public void xp_bar(){
        //SET THE XP BAR
        container= new ImageView(this);
        bar= new ImageView(this);


        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(setx(500),sety(100));
        addContentView(container,layoutParams5);
        container.setBackgroundResource(R.drawable.container);
        container.setY(sety(800));
        container.setX(setx(290));

        RelativeLayout.LayoutParams layoutParams6 = new RelativeLayout.LayoutParams(setx((500* MultiplayerMenu.xp)/(MultiplayerMenu.level*100)),sety(100));
        addContentView(bar,layoutParams6);
        bar.setBackgroundResource(R.drawable.bar);
        bar.setX(setx(290));
        bar.setY(sety(800));
    }

    public void make_textViews(){

        //SET THE LAYOUT TO ALIGN OBJECTS
        div = new RelativeLayout(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        addContentView(div,layoutParams);

        //SET RESULT TEXT VIEW
        result= new TextView(this);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams1.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(result,layoutParams1);

        result.setTextSize(setx(40));
        int yellow= Color.parseColor("#D18D1B");
        result.setTextColor(yellow);
        result.setTypeface(fredoka);
        result.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        result.setY(sety(200));



        //SET LEVEL TEXT VIEW
        levelTV= new TextView(this);

        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams3.addRule(RelativeLayout.CENTER_HORIZONTAL);
        div.addView(levelTV,layoutParams3);

        int blue= Color.parseColor("#1D8189");
        levelTV.setTextColor(blue);
        levelTV.setTypeface(fredoka);
        levelTV.setTextSize(setx(40));
        levelTV.setY(sety(600));
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
        Intent intent = new Intent(GameFinished.this, MultiplayerMenu.class);
        startActivity(intent);
    }

    public int setx(int x){
        int i;

        i= (int) ((x* Multiplayer.width)/1080);

        return i;
    }

    public int sety(int x){
        int i;

        i= (int) ((x* Multiplayer.height)/1770);

        return i;
    }

    @Override
    protected void onStop() {
        super.onStop();
        opened=0;
        AppClosed app_closed = new AppClosed();
        app_closed.activity_closed();
    }

    @Override
    public void onResume() {
        super.onResume();

        opened=1;

        if (MainActivity.musicBoolean){
            MainActivity.music.start();
            MainActivity.music.setLooping(true);

        }
    }

}
