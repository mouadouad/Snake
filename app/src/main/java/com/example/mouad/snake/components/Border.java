package com.example.mouad.snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Border extends View {
    public Border(Context context) {
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        @SuppressLint("DrawAllocation") final Paint borderPaint=new Paint();
        int border_color= Color.parseColor("#3A4647");
        borderPaint.setColor(border_color);
        borderPaint.setStyle(Paint.Style.FILL);

        @SuppressLint("DrawAllocation") Rect topBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect bottomBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect leftBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect rightBar=new Rect();

        int width = (start_game.width);

        leftBar.set(0,0,Shared.setX(20),Shared.setY(1600));
        rightBar.set(width-Shared.setX(20),0,width,Shared.setY(1600));
        topBar.set(0,0,width,Shared.setY(20));
        bottomBar.set(0,Shared.setY(1580),width,Shared.setY(1600));

        canvas.drawRect(leftBar,borderPaint);
        canvas.drawRect(rightBar,borderPaint);
        canvas.drawRect(topBar,borderPaint);
        canvas.drawRect(bottomBar,borderPaint);
    }
}
