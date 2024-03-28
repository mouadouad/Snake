package com.example.mouad.snake.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Rects;
import com.example.mouad.snake.Shared;
import com.example.mouad.snake.enums.GameStates;
import com.example.mouad.snake.enums.States;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class Multiplayer extends AppCompatActivity {

    int side;
    float Y_start, X_start, X1_start, Y1_start;
    Boolean started = false;
    FrameLayout dim;
    String my_player, his_player;
    Boolean contentV = false;
    Dialog alertDialog;
    TextView roundTextView;
    Boolean recreate = false, quit = false;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Rects rects = new Rects(this);
        setContentView(rects);

        //SEE WHO ENTERED
        Intent a = getIntent();
        if (a.getSerializableExtra(Shared.who_key) == States.CREATE) { //!!CREATE IS PLAYER1 !!JOIN IS PLAYER2
            my_player = "player1";
            his_player = "player2";
        } else {
            my_player = "player2";
            his_player = "player1";
        }

        //FIND VARIABLES
        final RelativeLayout layout = new RelativeLayout(this);
        roundTextView = new TextView(this);

        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        dim = new FrameLayout(this);
        dim.setForeground(shape);

        //SEE WHICH SIDE MAKE DIM
        if (my_player.equals("player1")) {
            side = Waiting.side;
            if (side == 1) {
                dim.setY(Shared.setY(800));
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((Shared.width), (Shared.height));
                addContentView(dim, layoutParams1);
            } else {
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((Shared.width), (Shared.setY(1600) / 2));
                addContentView(dim, layoutParams1);
            }
            dim.getForeground().setAlpha(200);

        } else {

            side = 1 - Waiting.side;
            if (side == 1) {
                dim.setY(Shared.setY(800));
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((Shared.width), (Shared.height));
                addContentView(dim, layoutParams1);
                dim.getForeground().setAlpha(200);
            } else {
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams((Shared.width), (Shared.setY(1600) / 2));
                addContentView(dim, layoutParams1);
                dim.getForeground().setAlpha(200);
            }
        }

        set_rounds_tv();

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);

        // CHOOSE THE PLACE WHERE TO START
        layout.setOnTouchListener((view, motionEvent) -> {
            Y_start = motionEvent.getY();
            X_start = motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side == 1 && Y_start < Shared.setY(800)) || (side == 0 && Y_start > Shared.setY(800))) {
                //CHECK IF ALREADY STARTED
                if (!started) {

                    //SEE WHAT EDGE IS TH CLOSEST
                    if (side == 0) {
                        if (Shared.setY(1600) - Y_start < Shared.width - X_start && Shared.setY(1600) - Y_start < X_start) {
                            Y_start = Shared.height;
                        } else {
                            if (X_start > Shared.width - X_start) {
                                X_start = Shared.width;
                            } else {
                                X_start = 0;
                            }
                        }

                    } else {
                        if (Y_start < Shared.width - X_start && Y_start < X_start) {
                            Y_start = 0;
                        } else {
                            if (X_start > Shared.width - X_start) {
                                X_start = Shared.width;
                            } else {
                                X_start = 0;
                            }
                        }

                    }

                    MultiplayerMenu.socket.emit("ready", X_start / Shared.width, Y_start / Shared.height, my_player); // SEND START COORDINATES AND SAY THAT AM READY

                    MultiplayerMenu.socket.on("readyBack", args -> runOnUiThread(() -> {
                        JSONObject room = (JSONObject) args[0];

                        try {
                            JSONObject player = room.getJSONObject(his_player);
                            int ready = player.getInt("ready"); // SEE IF OTHER PLAYER IS READY
                            if (ready == 1) {

                                //GET OTHER PLAYER START COORDINATES
                                X1_start = Float.parseFloat(player.getString("x_start")) * Shared.width;
                                Y1_start = Float.parseFloat(player.getString("y_start")) * Shared.height;

                                start();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }));

                }

            }
            return false;
        });


        //GET DATA FROM SERVER
        MultiplayerMenu.socket.on("repeat", args -> runOnUiThread(() -> {
            final JSONObject room = (JSONObject) args[0];

            try {
                final JSONArray json = room.getJSONObject(my_player).getJSONArray("variables");
                final JSONArray json1 = room.getJSONObject(his_player).getJSONArray("variables");

                //CONVERT JSON ARRAYS TO ARRAYLISTS
                for (int i = 0; i < json.length(); i++) {
                    final int[] Jint;
                    Jint = new int[4];
                    for (int a1 = 0; a1 < 4; a1++) {
                        Jint[a1] = json.getJSONArray(i).getInt(a1);
                    }

                    if (Rects.my_variables.toArray().length < i + 1) {
                        Rects.my_variables.add(Jint);
                    } else {
                        Rects.my_variables.set(i, Jint);
                    }

                }

                for (int i = 0; i < json1.length(); i++) {
                    final int[] Jint;
                    Jint = new int[4];
                    for (int a1 = 0; a1 < json1.getJSONArray(i).length(); a1++) {
                        Jint[a1] = json1.getJSONArray(i).getInt(a1);
                    }
                    if (Rects.his_variables.toArray().length < i + 1) {
                        Rects.his_variables.add(Jint);
                    } else {
                        Rects.his_variables.set(i, Jint);
                    }

                }

                //ASSIGN COUNTERS
                Rects.my_counter = room.getJSONObject(my_player).getInt("counter");
                Rects.his_counter = room.getJSONObject(his_player).getInt("counter");

                //SEE IF ALREADY SET CONTENT VIEW
                if (!contentV) {
                    setContentView(rects);
                    place_controllers();
                    back_button();
                    contentV = true;
                }
                rects.repeat();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }));

        //GET WHO WON
        MultiplayerMenu.socket.on("won", args -> runOnUiThread(() -> {
            final String won = (String) args[0];
            if (won.equals(my_player)) {
                gameOver(GameStates.WON);
            } else if (won.equals(his_player)) {
                gameOver(GameStates.LOST);
            } else {
                gameOver(GameStates.DRAW);
            }
        }));

        //IF QUIT
        MultiplayerMenu.socket.on("quit", args -> runOnUiThread(() -> {
            MultiplayerMenu.socket.disconnect();
            MultiplayerMenu.my_score = 0;
            MultiplayerMenu.his_score = 0;
            Intent i = new Intent(Multiplayer.this, GameFinished.class);
            startActivity(i);
        }));

        //WHEN FINISHED
        MultiplayerMenu.socket.on("Rfinished", args -> runOnUiThread(() -> {
            MultiplayerMenu.round = (int) args[0];

            if (my_player.equals("player1")) {
                MultiplayerMenu.my_score = (int) args[1];
                MultiplayerMenu.his_score = (int) args[2];
            } else {
                MultiplayerMenu.my_score = (int) args[2];
                MultiplayerMenu.his_score = (int) args[1];
            }

            if (MultiplayerMenu.round < 4) {
                alertDialog.cancel();
                recreate();
                recreate = true;
            } else {
                MultiplayerMenu.socket.disconnect();
                Intent i = new Intent(Multiplayer.this, GameFinished.class);
                startActivity(i);
            }

        }));
        back_button();
    }

    private void set_rounds_tv() {
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(roundTextView, layoutParams);

        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        roundTextView.setY(Shared.setY(200));
        roundTextView.setX(Shared.setX(400));
        final int yellow = Color.parseColor("#D18D1B");
        roundTextView.setTextColor(yellow);
        roundTextView.setTypeface(fredoka);
        roundTextView.setTextSize(Shared.setX(20));
        roundTextView.setText(R.string.round);
        roundTextView.append(String.valueOf(MultiplayerMenu.round));
    }

    public void start() {
        //WHEN CHOOSE THE PLACE WHERE TO START
        started = true;
        //MAKE THE SCREEN CLEAR
        dim.setVisibility(View.GONE);

    }

    public void place_controllers() {
        final Button left = new Button(this);
        final Button right = new Button(this);

        left.setBackgroundResource(R.drawable.left_button);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(120), Shared.setY(120));
        addContentView(left, layoutParams2);

        left.setY(Shared.height - Shared.statusBarHeight - Shared.setY(200));
        left.setX(Shared.setX(415));

        right.setBackgroundResource(R.drawable.right_button);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(120), Shared.setY(120));
        addContentView(right, layoutParams3);

        right.setY(Shared.height - Shared.statusBarHeight - Shared.setY(200));
        right.setX(Shared.setX(565));

        left.setOnClickListener(view -> {
            //TURN LEFT
            MultiplayerMenu.socket.emit("turn_left", my_player);
        });

        right.setOnClickListener(view -> {
            //TURN RIGHT
            MultiplayerMenu.socket.emit("turn_right", my_player);
        });

    }

    public void gameOver(GameStates result) {
        final RelativeLayout message_box;
        message_box = new RelativeLayout(this);

        if (result.equals(GameStates.WON)) {
            message_box.setBackgroundResource(R.drawable.win_box);
        } else if (result.equals(GameStates.LOST)) {
            message_box.setBackgroundResource(R.drawable.lost_box);
        } else {
            message_box.setBackgroundResource(R.drawable.draw_box);
        }
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(700), Shared.setY(300));
        message_box.setLayoutParams(layoutParams);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public void back_button() {
        Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> {
            MultiplayerMenu.socket.emit("quit");
            MultiplayerMenu.socket.disconnect();
            this.finish();
        });
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Multiplayer.this, MultiplayerMenu.class);
        startActivity(intent);

        MultiplayerMenu.socket.emit("quit");
        MultiplayerMenu.socket.disconnect();
    }
    @Override
    protected void onStop() {
        super.onStop();
        if (!recreate) {
            if (!Shared.foreGround) {
                MainActivity.music.pause();
                MainActivity.isMusicPlaying = false;
            }
            MultiplayerMenu.socket.emit("quit");
            MultiplayerMenu.socket.disconnect();
            quit = true;
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        if (!recreate) {
            Shared.foreGround = true;
            if (MainActivity.musicBoolean && !MainActivity.isMusicPlaying) {
                MainActivity.music.start();
                MainActivity.music.setLooping(true);
                MainActivity.isMusicPlaying = true;
            }
            if (quit) {
                Intent intent = new Intent(Multiplayer.this, MultiplayerMenu.class);
                startActivity(intent);
            }
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        Shared.foreGround = false;
    }

}
