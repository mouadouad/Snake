package com.example.mouad.snake.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.mouad.snake.Shared;
import com.example.mouad.snake.activities.MainActivity;

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

        @SuppressLint("DrawAllocation") Rect topBar=new Rect(0,0,MainActivity.width,Shared.setY(20));
        @SuppressLint("DrawAllocation")  Rect bottomBar=new Rect(0,Shared.setY(1580),MainActivity.width,Shared.setY(1600));
        @SuppressLint("DrawAllocation")  Rect leftBar=new Rect(0,0, Shared.setX(20),Shared.setY(1600));
        @SuppressLint("DrawAllocation")  Rect rightBar=new Rect(MainActivity.width-Shared.setX(20),0,MainActivity.width,Shared.setY(1600));
        
        canvas.drawRect(leftBar,borderPaint);
        canvas.drawRect(rightBar,borderPaint);
        canvas.drawRect(topBar,borderPaint);
        canvas.drawRect(bottomBar,borderPaint);
    }
}
