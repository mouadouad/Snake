package com.example.mouad.snake.shared;

import android.media.MediaPlayer;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;


public class MusicObserver implements DefaultLifecycleObserver {
    private boolean playMusic = false;
    private boolean isMusicPlaying = false;
    private boolean foreGround = true;
    private MediaPlayer music;

    private static MusicObserver instance = null;

    private MusicObserver() {}

    public static MusicObserver getInstance() {
        return instance;
    }

    public static void setMusic(boolean playMusic) {
        instance.playMusic = playMusic;
        if (playMusic && !instance.isMusicPlaying) {
            instance.music.start();
            instance.music.setLooping(true);
            instance.isMusicPlaying = true;
        }else{
            instance.isMusicPlaying = false;
            instance.music.pause();
        }
    }

    public static void setMusic(MediaPlayer music, boolean playMusic) {
        if (instance == null) {
            instance = new MusicObserver();
            instance.music = music;
        }
        instance.playMusic = playMusic;

        if (playMusic) {
            instance.music.start();
            instance.music.setLooping(true);
            instance.isMusicPlaying = true;
        }

    }

    @Override
    public void onResume(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onResume(owner);
        instance.foreGround = true;
        if(instance.playMusic && !instance.isMusicPlaying) {
            instance.music.start();
            instance.music.setLooping(true);
            instance.isMusicPlaying = true;
        }
    }

    @Override
    public void onPause(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onPause(owner);
        instance.foreGround = false;
        final Handler handler = new Handler();
        final Runnable runnable = () -> {
            if (!instance.foreGround) {
                instance.isMusicPlaying = false;
                instance.music.pause();
            }
        };
        handler.postDelayed(runnable, 100);
    }

}
