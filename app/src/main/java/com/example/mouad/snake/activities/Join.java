package com.example.mouad.snake.activities;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.example.mouad.snake.R;
import com.example.mouad.snake.shared.MusicObserver;
import com.example.mouad.snake.shared.Shared;
import com.example.mouad.snake.shared.PlayerInfo;

public class Join extends AppCompatActivity {
    EditText nameOfLobby;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Shared.background(this, this);
        Shared.banner(this, this);
        Shared.backButton(this, this,  v -> onBack());
        getLifecycle().addObserver(MusicObserver.getInstance());

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
            PlayerInfo.name = nameOfLobby.getText().toString();
            MultiplayerMenu.socket.emit("join", PlayerInfo.name);
        });

        MultiplayerMenu.socket.on("joined", args -> runOnUiThread(() -> {
            PlayerInfo.name = nameOfLobby.getText().toString();
            Intent intent = new Intent(Join.this, Waiting.class);
            startActivity(intent);
        }));
    }

    private void onBack() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MultiplayerMenu.socket.off("joined");
        MultiplayerMenu.socket.off("errorJoining");
    }
}
