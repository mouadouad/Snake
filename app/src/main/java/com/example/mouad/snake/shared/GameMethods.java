package com.example.mouad.snake.shared;

import com.example.mouad.snake.activities.MainActivity;

public class GameMethods {
    public static float[] findClosestEdge(float x, float y, int side) {

        if (side == 0) {
            if (Shared.setY(1600) - y < MainActivity.width - x && Shared.setY(1600) - y < x) {
                y = MainActivity.height;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        } else {
            if (y < MainActivity.width - x && y < x) {
                y = 0;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        }
        return new float[]{x, y};
    }
}
