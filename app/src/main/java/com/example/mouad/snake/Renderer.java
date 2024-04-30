package com.example.mouad.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class Renderer extends View {

    private final ArrayList<int[]> my_variables, his_variables;
    private final Paint blackPaint,bluePaint,borderPaint;
    private final Rect rectangle, border;

    public Renderer(Context context) {
        super(context);
        int back_color= Color.parseColor("#82B2B6");
        setBackgroundColor(back_color);

        my_variables = new ArrayList<>();
        his_variables = new ArrayList<>();

        rectangle=new Rect();
        border = new Rect();

        blackPaint=new Paint();
        bluePaint=new Paint();
        borderPaint=new Paint();

        blackPaint.setColor(Shared.BLACK);
        blackPaint.setStyle(Paint.Style.FILL);
        bluePaint.setColor(Shared.BLUE);
        bluePaint.setStyle(Paint.Style.FILL);
        borderPaint.setColor(Shared.BORDER_COLOR);
        borderPaint.setStyle(Paint.Style.FILL);
    }
    public void setVariables(JSONArray my_array, JSONArray his_array){
        my_variables.clear();
        his_variables.clear();

        try {
            convert(my_array,my_variables);
            convert(his_array,his_variables);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
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
        my_variables.clear();
        his_variables.clear();
    }
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawRectangles(canvas,my_variables, blackPaint);
        drawRectangles(canvas,his_variables, bluePaint);
        
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
            final int bot = variables.get(i)[2];

            if (variables.get(i)[3]==90){
                rectangle.set(Shared.setX(-bot),Shared.setY(left),Shared.setX(-top), Shared.setY(left +30));
                canvas.drawRect(rectangle,paint);
            }else if (variables.get(i)[3]==-90){
                rectangle.set(Shared.setX(top),Shared.setY(-left -30),Shared.setX( bot),Shared.setY(-left));
                canvas.drawRect(rectangle,paint);
            }else if (variables.get(i)[3]==180){
                rectangle.set(Shared.setX(-left -30),Shared.setY(-bot),Shared.setX(-left),Shared.setY(-top));
                canvas.drawRect(rectangle,paint);
            }else if (variables.get(i)[3]==0){
                rectangle.set(Shared.setX(left), Shared.setY(top),Shared.setX( left +30), Shared.setY(bot));
                canvas.drawRect(rectangle,paint);
            }
        }
    }
    public void refresh(){
        this.invalidate();
    }

}
