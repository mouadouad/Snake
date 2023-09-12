package com.example.mouad.snake;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.github.nkzawa.emitter.Emitter;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends AppCompatActivity {

    public final static String who_key = "com.mouad0.hp.snake.who_key";
    public static int opened=0;

    public static float width,height;
    RelativeLayout layout;
    int side;
    float Y_start,X_start,X1_start,Y1_start;
    Boolean started=false;
    FrameLayout dim;
    Button left,right;
    String my_player,his_player;
    Boolean contentV=false;
    Dialog alertDialog ;
    TextView roundtv;
    Boolean recreate =false,quit=false;



    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;


        final rects rects=new rects(this);
        setContentView(rects);

        Intent a = getIntent();

        //SEE WHO ENTRED
        if (a.getStringExtra(who_key).equals("create")){ //!!CREATE IS PLAYER1 !!JOIN IS PLAYER2

            my_player="player1";
            his_player="player2";

        }else {

            my_player="player2";
            his_player="player1";

        }

        //FIND VARIABLES
        layout =new RelativeLayout(this);
        left=new Button(this);
        right=new Button(this);
        roundtv= new TextView(this);


        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        dim=new FrameLayout(this);
        dim.setForeground(shape);


        //SEE WHICH SIDE MAKE DIM
        if (my_player.equals("player1")) {
            side=waiting.side;
            if (side == 1) {

                dim.setY(sety(800) );
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (width), (int) (height));
                addContentView(dim, layoutParams1);

            } else {
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (width), (sety(1600) / 2));
                addContentView(dim, layoutParams1);

            }
            dim.getForeground().setAlpha(200);

        }else {

            side=1-waiting.side;
            if (side == 1) {

                dim.setY(sety(800) );
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (width), (int) (height ));
                addContentView(dim, layoutParams1);
                dim.getForeground().setAlpha(200);

            } else {
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((int) (width), (sety(1600) / 2));
                addContentView(dim, layoutParams1);
                dim.getForeground().setAlpha(200);

            }

        }

        set_rounds_tv();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams((int) width, (int) height);
        addContentView(layout,layoutParams);

        // CHOOSE THE PLACE WHERE TO START
        layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Y_start=motionEvent.getY();
                X_start=motionEvent.getX();
                // CHECK IF THE RIGHT SIDE IS CLICKED
                if ((side==1&&Y_start<sety(800))||(side==0&&Y_start>sety(800))){
                    //CHECK IF ALREADY STARTED
                    if (!started) {

                        //SEE WHAT EDGE IS TH CLOSEST
                        if (side==0){
                            if (sety(1600)-Y_start<width-X_start&&sety(1600)-Y_start<X_start){
                                Y_start=height;
                            }else{
                                if (X_start>width-X_start){
                                    X_start=width;
                                }else {
                                    X_start=0;
                                }
                            }

                        }else {
                            if (Y_start<width-X_start&&Y_start<X_start){
                                Y_start=0;
                            }else{
                                if (X_start>width-X_start){
                                    X_start=width;
                                }else {
                                    X_start=0;
                                }
                            }

                        }

                        start.socket.emit("ready",X_start/width,Y_start/height,my_player); // SEND START COORDINATES AND SAY THAT AM READY

                        start.socket.on("readyBack", new Emitter.Listener() {
                            @Override
                            public void call(final Object... args) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        JSONObject room = (JSONObject) args[0];

                                        try {
                                            JSONObject player = room.getJSONObject(his_player);
                                            int ready = player.getInt("ready"); // SEE IF OTHER PLAYER IS READY
                                            if (ready==1){

                                                //GET OTHER PLAYER START COORDINATES
                                               X1_start=Float.parseFloat( player.getString("x_start"))*width;
                                               Y1_start= Float.parseFloat( player.getString("y_start"))*height;

                                               start();

                                            }

                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                    }
                                });
                            }
                        });

                    }

                }
                return false;
            }
        });



        //GET DATA FROM SERVER
        start.socket.on("repeat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final JSONObject room = (JSONObject) args[0];

                        try {
                            final JSONArray json = room.getJSONObject(my_player).getJSONArray("variables");
                            final JSONArray json1 = room.getJSONObject(his_player).getJSONArray("variables");

                            //CONVERT JSON ARRAYS TO ARRATLISTS
                            for (int i=0;i<json.length();i++){
                                final int[] Jint;
                                Jint = new int[4];
                                for (int a=0;a<4;a++){
                                    Jint[a]=json.getJSONArray(i).getInt(a);
                                }

                                if (com.example.mouad.snake.rects.my_variables.toArray().length<i+1){
                                    com.example.mouad.snake.rects.my_variables.add(Jint);
                                }else {
                                    com.example.mouad.snake.rects.my_variables.set(i, Jint);
                                }

                            }

                            for (int i=0;i<json1.length();i++){
                                final int[] Jint;
                                Jint = new int[4];
                                for (int a=0;a<json1.getJSONArray(i).length();a++){
                                    Jint[a]=json1.getJSONArray(i).getInt(a);
                                }
                                if (com.example.mouad.snake.rects.his_variables.toArray().length<i+1){
                                    com.example.mouad.snake.rects.his_variables.add(Jint);
                                }else {
                                    com.example.mouad.snake.rects.his_variables.set(i, Jint);
                                }

                            }

                            //ASSIGN COUNTERS
                            com.example.mouad.snake.rects.my_counter= room.getJSONObject(my_player).getInt("counter");
                            com.example.mouad.snake.rects.his_counter= room.getJSONObject(his_player).getInt("counter");

                        //SEE IF ALREADY SET CONTENT VIEW
                        if (!contentV) {
                            setContentView(rects);
                            place_controllers();
                            back_button();
                            contentV=true;
                        }
                            rects.repeat();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });


        //GET WHO WON
        start.socket.on("won", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final String won = (String) args[0];
                        if (won.equals(my_player)){
                            won();
                        }else if(won.equals(his_player)){
                            lost();
                        }else {
                            draw();
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
                        Intent i = new Intent(MainActivity.this,game_finished.class);
                        startActivity(i);
                    }
                });
            }
        });

        //WHEN FINISHED
        start.socket.on("Rfinished", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                         start.round = (int) args[0];


                        if (my_player.equals("player1")){
                            start.my_score=(int) args[1];
                            start.his_score=(int) args[2];
                        }else{
                            start.my_score=(int) args[2];
                            start.his_score=(int) args[1];
                        }

                        if(start.round<4){

                            alertDialog.cancel();
                            recreate();
                            recreate=true;

                        }else{

                            start.socket.disconnect();
                            Intent i = new Intent(MainActivity.this,game_finished.class);
                            startActivity(i);

                        }

                    }
                });
            }
        });


        back_button();

    }

    private void set_rounds_tv() {
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(300),sety(200));
        addContentView(roundtv,layoutParams2);

        Typeface fredoka= Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        roundtv.setY(sety(200));
        roundtv.setX(setx(400));
        int yellow= Color.parseColor("#D18D1B");
        roundtv.setTextColor(yellow);
        roundtv.setTypeface(fredoka);
        roundtv.setTextSize(setx(20));
        roundtv.setText(R.string.round);
        roundtv.append(String.valueOf(start.round));
    }

    public void start(){
        //WHEN CHOOSE THE PLACE WHERE TO START
        started=true;
        //MAKE THE SCREEN CLEAR
        dim.setVisibility(View.GONE);

    }

    public void place_controllers(){

        left.setBackgroundResource(R.drawable.left_button);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(120),sety(120));
        addContentView(left,layoutParams2);

        left.setY(height-getStatusBarHeight()-sety(200));
        left.setX(setx(415));

        right.setBackgroundResource(R.drawable.right_button);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(120),sety(120));
        addContentView(right,layoutParams3);

        right.setY(height-getStatusBarHeight()-sety(200));
        right.setX(setx(565));

        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TURN LEFT
                start.socket.emit("turn_left",my_player);
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //TURN RIGHT
               start.socket.emit("turn_right",my_player);

            }});

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void won(){

        //SETTING THE FINISH MESSAGE

        final RelativeLayout message_box;
        message_box=new RelativeLayout(this);

        //BOX
        message_box.setBackgroundResource(R.drawable.win_box);
        RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(setx(700),sety(300) );
        message_box.setLayoutParams(layoutParams4);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);
        if (alertDialog.getWindow()!=null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);


    }

    public void lost(){

        //SETTING THE FINISH MESSAGE

        final RelativeLayout message_box;

        message_box=new RelativeLayout(this);

        //BOX
        message_box.setBackgroundResource(R.drawable.lost_box);
        RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(setx(700),sety(300) );
        message_box.setLayoutParams(layoutParams4);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);
        if (alertDialog.getWindow()!=null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);


    }

    public void draw(){

        //SETTING THE FINISH MESSAGE

        final RelativeLayout message_box;

        message_box=new RelativeLayout(this);

        //BOX
        message_box.setBackgroundResource(R.drawable.draw_box);
        RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(setx(700),sety(300) );
        message_box.setLayoutParams(layoutParams4);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);
        if (alertDialog.getWindow()!=null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);


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

                Intent intent= new Intent(MainActivity.this,start.class);
                startActivity(intent);
                start.socket.emit("quit");
                start.socket.disconnect();



            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(MainActivity.this,start.class);
        startActivity(intent);

        start.socket.emit("quit");
        start.socket.disconnect();


    }

    public int setx(int x){
        int i;

        i= (int) ((x*MainActivity.width)/1080);

        return i;
    }

    public int sety(int x){
        int i;

        i= (int) ((x*MainActivity.height)/1770);

        return i;
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (!recreate){
            opened=0;
            app_closed app_closed = new app_closed();
            app_closed.activity_closed();
            start.socket.emit("quit");
            start.socket.disconnect();
            quit=true;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!recreate) {
            opened = 1;

            if (start_game.musicBoolean) {
                start_game.music.start();
                start_game.music.setLooping(true);

            }
            if (quit) {
                Intent intent = new Intent(MainActivity.this, start.class);
                startActivity(intent);
            }
        }
    }


}
