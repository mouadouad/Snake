package com.example.mouad.snake.components;

import java.util.ArrayList;

public class Rectangles{

    final private ArrayList<float[]> Variables;

    public void addRectangle(float[] rect) {
        Variables.add(rect);
    }

    public float[] getLastRectangle() {
        return Variables.get(Variables.size() - 1);
    }

    public ArrayList<float[]> getRectangles() {
        return Variables;
    }

    public Rectangles() {
        Variables = new ArrayList<>();
    }

}
