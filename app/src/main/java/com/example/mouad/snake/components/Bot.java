package com.example.mouad.snake.components;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

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

public class Bot {
    private final short pixels, obsSize;
    private Interpreter tfLite;
    protected Interpreter.Options tfliteOptions;
    private final Activity activity;

    public Bot(Activity activity, short pixels, short obsSize) {
        this.pixels = pixels;
        this.obsSize = obsSize;
        this.activity = activity;
        loadModelFile();
    }

    private void loadModelFile() {
        try {
            tfliteOptions = new Interpreter.Options();
            tfLite = new Interpreter(Objects.requireNonNull(loadModelFile(activity)), tfliteOptions);
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

    public short play(Rectangles playerRectangles, Rectangles botRectangles) {
        float[][][] input = preprocess(botRectangles.getRectangles(), playerRectangles.getRectangles());
        Map<Integer, Object> output = new HashMap<>();

        float[][] prediction = new float[1][3];
        output.put(0, prediction);

        tfLite.runForMultipleInputsOutputs(input, output);

        final float[] probabilities = prediction[0];
        return argMax(probabilities);
    }

    public static short argMax(float[] arr) {
        float max = arr[0];
        short index = 0;
        for (short i = 1; i < arr.length; i++) {
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
            int clm, row;
            switch(x3) {
                case 0:
                    clm = x0 / 30;
                    if (clm > 35) {
                        clm = 35;
                    }
                    last[0] = x1 / 30;
                    last[1] = clm;
                    for (int j = x1 / 30; j < x2 / 30; j++) {
                        arr[0][j][clm] = 1;
                    }
                    break;
                case 180:
                    clm = abs(x0 / 30);
                    if (clm > 36) {
                        clm = 36;
                    }
                    for (int j = x1 / 30; j < x2 / 30; j++) {
                        arr[0][abs(j) - 1][clm - 1] = 1;
                        last[0] = abs(x1 / 30) - 1;
                        last[1] = clm - 1;
                    }
                    break;
                case 90:
                    row = abs(x0 / 30);
                    if (x1 < -1080) {
                        x1 = -1080;
                    }
                    for (int j = x1 / 30; j < x2 / 30; j++) {
                        arr[0][row][abs(j) - 1] = 1;
                        last[0] = row;
                        last[1] = abs(x1 / 30) - 1;
                    }
                    break;
                case -90:
                    row = abs(x0 / 30);
                    last[0] = row - 1;
                    last[1] = x1 / 30;
                    for (int j = x1 / 30; j < x2 / 30; j++) {
                        arr[0][row - 1][j] = 1;
                    }
                    break;
                default:
                    break;
            }
        }
        arr[0][last[0]][last[1]] = 2;

        float[][][] result = new float[1][obsSize][obsSize];

        for (int i = 0; i < obsSize; i++) {
            for (int j = 0; j < obsSize; j++) {
                int x = last[0] - obsSize / 2 + i;
                int y = last[1] - obsSize / 2 + j;
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
}
