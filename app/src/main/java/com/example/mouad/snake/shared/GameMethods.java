package com.example.mouad.snake.shared;

public class GameMethods {
    public static float[] findClosestEdge(float x, float y, int side) {

        if (side == 1) {
            final float top = y;
            final float left = x;
            final float right = Constants.mapWidth - x;
            if (top < left && top < right) {
                y = 0;
            } else if (right < left) {
                    x = Constants.mapWidth;
                } else {
                    x = 0;
                }
        } else {
            final float bot = Constants.mapHeight - y;
            final float left = x;
            final float right = Constants.mapWidth - x;
            if (bot < left && bot < right) {
                y = Constants.mapHeight;
            } else if (right < left) {
                x = Constants.mapWidth;
            } else {
                x = 0;
            }
        }
        return new float[]{x, y};
    }
}
