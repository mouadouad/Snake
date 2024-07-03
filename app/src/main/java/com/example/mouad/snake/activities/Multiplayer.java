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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.mouad.snake.R;
import com.example.mouad.snake.components.Renderer;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.components.Game;
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
                e.printStackTrace();
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
            dim.setY(Shared.setY(800));
            dimParams = new RelativeLayout.LayoutParams((Shared.width), (Shared.height));
        } else {
            dimParams = new RelativeLayout.LayoutParams((Shared.width), (Shared.setY(1600) / 2));
        }
        addContentView(dim, dimParams);
        dim.getForeground().setAlpha(200);
    }
    @SuppressLint("ClickableViewAccessibility")
    private void chooseStartingPosition() {
        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);

        layout.setOnTouchListener((view, motionEvent) -> {
            final float y = motionEvent.getY();
            final float x = motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((PlayerInfo.side == 1 && y < Shared.setY(800)) || (PlayerInfo.side == 0 && y > Shared.setY(800))) {
                if (!started) {
                    final float[] coordinates = Game.findClosestEdge(x, y, PlayerInfo.side);

                    MultiplayerMenu.socket.emit("ready", coordinates[0] / Shared.width,
                            coordinates[1] / Shared.height);
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
    private void placeControllers() {
        final Button left = new Button(this);
        final Button right = new Button(this);

        left.setBackgroundResource(R.drawable.left_button);
        right.setBackgroundResource(R.drawable.right_button);

        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        addContentView(layout, layoutParams);

        final RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(Shared.setX(120), Shared.setY(120));
        buttonParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        buttonParams.bottomMargin = Shared.setY(200);
        layout.addView(left, buttonParams);
        layout.addView(right, buttonParams);

        left.setX(Shared.setX(415));
        right.setX(Shared.setX(565));

        left.setOnClickListener(view -> MultiplayerMenu.socket.emit("turnLeft"));
        right.setOnClickListener(view -> MultiplayerMenu.socket.emit("turnRight"));

    }
    private void gameOver(GameStates result) {
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
