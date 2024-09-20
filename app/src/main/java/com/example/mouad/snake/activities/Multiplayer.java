package com.example.mouad.snake.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.components.Renderer;
import com.example.mouad.snake.shared.Constants;
import com.example.mouad.snake.shared.GameMethods;
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.enums.GameStates;
import com.example.mouad.snake.shared.PlayerInfo;

import org.json.JSONArray;
import org.json.JSONException;

public class Multiplayer extends AppCompatActivity {

    private Dialog alertDialog;
    private Boolean started = false, gameFinished = false;
    private Renderer renderer;
    private int round = 1;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        renderer = new Renderer(this);
        setContentView(renderer);

        makeScreenDim();
        setRoundsTextView();
        chooseStartingPosition();
        getLifecycle().addObserver(MusicObserver.getInstance());

        MultiplayerMenu.socket.emit("enterGame");

        MultiplayerMenu.socket.on("startGame", args -> runOnUiThread(() -> {
            started = true;
            setContentView(renderer);
            placeControllers();
            Shared.backButton(this, this, v -> onBack());
        }));

        MultiplayerMenu.socket.on("update", args -> runOnUiThread(() -> {
            final JSONArray room = (JSONArray) args[0];
            try {
                final JSONArray myJson = room.getJSONObject(0).getJSONArray("variables");
                final JSONArray hisJson = room.getJSONObject(1).getJSONArray("variables");

                renderer.setVariables(myJson, hisJson);

                if(!started) {
                    started = true;
                    setContentView(renderer);
                    placeControllers();
                    Shared.backButton(this, this, v -> onBack());
                }

                renderer.refresh();

            } catch (JSONException e) {
                Log.e("TAG", String.valueOf(e));
            }
        }));

        MultiplayerMenu.socket.on("won", args -> runOnUiThread(() -> gameOver(GameStates.WON)));

        MultiplayerMenu.socket.on("lost", args -> runOnUiThread(() -> gameOver(GameStates.LOST)));

        MultiplayerMenu.socket.on("draw", args -> runOnUiThread(() -> gameOver(GameStates.DRAW)));

        MultiplayerMenu.socket.on("roundFinished", args -> runOnUiThread(() -> {
            round = (int) args[0];
            refresh();
        }));

        MultiplayerMenu.socket.on("gameFinished", args -> runOnUiThread(() -> {
            final int myScore = (int) args[0];
            final int hisScore = (int) args[1];
            gameFinished = true;
            finish();
            final Intent intent = new Intent(Multiplayer.this, GameFinished.class);
            intent.putExtra("myScore", myScore);
            intent.putExtra("hisScore", hisScore);
            startActivity(intent);
        }));

