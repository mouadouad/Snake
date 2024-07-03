package com.example.mouad.snake.shared;

import android.media.MediaPlayer;
import android.os.Handler;

import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.OnLifecycleEvent;

public class MusicObserver implements LifecycleObserver {
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

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    public void onResumeEvent() {
        instance.foreGround = true;
        if(instance.playMusic && !instance.isMusicPlaying) {
            instance.music.start();
            instance.music.setLooping(true);
            instance.isMusicPlaying = true;
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    public void onPauseEvent() {
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
