package com.example.mouad.snake;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mouad.snake.activities.MainActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;


public class Shared {

    public static int width, height, statusBarHeight;
    public static final int BLACK = Color.parseColor("#20292A");
    public static final int YELLOW = Color.parseColor("#D18D1B");
    public static final int BLUE = Color.parseColor("#1D8189");
    public static final int BORDER_COLOR = Color.parseColor("#3A4647");

    public static boolean foreGround = true;
    public final static String SHARED_PREFS = "shared_prefs";
    public final static String MUSIC_SHARED_PREFS = "MUSIC_SHARED_PREFS";
    public final static String SOUND_SHARED_PREFS = "SOUND_SHARED_PREFS";
    public static final String Level = "level";
    public static final String Xp = "xp";

    public static int setX(int x) {
        return ((x * MainActivity.width) / 1080);

    }
    public static int setY(int y) {
        return ((y * MainActivity.height) / 1770);

    }

    public static void background(Context context, AppCompatActivity activity) {
        final RelativeLayout background = new RelativeLayout(context);
        final RelativeLayout.LayoutParams backParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        final int back_color = Color.parseColor("#3A4647");
        background.setBackgroundColor(back_color);
        activity.addContentView(background, backParams);
    }
    public static void banner(Context context, AppCompatActivity activity) {
        AdView adView = new AdView(context);
        adView.setAdSize(AdSize.BANNER);
        adView.setAdUnitId("ca-app-pub-3922358669029120/2831354657");

        RelativeLayout layout = new RelativeLayout(context);
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(MainActivity.width, MainActivity.height - Shared.statusBarHeight);
        activity.addContentView(layout, layoutParams1);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        layout.addView(adView, layoutParams);

        //MobileAds.initialize(this,"ca-app-pub-3922358669029120~3985187056");
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }
    public static void backButton(Context context, AppCompatActivity activity, View.OnClickListener listener) {
        final Button back = new Button(context);
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(100), Shared.setY(50));
        back.setBackgroundResource(R.drawable.back_button);
        activity.addContentView(back, layoutParams);
        back.setY(Shared.setY(50));
        back.setX(Shared.setX(50));

        back.setOnClickListener(listener);
    }

    public static void addElement(AppCompatActivity activity, View view, int width, int height, int x, int y) {
        final RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(Shared.setX(width), Shared.setY(height));
        activity.addContentView(view, layoutParams);
        view.setX(Shared.setX(x));
        view.setY(Shared.setY(y));
    }

}
