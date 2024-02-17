package com.hn.cat.utils.random;
import java.util.Random;

public class RandomUtil {
    public static int RandomNumberByNext(int next){
        Random rand = new Random();
        return rand.nextInt(next);
    }
}
