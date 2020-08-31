package com.sudoku.utils;

import android.util.Log;

import com.sudoku.model.Word;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

// This is a CSVFile reader. It reads a csv file as an inputstream
// Csv formatting : String1,String2

public class CSVFile {
    private static final String TAG = "CSVFile";
    InputStream inputStream;
    ArrayList<Word> csvWordList;
    int sideLength = 0;

    public CSVFile(InputStream inputStream) {
        if (inputStream == null) {
            throw new NullPointerException();
        }
        this.inputStream = inputStream;
    }

    public CSVFile(InputStream inputStream, int sideLength) {
        this.sideLength = sideLength;
        if (inputStream == null) {
            throw new NullPointerException();
        }
        this.inputStream = inputStream;
    }

    // Reads the input stream and generates an arrayList with words corresponding to each line
    public ArrayList<Word> read() {
        // csvWordList contains the entire file
        csvWordList = new ArrayList<Word>();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            int i = 0;

            // read line by line
            while ((csvLine = reader.readLine()) != null) {
                // row = temporary array to store String1,String2
                String[] row = csvLine.split(",");
                csvWordList.add(new Word(row[0], row[1], i));
                Log.d(TAG, i + " row[0] " + row[0]);
                Log.d(TAG, i + " row[1] " + row[1]);
                i++;

            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        if (csvWordList.size() < sideLength) {
            throw new RuntimeException("CSV list is too small (<9)");
        }

        return csvWordList;
    }

    // cuts the list into length according to desired length
    public ArrayList<Word> cut() {
        Log.d(TAG, "Generating " + sideLength + " randoms");
        // cut the array into random set of words
        // result size = "sideLength"
        ArrayList<Word> selectedWords = new ArrayList<>();
        Random rand = new Random();
        int num;
        Word tmpWord;
        for (int i = 0; i < sideLength; i++) {
            Log.d(TAG, "i = " + i);
            // look for random word
            num = rand.nextInt(csvWordList.size());
            tmpWord = csvWordList.get(num);
            // while already exists in results then look for another word
            while (selectedWords.contains(tmpWord)) {
                num = rand.nextInt(csvWordList.size());
                tmpWord = csvWordList.get(num);
            }
            selectedWords.add(tmpWord);
        }
        return selectedWords;
    }

    // utilizes a priority list to generate a word list
    public ArrayList<Word> cut(int[] priorityList) {
        int[] pri = priorityThree(priorityList);
        Log.d(TAG, "Generating " + sideLength + " randoms");
        // randomly select sideLength from the csv list
        ArrayList<Word> selectedWords = new ArrayList<>();
        Random rand = new Random();
        int num;
        Word tmpWord;
        for (int i = 0; i < sideLength; i++) {
            Log.d(TAG, "i = " + i);
            num = rand.nextInt(csvWordList.size());
            tmpWord = csvWordList.get(num);
            while (selectedWords.contains(tmpWord)) {
                num = rand.nextInt(csvWordList.size());
                // if the word matches a priority
                if (num == pri[0] || num == pri[1] || num == pri[2]) {
                    tmpWord = csvWordList.get(num);
                    break;
                }
                tmpWord = csvWordList.get(num);


            }
            selectedWords.add(tmpWord);
        }
        return selectedWords;

    }

    // helper
    private int[] priorityThree(int[] priorityList) {
        int one = 0;
        int two = 0;
        int three = 3;

        for (int i = 0; i < priorityList.length; i++) {
            if (priorityList[i] > one) {
                one = i;
            } else if (priorityList[i] > two) {
                two = i;
            } else if (priorityList[i] > three) {
                three = i;
            }
        }

        return new int[]{one,two,three};
    }

}
