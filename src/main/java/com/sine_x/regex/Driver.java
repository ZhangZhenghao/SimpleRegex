package com.sine_x.regex;

import com.mifmif.common.regex.Generex;
import com.sine_x.regex.Pattern;
import com.sine_x.regex.exeception.RegexException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Driver {

    private static final String FILE_NAME = "test/regex.txt";
    private static final int TEST_NUM = 100;
    private static final int SHUFFLE_NUM = 1;
    private static final int NANO_TO_MILLI = 1000000;

    private static Random random = new Random(System.currentTimeMillis());

    public static String shuffle(String string) {
        StringBuilder builder = new StringBuilder(string);
        for (int i = 0; i < SHUFFLE_NUM; i++) {
            int action = random.nextInt(3);
            switch (action) {
                case 0:     // Replace two elements
                    if (builder.length() > 0) {
                        int a = random.nextInt(builder.length());
                        int b = random.nextInt(builder.length());
                        char c = builder.charAt(a);
                        builder.setCharAt(a, builder.charAt(b));
                        builder.setCharAt(b, c);
                    }
                    break;
                case 1:     // Insert a element
                    char c = (char) random.nextInt(Character.MAX_VALUE);
                    int a;
                    if (builder.length() > 0)
                        a = random.nextInt(builder.length());
                    else
                        a = 0;
                    builder.insert(a, c);
                    break;
                case 2:     // Remove a element
                    if (builder.length() > 0) {
                        a = random.nextInt(builder.length());
                        builder.deleteCharAt(a);
                    }
                    break;
            }
        }
        return builder.toString();
    }

    public static void main(String[] args) {
        try {
            FileReader fileReader = new FileReader(FILE_NAME);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            double totalCompileTime = 0;
            double totalRunTime = 0;
            int totalRegexp = 0;
            int acceptedRegexp = 0;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                // Generate test case
                Generex generex = new Generex(line, random);
                List<String> strings = new ArrayList<String>();
                for (int i = 0; i < TEST_NUM; i++) {
                    String string = generex.random();
                    strings.add(string);
                    strings.add(shuffle(string));
                }
                // Test
                double compileTime = 0;
                double runTime = 0;
                long startTime = 0;
                long endTime = 0;
                int acceptedTime = 0;
                String status = "";
                try {
                    startTime = System.nanoTime();
                    Pattern pattern = Pattern.compile(line);
                    endTime = System.nanoTime();
                    compileTime = (double) (endTime - startTime) / NANO_TO_MILLI;
                    for (String string : strings) {
                        startTime = System.nanoTime();
                        boolean m = pattern.match(string);
                        endTime = System.nanoTime();
                        runTime += (double) (endTime - startTime) / NANO_TO_MILLI;
                        if (string.matches(line) == m)
                            acceptedTime++;
                        else
                            System.err.printf("match %s failed\n", string);
                    }
                } catch (RegexException e) {
                    status = "compile err";
                }
                // Report
                if (status.isEmpty())
                    if (acceptedTime != TEST_NUM * 2) {
                        status = "match err";
                    } else {
                        status = "accepted";
                        acceptedRegexp++;
                    }
                System.out.printf("%-16s%d/%d\t%fms\t%fms\t%s\n",
                        status, acceptedTime, TEST_NUM * 2, compileTime, runTime, line);
                // Sum to total
                totalCompileTime += compileTime;
                totalRunTime += runTime;
                totalRegexp++;
            }
            // Summary
            System.out.printf("%-16s%d/%d\t%fms\t%fms\n",
                    "total", acceptedRegexp, totalRegexp, totalCompileTime, totalRunTime);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
