package com.sudoku;

import com.sudoku.utils.CSVFile;

import org.junit.Test;

import java.io.InputStream;

import static org.junit.Assert.*;

// This is the most testing i can do because I am unable to generate a mock CSV file

public class CSVFileTest {
    @Test(expected = NullPointerException.class)
    public void testNullPointerException() {
        InputStream nullstream = null;
        CSVFile file = new CSVFile(nullstream);
    }

}