package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import com.example.mouad.snake.Shared;

public class Rectangle extends View {
    public int[] rect;
    private int color;
    private Paint paint;
    private RectF rectangle;
    private Path path;
    boolean isRounded = false;
    float[] topCorners, bottomCorners, leftCorners, rightCorners, corners;

    public Rectangle(Context context) {
        super(context);

    }

    public Rectangle(Context context, int[] rect, int color) {
        super(context);
        this.rect = rect;
        this.color = color;

        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        rectangle = new RectF();

        topCorners = new float[]{
                80, 80,
                80, 80,
                0, 0,
                0, 0
        };
        bottomCorners = new float[]{
                0, 0,
                0, 0,
                80, 80,
                80, 80
        };
        leftCorners = new float[]{
                80, 80,
                0, 0,
                0, 0,
                80, 80
        };
        rightCorners = new float[]{
                0, 0,
                80, 80,
                80, 80,
                0, 0
        };
        path = new Path();
    }

    public void setRounded() {
        isRounded = true;
    }
    public void setSquared() {
        isRounded = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(color);

        int left, top, bottom;

        left = rect[0];
        top = rect[1];
        bottom = rect[2];

        switch (rect[3]) {
            case 90:
                rectangle.set(Shared.setX(-bottom), Shared.setY(left), Shared.setX(-top), Shared.setY(left + 30));
                corners = rightCorners;
                break;
            case -90:
                rectangle.set(Shared.setX(top), Shared.setY(-left - 30), Shared.setX(bottom), Shared.setY(-left));
                corners = leftCorners;
                break;
            case 180:
                rectangle.set(Shared.setX(-left - 30), Shared.setY(-bottom), Shared.setX(-left), Shared.setY(-top));
                corners = bottomCorners;
                break;
            case 0:
                rectangle.set(Shared.setX(left), Shared.setY(top), Shared.setX(left + 30), Shared.setY(bottom));
                corners = topCorners;
                break;
        }
        if(isRounded) {
            path.reset();
            path.addRoundRect(rectangle, corners, Path.Direction.CW);
            canvas.drawPath(path, paint);
        }else{
            canvas.drawRect(rectangle, paint);
        }


    }
}
