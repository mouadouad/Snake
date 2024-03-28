package com.example.mouad.snake.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.example.mouad.snake.enums.States;

public class Join extends AppCompatActivity {
    EditText name_of_lobby;
    Button confirm;
    Boolean entered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        buttons_editText();
        back_button();

        //GET MY SIDE FROM SERVER
        MultiplayerMenu.socket.on("side", args -> runOnUiThread(() -> Waiting.side = (Integer) args[0]));

        confirm_click_listener();
    }

    private void confirm_click_listener() {
        confirm.setOnClickListener(view -> {

            //SAY TO SERVER THE ROOM I WANT TO ENTER TO
            MultiplayerMenu.name = name_of_lobby.getText().toString();
            MultiplayerMenu.socket.emit("join", MultiplayerMenu.name);

            MultiplayerMenu.socket.on("entred1", args -> { //SEE IF ROOM EXISTS
                runOnUiThread(() -> {
                    entered = (Boolean) args[0];
                    if (entered) { //ROOM EXISTS AND IS AVAILABLE GO WAITING
                        MultiplayerMenu.name = name_of_lobby.getText().toString();
                        Intent intent = new Intent(Join.this, Waiting.class);
                        intent.putExtra(Shared.who_key, States.JOIN);
                        startActivity(intent);
                    }
                });
            });

        });
    }

    public void back_button() {
        final Button back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> this.finish());
    }

    private void buttons_editText() {
        //CREATE BUTTON AND EDIT TEXT
        name_of_lobby = new EditText(this);
        confirm = new Button(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(name_of_lobby, layoutParams);

        name_of_lobby.setY(Shared.setY(200));
        name_of_lobby.setX(Shared.setX(400));

        final RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams1);
        confirm.setBackgroundResource(R.drawable.join_button);
        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));
    }

    @Override
    public void onResume() {
        super.onResume();
        Shared.foreGround = true;
        if (MainActivity.musicBoolean && !MainActivity.isMusicPlaying) {
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
        if (!Shared.foreGround) {
            MainActivity.music.pause();
            MainActivity.isMusicPlaying = false;
        }
    }
}
