package com.example.mouad.snake;

import android.os.Handler;


class app_closed {

    void activity_closed(){

        Handler handler2=new Handler();
        Runnable runnable2;


        runnable2=new Runnable() {
            @Override
            public void run() {


                if (start_game.opened==0&&settings.opened==0&&start.opened==0&&create.opened==0&&join.opened==0&&
                        waiting.opened==0&&MainActivity.opened==0&&game_finished.opened==0&&normal.opened==0){

                    start_game.music.pause();

                }

                start_game.opened=0;
                settings.opened=0;
                start.opened=0;
                create.opened=0;
                join.opened=0;
                waiting.opened=0;
                MainActivity.opened=0;
                game_finished.opened=0;
                normal.opened=0;




            }
        };handler2.postDelayed(runnable2,30);



    }





}
