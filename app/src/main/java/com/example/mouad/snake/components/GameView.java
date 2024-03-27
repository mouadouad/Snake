package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.example.mouad.snake.components.Border;

public class GameView extends ViewGroup {

    public GameView(Context context) {
        super(context);
        int backColor = Color.parseColor("#82B2B6");
        setBackgroundColor(backColor);

        Border border = new Border(context);
        addView(border);

    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

    }

    @Override
    protected void onLayout(boolean b, int left, int top, int right, int bottom) {
        int childCount = getChildCount();
        getChildAt(0).layout(0, 0, right - left, bottom - top);
        for (int j = 1; j < childCount; j++) {
            ViewGroup child = (ViewGroup) getChildAt(j);
            for (int i = 0; i < child.getChildCount(); i++) {
                View nestedChild = child.getChildAt(i);
                nestedChild.layout(0, 0, right - left, bottom - top);
            }
            child.layout(0, 0, right - left, bottom - top);
        }
    }
}
