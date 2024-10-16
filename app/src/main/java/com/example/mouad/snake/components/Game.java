package com.example.mouad.snake.components;

import android.graphics.Rect;
import android.os.Handler;
import android.util.Log;

import com.example.mouad.snake.shared.Constants;
import com.example.mouad.snake.shared.GameMethods;
import com.example.mouad.snake.activities.Normal;
import com.example.mouad.snake.enums.GameStates;

import java.util.Random;

public class Game {
    private static final short TIMEOUT = 10;
    protected short timeout = TIMEOUT;
    public boolean finished = false;
    private final Bot bot;
    private final Normal normal;
    public final Renderer renderer;
    private static Rect topBar, bottomBar, leftBar, rightBar;
    private final Rectangles playerRectangles, botRectangles;

    public Game(Bot bot, Renderer renderer, Normal normal) {
        this.bot = bot;
        this.normal = normal;
        this.renderer = renderer;

        playerRectangles = new Rectangles();
        botRectangles = new Rectangles();
        renderer.setVariables(playerRectangles, botRectangles);

        leftBar = new Rect(0, 0, (int) Constants.borderWidth, (int) Constants.mapHeight);
        rightBar = new Rect((int) (Constants.mapWidth - Constants.borderWidth), 0, (int) Constants.mapWidth, (int) Constants.mapHeight);
        topBar = new Rect(0, 0, (int) Constants.mapWidth, (int) Constants.borderWidth);
        bottomBar = new Rect(0, (int) (Constants.mapHeight - Constants.borderWidth), (int) Constants.mapWidth, (int) Constants.mapHeight);
    }

    public void start(float playerXStart, float playerYStart, int side) {
        final float[] playerCoordinates = GameMethods.findClosestEdge(playerXStart, playerYStart, side);
        final float[] botCoordinates = generateBotPosition(side);
        addStartingRectangles(playerCoordinates, botCoordinates);
        repeat();
    }

    private float[] generateBotPosition(int hisSide) {
        final Random random = new Random();
        final int x = random.nextInt((int) Renderer.screenWidth);
        int y = random.nextInt((int) (Renderer.screenHeight / 2));
        if (hisSide == 1) {
            y += (int) (Renderer.screenHeight / 2);
        }
        return GameMethods.findClosestEdge(renderer.getX(x), renderer.getY(y), (hisSide + 1) % 2);
    }

    private static float[] findStartingPosition(float x, float y) {
        int angle = 0;
        if (y == 0) {
            angle = 180;
            if (x <= (Constants.borderWidth + Constants.snakeWidth)) {
                x = (Constants.borderWidth + 1 + Constants.snakeWidth);
            }
            if (x >= (Constants.mapWidth - Constants.borderWidth)) {
                x = (Constants.mapWidth - Constants.borderWidth - 1);
            }
        } else if (x == 0) {
            angle = 90;
            if (y >= (Constants.mapHeight - Constants.borderWidth - Constants.snakeWidth)) {
                y = (Constants.mapHeight - Constants.borderWidth - 1 - Constants.snakeWidth);
            }
            if (y <= (Constants.snakeWidth)) {
                y = (Constants.borderWidth + 1);
            }
        } else if (x == Constants.mapWidth) {
            angle = -90;
            if (y >= (Constants.mapHeight - Constants.borderWidth)) {
                y = (Constants.mapHeight - Constants.borderWidth - 1);
            }
            if (y <= (Constants.borderWidth + Constants.snakeWidth)) {
                y = (Constants.borderWidth + 1 + Constants.snakeWidth);
            }
        } else if (y == Constants.mapHeight) {
            if (x <= (Constants.borderWidth)) {
                x = (Constants.borderWidth + 1);
            }
            if (x >= (Constants.mapWidth - Constants.borderWidth - Constants.snakeWidth)) {
                x = (Constants.mapWidth - Constants.borderWidth - 1 - Constants.snakeWidth);
            }
        } else {
            Log.d("error", "findStartingPosition: ");
        }

        return new float[]{x, y, angle};
    }

    private void addStartingRectangles(float[] playerCoordinates, float[] botCoordinates) {

        final float[] botPosition = findStartingPosition(botCoordinates[0], botCoordinates[1]);
        final float[] firstRect = startingRect( botPosition[0], botPosition[1],botPosition[2]);

        botRectangles.addRectangle(firstRect);

        final float[] playerPosition = findStartingPosition(playerCoordinates[0],playerCoordinates[1]);
        float[] firstRectPlayer = startingRect(playerPosition[0], playerPosition[1],playerPosition[2]);

        playerRectangles.addRectangle(firstRectPlayer);
    }

