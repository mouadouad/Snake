package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.mouad.snake.activities.MainActivity;
import com.example.mouad.snake.shared.Constants;
import com.example.mouad.snake.shared.Shared;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Renderer extends View {

    private ArrayList<float[]> myVariables, hisVariables;
    private final Paint blackPaint,bluePaint,borderPaint;
    private final RectF rectangle, border;
    private final float[] topCorners, bottomCorners, leftCorners, rightCorners;
    private  float[] corners;
    private final Path path;
    private final float offsetX = 0;
    private float offsetY = 0;
    private float scaleFactor;
    private static final float mapWidth = Constants.mapWidth;
    private static final float mapHeight = Constants.mapHeight;
    private static final float borderWidth = Constants.borderWidth;
    private static final float snakeWidth = Constants.snakeWidth;
    public static final float screenWidth = MainActivity.width;
    public static final float screenHeight = MainActivity.height;

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

        calculateVariables();
    }

    private void calculateVariables(){

        float scaleFactorX = mapWidth / screenWidth;
        float scaleFactorY = mapHeight / screenHeight;

        if ((scaleFactorX < 1 && scaleFactorY < 1) ) {
                scaleFactor = 1 / scaleFactorX;


            offsetY = Math.abs(screenHeight - (mapHeight * scaleFactor)) / 2;
        }else if ((scaleFactorX > 1 && scaleFactorY > 1)){
                scaleFactor = scaleFactorX;
            scaleFactor = 1 / scaleFactor;
            offsetY = Math.abs(screenHeight - (mapHeight * scaleFactor)) / 2;

        }else {
            scaleFactor = scaleFactorX;
            if(scaleFactor > 1){
                scaleFactor = 1 / scaleFactor;
            }
            offsetY = Math.abs(screenHeight - (mapHeight * scaleFactor)) / 2;
        }

    }

    private float computeX(float x){
        return (x* scaleFactor + offsetX);
    }

    private float computeY(float y){
        return (y * scaleFactor + offsetY);
    }

    public float getX(float x){
        return (x- offsetX) / scaleFactor ;
    }

    public float getY(float y){
        return (y- offsetY) / scaleFactor ;
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
    private void convert(JSONArray json, ArrayList<float[]> variables) throws JSONException {
        for (int i = 0; i < json.length(); i++) {
            final float[] converted = new float[4];
            for (int a1 = 0; a1 < 4; a1++) {
                converted[a1] = (float) json.getJSONArray(i).getDouble(a1);
            }
            variables.add(converted);
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
        
        border.set(computeX(0),computeY(0),computeX(borderWidth),computeY(mapHeight));
        canvas.drawRect(border,borderPaint);
        border.set(computeX(mapWidth-borderWidth),computeY(0),computeX(mapWidth),computeY(mapHeight));
        canvas.drawRect(border,borderPaint);
        border.set(computeX(0),computeY(0),computeX(mapWidth),computeY(borderWidth));
        canvas.drawRect(border,borderPaint);
        border.set(computeX(0),computeY(mapHeight - borderWidth),computeX(mapWidth), computeY(mapHeight));
        canvas.drawRect(border,borderPaint);
    }
    private void drawRectangles(Canvas canvas, ArrayList<float[]>  variables, Paint paint){
        for (int i = 0; i < variables.size(); i++){
            final float left = variables.get(i)[0];
            final float top = variables.get(i)[1];
            final float bottom = variables.get(i)[2];

            switch ((int) variables.get(i)[3]) {
                case 90:
                    rectangle.set(computeX(-bottom), computeY(left), computeX(-top), computeY(left + snakeWidth));
                    corners = rightCorners;
                    break;
                case -90:
                    rectangle.set(computeX(top), computeY(-left - snakeWidth), computeX(bottom), computeY(-left));
                    corners = leftCorners;
                    break;
                case 180:
                    rectangle.set(computeX(-left - snakeWidth), computeY(-bottom), computeX(-left), computeY(-top));
                    corners = bottomCorners;
                    break;
                case 0:
                    rectangle.set(computeX(left), computeY(top), computeX(left + snakeWidth), computeY(bottom));
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
