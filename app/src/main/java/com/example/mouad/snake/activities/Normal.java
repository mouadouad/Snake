package com.example.mouad.snake.activities;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.mouad.snake.R;
import com.example.mouad.snake.components.Renderer;
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.components.Bot;
import com.example.mouad.snake.components.Game;
import com.example.mouad.snake.enums.GameStates;

import java.util.Random;

public class Normal extends AppCompatActivity {
    private static final short pixels = 30, obsSize = 5;
    private Boolean started = false;
    private Game game;
    private FrameLayout dim;
    private Button back;

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
        final RelativeLayout layout = new RelativeLayout(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.width, Shared.height);
        addContentView(layout, layoutParams);
        layout.setOnTouchListener((view, motionEvent) -> {
            if(view.performClick()) { return false;}
            float playerYStart = motionEvent.getY();
            float playerXStart = motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side == 1 && playerYStart < Shared.setY(800)) || (side == 0 && playerYStart > Shared.setY(800))) {
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

        dim.setY(side * Shared.setY(800));
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MainActivity.width, Shared.setY(800) + Shared.setY(800) * side);
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

        left.setOnClickListener(view -> game.playerTurnLeft());
        right.setOnClickListener(view -> game.playerTurnRight());

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
        ((ViewGroup) back.getParent()).removeView(back);
        message_box.addView(back);

        Button play_again = new Button(this);
        RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        buttonParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        buttonParams.topMargin = Shared.setY(290);
        play_again.setBackgroundResource(R.drawable.play_again_button);
        message_box.addView(play_again, buttonParams);

        play_again.setOnClickListener(view -> recreate());

        Dialog alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);

        if (alertDialog.getWindow() != null) {    //MAKE BACKGROUND OF DIALOG TRANSPARENT
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }
    private void onBack() {
        this.finish();
    }
}
