package com.example.mouad.snake;

import android.os.Handler;


class app_closed {

    void activity_closed(){

        Handler handler = new Handler();

        Runnable runnable = () -> {

            if (start_game.opened==0&& Settings.opened==0&& Start.opened==0&& Create.opened==0&& Join.opened==0&&
                    Waiting.opened==0&&MainActivity.opened==0&&game_finished.opened==0&& Normal.opened==0){

                start_game.music.pause();

            }

            start_game.opened=0;
            Settings.opened=0;
            Start.opened=0;
            Create.opened=0;
            Join.opened=0;
            Waiting.opened=0;
            MainActivity.opened=0;
            game_finished.opened=0;
            Normal.opened=0;

        };handler.postDelayed(runnable,30);



    }





}
