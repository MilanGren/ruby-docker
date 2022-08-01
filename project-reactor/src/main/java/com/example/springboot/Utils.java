package com.example.springboot;

import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static List<String> getRandomListOfNumbers(int min, int max) {

        List<String> list = new ArrayList<>();

        int numberOfNewEnvelopes = getRandomNumber(min, max);

        System.out.println(numberOfNewEnvelopes + " new messages");

        for (int i = 0; i < numberOfNewEnvelopes; i++) {
            list.add("message" + i);
        }
        return list;
    }

}
