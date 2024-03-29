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
    EditText nameOfLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());

        //GET MY SIDE FROM SERVER
        MultiplayerMenu.socket.on("side", args -> runOnUiThread(() -> Waiting.side = (Integer) args[0]));

        setEditText();
        setConfirmButton();
    }

    private void setEditText() {

        nameOfLobby = new EditText(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(nameOfLobby, layoutParams);

        nameOfLobby.setY(Shared.setY(200));
        nameOfLobby.setX(Shared.setX(400));

    }

    private void setConfirmButton() {
        final Button confirm = new Button(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams);
        confirm.setBackgroundResource(R.drawable.join_button);
        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));

        confirm.setOnClickListener(view -> {
            //SAY TO SERVER THE ROOM I WANT TO ENTER TO
            MultiplayerMenu.name = nameOfLobby.getText().toString();
            MultiplayerMenu.socket.emit("join", MultiplayerMenu.name);

            MultiplayerMenu.socket.on("entred1", args -> { //SEE IF ROOM EXISTS
                runOnUiThread(() -> {
                    final boolean entered = (boolean) args[0];
                    if (entered) { //ROOM EXISTS AND IS AVAILABLE GO WAITING
                        MultiplayerMenu.name = nameOfLobby.getText().toString();
                        Intent intent = new Intent(Join.this, Waiting.class);
                        intent.putExtra(Shared.who_key, States.JOIN);
                        startActivity(intent);
                    }
                });
            });
        });
    }

    private void onBack() {
        finish();
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
