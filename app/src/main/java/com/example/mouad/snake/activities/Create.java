package com.example.mouad.snake.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;


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

        final Intent intent = new Intent(Create.this, Waiting.class);

        MultiplayerMenu.socket.on("created", args -> runOnUiThread(() -> {
            MultiplayerMenu.name = nameOfLobby.getText().toString();
            startActivity(intent);
        }));

        MultiplayerMenu.socket.on("generated", args -> runOnUiThread(() -> {
            MultiplayerMenu.name = (String) args[0];
            startActivity(intent);
        }));
    }
    private void setConfirmButton() {
        final Button confirm = new Button(this);

        Shared.addElement(this, confirm, 300, 150, 400, 500);
        confirm.setBackgroundResource(R.drawable.create_button);

        confirm.setOnClickListener(view -> {

            if (nameOfLobby.getText().toString().contains(" ") ||
                nameOfLobby.getText().toString().isEmpty() ||
                nameOfLobby.getText().toString().length() > 50) {

                nameOfLobby.setError("Please choose another lobby");
            } else {
                MultiplayerMenu.name = nameOfLobby.getText().toString();
                MultiplayerMenu.socket.emit("create", MultiplayerMenu.name);
            }

        });
    }
    private void setEditText() {
        nameOfLobby = new EditText(this);
        Shared.addElement(this, nameOfLobby, 300, 150, 400, 200);
    }
    private void setGenerateButton() {
        final Button generate = new Button(this);
        Shared.addElement(this, generate, 300, 150, 400, 900);
        generate.setBackgroundResource(R.drawable.generate_button);
        generate.setOnClickListener(view -> MultiplayerMenu.socket.emit("generate"));
    }
    private void onBack() {
        Intent intent = new Intent(Create.this, MultiplayerMenu.class);
        startActivity(intent);
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
