package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mouad.snake.shared.Shared;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Renderer extends View {

    private ArrayList<int[]> myVariables, hisVariables;
    private final Paint blackPaint,bluePaint,borderPaint;
    private final RectF rectangle, border;
    private final float[] topCorners, bottomCorners, leftCorners, rightCorners;
    private  float[] corners;
    private final Path path;

    public Renderer(Context context) {
        super(context);
        setBackgroundColor(Shared.BACK_COLOR);

        myVariables = new ArrayList<>();
        hisVariables = new ArrayList<>();

        rectangle=new RectF();
        border = new RectF();

        blackPaint=new Paint();
        bluePaint=new Paint();
        borderPaint=new Paint();

        blackPaint.setColor(Shared.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(Shared.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Shared.BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.FILL);

        topCorners = new float[]    {80, 80, 80, 80, 0, 0, 0, 0};
        bottomCorners = new float[] {0, 0, 0, 0, 80, 80, 80, 80};
        leftCorners = new float[]   {80, 80, 0, 0, 0, 0, 80, 80};
        rightCorners = new float[]  {0, 0, 80, 80, 80, 80, 0, 0};
        path = new Path();
    }
    public void setVariables(JSONArray myArray, JSONArray hisArray){
        myVariables.clear();
        hisVariables.clear();

        try {
            convert(myArray, myVariables);
            convert(hisArray, hisVariables);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    public void setVariables(Rectangles playerRectangles, Rectangles botRectangles){
        myVariables = playerRectangles.getRectangles();
        hisVariables = botRectangles.getRectangles();
    }
    private void convert(JSONArray json, ArrayList<int[]> variables) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            final int[] Jint;
            Jint = new int[4];
            for (int a1 = 0; a1 < 4; a1++) {
                Jint[a1] = json.getJSONArray(i).getInt(a1);
            }
            variables.add(Jint);
        }
    }
    public void clear(){
        myVariables.clear();
        hisVariables.clear();
    }
    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);

        drawRectangles(canvas, myVariables, blackPaint);
        drawRectangles(canvas, hisVariables, bluePaint);
        
        border.set(0,0,Shared.setX(20),Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
        border.set(Shared.width-Shared.setX(20),0,Shared.width,Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
        border.set(0,0,Shared.width,Shared.setY(20));
        canvas.drawRect(border,borderPaint);
        border.set(0,Shared.setY(1580),Shared.width,Shared.setY(1600));
        canvas.drawRect(border,borderPaint);
    }
    private void drawRectangles(Canvas canvas, ArrayList<int[]>  variables, Paint paint){
        for (int i = 0; i < variables.size(); i++){
            final int left = variables.get(i)[0];
            final int top = variables.get(i)[1];
            final int bottom = variables.get(i)[2];

            switch (variables.get(i)[3]) {
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
            if(i == variables.size() - 1){
                path.reset();
                path.addRoundRect(rectangle, corners, Path.Direction.CW);
                canvas.drawPath(path, paint);
            }else{
                canvas.drawRect(rectangle, paint);
            }
        }
    }
    public void refresh(){
        this.invalidate();
    }

}