    private static float[] startingRect(float x, float y, float angle) {
        final float[] firstRect = new float[4];

        if (angle == 0) {
            firstRect[0] = x;
            firstRect[1] = Constants.mapHeight - 10;
            firstRect[2] = Constants.mapHeight;
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
                    botPlay();
                    playerRectangles.getLastRectangle()[1] -= 4;
                    botRectangles.getLastRectangle()[1] -= 4;

                    renderer.refresh();

                    checking();
                    repeat();
                }
            };
            handler.postDelayed(runnable, 15);
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
                }

                final float[] ints = playerRectangles.getRectangles().get(i);

                switch ((int) ints[3]) {
                    case 90:
                        ints[1] = -playerChecker.left;
                        break;
                    case -90:
                        ints[1] = playerChecker.left;
                        break;
                    case 180:
                        ints[1] = -playerChecker.top;
                        break;
                    case 0:
                        ints[1] = playerChecker.top;
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
                }
                final float[] ints = botRectangles.getRectangles().get(i);

                switch ((int) ints[3]) {
                    case 90:
                        ints[1] = -botChecker.left;
                        break;
                    case -90:
                        ints[1] = botChecker.left;
                        break;
                    case 180:
                        ints[1] = -botChecker.top;
                        break;
                    case 0:
                        ints[1] = botChecker.top;
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
        final float playerRectangleLength = rectangles.getLastRectangle()[2] - rectangles.getLastRectangle()[1];

        return HitBorder && (rectangles.getRectangles().size() > 1 || playerRectangleLength > Constants.borderWidth);
    }

    private static Rect getChecker(float[] variable) {
        final Rect checker = new Rect();
        float left = variable[0];
        float top = variable[1];
        switch ((int) variable[3]) {
            case 90:
                checker.set((int) (-top - 1), (int) left, (int) -top, (int) (left + Constants.snakeWidth));
                break;
            case -90:
                checker.set((int) top, (int) (-left - Constants.snakeWidth), (int) (top - 1), (int) -left);
                break;
            case 180:
                checker.set((int) (-left - Constants.snakeWidth), (int) (-top - 1), (int) -left, (int) -top);
                break;
            case 0:
                checker.set((int) left, (int) top, (int) (left + Constants.snakeWidth), (int) (top + 1));
                break;
        }
        return checker;
    }

    private static Rect getRect(float[] variable) {
        final Rect rect = new Rect();
        float left = variable[0];
        float top = variable[1];
        float bot = variable[2];
        switch ((int) variable[3]) {
            case 90:
                rect.set((int) -bot, (int) left, (int) -top, (int) (left + Constants.snakeWidth));
                break;
            case -90:
                rect.set((int) top, (int) (-left - Constants.snakeWidth), (int) bot, (int) -left);
                break;
            case 180:
                rect.set((int) (-left - Constants.snakeWidth), (int) -bot, (int) -left, (int) -top);
                break;
            case 0:
                rect.set((int) left, (int) top, (int) (left + Constants.snakeWidth), (int) bot);
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
        turnRight(botRectangles);
    }
    private void botTurnLeft() {
        turnLeft(botRectangles);
    }

    public void playerTurnRight() {
        turnRight(playerRectangles);
    }
    public void playerTurnLeft() {
        turnLeft(playerRectangles);
    }
    private static void turnLeft(Rectangles rectangles) {
        final float[] lastRectangle = rectangles.getLastRectangle();
        float lastDirection = lastRectangle[3];

        if (lastDirection == -90) {
            lastDirection = 180;
        } else {
            lastDirection -= 90;
        }

        //CHECK IF LAST TURN IS COMPLETE
        if (lastRectangle[2] - lastRectangle[1] > Constants.snakeWidth + 2) {
            final float[] rect = new float[4];
            rect[0] = -lastRectangle[1] - Constants.snakeWidth;
            rect[1] = lastRectangle[0];
            rect[2] = lastRectangle[0];
            rect[3] = lastDirection;

            rectangles.addRectangle(rect);
        }
    }
    private static void turnRight(Rectangles rectangles) {
        final float[] lastRectangle = rectangles.getLastRectangle();
        float lastDirection = lastRectangle[3];

        if (lastDirection == 180) {
            lastDirection = -90;
        } else {
            lastDirection += 90;
        }

        if (lastRectangle[2] - lastRectangle[1] > Constants.snakeWidth + 2) {
            final float[] rect = new float[4];
            rect[0] = lastRectangle[1];
            rect[1] = -lastRectangle[0] - Constants.snakeWidth;
            rect[2] = -lastRectangle[0] - Constants.snakeWidth;
            rect[3] = lastDirection;

            rectangles.addRectangle(rect);
        }
    }

}
