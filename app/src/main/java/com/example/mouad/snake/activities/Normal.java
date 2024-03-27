package com.example.mouad.snake.activities;

import static java.lang.Math.abs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.example.mouad.snake.AppClosed;
import com.example.mouad.snake.R;
import com.example.mouad.snake.Shared;
import com.example.mouad.snake.components.GameView;
import com.example.mouad.snake.components.Rectangle;
import com.example.mouad.snake.components.Rectangles;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;


public class Normal extends AppCompatActivity {
    public static Boolean back_clicked = false;
    public static int opened = 0;
    private static final short TIMEOUT = 10, pixels = 30;
    protected short timeout = TIMEOUT;
    private Boolean started = false, stillTraveling = true, finished = false;
    static Rect topBar, bottomBar, leftBar, rightBar;
    FrameLayout dim;
    Button back;
    Dialog alertDialog;
    protected Interpreter tfLite;
    protected Interpreter.Options tfliteOptions;
    Rectangles playerRectangles, botRectangles;

    @SuppressLint("ClickableViewAccessibility")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initializeLayout();
        loadModelFile();
        backButton();
    }

    private void initializeLayout() {
        final GameView gameView = new GameView(this);
        setContentView(gameView);

        playerRectangles = new Rectangles(this);
        gameView.addView(playerRectangles);

        botRectangles = new Rectangles(this);
        gameView.addView(botRectangles);

        final Resources res = getResources();
        final Drawable shape = ResourcesCompat.getDrawable(res, R.drawable.shape, null);
        dim = new FrameLayout(this);
        dim.setForeground(shape);

        final int side = setSide();

        // CHOOSE THE PLACE WHERE TO START
        gameView.setOnTouchListener((view, motionEvent) -> {
            if(view.performClick()) { return false;}
            float playerYStart = motionEvent.getY();
            float playerXStart = motionEvent.getX();
            // CHECK IF THE RIGHT SIDE IS CLICKED
            if ((side == 1 && playerYStart < Shared.setY(800)) || (side == 0 && playerYStart > Shared.setY(800))) {
                if (!started) {
                    final float[] coordinatesPlayer = findClosestEdge(playerXStart, playerYStart, side);
                    final float[] coordinatesBot = generateBotPosition(side);
                    start(coordinatesPlayer, coordinatesBot);
                }
            }
            return false;
        });

        leftBar = new Rect(0, 0, 20, 1600);
        rightBar = new Rect(1080 - 20, 0, 1080, 1600);
        topBar = new Rect(0, 0, 1080, 20);
        bottomBar = new Rect(0, 1580, 1080, 1600);
    }

    private void placeControllers() {
        final Button left = new Button(this);
        final Button right = new Button(this);

        left.setBackgroundResource(R.drawable.left_button);
        RelativeLayout.LayoutParams layoutParams2 = new RelativeLayout.LayoutParams(Shared.setX(120), Shared.setY(120));
        addContentView(left, layoutParams2);

        left.setY(MainActivity.height - Shared.statusBarHeight - Shared.setY(200));
        left.setX(Shared.setX(415));

        right.setBackgroundResource(R.drawable.right_button);
        RelativeLayout.LayoutParams layoutParams3 = new RelativeLayout.LayoutParams(Shared.setX(120), Shared.setY(120));
        addContentView(right, layoutParams3);

        right.setY(MainActivity.height - Shared.statusBarHeight - Shared.setY(200));
        right.setX(Shared.setX(565));

        left.setOnClickListener(view -> playerTurnLeft());
        right.setOnClickListener(view -> playerTurnRight());

    }

    private void loadModelFile() {
        try {
            tfliteOptions = new Interpreter.Options();
            tfLite = new Interpreter(Objects.requireNonNull(loadModelFile(this)), tfliteOptions);
            Log.d("input", String.valueOf(tfLite.getInputTensorCount()));
            Log.d("input", Arrays.toString(tfLite.getInputTensor(0).shape()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
            FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
            FileChannel fileChannel = inputStream.getChannel();
            long startOffset = fileDescriptor.getStartOffset();
            long declaredLength = fileDescriptor.getDeclaredLength();
            return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private int setSide() {
        final Random random = new Random();
        final int side = random.nextInt(2);

        dim.setY(side * Shared.setY(800));
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(MainActivity.width, Shared.setY(800) + Shared.setY(800) * side);
        addContentView(dim, layoutParams);

        dim.getForeground().setAlpha(200);
        return side;
    }

    private static float[] findClosestEdge(float x, float y, int side) {

        if (side == 0) {
            if (Shared.setY(1600) - y < MainActivity.width - x && Shared.setY(1600) - y < x) {
                y = MainActivity.height;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        } else {
            if (y < MainActivity.width - x && y < x) {
                y = 0;
            } else {
                if (x > MainActivity.width - x) {
                    x = MainActivity.width;
                } else {
                    x = 0;
                }
            }
        }
        return new float[]{x, y};
    }

    private static float[] generateBotPosition(int hisSide) {
        final Random random = new Random();
        final int x = random.nextInt(MainActivity.width);
        int y = random.nextInt(Shared.setY(800));
        if (hisSide == 1) {
            y += Shared.setY(800);
        }
        return findClosestEdge(x, y, (hisSide + 1) % 2);
    }

    private static float[] findStartingPosition(float x, float y) {
        int angle = 0;

        if (y == 0) {
            angle = 180;
            if (x <= (20 + 30)) {
                x = (20 + 1 + 30);
            }
            if (x >= (1060)) {
                x = (1060 - 1);
            }
        } else if (x == 0) {
            angle = 90;
            if (y >= (1580 - 30)) {
                y = (1580 - 1 - 30);
            }
            if (y <= (30)) {
                y = (20 + 1);
            }
        } else if (x == 1080) {
            angle = -90;
            if (y >= (1580)) {
                y = (1580 - 1);
            }
            if (y <= (20 + 30)) {
                y = (20 + 1 + 30);
            }
        } else {
            if (x <= (20)) {
                x = (20 + 1);
            }
            if (x >= (1060 - 30)) {
                x = (1060 - 1 - 30);
            }
        }

        return new float[]{x, y, angle};
    }

    private static int[] startingRect(int x, int y, int angle) {
        final int[] firstRect = new int[4];

        if (angle == 0) {
            firstRect[0] = x;
            firstRect[1] = 1600 - 10;
            firstRect[2] = 1600;
        } else if (angle == 180) {
            firstRect[0] = -x;
            firstRect[1] = -10;
            firstRect[2] = 0;
        } else if (angle == 90) {
            firstRect[0] = y;
            firstRect[1] = -10;
            firstRect[2] = 0;
        } else {
            firstRect[0] = -y;
            firstRect[1] = (x - 10);
            firstRect[2] = x;
        }
        firstRect[3] = angle;

        return firstRect;

    }

    private void addStartingRectangles(float[] playerCoordinates, float[] botCoordinates) {

        final float[] botPosition = findStartingPosition(botCoordinates[0], botCoordinates[1]);
        final int[] firstRect = startingRect((int) botPosition[0], (int) botPosition[1], (int) botPosition[2]);

        botRectangles.addRectangle(firstRect);
        Rectangle rectangle = new Rectangle(this, firstRect, Shared.BLACK);
        botRectangles.addView(rectangle);

        final float[] playerPosition = findStartingPosition(playerCoordinates[0] * 1080 / MainActivity.width, playerCoordinates[1] * 1770 / MainActivity.height);
        int[] firstRectPlayer = startingRect((int) playerPosition[0], (int) playerPosition[1], (int) playerPosition[2]);

        playerRectangles.addRectangle(firstRectPlayer);
        rectangle = new Rectangle(this, firstRectPlayer, Shared.BLUE);
        playerRectangles.addView(rectangle);
    }

    private void start(float[] playerCoordinates, float[] botCoordinates) {
        started = true;
        dim.setVisibility(View.GONE);

        addStartingRectangles(playerCoordinates, botCoordinates);
        placeControllers();
        repeat();
    }

    private void repeat() {
        while (stillTraveling && !finished) {
            stillTraveling = false;
            final Handler handler = new Handler();

            final Runnable runnable = () -> {
                if (!finished) {
                    playerRectangles.getLastRectangle()[1] -= (pixels / TIMEOUT);
                    botRectangles.getLastRectangle()[1] -= (pixels / TIMEOUT);

                    stillTraveling = true;
                    repeat();

                    playerRectangles.getChildAt(playerRectangles.getChildCount() - 1).invalidate();
                    botRectangles.getChildAt(botRectangles.getChildCount() - 1).invalidate();
                    botPlay();
                }
            };
            handler.postDelayed(runnable, 10);
        }

        checking();
    }

    private void checking() {

        boolean won = false, lost = false;

        final Rect playerChecker = getChecker(playerRectangles.getLastRectangle());
        final Rect botChecker = getChecker(botRectangles.getLastRectangle());

        if (hitBorder(playerChecker, playerRectangles)) {
            lost = true;
        }

        if (hitBorder(botChecker, botRectangles)) {
            won = true;
        }

        for (int i = 0; i < playerRectangles.getRectangles().size(); i++) {
            final Rect rect = getRect(playerRectangles.getRectangles().get(i));

            if (botChecker.intersect(rect)) {
                won = true;
            }

            if (playerChecker.intersect(rect) && i < playerRectangles.getRectangles().size() - 1) {
                for (int remove = playerRectangles.getRectangles().size() - 1; remove > i; remove--) {
                    playerRectangles.getRectangles().remove(i + 1);
                    playerRectangles.removeViewAt(i + 1);
                }

                final int[] ints = playerRectangles.getRectangles().get(i);

                switch (ints[3]) {
                    case 90:
                        ints[1] = -(playerChecker.left * 1080) / MainActivity.width;
                        break;
                    case -90:
                        ints[1] = (playerChecker.left * 1080) / MainActivity.width;
                        break;
                    case 180:
                        ints[1] = -(playerChecker.top * 1770) / MainActivity.height;
                        break;
                    case 0:
                        ints[1] = (playerChecker.top * 1770) / MainActivity.height;
                        break;
                }
                playerRectangles.getRectangles().set(i, ints);

            }
        }

        for (int i = 0; i < botRectangles.getRectangles().size(); i++) {
            final Rect rect = getRect(botRectangles.getRectangles().get(i));

            if (playerChecker.intersect(rect)) {
                lost = true;
            }

            if (botChecker.intersect(rect) && i < botRectangles.getRectangles().size() - 1) {
                for (int remove = botRectangles.getRectangles().size() - 1; remove > i; remove--) {
                    botRectangles.getRectangles().remove(i + 1);
                    botRectangles.removeViewAt(i + 1);
                }
                final int[] ints = botRectangles.getRectangles().get(i);

                switch (ints[3]) {
                    case 90:
                        ints[1] = -(botChecker.left * 1080) / MainActivity.width;
                        break;
                    case -90:
                        ints[1] = (botChecker.left * 1080) / MainActivity.width;
                        break;
                    case 180:
                        ints[1] = -(botChecker.top * 1770) / MainActivity.height;
                        break;
                    case 0:
                        ints[1] = (botChecker.top * 1770) / MainActivity.height;
                        break;
                }
                botRectangles.getRectangles().set(i, ints);
            }
        }

        if (won && lost) {
            gameOver("draw");
        } else if (won) {
            gameOver("won");
        } else if (lost) {
            gameOver("lost");
        }
    }

    static private boolean hitBorder(Rect checker, Rectangles rectangles) {
        final boolean HitBorder = checker.intersect(topBar) || checker.intersect(leftBar) ||
                checker.intersect(rightBar) || checker.intersect(bottomBar);
        final int playerRectangleLength = rectangles.getLastRectangle()[2] - rectangles.getLastRectangle()[1];

        return HitBorder && (rectangles.getRectangles().size() > 1 || playerRectangleLength > 20);
    }

    private static Rect getChecker(int[] variable) {
        final Rect checker = new Rect();
        int left = variable[0];
        int top = variable[1];
        switch (variable[3]) {
            case 90:
                checker.set((-top - 1), (left), (-top), (left + 30));
                break;
            case -90:
                checker.set((top), (-left - 30), (top - 1), (-left));
                break;
            case 180:
                checker.set((-left - 30), (-top - 1), (-left), (-top));
                break;
            case 0:
                checker.set((left), (top), (left + 30), (top + 1));
                break;
        }
        return checker;
    }

    private static Rect getRect(int[] variable) {
        final Rect rect = new Rect();
        int left = variable[0];
        int top = variable[1];
        int bot = variable[2];
        switch (variable[3]) {
            case 90:
                rect.set(-bot, (left), -top, left + 30);
                break;
            case -90:
                rect.set((top), -left - 30, bot, -left);
                break;
            case 180:
                rect.set(-left - 30, -bot, -left, -top);
                break;
            case 0:
                rect.set(left, top, left + 30, bot);
                break;
        }
        return rect;
    }

    private void botPlay() {
        if (timeout == 1) {
            float[][][] input = preprocess(botRectangles.getRectangles(), playerRectangles.getRectangles());
            Map<Integer, Object> output = new HashMap<>();

            float[][] prediction = new float[1][3];
            output.put(0, prediction);

            tfLite.runForMultipleInputsOutputs(input, output);

            final float[] probabilities = prediction[0];
            int action = argMax(probabilities);

            if (action == 1) {
                botTurnRight();
            } else if (action == 2) {
                botTurnLeft();
            }
            timeout = TIMEOUT;
        } else {
            timeout--;
        }
    }

    public static int argMax(float[] arr) {
        float max = arr[0];
        int index = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
                index = i;
            }
        }
        return index;
    }

    private static void addToGrid(Iterable<int[]> variables, float[][][] arr) {
        for (int[] border : variables) {
            int x0 = border[0];
            int x1 = border[1];
            int x2 = border[2];
            int x3 = border[3];

            if (x3 == 0) {
                int clm = x0 / 30;
                if (clm > 35) {
                    clm = 35;
                }
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][j][clm] = -1;
                }
            }

            if (x3 == 180) {
                int clm = abs(x0 / 30);
                if (clm > 36) {
                    clm = 36;
                }
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][abs(j) - 1][clm - 1] = -1;
                }
            }

            if (x3 == 90) {
                int row = abs(x0 / 30);
                if (x1 < -1080) {
                    x1 = -1080;
                }
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][row][abs(j) - 1] = -1;
                }
            }

            if (x3 == -90) {
                int row = abs(x0 / 30);
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][row - 1][j] = -1;
                }
            }

        }
    }

    private float[][][] preprocess(ArrayList<int[]> botVariables, ArrayList<int[]> playerVariables) {
        final int gridRows = 1600 / pixels;
        final int gridColumns = 1080 / pixels;
        float[][][] arr = new float[1][gridRows][gridColumns];
        int[] last = new int[2];

        final ArrayList<int[]> borders = new ArrayList<>();
        borders.add(new int[]{0, 0, 1080, 0});
        borders.add(new int[]{1080 - 30, 0, 1080, 0});
        borders.add(new int[]{0, -1080, 0, 90});
        borders.add(new int[]{1570, -1080, 0, 90});

        addToGrid(playerVariables, arr);
        addToGrid(borders, arr);

        for (int i = 0; i < botVariables.size(); i++) {
            int x0 = botVariables.get(i)[0];
            int x1 = botVariables.get(i)[1];
            int x2 = botVariables.get(i)[2];
            int x3 = botVariables.get(i)[3];

            if (x3 == 0) {
                int clm = x0 / 30;
                if (clm > 35) {
                    clm = 35;
                }
                last[0] = x1 / 30;
                last[1] = clm;
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][j][clm] = 1;

                }
            }

            if (x3 == 180) {
                int clm = abs(x0 / 30);
                if (clm > 36) {
                    clm = 36;
                }
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][abs(j) - 1][clm - 1] = 1;
                    last[0] = abs(x1 / 30) - 1;
                    last[1] = clm - 1;
                }
            }

            if (x3 == 90) {
                int row = abs(x0 / 30);
                if (x1 < -1080) {
                    x1 = -1080;
                }
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][row][abs(j) - 1] = 1;
                    last[0] = row;
                    last[1] = abs(x1 / 30) - 1;
                }
            }

            if (x3 == -90) {
                int row = abs(x0 / 30);
                last[0] = row - 1;
                last[1] = x1 / 30;
                for (int j = x1 / 30; j < x2 / 30; j++) {
                    arr[0][row - 1][j] = 1;

                }
            }

        }
        arr[0][last[0]][last[1]] = 2;

        final short n = 5;
        float[][][] result = new float[1][n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int x = last[0] - n / 2 + i;
                int y = last[1] - n / 2 + j;
                if (x < 0 || x >= 54 || y < 0 || y >= 36) {
                    result[0][i][j] = -1;
                } else {
                    result[0][i][j] = arr[0][x][y];
                }
            }
        }

        //Log.d("result", Arrays.deepToString(result[0]));
        return result;

    }

    private void playerTurnRight() {
        final int[] lastRectangle = playerRectangles.getLastRectangle();
        int lastDirection = lastRectangle[3];

        if (lastDirection == 180) {
            lastDirection = -90;
        } else {
            lastDirection += 90;
        }

        final int[] rect;
        rect = new int[4];

        rect[0] = lastRectangle[1];
        rect[1] = -lastRectangle[0] - 30;
        rect[2] = -lastRectangle[0] - 30;
        rect[3] = lastDirection;


        playerRectangles.addRectangle(rect);
        Rectangle rectangle = new Rectangle(this, rect, Shared.BLUE);
        playerRectangles.addView(rectangle);

    }

    private void playerTurnLeft() {
        final int[] lastRectangle = playerRectangles.getLastRectangle();
        int lastDirection = lastRectangle[3];

        if (lastDirection == -90) {
            lastDirection = 180;
        } else {
            lastDirection -= 90;
        }

        final int[] rect;
        rect = new int[4];

        rect[0] = -lastRectangle[1] - 30;
        rect[1] = lastRectangle[0];
        rect[2] = lastRectangle[0];
        rect[3] = lastDirection;


        playerRectangles.addRectangle(rect);
        Rectangle rectangle = new Rectangle(this, rect, Shared.BLUE);
        playerRectangles.addView(rectangle);
    }

    private void botTurnRight() {
        final int[] lastRectangle = botRectangles.getLastRectangle();
        int lastDirection = lastRectangle[3];

        if (lastDirection == 180) {
            lastDirection = -90;
        } else {
            lastDirection += 90;
        }

        final int[] rect = new int[4];

        rect[0] = lastRectangle[1];
        rect[1] = -lastRectangle[0] - 30;
        rect[2] = -lastRectangle[0] - 30;
        rect[3] = lastDirection;

        botRectangles.addRectangle(rect);
        Rectangle rectangle = new Rectangle(this, rect, Shared.BLACK);
        botRectangles.addView(rectangle);
    }

    private void botTurnLeft() {
        final int[] lastRectangle = botRectangles.getLastRectangle();
        int lastDirection = lastRectangle[3];

        if (lastDirection == -90) {
            lastDirection = 180;
        } else {
            lastDirection -= 90;
        }

        final int[] rect = new int[4];

        rect[0] = -lastRectangle[1] - 30;
        rect[1] = lastRectangle[0];
        rect[2] = lastRectangle[0];
        rect[3] = lastDirection;

        botRectangles.addRectangle(rect);
        Rectangle rectangle = new Rectangle(this, rect, Shared.BLACK);
        botRectangles.addView(rectangle);
    }

    public void gameOver(String result) {
        finished = true;

        //SETTING THE FINISH MESSAGE
        final RelativeLayout message_box;
        message_box = new RelativeLayout(this);

        //BOX
        if (result.equals("won")) {
            message_box.setBackgroundResource(R.drawable.win_box);
        } else if (result.equals("lost")) {
            message_box.setBackgroundResource(R.drawable.lost_box);
        } else {
            message_box.setBackgroundResource(R.drawable.draw_box);
        }

        RelativeLayout.LayoutParams layoutParams4 = new RelativeLayout.LayoutParams(Shared.setX(700), Shared.setY(300));
        message_box.setLayoutParams(layoutParams4);
        ((ViewGroup) back.getParent()).removeView(back);
        message_box.addView(back);

        // PLAY AGAIN BUTTON
        Button play_again = new Button(this);
        RelativeLayout.LayoutParams layoutParams5 = new RelativeLayout.LayoutParams(Shared.setX(300), Shared.setY(150));
        layoutParams5.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layoutParams5.topMargin = Shared.setY(290);
        play_again.setBackgroundResource(R.drawable.play_again_button);
        message_box.addView(play_again, layoutParams5);

        play_again.setOnClickListener(view -> recreate());

        alertDialog = new Dialog(this);
        alertDialog.setContentView(message_box);

        if (alertDialog.getWindow() != null) {    //MAKE BACKGROUND OF DIALOG TRANSPARENT
            alertDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);

    }

    public void backButton() {
        back = new Button(this);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(view -> {
            //CLEAR EVERYTHING AND BACK
            finished = true;
            back_clicked = true;
            Intent intent = new Intent(Normal.this, MainActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //CLEAR EVERYTHING AND BACK
        back_clicked = true;
        finished = true;
        Intent intent = new Intent(Normal.this, MainActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        opened = 0;
        AppClosed app_closed = new AppClosed();
        app_closed.activity_closed();
    }

    @Override
    public void onResume() {
        super.onResume();
        opened = 1;

        if (MainActivity.musicBoolean) {
            MainActivity.music.start();
            MainActivity.music.setLooping(true);

        }
    }

}
