package com.example.mouad.snake;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import org.tensorflow.lite.Interpreter;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class normal extends AppCompatActivity {


    public static Boolean back_clicked=false;
    public static int opened=0;

    int width,height;
    RelativeLayout layout;
    int side;
    float Y_start,X_start,X1_start=500,Y1_start=1580;
    Boolean started=false;
    FrameLayout dim;
    Button left,right,back;
    Dialog alertDialog ;
    Boolean still_traveling=true,finished=false;
    Runnable runnable;
    Handler handler ;
    rects rects1;
    int GAUCHE,BAS,HAUT;
    int firstbump,firstbump1;
    int list_iterator = 0;
    //int[] my_list = {0, 0, 0, 1, 0, 0, 0, 2, 1, 0, 0, 2, 0, 0, 0, 2, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 2, 0, 2, 0, 0, 1, 0, 0, 0, 1 , 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 2};
    protected Interpreter tflite;
    protected Interpreter.Options tfliteOptions;

    private final Object[] inputs = new Object[4];
    protected int timeout = 10;


    @SuppressLint("DrawAllocation") public static Rect checker=new Rect();


    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Display display = getWindowManager().getDefaultDisplay();
        Point size= new Point() ;
        display.getSize(size);
        width = size.x;
        height = size.y;

        try {
            tfliteOptions = new Interpreter.Options();
            tflite = new Interpreter(loadModelFile(this), tfliteOptions);
        } catch (IOException e) {
            e.printStackTrace();
        }


        rects1 =new rects(this);
        setContentView(rects1);


        //FIND VARIABLES
        layout =new RelativeLayout(this);
        left=new Button(this);
        right=new Button(this);
        handler=  new Handler();


        //MAKE THE SCREEN DIM
        Resources res = getResources();
        Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        dim=new FrameLayout(this);
        dim.setForeground(shape);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width,  height);
        addContentView(layout,layoutParams);

        Random random=new Random();
        side=random.nextInt(2);

        if (side == 1) {
            dim.setY(sety(800));
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams( (width), (height));
            addContentView(dim, layoutParams1);

        } else {
            dim.setY(0);
            RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(width, (sety(1600) / 2));
            addContentView(dim, layoutParams1);

        }
        dim.getForeground().setAlpha(200);

        // CHOOSE THE PLACE WHERE TO START
        layout.setOnTouchListener((view, motionEvent) -> {
            Y_start=motionEvent.getY();
            X_start=motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side==1&&Y_start<sety(800))||(side==0&&Y_start>sety(800))){
                //CHECK IF ALREADY STARTED
                if (!started) {

                    //SEE WHAT EDGE IS TH CLOSEST
                    if (side==0){
                        if (sety(1600)-Y_start<width-X_start&&sety(1600)-Y_start<X_start){
                            Y_start=height;
                        }else{
                            if (X_start>width-X_start){
                                X_start=width;
                            }else {
                                X_start=0;
                            }
                        }

                    }else {
                        if (Y_start<width-X_start&&Y_start<X_start){
                            Y_start=0;
                        }else{
                            if (X_start>width-X_start){
                                X_start=width;
                            }else {
                                X_start=0;
                            }
                        }

                    }
                    //here

                    start();
                }

            }
            return false;
        });

        back_button();

    }

    /** Memory-map the model file in Assets. */
    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("snake1_tf_agents.tflite");
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }

    public void start(){
        //WHEN CHOOSE THE PLACE WHERE TO START
        started=true;
        //MAKE THE SCREEN CLEAR
        dim.setVisibility(View.GONE);

        rects1.get_launch(X_start,Y_start,width,X1_start,Y1_start);
        place_controllers();
        rects.my_variables.get(rects.my_counter - 1)[1] = (rects.my_variables.get(rects.my_counter - 1)[1] - 10);
        repeat();


    }

    public void repeat(){

        while (still_traveling&&!finished){

            still_traveling=false;

            runnable= () -> {
                if (!finished) {

                    //rects.my_variables.get(rects.my_counter - 1)[1] = (rects.my_variables.get(rects.my_counter - 1)[1] - 5);
                    rects.his_variables.get(rects.his_counter - 1)[1] = (rects.his_variables.get(rects.his_counter - 1)[1] - 3);

                    still_traveling = true;
                    repeat();
                    rects1.invalidate();
                }

            };handler.postDelayed(runnable,10);

        }

        // SET THE CHECKER (MY FIRST RECT)

        GAUCHE = rects.my_variables.get(rects.my_counter-1 )[0];
        HAUT = rects.my_variables.get(rects.my_counter-1 )[1];
        BAS = rects.my_variables.get(rects.my_counter-1 )[2];

        if (rects.my_variables.get(rects.my_counter-1)[3]==90){
            checker.set(setx(-HAUT-1),sety(GAUCHE),setx(-HAUT), sety(GAUCHE +30));
        }else if (rects.my_variables.get(rects.my_counter-1)[3]==-90){
            checker.set(setx(HAUT),sety(-GAUCHE -30), setx(HAUT-1),sety(-GAUCHE));
        }else if (rects.my_variables.get(rects.my_counter-1)[3]==180){
            checker.set(setx(-GAUCHE -30),sety(-HAUT-1),setx(-GAUCHE),sety(-HAUT));
        }else if (rects.my_variables.get(rects.my_counter-1)[3]==0){
            checker.set(setx(GAUCHE), sety(HAUT),setx( GAUCHE +30), sety(HAUT+1));
        }

        // SET THE CHECKER (HIS FIRST RECT)
        @SuppressLint("DrawAllocation")  Rect checker1=new Rect();
        GAUCHE = rects.his_variables.get(rects.his_counter-1 )[0];
        HAUT = rects.his_variables.get(rects.his_counter-1 )[1];
        BAS = rects.his_variables.get(rects.his_counter-1 )[2];

        if (rects.his_variables.get(rects.his_counter-1)[3]==90){
            checker1.set(-HAUT-1, GAUCHE,-HAUT, GAUCHE +30);
        }else if (rects.his_variables.get(rects.his_counter-1)[3]==-90){
            checker1.set(HAUT,-GAUCHE -30, HAUT-1,-GAUCHE);
        }else if (rects.his_variables.get(rects.his_counter-1)[3]==180){
            checker1.set(-GAUCHE -30,-HAUT-1,-GAUCHE,-HAUT);
        }else if (rects.his_variables.get(rects.his_counter-1)[3]==0){
            checker1.set(GAUCHE, HAUT, GAUCHE +30, HAUT+1);
        }

        //SET THE PLAYGROUND
        @SuppressLint("DrawAllocation")  Rect topBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect bottomBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect leftBar=new Rect();
        @SuppressLint("DrawAllocation")  Rect rightBar=new Rect();

        leftBar.set(0,0,setx(20),sety(1600));
        rightBar.set(width-setx(20),0,width,sety(1600));
        topBar.set(0,0,width,sety(20));
        bottomBar.set(0,sety(1580),width,sety(1600));

        boolean won=false,lost=false;

        for (int forloop = 0; forloop< rects.my_counter; forloop++){

            GAUCHE = rects.my_variables.get(forloop )[0];
            HAUT = rects.my_variables.get(forloop )[1];
            BAS = rects.my_variables.get(forloop )[2];

            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();


            //SET MY RECTS
            if (rects.my_variables.get(forloop)[3]==90){
                rect1.set(setx(-BAS),sety( GAUCHE),setx(-HAUT), sety(GAUCHE +30));

            }else if (rects.my_variables.get(forloop)[3]==-90){

                rect1.set(setx(HAUT),sety(-GAUCHE -30),setx( BAS),sety(-GAUCHE));

            }else if (rects.my_variables.get(forloop)[3]==180){

                rect1.set(setx(-GAUCHE -30),sety(-BAS),setx(-GAUCHE),sety(-HAUT));

            }else if (rects.my_variables.get(forloop)[3]==0){

                rect1.set(setx(GAUCHE), sety(HAUT),setx( GAUCHE +30), sety(BAS));

            }

            //IF I BUMP INTO MYSELF
            if (checker.intersect(rect1)&&forloop<rects.my_counter-1){

                //REMOVE ALL THE RECTS AFTER THE ONE I BUMPED INTO
                for (int remove = rects.my_counter-1; remove>forloop; remove--){
                    rects.my_variables.remove(forloop+1);
                }

                rects.my_counter=forloop+1;

                final int[] ints=new int[4];
                ints[0]=rects.my_variables.get(forloop)[0];
                ints[1]=rects.my_variables.get(forloop)[1];
                ints[2]=rects.my_variables.get(forloop)[2];
                ints[3]=rects.my_variables.get(forloop)[3];

                if (rects.my_variables.get(forloop)[3]==90){
                    ints[1]=-(checker.left*1080)/start_game.width;
                    rects.my_variables.set(forloop,ints);
                }else if (rects.my_variables.get(forloop)[3]==-90){
                    ints[1]=(checker.left*1080)/start_game.width;
                    rects.my_variables.set(forloop,ints);
                }else if (rects.my_variables.get(forloop)[3]==180){
                    ints[1]=-(checker.top*1770)/start_game.height;
                    rects.my_variables.set(forloop,ints);
                }else if (rects.my_variables.get(forloop)[3]==0){
                    ints[1]=(checker.top*1770)/start_game.height;
                    rects.my_variables.set(forloop,ints);
                }


            }

            //IF HE LOSE
            if (checker1.intersect(rect1)){
                won=true;
            }

        }

        for (int forloop = 0; forloop< rects.his_counter; forloop++){

            GAUCHE = rects.his_variables.get(forloop )[0];
            HAUT = rects.his_variables.get(forloop )[1];
            BAS = rects.his_variables.get(forloop )[2];

            @SuppressLint("DrawAllocation") final Rect rect1= new Rect();

            // SET HIS RECTS
            if (rects.his_variables.get(forloop)[3]==90){
                rect1.set(-BAS, GAUCHE,-HAUT, GAUCHE +30);

            }else if (rects.his_variables.get(forloop)[3]==-90){
                rect1.set(HAUT,-GAUCHE -30, BAS,-GAUCHE);
            }else if (rects.his_variables.get(forloop)[3]==180){
                rect1.set(-GAUCHE -30,-BAS,-GAUCHE,-HAUT);

            }else if (rects.his_variables.get(forloop)[3]==0){
                rect1.set(GAUCHE, HAUT, GAUCHE +30, BAS);

            }

            //IF HE BUMP INTO HIMSELF
            if (checker1.intersect(rect1)&&forloop<rects.his_counter-1){

                //REMOVE ALL THE RECTS AFTER THE ONE I BUMPED INTO
                for (int remove = rects.his_counter-1; remove>forloop; remove--){
                    rects.his_variables.remove(forloop+1);
                }

                rects.his_counter=forloop+1;
                final int[] ints=new int[4];
                ints[0]=rects.his_variables.get(forloop)[0];
                ints[1]=rects.his_variables.get(forloop)[1];
                ints[2]=rects.his_variables.get(forloop)[2];
                ints[3]=rects.his_variables.get(forloop)[3];

                if (rects.his_variables.get(forloop)[3]==90){
                    ints[1]=-checker1.left;
                    rects.his_variables.set(forloop,ints);
                }else if (rects.his_variables.get(forloop)[3]==-90){
                    ints[1]=checker1.left;
                    rects.his_variables.set(forloop,ints);
                }else if (rects.his_variables.get(forloop)[3]==180){
                    ints[1]=-checker1.top;
                    rects.his_variables.set(forloop,ints);
                }else if (rects.his_variables.get(forloop)[3]==0){
                    ints[1]=checker1.top;
                    rects.his_variables.set(forloop,ints);
                }
                //MAKE HIM LOSE AFTER BUMP HIMSELF
                won = true;
            }

            //IF I LOSE
            if (checker.intersect(rect1)){
                lost=true;
            }

        }


        //CHECK IF BUMP TO EDGES
        if (checker.intersect(topBar)||checker.intersect(leftBar)||checker.intersect(rightBar)||checker.intersect(bottomBar)){
            if (firstbump>10) {
                lost=true;
            }
            firstbump++;
        }else{
            if (firstbump>0) {
                firstbump = 11;
            }
        }

        if (checker1.intersect(topBar)||checker1.intersect(leftBar)||checker1.intersect(rightBar)||checker1.intersect(bottomBar)){
            if (firstbump1>10) {
                won=true;

            }
            firstbump1++;
        }else{
            if (firstbump1>0) {
                firstbump1 = 11;
            }
        }

        if (won&&lost){game_over("draw"); }else if (won){game_over("won");}else if (lost){game_over("lost");}

        if (timeout==1) {
            final long action;
            prepareInput(rects.his_variables);
            Map<Integer, Object> output = new HashMap<>();
            // TF Agent directly returns the predicted action
            int[] prediction = new int[1];
            output.put(0, prediction);
            tflite.runForMultipleInputsOutputs(inputs, output);
            action = prediction[0];
            Log.d("action", String.valueOf(action));
            //action = my_list[list_iterator];
            list_iterator++;

            if (action == 1) {
                turn_right1();
            } else if (action == 2) {
                turn_left1();
            }
            timeout = 10;
        }else{
            timeout--;
        }

    }

    protected void prepareInput(ArrayList<int[]> variables){

       /*float[][][] input = new float[1][100][4];

        for (int i = 0; i < variables.size() ;i++){
            final float[] f_input = new float[4];
            f_input[0] = variables.get(i)[0];
            f_input[1] = variables.get(i)[1];
            f_input[2] = variables.get(i)[2];
            f_input[3] = variables.get(i)[3];

            input[0][i] = f_input;

        }*/

        int stepType = 0;
        float discount = 0;
        float reward = 0;
        inputs[0] = stepType;
        inputs[1] = discount;
        inputs[2] = preprocess(variables);
        inputs[3] = reward;

    }

    protected float[][] preprocess(ArrayList<int[]> variables){
        float[][][] arr = new float[1][54][36];
        int[] last = new int[2];

        for (int i = 0; i < variables.size() ;i++){
            int x0 = variables.get(i)[0];
            int x1 = variables.get(i)[1];
            int x2 = variables.get(i)[2];
            int x3 = variables.get(i)[3];

            if(x3 == 0){
                int clm = x0 / 30;
                if(clm > 35){
                    clm = 35;
                }
                last[0] = x1 / 30;
                last[1] = clm;
                for(int j = x1 / 30; j < x2 / 30; j++ ){
                    arr[0][j][clm] = 1;

                }
            }

            if(x3 == 180){
                int clm = abs(x0 / 30);
                if(clm > 36){
                    clm = 36;
                }
                for(int j = x1 / 30; j < x2 / 30; j++ ){
                    arr[0][abs(j) - 1][clm - 1] = 1;
                    last[0] = abs(x1 / 30) - 1;
                    last[1] = clm - 1;
                }
            }

            if(x3 == 90){
                int row = abs(x0 / 30);
                if(x1 < -1080){
                    x1 = -1080;
                }
                for(int j = x1 / 30; j < x2 / 30; j++ ){
                    arr[0][row][abs(j) - 1] = 1;
                    last[0] = row;
                    last[1] = abs(x1 / 30) - 1;
                }
            }

            if(x3 == -90){
                int row = abs(x0 / 30);
                last[0] = row - 1;
                last[1] = x1 / 30;
                for(int j = x1 / 30; j < x2 / 30; j++ ){
                    arr[0][row - 1][j] = 1;

                }
            }

        }
        arr[0][last[0]][last[1]] = 2;

        float[][] result = new float[1][3];

        int right = last[1] + 1;
        while (right < 35 && arr[0][last[0]][right] == 0){
            right += 1;
        }
        int left = last[1] - 1;
        while (left > 0 && arr[0][last[0]][left] == 0){
            left -= 1;
        }
        int top = last[0] - 1;
        while (top > 0 && arr[0][top][last[1]] == 0){
            top -= 1;
        }

        int bot = last[0] + 1;
        while (bot < 53 && arr[0][bot][last[1]] == 0 ){
            bot += 1;
        }

        switch(variables.get(rects.his_counter - 1)[3]){
            case 90 :
                result[0][0] = abs(top - last[0]);
                result[0][1] = abs(right - last[1]);
                result[0][2] = abs(bot - last[0]);
                break;
            case -90:
                result[0][0] = abs(bot - last[0]);
                result[0][1] = abs(left - last[1]);
                result[0][2] = abs(top - last[0]);
                break;
            case 180:
                result[0][2] = abs(left - last[1]);
                result[0][1] = abs(bot - last[0]);
                result[0][0] = abs(right - last[1]);
                break;
            default:
                result[0][0] = abs(left - last[1]);
                result[0][1] = abs(top - last[0]);
                result[0][2] = abs(right - last[1]);
                break;

        }

        return result;

    }

    public void turn_right(){

        int lastposition;

        lastposition=rects.my_variables.get(rects.my_counter -1 )[3];

        if (lastposition == 0) {
            lastposition = 90;
        } else if (lastposition == 180) {
            lastposition = -90;
        }else if (lastposition==-90){lastposition=0;
        }else if (lastposition==90){
            lastposition=180;}

        final int[] first_rect_postition;
        first_rect_postition = new int[4];

        first_rect_postition[0]= rects.my_variables.get(rects.my_counter -1 )[1];
        first_rect_postition[1]= -rects.my_variables.get(rects.my_counter -1)[0]-30;
        first_rect_postition[2]= -rects.my_variables.get(rects.my_counter -1)[0]-30;
        first_rect_postition[3]=lastposition;


        rects.my_variables.add(first_rect_postition);
        rects.my_counter++;

    }
    public void turn_left() {

        int lastposition;

        lastposition = rects.my_variables.get(rects.my_counter - 1)[3];

        if (lastposition == 0) {
            lastposition = -90;
        } else if (lastposition == 180) {
            lastposition = 90;
        } else if (lastposition == -90) {
            lastposition = 180;
        } else if (lastposition == 90) {
            lastposition = 0;
        }

        final int[] first_rect_postition;
        first_rect_postition = new int[4];

        first_rect_postition[0] = -rects.my_variables.get(rects.my_counter - 1)[1]-30;
        first_rect_postition[1] = rects.my_variables.get(rects.my_counter - 1)[0];
        first_rect_postition[2] = rects.my_variables.get(rects.my_counter - 1)[0];
        first_rect_postition[3] = lastposition;


        rects.my_variables.add(first_rect_postition);
        rects.my_counter++;
    }

    public void turn_right1(){

        int lastposition;

        lastposition=rects.his_variables.get(rects.his_counter -1 )[3];

        if (lastposition == 0) {
            lastposition = 90;
        } else if (lastposition == 180) {
            lastposition = -90;
        }else if (lastposition==-90){lastposition=0;
        }else if (lastposition==90){
            lastposition=180;}

        final int[] first_rect_postition;
        first_rect_postition = new int[4];

        first_rect_postition[0]= rects.his_variables.get(rects.his_counter -1 )[1];
        first_rect_postition[1]= -rects.his_variables.get(rects.his_counter -1)[0]-30;
        first_rect_postition[2]= -rects.his_variables.get(rects.his_counter -1)[0]-30;
        first_rect_postition[3]=lastposition;


        rects.his_variables.add(first_rect_postition);
        rects.his_counter++;

    }
    public void turn_left1() {

        int lastposition;

        lastposition = rects.his_variables.get(rects.his_counter - 1)[3];

        if (lastposition == 0) {
            lastposition = -90;
        } else if (lastposition == 180) {
            lastposition = 90;
        } else if (lastposition == -90) {
            lastposition = 180;
        } else if (lastposition == 90) {
            lastposition = 0;
        }

        final int[] first_rect_postition;
        first_rect_postition = new int[4];

        first_rect_postition[0] = -rects.his_variables.get(rects.his_counter - 1)[1]- 30;
        first_rect_postition[1] = rects.his_variables.get(rects.his_counter - 1)[0];
        first_rect_postition[2] = rects.his_variables.get(rects.his_counter - 1)[0];
        first_rect_postition[3] = lastposition;


        rects.his_variables.add(first_rect_postition);
        rects.his_counter++;
    }

    public void place_controllers(){

        left.setBackgroundResource(R.drawable.left_button);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(setx(120),sety(120));
        addContentView(left,layoutParams2);

        left.setY(height-getStatusBarHeight()-sety(200));
        left.setX(setx(415));

        right.setBackgroundResource(R.drawable.right_button);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(setx(120),sety(120));
        addContentView(right,layoutParams3);

        right.setY(height-getStatusBarHeight()-sety(200));
        right.setX(setx(565));

        left.setOnClickListener(view -> {

            //TURN LEFT
            turn_left();

        });

        right.setOnClickListener(view -> {

            //TURN RIGHT
            turn_right();

        });

    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public void game_over(String result){
        finished=true;
        Log.i("yeeeeeeeeeeeeeees   ", String.valueOf(list_iterator));

        //SETTING THE FINISH MESSAGE
        final RelativeLayout message_box;
        message_box=new RelativeLayout(this);

        //BOX
        if (result.equals("won")){
            message_box.setBackgroundResource(R.drawable.win_box);
        }else  if (result.equals("lost")){
            message_box.setBackgroundResource(R.drawable.lost_box);
        }else{
            message_box.setBackgroundResource(R.drawable.draw_box);}

        RelativeLayout.LayoutParams layoutParams4= new RelativeLayout.LayoutParams(setx(700),sety(300) );
        message_box.setLayoutParams(layoutParams4);
        ((ViewGroup) back.getParent()).removeView(back);
        message_box.addView(back);

        // PLAY AGAIN BUTTON
        Button play_again= new Button(this );
        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(setx(300),sety(150));
        layoutParams5.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams5.topMargin=sety(290);
        play_again.setBackgroundResource(R.drawable.play_again_button);
        message_box.addView(play_again,layoutParams5);

        play_again.setOnClickListener(view -> {

            //CLEAR EVERYTHING AND RESTART
            recreate();
            rects.my_variables.clear();
            rects.his_variables.clear();
            rects.my_counter=0;
            rects.his_counter=0;

        });

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);

        if (alertDialog.getWindow()!=null) {    //MAKE BACKGROUND OF DIALOG TRANSPARENT
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

    }

    public void back_button(){
        back =new Button(this );
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(setx(100),sety(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back,layoutParams);
        back.setY(sety(50));
        back.setX(setx(50));

        back.setOnClickListener(view -> {

            //CLEAR EVERYTHING AND BACK
            finished=true;
            back_clicked=true;
            Intent intent= new Intent(normal.this,start_game.class);
            startActivity(intent);
            rects.my_variables.clear();
            rects.his_variables.clear();
            rects.my_counter=0;
            rects.his_counter=0;

        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //CLEAR EVERYTHING AND BACK
        back_clicked=true;
        finished=true;
        Intent intent= new Intent(normal.this,start_game.class);
        startActivity(intent);
        rects.my_variables.clear();
        rects.his_variables.clear();
        rects.my_counter=0;
        rects.his_counter=0;
    }

    public int setx(int x){
        int i;

        i=  ((x*width)/1080);

        return i;
    }

    public int sety(int x){
        int i;

        i=  ((x*height)/1770);

        return i;
    }

    @Override
    protected void onStop() {
        super.onStop();
        opened=0;
        app_closed app_closed = new app_closed();
        app_closed.activity_closed();
    }

    @Override
    public void onResume() {
        super.onResume();

        opened=1;

        if (start_game.musicBoolean){
            start_game.music.start();
            start_game.music.setLooping(true);

        }
    }

}
