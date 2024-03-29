package com.example.mouad.snake.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
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

    Boolean started = false;
    String my_player, his_player;
    Boolean contentV = false;
    Dialog alertDialog;

    Boolean recreate = false, quit = false;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Rects rects = new Rects(this);
        setContentView(rects);

        //SEE WHO ENTERED
        final Intent a = getIntent();
        if (a.getSerializableExtra(Shared.who_key) == States.CREATE) { //!!CREATE IS PLAYER1 !!JOIN IS PLAYER2
            my_player = "player1";
            his_player = "player2";
        } else {
            my_player = "player2";
            his_player = "player1";
        }

        //MAKE THE SCREEN DIM
        final Resources res = getResources();
        final Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        final FrameLayout dim = new FrameLayout(this);
        dim.setForeground(shape);

        //SEE WHICH SIDE MAKE DIM
        int side;
        if (my_player.equals("player1")) {
            side = Waiting.side;
        } else {
            side = 1 - Waiting.side;
        }

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

        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);

        // CHOOSE THE PLACE WHERE TO START
        layout.setOnTouchListener((view, motionEvent) -> {
            final float y = motionEvent.getY();
            final float x = motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side == 1 && y < Shared.setY(800)) || (side == 0 && y > Shared.setY(800))) {
                if (!started) {
                    final float[] coordinates = findClosestEdge(x, y, side);

                    // SEND START COORDINATES AND SAY THAT AM READY
                    MultiplayerMenu.socket.emit("ready", coordinates[0] / Shared.width, coordinates[1] / Shared.height, my_player);

                    MultiplayerMenu.socket.on("readyBack", args -> runOnUiThread(() -> {
                        JSONObject room = (JSONObject) args[0];
                        try {
                            JSONObject player = room.getJSONObject(his_player);
                            int ready = player.getInt("ready"); // SEE IF OTHER PLAYER IS READY
                            if (ready == 1) {
                                started = true;
                                dim.setVisibility(View.GONE);
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
                    placeControllers();
                    Shared.backButton(this, this, v -> onBack());
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

        setRoundsTextView();
    }

    private static float[] findClosestEdge(float x, float y, int side) {

        if (side == 0) {
            if (Shared.setY(1600) - y < MainActivity.width - x && Shared.setY(1600) - y < x) {
                y = MainActivity.height;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        } else {
            if (y < MainActivity.width - x && y < x) {
                y = 0;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        }
        return new float[]{x, y};
    }
    private void setRoundsTextView() {
        final TextView roundTextView = new TextView(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(200));
        addContentView(roundTextView, layoutParams);

        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        roundTextView.setY(Shared.setY(200));
        roundTextView.setX(Shared.setX(400));
        roundTextView.setTextColor(Shared.yellow);
        roundTextView.setTypeface(fredoka);
        roundTextView.setTextSize(Shared.setX(20));
        roundTextView.setText(R.string.round);
        roundTextView.append(String.valueOf(MultiplayerMenu.round));
    }

    public void placeControllers() {
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

        left.setOnClickListener(view -> MultiplayerMenu.socket.emit("turn_left", my_player));

        right.setOnClickListener(view -> MultiplayerMenu.socket.emit("turn_right", my_player));

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

    private void onBack() {
        Intent intent = new Intent(Multiplayer.this, MultiplayerMenu.class);
        startActivity(intent);

        MultiplayerMenu.socket.emit("quit");
        MultiplayerMenu.socket.disconnect();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
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
