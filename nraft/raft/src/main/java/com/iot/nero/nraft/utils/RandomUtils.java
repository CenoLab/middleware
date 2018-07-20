package com.iot.nero.nraft.utils;

import java.util.Random;

/**
 * Author neroyang
 * Email  nerosoft@outlook.com
 * Date   2018/7/19
 * Time   10:43 AM
 */
public class RandomUtils {
    public static Integer getRandom(int min, int max){
        Random random = new Random();
        int s = random.nextInt(max) % (max - min + 1) + min;
        return s;

    }
}
