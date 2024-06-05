package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.mouad.snake.Shared;


public class Border extends View {
    Paint borderPaint;
    Rect border;
    public Border(Context context) {
        super(context);
        border = new Rect();
        borderPaint=new Paint();
        borderPaint.setColor(Shared.BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        border.set(0,0,Shared.setX(20),Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
        border.set(Shared.width-Shared.setX(20),0,Shared.width,Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
        border.set(0,0,Shared.width,Shared.setY(20));
        canvas.drawRect(border,borderPaint);
        border.set(0,Shared.setY(1580),Shared.width,Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
    }
}
