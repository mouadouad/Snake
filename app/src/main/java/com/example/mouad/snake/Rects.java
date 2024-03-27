package com.example.mouad.snake;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import java.util.ArrayList;


public class rects extends View {


    int HAUT, BAS, GAUCHE;
    public static int my_counter,his_counter;
    public static ArrayList<int[]> my_variables =new ArrayList<>(),his_variables =new ArrayList<>();


    public rects(Context context) {
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


        Y_start= (int) (y_start*1770)/start_game.height;
        X_start=(int) (x_start*1080)/start_game.width;

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



        rects.my_variables.add(first_rect_postition);
        rects.my_counter =1;


        // TO SEE WHAT ANGLE START WITH
        int lastposition1=0;

        if (y2_start==0){
            lastposition1=180;
            if(x2_start<=setx(20+30)){x2_start=setx(20+1+30);}
            if(x2_start>=setx(1060)){x2_start=setx(1060-1);}
        }else if (x2_start==0){
            lastposition1 =90;
            if(y2_start>=sety(1580-30)){y2_start=sety(1580-1-30);}
            if(y2_start<=sety(30)){y2_start=sety(20+1);}
        }else if (x2_start==1080){
            lastposition1=-90;
            if(y2_start>=sety(1580)){y2_start=sety(1580-1);}
            if(y2_start<=sety(20+30)){y2_start=sety(20+1+30);}
        }else{
            if(x2_start<=setx(20)){x2_start=setx(20+1);}
            if(x2_start>=setx(1060-30)){x2_start=setx(1060-1-30);}
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

        rects.his_variables.add(first_rect_postition1);
        rects.his_counter =1;

        /*Log.d("1",String.valueOf(rects.his_variables.get(0)[0]) );
        Log.d("2",String.valueOf(rects.his_variables.get(0)[1]) );
        Log.d("3",String.valueOf(rects.his_variables.get(0)[2]) );
        Log.d("4",String.valueOf(rects.his_variables.get(0)[3]) );*/


    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        @SuppressLint("DrawAllocation") final Paint color=new Paint();
        @SuppressLint("DrawAllocation") final Paint color1=new Paint();
        @SuppressLint("DrawAllocation") final Paint color2=new Paint();
        int black_color= Color.parseColor("#20292A");
        color.setColor(black_color);
        color.setStyle(Paint.Style.FILL);
        int blue_color= Color.parseColor("#1D8189");
        color1.setColor(blue_color);
        color1.setStyle(Paint.Style.FILL);
        int border_color= Color.parseColor("#3A4647");
        color2.setColor(border_color);
        color2.setStyle(Paint.Style.FILL);

        canvas.drawRect(normal.checker,color1);


        for (int forloop = 0; forloop<rects.my_counter; forloop++){

            GAUCHE =rects.my_variables.get(forloop )[0];
            HAUT =rects.my_variables.get(forloop )[1];
            BAS =rects.my_variables.get(forloop )[2];


            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();

            //SET MY RECTS

            if (rects.my_variables.get(forloop)[3]==90){
                rect1.set(setx(-BAS),sety( GAUCHE),setx(-HAUT), sety(GAUCHE +30));
                canvas.drawRect(rect1,color);
            }else if (rects.my_variables.get(forloop)[3]==-90){

                rect1.set(setx(HAUT),sety(-GAUCHE -30),setx( BAS),sety(-GAUCHE));
                canvas.drawRect(rect1,color);
            }else if (rects.my_variables.get(forloop)[3]==180){

                rect1.set(setx(-GAUCHE -30),sety(-BAS),setx(-GAUCHE),sety(-HAUT));
                canvas.drawRect(rect1,color);

            }else if (rects.my_variables.get(forloop)[3]==0){

                rect1.set(setx(GAUCHE), sety(HAUT),setx( GAUCHE +30), sety(BAS));
                canvas.drawRect(rect1,color);

            }


        }

        for (int forloop = 0; forloop<rects.his_counter; forloop++){

            GAUCHE =rects.his_variables.get(forloop )[0];
            HAUT =rects.his_variables.get(forloop )[1];
            BAS =rects.his_variables.get(forloop )[2];


            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();

            // SET HIS RECTS
            if (rects.his_variables.get(forloop)[3]==90){
                rect1.set(setx(-BAS),sety( GAUCHE),setx(-HAUT), sety(GAUCHE +30));
                canvas.drawRect(rect1,color1);
            }else if (rects.his_variables.get(forloop)[3]==-90){

                rect1.set(setx(HAUT),sety(-GAUCHE -30),setx( BAS),sety(-GAUCHE));
                canvas.drawRect(rect1,color1);
            }else if (rects.his_variables.get(forloop)[3]==180){

                rect1.set(setx(-GAUCHE -30),sety(-BAS),setx(-GAUCHE),sety(-HAUT));
                canvas.drawRect(rect1,color1);

            }else if (rects.his_variables.get(forloop)[3]==0){

                rect1.set(setx(GAUCHE), sety(HAUT),setx( GAUCHE +30), sety(BAS));
                canvas.drawRect(rect1,color1);

            }


        }



        //SET THE PLAYGROUND
        @SuppressLint("DrawAllocation")  Rect topBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect bottomBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect leftBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect rightBar=new Rect();

        int width = (start_game.width);

        leftBar.set(0,0,setx(20),sety(1600));
        rightBar.set(width-setx(20),0,width,sety(1600));
        topBar.set(0,0,width,sety(20));
        bottomBar.set(0,sety(1580),width,sety(1600));

        canvas.drawRect(leftBar,color2);
        canvas.drawRect(rightBar,color2);
        canvas.drawRect(topBar,color2);
        canvas.drawRect(bottomBar,color2);


    }

    public void repeat(){

        //REFRESH CANVAS
        this.invalidate();

    }

    public int setx(int x){
        int i;

        i=  ((x*start_game.width)/1080);

        return i;
    }

    public int sety(int x){
        int i;

        i=  ((x*start_game.height)/1770);

        return i;
    }



}
