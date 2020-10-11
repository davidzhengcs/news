package com.yizheng.newsgateway;

import android.graphics.Color;

import java.util.Random;

public class Utility {

    static int generateRandomColor(){
        Random rnd = new Random();
        return Color.argb(255, rnd.nextInt(150)+50, rnd.nextInt(150)+50, rnd.nextInt(150)+50);
    }

}
