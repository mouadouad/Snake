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

import java.util.Random;


public class Create extends AppCompatActivity {

    EditText nameOfLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());

        setEditText();
        setConfirmButton();
        setGenerateButton();
    }

    private void onBack() {
        Intent intent = new Intent(Create.this, MultiplayerMenu.class);
        startActivity(intent);
    }

    private void setConfirmButton() {
        final Button confirm = new Button(this);

        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(confirm, layoutParams);
        confirm.setBackgroundResource(R.drawable.create_button);
        confirm.setY(Shared.setY(500));
        confirm.setX(Shared.setX(400));

        confirm.setOnClickListener(view -> {

            if (nameOfLobby.getText().toString().contains(" ") ||
                nameOfLobby.getText().toString().isEmpty() ||
                nameOfLobby.getText().toString().length() > 50) {

                nameOfLobby.setError("Please choose another lobby");
            } else {
                MultiplayerMenu.name = nameOfLobby.getText().toString();
                MultiplayerMenu.socket.emit("create", MultiplayerMenu.name);
            }

            //SEE IF ROOM IS AVAILABLE
            MultiplayerMenu.socket.on("created", args -> {
                runOnUiThread(() -> {
                    final boolean isNameAvailable = (boolean) args[0];
                    if (isNameAvailable) {
                        MultiplayerMenu.name = nameOfLobby.getText().toString();
                        Intent intent = new Intent(Create.this, Waiting.class);
                        intent.putExtra(Shared.who_key, States.CREATE);
                        startActivity(intent);
                    }
                });
            });
        });
    }

    private void setEditText() {
        nameOfLobby = new EditText(this);

        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(nameOfLobby, layoutParams2);

        nameOfLobby.setY(Shared.setY(200));
        nameOfLobby.setX(Shared.setX(400));
    }

    private void setGenerateButton() {
        final Button generate = new Button(this);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        addContentView(generate, layoutParams);
        generate.setBackgroundResource(R.drawable.generate_button);

        generate.setY(Shared.setY(900));
        generate.setX(Shared.setX(400));

        generate.setOnClickListener(view -> generate());
    }
    public void generate() {
        final String alphabets = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        String generated = "";

        //RANDOM NAME
        for (int i = 0; i < 6; i++) {

            final Random rand = new Random();
            final int position = rand.nextInt(36);

            generated = String.format("%s%s", generated, alphabets.charAt(position));
        }

        final String lobbyName = generated;

        //SAY THAT I WANT THIS ROOM
        MultiplayerMenu.socket.emit("create", lobbyName);

        //SEE IF ROOM IS AVAILABLE
        MultiplayerMenu.socket.on("created", args -> {
            runOnUiThread(() -> {
                final boolean isNameAvailable = (boolean) args[0];
                if (isNameAvailable) {
                    MultiplayerMenu.name = lobbyName;
                    Intent intent = new Intent(Create.this, Waiting.class);
                    intent.putExtra(Shared.who_key, States.CREATE);
                    startActivity(intent);
                } else {
                    generate();
                }
            });
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(Create.this, MultiplayerMenu.class);
        startActivity(intent);
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
