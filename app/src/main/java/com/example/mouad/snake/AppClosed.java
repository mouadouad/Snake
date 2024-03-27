package com.example.mouad.snake;

import android.os.Handler;

import com.example.mouad.snake.activities.Create;
import com.example.mouad.snake.activities.GameFinished;
import com.example.mouad.snake.activities.Join;
import com.example.mouad.snake.activities.MainActivity;
import com.example.mouad.snake.activities.Multiplayer;
import com.example.mouad.snake.activities.MultiplayerMenu;
import com.example.mouad.snake.activities.Normal;
import com.example.mouad.snake.activities.Settings;
import com.example.mouad.snake.activities.Waiting;


public class AppClosed {

    public void activity_closed(){

        Handler handler = new Handler();

        Runnable runnable = () -> {

            if (MainActivity.opened==0&& Settings.opened==0&& MultiplayerMenu.opened==0&& Create.opened==0&& Join.opened==0&&
                    Waiting.opened==0&& Multiplayer.opened==0&& GameFinished.opened==0&& Normal.opened==0){

                MainActivity.music.pause();

            }

            MainActivity.opened=0;
            Settings.opened=0;
            MultiplayerMenu.opened=0;
            Create.opened=0;
            Join.opened=0;
            Waiting.opened=0;
            Multiplayer.opened=0;
            GameFinished.opened=0;
            Normal.opened=0;

        };handler.postDelayed(runnable,30);


    }





}
