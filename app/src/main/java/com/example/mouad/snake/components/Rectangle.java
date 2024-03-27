package com.example.mouad.snake;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public class Rectangle extends View {

    public int[] rect;
    private String color;

    public Rectangle(Context context) {
        super(context);

    }

    public Rectangle(Context context, int[] rect, String color) {
        super(context);
        this.rect = rect;
        this.color = color;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        @SuppressLint("DrawAllocation") final Paint blackPaint=new Paint();
        int black_color= Color.parseColor(color);
        blackPaint.setColor(black_color);
        blackPaint.setStyle(Paint.Style.FILL);

        int GAUCHE, HAUT, BAS;

        GAUCHE = rect[0];
        HAUT = rect[1];
        BAS = rect[2];

        @SuppressLint("DrawAllocation") final Rect rectangle= new Rect();

        switch (rect[3]){
            case 90:
                rectangle.set(Shared.setX(-BAS),Shared.setY( GAUCHE),Shared.setX(-HAUT), Shared.setY(GAUCHE +30));
                break;
            case -90:
                rectangle.set(Shared.setX(HAUT),Shared.setY(-GAUCHE -30),Shared.setX( BAS),Shared.setY(-GAUCHE));
                break;
            case 180:
                rectangle.set(Shared.setX(-GAUCHE -30),Shared.setY(-BAS),Shared.setX(-GAUCHE),Shared.setY(-HAUT));
                break;
            case 0:
                rectangle.set(Shared.setX(GAUCHE), Shared.setY(HAUT),Shared.setX( GAUCHE +30), Shared.setY(BAS));
                break;
        }

        canvas.drawRect(rectangle,blackPaint);

    }
}
