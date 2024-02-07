package com.example.triptix.Util;

public class RandomNumber {
    public static int generateRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
}
