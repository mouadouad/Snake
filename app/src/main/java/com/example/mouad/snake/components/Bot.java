package com.example.mouad.snake.components;

import static java.lang.Math.abs;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.util.Log;

import com.example.mouad.snake.shared.Constants;

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
            Log.e("TAG", String.valueOf(e));
        }
    }

    private MappedByteBuffer loadModelFile(Activity activity) throws IOException {
        try {
            AssetFileDescriptor fileDescriptor = activity.getAssets().openFd("model.tflite");
            try (FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor())) {
                FileChannel fileChannel = inputStream.getChannel();
                long startOffset = fileDescriptor.getStartOffset();
                long declaredLength = fileDescriptor.getDeclaredLength();
                return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
            } catch (IOException e) {
                Log.e("TAG", String.valueOf(e));
            }


        } catch (IOException e) {
            Log.e("TAG", String.valueOf(e));
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

    private static void addToGrid(Iterable<float[]> variables, float[][][] arr, short pixels) {
        final short numbRows = (short) (Constants.mapHeight / pixels);
        final short numbColumns = (short) (Constants.mapWidth / pixels);

        for (float[] border : variables) {
            float x0 = border[0];
            float x1 = border[1];
            float x2 = border[2];
            float x3 = border[3];

            int clm, row, j;

            if (x3 == 0) {
                clm = (int) (x0 / pixels);
                if (clm > numbColumns) {
                    clm = numbColumns;
                }
                for (j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                    arr[0][j][clm] = -1;
                }
            }

            if (x3 == 180) {
                clm = (int) abs(x0 / pixels);
                if (clm > numbRows) {
                    clm = numbRows;
                }
                for (j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                    arr[0][abs(j) - 1][clm - 1] = -1;
                }
            }

            if (x3 == 90) {
                row = (int) abs(x0 / pixels);
                if (x1 < -Constants.mapWidth) {
                    x1 = -Constants.mapWidth;
                }
                for (j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                    arr[0][row][abs(j) - 1] = -1;
                }
            }

            if (x3 == -90) {
                row = (int) abs(x0 / pixels);
                for (j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                    arr[0][row - 1][j] = -1;
                }
            }

        }
    }

    private float[][][] preprocess(ArrayList<float[]> botVariables, ArrayList<float[]> playerVariables) {
        final int gridRows = (int) (Constants.mapHeight / pixels);
        final int gridColumns = (int) (Constants.mapWidth / pixels);
        float[][][] arr = new float[1][gridRows][gridColumns];
        int[] last = new int[2];

        final ArrayList<float[]> borders = new ArrayList<>();
        borders.add(new float[]{0, 0, Constants.mapWidth, 0});
        borders.add(new float[]{Constants.mapWidth - Constants.borderWidth, 0, Constants.mapWidth, 0});
        borders.add(new float[]{0, -Constants.mapWidth, 0, 90});
        borders.add(new float[]{Constants.mapWidth - Constants.borderWidth, -Constants.mapWidth, 0, 90});

        addToGrid(playerVariables, arr,pixels);
        addToGrid(borders, arr, pixels);

        for (int i = 0; i < botVariables.size(); i++) {
            float x0 = botVariables.get(i)[0];
            float x1 = botVariables.get(i)[1];
            float x2 = botVariables.get(i)[2];
            float x3 = botVariables.get(i)[3];
            int clm, row;
            switch((int) x3) {
                case 0:
                    clm = (int) (x0 / pixels);
                    if (clm > gridColumns) {
                        clm = gridColumns;
                    }
                    last[0] = (int) (x1 / pixels);
                    last[1] = clm;
                    for (int j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                        arr[0][j][clm] = 1;
                    }
                    break;
                case 180:
                    clm = (int) abs(x0 / pixels);
                    if (clm > gridRows) {
                        clm = gridRows;
                    }
                    for (int j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                        arr[0][abs(j) - 1][clm - 1] = 1;
                        last[0] = (int) (abs(x1 / pixels) - 1);
                        last[1] = clm - 1;
                    }
                    break;
                case 90:
                    row = (int) abs(x0 / pixels);
                    if (x1 < -Constants.mapWidth) {
                        x1 = (int) -Constants.mapWidth;
                    }
                    for (int j = (int) (x1 / pixels); j < x2 / pixels; j++) {
                        arr[0][row][abs(j) - 1] = 1;
                        last[0] = row;
                        last[1] = (int) (abs(x1 / pixels) - 1);
                    }
                    break;
                case -90:
                    row = (int) abs(x0 / pixels);
                    last[0] = row - 1;
                    last[1] = (int) (x1 / pixels);
                    for (int j = (int) (x1 / pixels); j < x2 / pixels; j++) {
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
                if (x < 0 || x >= gridRows || y < 0 || y >= gridColumns) {
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
