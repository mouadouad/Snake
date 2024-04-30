package com.example.mouad.snake.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;

public class Join extends AppCompatActivity {
    EditText nameOfLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());

        setEditText();
        setConfirmButton();
    }

    private void setEditText() {
        nameOfLobby = new EditText(this);
        Shared.addElement(this, nameOfLobby, 300, 150, 400, 200);

        MultiplayerMenu.socket.on("errorJoining", args -> runOnUiThread(()
                -> nameOfLobby.setError(getString(R.string.errorJoining))));
    }

    private void setConfirmButton() {
        final Button confirm = new Button(this);
        Shared.addElement(this, confirm, 300, 150, 400, 500);
        confirm.setBackgroundResource(R.drawable.join_button);

        confirm.setOnClickListener(view -> {
            MultiplayerMenu.name = nameOfLobby.getText().toString();
            MultiplayerMenu.socket.emit("join", MultiplayerMenu.name);
        });

        MultiplayerMenu.socket.on("joined", args -> runOnUiThread(() -> {
            MultiplayerMenu.name = nameOfLobby.getText().toString();
            Intent intent = new Intent(Join.this, Waiting.class);
            startActivity(intent);
        }));
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
