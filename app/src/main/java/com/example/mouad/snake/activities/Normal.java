package com.example.mouad.snake.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.example.mouad.snake.R;
import com.example.mouad.snake.components.Renderer;
import com.example.mouad.snake.shared.Constants;
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.components.Bot;
import com.example.mouad.snake.components.Game;
import com.example.mouad.snake.enums.GameStates;

import java.util.Random;

public class Normal extends AppCompatActivity {
    private static final short pixels = 40, obsSize = 5;
    private Boolean started = false;
    private Game game;
    private FrameLayout dim;
    private Button back;
    Dialog alertDialog;
    private RelativeLayout layout;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeLayout();
        backButton();
        getLifecycle().addObserver(MusicObserver.getInstance());
    }

    private void initializeLayout() {
        final Renderer renderer = new Renderer(this);
        setContentView(renderer);

        final Bot bot = new Bot(this, pixels, obsSize);
        game = new Game(bot, renderer, this);

        final Resources res = getResources();
        final Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        dim = new FrameLayout(this);
        dim.setForeground(shape);

        chooseStartingPosition(setSide());
    }

    @SuppressLint("ClickableViewAccessibility")
    private void chooseStartingPosition(int side) {
        layout= new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);
        layout.setOnTouchListener((view, motionEvent) -> {
            if(view.performClick()) { return false;}
            float playerYStart = game.renderer.getY(motionEvent.getY());
            float playerXStart = game.renderer.getX(motionEvent.getX());
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side == 1 && playerYStart < Constants.mapHeight / 2 || (side == 0 && playerYStart > Constants.mapHeight / 2))) {
                if (!started) {
                    start(playerXStart, playerYStart, side);
                }
            }
            return false;
        });
    }

    private int setSide() {
        final Random random = new Random();
        final int side = random.nextInt(2);

        dim.setY(side * ((float) MainActivity.height / 2));
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                MainActivity.width, (MainActivity.height / 2) + (MainActivity.height / 2) * side);
        addContentView(dim, layoutParams);

        dim.getForeground().setAlpha(200);
        return side;
    }

    private void start(float playerXStart, float playerYStart, int side) {
        game.start(playerXStart, playerYStart, side);
        started = true;
        dim.setVisibility(View.GONE);
        placeControllers();
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

        layout.setOnTouchListener((view, motionEvent) -> {
            if(view.performClick()) { return false;}
            float x = motionEvent.getX();
            if (x < Shared.setX(540)){
                game.playerTurnLeft();
            }else {
                game.playerTurnRight();
            }
            return false;
        });
    }
    private void backButton() {
        back = new Button(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        this.addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(v -> onBack());
    }

    public void gameOver(GameStates result) {
        final RelativeLayout message_box = getMessageBox(result);
        ((ViewGroup) back.getParent()).removeView(back);
        message_box.addView(back);

        Button play_again = new Button(this);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.topMargin = Shared.setY(290);
        play_again.setBackgroundResource(R.drawable.play_again_button);
        message_box.addView(play_again, buttonParams);

        play_again.setOnClickListener(view -> recreate());

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);

        if (alertDialog.getWindow() != null) {    //MAKE BACKGROUND OF DIALOG TRANSPARENT
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        if(!isFinishing() || !isDestroyed()){
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

    private void onBack() {
        this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        alertDialog.dismiss();
    }
}
