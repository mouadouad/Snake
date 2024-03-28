package com.example.mouad.snake;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.example.mouad.snake.activities.MainActivity;

import java.util.ArrayList;


public class Rects extends View {

    int HAUT, BAS, GAUCHE;
    public static int my_counter,his_counter;
    public static ArrayList<int[]> my_variables =new ArrayList<>(),his_variables =new ArrayList<>();


    public Rects(Context context) {
        super(context);
        int back_color= Color.parseColor("#82B2B6");
        setBackgroundColor(back_color);

        my_variables.clear();
        his_variables.clear();
        his_counter=0;
        my_counter=0;

    }
    public void get_launch(float x_start,float y_start, float width,float x2_start, float y2_start) {

        int Y_start,X_start;
        int lastposition=0;


        Y_start= (int) (y_start*1770)/ MainActivity.height;
        X_start=(int) (x_start*1080)/ MainActivity.width;

        // TO SEE WHAT ANGLE START WITH
        if (Y_start==0){
            lastposition=180;
            if(X_start<=20+30){X_start=20+1+30;}
            if(X_start>=1060){X_start=1060-1;}

        }else if (X_start==0){
            lastposition =90;
            if(Y_start>=1580-30){Y_start=1580-1-3;}
            if(Y_start<=20){Y_start=(20+1);}
        }else if (X_start==1080){
            lastposition=-90;
            if(Y_start>=1580){Y_start=1580-1;}
            if(Y_start<=20+30){Y_start=20+1+30;}
        }else{
            if(X_start<=20){X_start= 20+1;}
            if(X_start>=1060-30){X_start=1060-1-30;}
        }

        int[] first_rect_postition;
        first_rect_postition = new int[4];

        if(lastposition==0){
            first_rect_postition[0]=  X_start;
            first_rect_postition[1]=  1600-10;
            first_rect_postition[2]=  1600;
        }else if(lastposition==180){
            first_rect_postition[0]=  -X_start;
            first_rect_postition[1]=  -10;
            first_rect_postition[2]=  0;
        }else if(lastposition==90){
            first_rect_postition[0]=  Y_start;
            first_rect_postition[1]= -10;
            first_rect_postition[2]=  0;
        }else{
            first_rect_postition[0]=  -Y_start;
            first_rect_postition[1]=  X_start-10;
            first_rect_postition[2]=  X_start;
        }
        first_rect_postition[3]=lastposition;



        Rects.my_variables.add(first_rect_postition);
        Rects.my_counter =1;


        // TO SEE WHAT ANGLE START WITH
        int lastposition1=0;

        if (y2_start==0){
            lastposition1=180;
            if(x2_start<=Shared.setX(20+30)){x2_start=Shared.setX(20+1+30);}
            if(x2_start>=Shared.setX(1060)){x2_start=Shared.setX(1060-1);}
        }else if (x2_start==0){
            lastposition1 =90;
            if(y2_start>=Shared.setY(1580-30)){y2_start=Shared.setY(1580-1-30);}
            if(y2_start<=Shared.setY(30)){y2_start=Shared.setY(20+1);}
        }else if (x2_start==1080){
            lastposition1=-90;
            if(y2_start>=Shared.setY(1580)){y2_start=Shared.setY(1580-1);}
            if(y2_start<=Shared.setY(20+30)){y2_start=Shared.setY(20+1+30);}
        }else{
            if(x2_start<=Shared.setX(20)){x2_start=Shared.setX(20+1);}
            if(x2_start>=Shared.setX(1060-30)){x2_start=Shared.setX(1060-1-30);}
        }

        int[] first_rect_postition1;
        first_rect_postition1 = new int[4];

        if(lastposition1==0){
            first_rect_postition1[0]= (int) x2_start;
            first_rect_postition1[1]=  1600-10;
            first_rect_postition1[2]=  1600;
        }else if(lastposition1==180){
            first_rect_postition1[0]= (int) -x2_start;
            first_rect_postition1[1]=  -10;
            first_rect_postition1[2]=  0;
        }else if(lastposition1==90){
            first_rect_postition1[0]= (int) y2_start;
            first_rect_postition1[1]= -10;
            first_rect_postition1[2]=  0;
        }else{
            first_rect_postition1[0]= (int) -y2_start;
            first_rect_postition1[1]= (int) (x2_start-10);
            first_rect_postition1[2]= (int) x2_start;
        }
        first_rect_postition1[3]=lastposition1;

        Rects.his_variables.add(first_rect_postition1);
        Rects.his_counter =1;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        @SuppressLint("DrawAllocation") final Paint blackPaint=new Paint();
        @SuppressLint("DrawAllocation") final Paint bluePaint=new Paint();
        @SuppressLint("DrawAllocation") final Paint borderPaint=new Paint();
        int black_color= Color.parseColor("#20292A");
        blackPaint.setColor(black_color);
        blackPaint.setStyle(Paint.Style.FILL);
        int blue_color= Color.parseColor("#1D8189");
        bluePaint.setColor(blue_color);
        bluePaint.setStyle(Paint.Style.FILL);
        int border_color= Color.parseColor("#3A4647");
        borderPaint.setColor(border_color);
        borderPaint.setStyle(Paint.Style.FILL);



        for (int forloop = 0; forloop< Rects.my_counter; forloop++){

            GAUCHE = Rects.my_variables.get(forloop )[0];
            HAUT = Rects.my_variables.get(forloop )[1];
            BAS = Rects.my_variables.get(forloop )[2];


            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();

            //SET MY RECTS

            if (Rects.my_variables.get(forloop)[3]==90){
                rect1.set(Shared.setX(-BAS),Shared.setY( GAUCHE),Shared.setX(-HAUT), Shared.setY(GAUCHE +30));
                canvas.drawRect(rect1,blackPaint);
            }else if (Rects.my_variables.get(forloop)[3]==-90){

                rect1.set(Shared.setX(HAUT),Shared.setY(-GAUCHE -30),Shared.setX( BAS),Shared.setY(-GAUCHE));
                canvas.drawRect(rect1,blackPaint);
            }else if (Rects.my_variables.get(forloop)[3]==180){

                rect1.set(Shared.setX(-GAUCHE -30),Shared.setY(-BAS),Shared.setX(-GAUCHE),Shared.setY(-HAUT));
                canvas.drawRect(rect1,blackPaint);

            }else if (Rects.my_variables.get(forloop)[3]==0){

                rect1.set(Shared.setX(GAUCHE), Shared.setY(HAUT),Shared.setX( GAUCHE +30), Shared.setY(BAS));
                canvas.drawRect(rect1,blackPaint);

            }


        }

        for (int forloop = 0; forloop< Rects.his_counter; forloop++){

            GAUCHE = Rects.his_variables.get(forloop )[0];
            HAUT = Rects.his_variables.get(forloop )[1];
            BAS = Rects.his_variables.get(forloop )[2];


            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();

            // SET HIS RECTS
            if (Rects.his_variables.get(forloop)[3]==90){
                rect1.set(Shared.setX(-BAS),Shared.setY( GAUCHE),Shared.setX(-HAUT), Shared.setY(GAUCHE +30));
                canvas.drawRect(rect1,bluePaint);
            }else if (Rects.his_variables.get(forloop)[3]==-90){

                rect1.set(Shared.setX(HAUT),Shared.setY(-GAUCHE -30),Shared.setX( BAS),Shared.setY(-GAUCHE));
                canvas.drawRect(rect1,bluePaint);
            }else if (Rects.his_variables.get(forloop)[3]==180){

                rect1.set(Shared.setX(-GAUCHE -30),Shared.setY(-BAS),Shared.setX(-GAUCHE),Shared.setY(-HAUT));
                canvas.drawRect(rect1,bluePaint);

            }else if (Rects.his_variables.get(forloop)[3]==0){

                rect1.set(Shared.setX(GAUCHE), Shared.setY(HAUT),Shared.setX( GAUCHE +30), Shared.setY(BAS));
                canvas.drawRect(rect1,bluePaint);

            }
        }

        //SET THE PLAYGROUND
        @SuppressLint("DrawAllocation")  Rect topBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect bottomBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect leftBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect rightBar=new Rect();
        
        leftBar.set(0,0,Shared.setX(20),Shared.setY(1600));
        rightBar.set(Shared.width-Shared.setX(20),0,Shared.width,Shared.setY(1600));
        topBar.set(0,0,Shared.width,Shared.setY(20));
        bottomBar.set(0,Shared.setY(1580),Shared.width,Shared.setY(1600));

        canvas.drawRect(leftBar,borderPaint);
        canvas.drawRect(rightBar,borderPaint);
        canvas.drawRect(topBar,borderPaint);
        canvas.drawRect(bottomBar,borderPaint);

    }
    public void repeat(){
        //REFRESH CANVAS
        this.invalidate();
    }

}
