package com.example.mouad.snake.shared;

public class GameMethods {
    public static float[] findClosestEdge(float x, float y, int side) {

        if (side == 0) {
            if (Constants.mapHeight - y < Constants.mapWidth - x && Constants.mapHeight - y < x) {
                y = Constants.mapHeight;
            } else {
                if (x > Constants.mapHeight - x) {
                    x = Constants.mapWidth;
                } else {
                    x = 0;
                }
            }
        } else {
            if (y < Constants.mapWidth - x && y < x) {
                y = 0;
            } else {
                if (x > Constants.mapWidth - x) {
                    x = Constants.mapWidth;
                } else {
                    x = 0;
                }
            }
        }
        return new float[]{x, y};
    }
}
