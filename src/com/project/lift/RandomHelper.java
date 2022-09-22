package com.project.lift;

import java.util.Random;

public interface RandomHelper {
    static byte getFloor(byte curFloor, byte maxFloor) {
        Random rand = new Random();
        byte numb = (byte) rand.nextInt(1, maxFloor);
        if (curFloor != 0 && curFloor == numb) {
            return getFloor(curFloor, (byte) (maxFloor));
        }
        return numb;
    }

    static byte getNumb(int min, int max) {
        Random rand = new Random();
        int n = rand.nextInt(min, max);
        return (byte) n;
    }
}
