package com.example.mouad.snake;

import com.example.mouad.snake.activities.MainActivity;

public class Shared {

    public static int width, height, statusBarHeight;
    public static final String BLACK = "#20292A", BLUE = "#1D8189";
    public static boolean foreGround = true;

    public final static String SHARED_PREFS="shared_prefs";
    public final static String MUSIC_SHARED_PREFS="MUSIC_SHARED_PREFS";
    public final static String SOUND_SHARED_PREFS = "SOUND_SHARED_PREFS";

    public static int setX(int x) {
        return ((x * MainActivity.width) / 1080);

    }

    public static int setY(int x) {
        return ((x * MainActivity.height) / 1770);

    }

}
