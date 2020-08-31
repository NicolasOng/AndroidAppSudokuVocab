package com.sudoku;

import com.sudoku.model.Word;

import org.junit.Test;

import static org.junit.Assert.*;

public class WordTest {

    @Test
    public void wordTest() {
        Word word = new Word("bye", "au revoir", 0);
        assertSame("bye", word.getEnglish());
        assertSame("au revoir", word.getNotEnglish());
        assertSame(0, word.getValue());

        Word word2 = new Word("bye", "au revoir", 5);
        assertSame("au revoir", word2.getNotEnglish());
        assertSame("bye", word2.getEnglish());
        assertSame(5, word2.getValue());

        Word word3 = new Word("hello", "nihao", 1214231);
        assertNotEquals("au revoir", word3.getNotEnglish());
        assertNotEquals("nihao", word3.getEnglish());
        assertEquals(1214231, word3.getValue());

    }

}