        MultiplayerMenu.socket.on("gameEnded", args -> runOnUiThread(() -> {
            gameFinished = true;
            finish();
            final Intent intent = new Intent(Multiplayer.this, GameFinished.class);
            startActivity(intent);
        }));
    }
    private void makeScreenDim() {
        final Resources res = getResources();
        final Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        final FrameLayout dim = new FrameLayout(this);
        dim.setForeground(shape);

        RelativeLayout.LayoutParams dimParams;

        if (PlayerInfo.side == 1) {
            dim.setY((float) MainActivity.height / 2);
        }
        dimParams = new RelativeLayout.LayoutParams((Shared.width), (MainActivity.height / 2));
        addContentView(dim, dimParams);
        dim.getForeground().setAlpha(200);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void chooseStartingPosition() {
        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);

        layout.setOnTouchListener((view, motionEvent) -> {
            float y = renderer.getY(motionEvent.getY());
            float x = renderer.getX(motionEvent.getX());
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((PlayerInfo.side == 1 && y < Constants.mapHeight / 2 || (PlayerInfo.side == 0 && y > Constants.mapHeight / 2))) {
                if (!started) {
                    final float[] coordinates = GameMethods.findClosestEdge(x, y, PlayerInfo.side);

                    MultiplayerMenu.socket.emit("ready", coordinates[0],
                            coordinates[1]);
                }
            }
            return false;
        });
    }
    private void setRoundsTextView() {
        final Typeface fredoka = Typeface.createFromAsset(getAssets(),
                "FredokaOne-Regular.ttf");

        TextView roundTextView = new TextView(this);
        Shared.addElement(this, roundTextView, 300, 200, 400, 200);

        roundTextView.setTextColor(Shared.YELLOW);
        roundTextView.setTypeface(fredoka);
        roundTextView.setTextSize(Shared.setX(20));
        roundTextView.setText(R.string.round);
        roundTextView.append(String.valueOf(round));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void placeControllers() {
        LinearLayout parentLayout = new LinearLayout(this);
        parentLayout.setOrientation(LinearLayout.HORIZONTAL);
        LinearLayout.LayoutParams parentLayoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT
        );

        RelativeLayout leftLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams leftLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        ImageView leftImg = new ImageView(this);
        leftImg.setBackgroundResource(R.drawable.left);
        RelativeLayout.LayoutParams leftImgParams = new RelativeLayout.LayoutParams(500, 500);
        leftImgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        leftLayout.addView(leftImg, leftImgParams);

        RelativeLayout rightLayout = new RelativeLayout(this);
        LinearLayout.LayoutParams rightLayoutParams = new LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.MATCH_PARENT,
                1.0f
        );

        ImageView rightImg = new ImageView(this);
        rightImg.setBackgroundResource(R.drawable.right);
        RelativeLayout.LayoutParams rightImgParams = new RelativeLayout.LayoutParams(500, 500);
        rightImgParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        rightLayout.addView(rightImg, rightImgParams);

        parentLayout.addView(leftLayout, leftLayoutParams);
        parentLayout.addView(rightLayout, rightLayoutParams);
        rightLayout.setVisibility(View.INVISIBLE);

        this.addContentView(parentLayout, parentLayoutParams);

        Animation animation = new AlphaAnimation(1, 0);
        animation.setDuration(1000);
        animation.setInterpolator(new LinearInterpolator());
        leftLayout.startAnimation(animation);
        leftLayout.setVisibility(View.INVISIBLE);

        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            rightLayout.setVisibility(View.VISIBLE);
            rightLayout.startAnimation(animation);
            rightLayout.setVisibility(View.INVISIBLE);

        };
        handler.postDelayed(runnable, 250);

        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);
        layout.setOnTouchListener((view, motionEvent) -> {
            Log.d("tg", "placeControllers: ");
            if(view.performClick()) { return false;}
            float x = motionEvent.getX();
            if (x < Shared.setX(540)){
                MultiplayerMenu.socket.emit("turnLeft");
            }else {
                MultiplayerMenu.socket.emit("turnRight");
            }
            return false;
        });
    }
    private void gameOver(GameStates result) {
        final RelativeLayout message_box = getMessageBox(result);

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);
        if (alertDialog.getWindow() != null) {
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        if(!isFinishing() && !isDestroyed()){
            alertDialog.show();
        }
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @NonNull
    private RelativeLayout getMessageBox(GameStates result) {
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
        return message_box;
    }

    private void refresh() {
        started = false;
        gameFinished = false;
        alertDialog.cancel();
        makeScreenDim();
        setRoundsTextView();
        chooseStartingPosition();
        renderer.clear();
        renderer.refresh();
    }
    private void onBack() {
        finish();
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBack();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!gameFinished) {
            MultiplayerMenu.socket.emit("quitGame");
        }
        MultiplayerMenu.socket.off("gameEnded");
        MultiplayerMenu.socket.off("startGame");
        MultiplayerMenu.socket.off("update");
        MultiplayerMenu.socket.off("gameFinished");
        MultiplayerMenu.socket.off("roundFinished");
        MultiplayerMenu.socket.off("won");
        MultiplayerMenu.socket.off("lost");
        MultiplayerMenu.socket.off("draw");
    }

}
