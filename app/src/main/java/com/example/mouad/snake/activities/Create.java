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


public class Create extends AppCompatActivity {

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
        setGenerateButton();

        final Intent intent = new Intent(Create.this, Waiting.class);

        MultiplayerMenu.socket.on("created", args -> runOnUiThread(() -> {
            PlayerInfo.name = nameOfLobby.getText().toString();
            startActivity(intent);
        }));

        MultiplayerMenu.socket.on("generated", args -> runOnUiThread(() -> {
            PlayerInfo.name = (String) args[0];
            startActivity(intent);
        }));

        MultiplayerMenu.socket.on("errorCreating", args -> runOnUiThread(()
                -> nameOfLobby.setError(getString(R.string.errorCreating))));
    }
    private void setConfirmButton() {
        final Button confirm = new Button(this);

        Shared.addElement(this, confirm, 300, 150, 400, 500);
        confirm.setBackgroundResource(R.drawable.create_button);

        confirm.setOnClickListener(view -> {

            if (nameOfLobby.getText().toString().contains(" ") ||
                nameOfLobby.getText().toString().isEmpty() ||
                nameOfLobby.getText().toString().length() > 50) {

                nameOfLobby.setError(getString(R.string.errorCreating));
            } else {
                final String lobbyName = nameOfLobby.getText().toString();
                MultiplayerMenu.socket.emit("create", lobbyName);
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
    protected void onDestroy() {
        super.onDestroy();
        MultiplayerMenu.socket.off("created");
        MultiplayerMenu.socket.off("generated");
        MultiplayerMenu.socket.off("errorCreating");
    }
}
