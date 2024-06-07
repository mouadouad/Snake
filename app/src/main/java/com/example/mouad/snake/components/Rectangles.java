package com.example.mouad.snake.components;

import java.util.ArrayList;

public class Rectangles{

    final private ArrayList<int[]> Variables;

    public void addRectangle(int[] rect) {
        Variables.add(rect);
    }

    public int[] getLastRectangle() {
        return Variables.get(Variables.size() - 1);
    }

    public ArrayList<int[]> getRectangles() {
        return Variables;
    }

    public Rectangles() {
        Variables = new ArrayList<>();
    }

}
