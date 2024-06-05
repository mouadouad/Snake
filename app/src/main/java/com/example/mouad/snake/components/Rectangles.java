package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class Rectangles extends ViewGroup {

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

    public Rectangles(Context context) {
        super(context);
        Variables = new ArrayList<>();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        for (int j = 0; j < childCount; j++) {
            View child = getChildAt(j);
            child.layout(0, 0, right - left, bottom - top);
        }
    }

}
