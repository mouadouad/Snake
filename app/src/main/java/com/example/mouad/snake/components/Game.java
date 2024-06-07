package com.example.mouad.snake.components;

import android.content.Context;
import android.graphics.Rect;
import android.os.Handler;

import com.example.mouad.snake.Shared;
import com.example.mouad.snake.activities.MainActivity;
import com.example.mouad.snake.activities.Normal;
import com.example.mouad.snake.enums.GameStates;

import java.util.Random;

public class Game {
    private static final short TIMEOUT = 10;
    protected short timeout = TIMEOUT;
    public boolean finished = false;
    private final Context context;
    private final Bot bot;
    private final Normal normal;
    private static Rect topBar, bottomBar, leftBar, rightBar;
    private final Rectangles playerRectangles, botRectangles;

    public Game(Context context, Bot bot, GameView gameView, Normal normal) {
        this.context = context;
        this.bot = bot;
        this.normal = normal;

        playerRectangles = new Rectangles(context);
        gameView.addView(playerRectangles);

        botRectangles = new Rectangles(context);
        gameView.addView(botRectangles);

        leftBar = new Rect(0, 0, 20, 1600);
        rightBar = new Rect(1080 - 20, 0, 1080, 1600);
        topBar = new Rect(0, 0, 1080, 20);
        bottomBar = new Rect(0, 1580, 1080, 1600);
    }

    public void start(float playerXStart, float playerYStart, int side) {
        final float[] playerCoordinates = findClosestEdge(playerXStart, playerYStart, side);
        final float[] botCoordinates = generateBotPosition(side);
        addStartingRectangles(playerCoordinates, botCoordinates);
        repeat();
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

    private void addStartingRectangles(float[] playerCoordinates, float[] botCoordinates) {

        final float[] botPosition = findStartingPosition(botCoordinates[0], botCoordinates[1]);
        final int[] firstRect = startingRect((int) botPosition[0], (int) botPosition[1], (int) botPosition[2]);

        botRectangles.addRectangle(firstRect);
        Rectangle rectangle = new Rectangle(context, firstRect, Shared.BLACK);
        botRectangles.addView(rectangle);

        final float[] playerPosition = findStartingPosition(playerCoordinates[0] * 1080 / Shared.width, playerCoordinates[1] * 1770 / Shared.height);
        int[] firstRectPlayer = startingRect((int) playerPosition[0], (int) playerPosition[1], (int) playerPosition[2]);

        playerRectangles.addRectangle(firstRectPlayer);
        rectangle = new Rectangle(context, firstRectPlayer, Shared.BLUE);
        playerRectangles.addView(rectangle);
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

    private void repeat() {
        if (!finished) {
            final Handler handler = new Handler();
            final Runnable runnable = () -> {
                if (!finished) {
                    playerRectangles.getLastRectangle()[1] -= 3;
                    botRectangles.getLastRectangle()[1] -= 3;

                    playerRectangles.getChildAt(playerRectangles.getChildCount() - 1).invalidate();
                    botRectangles.getChildAt(botRectangles.getChildCount() - 1).invalidate();
                    botPlay();
                    checking();
                    repeat();
                }
            };
            handler.postDelayed(runnable, 10);
        }
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
            finished = true;
            normal.gameOver(GameStates.DRAW);
        } else if (won) {
            finished = true;
            normal.gameOver(GameStates.WON);
        } else if (lost) {
            finished = true;
            normal.gameOver(GameStates.LOST);
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
            short action = bot.play(playerRectangles, botRectangles);

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
        final Rectangle rectangle = new Rectangle(context, rect, Shared.BLACK);
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
        final Rectangle rectangle = new Rectangle(context, rect, Shared.BLACK);
        botRectangles.addView(rectangle);
    }

    public void playerTurnRight() {
        final int[] lastRectangle = playerRectangles.getLastRectangle();
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

        playerRectangles.addRectangle(rect);
        final Rectangle rectangle = new Rectangle(context, rect, Shared.BLUE);
        playerRectangles.addView(rectangle);
    }
    public void playerTurnLeft() {
        final int[] lastRectangle = playerRectangles.getLastRectangle();
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

        playerRectangles.addRectangle(rect);
        final Rectangle rectangle = new Rectangle(context, rect, Shared.BLUE);
        playerRectangles.addView(rectangle);
    }
}
