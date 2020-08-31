package com.sudoku.model;

// Defines a Word, typically utilized in conjunction with CSVFile reader
// Free to use anytime :)

public class Word {
    private String english;
    private String notEnglish;
    private int value;

    public Word(String english, String notEnglish, int value) {
        this.english = english;
        this.notEnglish = notEnglish;
        this.value = value;
    }

    public String getEnglish() {
        return english;
    }

    public String getNotEnglish() {
        return notEnglish;
    }

    public int getValue() {
        return value;
    }
}
