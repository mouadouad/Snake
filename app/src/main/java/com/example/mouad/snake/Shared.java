package com.example.mouad.snake;

import com.example.mouad.snake.activities.MainActivity;

public class Shared {

    public static int width, height, statusBarHeight;
    public static final String BLACK = "#20292A", BLUE = "#1D8189";

    public static int setX(int x) {
        return ((x * MainActivity.width) / 1080);

    }

    public static int setY(int x) {
        return ((x * MainActivity.height) / 1770);

    }

}
