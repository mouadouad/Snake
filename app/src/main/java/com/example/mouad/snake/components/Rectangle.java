package com.example.mouad.snake.components;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.mouad.snake.Shared;

public class Rectangle extends View {
    public int[] rect;
    private int color;

    public Rectangle(Context context) {
        super(context);
    }

    public Rectangle(Context context, int[] rect, int color) {
        super(context);
        this.rect = rect;
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        @SuppressLint("DrawAllocation") final Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);

        int GAUCHE, HAUT, BAS;

        GAUCHE = rect[0];
        HAUT = rect[1];
        BAS = rect[2];

        @SuppressLint("DrawAllocation") final Rect rectangle = new Rect();

        switch (rect[3]) {
            case 90:
                rectangle.set(Shared.setX(-BAS), Shared.setY(GAUCHE), Shared.setX(-HAUT), Shared.setY(GAUCHE + 30));
                break;
            case -90:
                rectangle.set(Shared.setX(HAUT), Shared.setY(-GAUCHE - 30), Shared.setX(BAS), Shared.setY(-GAUCHE));
                break;
            case 180:
                rectangle.set(Shared.setX(-GAUCHE - 30), Shared.setY(-BAS), Shared.setX(-GAUCHE), Shared.setY(-HAUT));
                break;
            case 0:
                rectangle.set(Shared.setX(GAUCHE), Shared.setY(HAUT), Shared.setX(GAUCHE + 30), Shared.setY(BAS));
                break;
        }

        canvas.drawRect(rectangle, paint);
    }
}
