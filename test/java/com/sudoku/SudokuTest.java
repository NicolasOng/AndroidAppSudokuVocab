package com.sudoku;

import com.sudoku.model.Sudoku;
import com.sudoku.model.Word;
import com.sudoku.utils.MODE;
import android.util.Log;
import org.junit.Test;

import static org.junit.Assert.*;

import java.io.InputStream;
import java.util.ArrayList;

public class SudokuTest {
    // english version of default word list for French mode
    private String[] eFrenchList = {"cat", "dog", "boy", "girl", "rat", "pig", "I", "bad", "yes", "hello", "five", "red"};
    // french version of default word list for French mode
    private String[] oFrenchList = {"chat", "chien", "garcon", "fille", "rat", "porc", "je", "mal", "oui", "bonjour", "cinq", "rouge"};

    // english version of default word list for Spanish mode
    private String[] eSpanishList = {"cat", "dog", "boy", "girl", "rat", "pig", "I", "bad", "yes", "hello", "five", "red"};
    // spanish version of default word list for Spanish mode
    private String[] oSpanishList = {"gata", "perra", "niño", "niña", "rata", "cerda", "yo", "mala", "sí", "hola", "cinco", "roja"};

    // used as manual word input
    private String[] manEngList = wordListE(9);
    private String[] manNonEngList = wordListO(9);

    // default board size
    int[] boardSizeNine = boardSizeNine(9);
    int[] boardSizeFour = boardSizeNine(4);
    int[] boardSizeSix = boardSizeNine(6);
    int[] boardSizeTwelve = boardSizeNine(12);

    @Test
    public void testFrenchDefault() {
        // testing default constructors
        // testing if default values are correct
        Sudoku a = new Sudoku();
        assertTrue(a.getDifficulty() == 0);
        assertTrue(a.getMode() == MODE.FRENCH);
        assertFalse(a.getMode() == MODE.SPANISH);

        // Test to make sure the word list contains all the words
        String[] english = a.getEngWords();
        String[] nonEnglish = a.getNonEngWords();
        for (int i = 0; i < eFrenchList.length; i++) {
            if (!contains(english, eFrenchList[i])) {
                throw new AssertionError();
            }
        }

        for (int i = 0; i < oFrenchList.length; i++) {
            if (!contains(nonEnglish, oFrenchList[i])) {
                throw new AssertionError();
            }
        }

    }

    @Test
    public void testSpanishDefaultL() {
        testSpanishDefault(boardSizeFour);
        testSpanishDefault(boardSizeSix);
        testSpanishDefault(boardSizeNine);
        testSpanishDefault(boardSizeTwelve);

    }
    public void testSpanishDefault(int[] boardSize) {
        // testing constructors
        // testing if default values are correct
        Sudoku a = new Sudoku(MODE.SPANISH, 1, boardSize);
        assertTrue(a.getDifficulty() == 1);
        assertTrue(a.getMode() == MODE.SPANISH);
        String[] english = a.getEngWords();
        String[] nonEnglish = a.getNonEngWords();
        for (int i = 0; i < boardSize[0]; i++) {
            if (!contains(eSpanishList, english[i])) {
                throw new AssertionError();
            }
        }

        for (int i = 0; i < boardSize[0]; i++) {
            if (!contains(oSpanishList, nonEnglish[i])) {
                throw new AssertionError();
            }
        }
        assertTrue(english.length == boardSize[0]);
        assertTrue(nonEnglish.length == boardSize[0]);
    }

    @Test(expected = IllegalArgumentException.class)
    public void wrongCons() {
        Sudoku a = new Sudoku(MODE.CSV, 0, boardSizeFour);
    }

    // CSV Mode requires an inputstream
    @Test(expected = IllegalArgumentException.class)
    public void testInputStream() {
        InputStream nullStream = null;
        Sudoku derp = new Sudoku(MODE.CSV, 1, boardSizeFour, nullStream);
    }

    // CSV Mode requires an inputstream
    @Test(expected = IllegalArgumentException.class)
    public void testInputStream2() {
        InputStream nullStream = null;
        Sudoku derp = new Sudoku(MODE.CSV, 1, boardSizeSix, nullStream);
    }

    // CSV Mode requires an inputstream
    @Test(expected = IllegalArgumentException.class)
    public void testInputStream3() {
        InputStream nullStream = null;
        Sudoku derp = new Sudoku(MODE.CSV, 1, boardSizeNine, nullStream);
    }

    // CSV Mode requires an inputstream
    @Test(expected = IllegalArgumentException.class)
    public void testInputStream4() {
        InputStream nullStream = null;
        Sudoku derp = new Sudoku(MODE.CSV, 1, boardSizeTwelve, nullStream);
    }

    // Same word lists exception
    @Test(expected = IllegalArgumentException.class)
    public void testManualSameWordList() {
        Sudoku derp2 = new Sudoku(MODE.MANUAL, 1, boardSizeNine, oFrenchList, oFrenchList);
    }

    @Test
    public void testPermanenceL() {
        testPermanence(MODE.FRENCH, 0, boardSizeFour);
        testPermanence(MODE.FRENCH, 2, boardSizeNine);
        testPermanence(MODE.FRENCH, 3, boardSizeSix);

        testPermanence(MODE.FRENCH, 1, boardSizeNine);
        testPermanence(MODE.FRENCH, 2, boardSizeSix);
        testPermanence(MODE.FRENCH, 3, boardSizeTwelve);

        testPermanence(MODE.SPANISH, 1, boardSizeTwelve);
        testPermanence(MODE.SPANISH, 2, boardSizeSix);
        testPermanence(MODE.SPANISH, 3, boardSizeFour);

        testPermanence(MODE.SPANISH, 0, boardSizeTwelve);
        testPermanence(MODE.SPANISH, 1, boardSizeNine);
        testPermanence(MODE.SPANISH, 2, boardSizeFour);
    }

    // attempt to alter cells that are permanent
    public void testPermanence(MODE mode, int difficulty, int[] boardSize) {
        // difficulty = 0, whole board should be permanent
        Sudoku a = new Sudoku(mode, difficulty, boardSize);
        for (int i = 0; i < boardSize[0]; i++) {
            for (int j = 0; j < boardSize[0]; j++) {
                // if difficulty == 0, all cells are permanent
                if (difficulty == 0) {
                    assertTrue(a.getPermanenceAt(i,j));
                }
                if (a.getPermanenceAt(i,j)) {
                    if (a.getValueAt(i,j) != 0) {
                        a.updateCell(i+1,j+1,1); // attempt to remove
                        assertTrue(a.getValueAt(i,j) != 0);
                    }
                    a.setActiveValue(2);
                    int tmp = a.getValueAt(i,j);
                    a.updateCell(i+1,j+1,0); // attempt to change
                    assertTrue(a.getValueAt(i,j) == tmp);
                }

            }
        }
    }
    @Test
    public void testModeL() {
        testMode(MODE.FRENCH, 1, boardSizeFour);
        testMode(MODE.FRENCH, 2, boardSizeFour);
        testMode(MODE.FRENCH, 3, boardSizeFour);
        testMode(MODE.FRENCH, 0, boardSizeFour);

        testMode(MODE.FRENCH, 1, boardSizeSix);
        testMode(MODE.FRENCH, 2, boardSizeSix);
        testMode(MODE.FRENCH, 3, boardSizeSix);
        testMode(MODE.FRENCH, 0, boardSizeSix);

        testMode(MODE.FRENCH, 1, boardSizeNine);
        testMode(MODE.FRENCH, 2, boardSizeNine);
        testMode(MODE.FRENCH, 3, boardSizeNine);
        testMode(MODE.FRENCH, 0, boardSizeNine);

        testMode(MODE.SPANISH, 1, boardSizeTwelve);
        testMode(MODE.SPANISH, 2, boardSizeTwelve);
        testMode(MODE.SPANISH, 3, boardSizeTwelve);
        testMode(MODE.SPANISH, 0, boardSizeTwelve);

        testMode(MODE.SPANISH, 1, boardSizeFour);
        testMode(MODE.SPANISH, 2, boardSizeFour);
        testMode(MODE.SPANISH, 3, boardSizeFour);
        testMode(MODE.SPANISH, 0, boardSizeFour);

        testMode(MODE.SPANISH, 1, boardSizeSix);
        testMode(MODE.SPANISH, 2, boardSizeSix);
        testMode(MODE.SPANISH, 3, boardSizeSix);
        testMode(MODE.SPANISH, 0, boardSizeSix);

        testMode(MODE.SPANISH, 1, boardSizeNine);
        testMode(MODE.SPANISH, 2, boardSizeNine);
        testMode(MODE.SPANISH, 3, boardSizeNine);
        testMode(MODE.SPANISH, 0, boardSizeNine);

        testMode(MODE.SPANISH, 1, boardSizeTwelve);
        testMode(MODE.SPANISH, 2, boardSizeTwelve);
        testMode(MODE.SPANISH, 3, boardSizeTwelve);
        testMode(MODE.SPANISH, 0, boardSizeTwelve);
    }

    public void testMode(MODE mode, int difficulty, int[] boardSize) {
        Sudoku derp = new Sudoku(mode, difficulty, boardSize);
        assertTrue(derp.getMode() == mode);

        if (mode == MODE.MANUAL) {
            ArrayList<Word> wordList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                wordList.add(new Word("" + i, "" + i*2, i));
            }
            Sudoku derp4 = new Sudoku(mode, difficulty, boardSize, wordList);
            assertTrue(derp4.getMode() == mode);
        }
    }

    // tests the difficulty to make sure
    @Test
    public void testDiff() {
        testDifficulty(MODE.FRENCH, boardSizeFour);
        testDifficulty(MODE.FRENCH, boardSizeSix);
        testDifficulty(MODE.FRENCH, boardSizeNine);
        testDifficulty(MODE.FRENCH, boardSizeTwelve);

        testDifficulty(MODE.SPANISH, boardSizeFour);
        testDifficulty(MODE.SPANISH, boardSizeSix);
        testDifficulty(MODE.SPANISH, boardSizeNine);
        testDifficulty(MODE.SPANISH, boardSizeTwelve);


        testDifficulty(MODE.MANUAL, boardSizeFour);
        testDifficulty(MODE.MANUAL, boardSizeSix);
        testDifficulty(MODE.MANUAL, boardSizeNine);
        testDifficulty(MODE.MANUAL, boardSizeTwelve);
    }

    public void testDifficulty(MODE mode, int[] boardSize) {
        if (mode == MODE.MANUAL) {
            Sudoku derpZ = new Sudoku(mode, 0, boardSize, eFrenchList, oFrenchList);
            assertTrue(derpZ.getDifficulty() == 0);

            Sudoku derp = new Sudoku(mode, 1, boardSize, eFrenchList, oFrenchList);
            assertTrue(derp.getDifficulty() == 1);

            Sudoku derp2 = new Sudoku(mode, 2, boardSize, eFrenchList, oFrenchList);
            assertTrue(derp2.getDifficulty() == 2);

            Sudoku derp3 = new Sudoku(mode, 3, boardSize, eFrenchList, oFrenchList);
            assertTrue(derp3.getDifficulty() == 3);
        } else {
            Sudoku derpZ = new Sudoku(mode, 0, boardSize);
            assertTrue(derpZ.getDifficulty() == 0);

            Sudoku derp = new Sudoku(mode, 1, boardSize);
            assertTrue(derp.getDifficulty() == 1);

            Sudoku derp2 = new Sudoku(mode, 2, boardSize);
            assertTrue(derp2.getDifficulty() == 2);

            Sudoku derp3 = new Sudoku(mode, 3, boardSize);
            assertTrue(derp3.getDifficulty() == 3);
        }
    }

    // Test the word lists are randomized
    // it is possible (unlikely) that this test fails but the functionality is correct
    // because the randomized result could be the same as the default...
    // IF this occurs, try running the test again
    @Test
    public void testRandomize() {
        // testing french mode
        Sudoku g = new Sudoku(MODE.FRENCH, 1, boardSizeNine);
        Sudoku g2 = new Sudoku(MODE.FRENCH, 1, boardSizeNine);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.FRENCH, 1, boardSizeFour);
        g2 = new Sudoku(MODE.FRENCH, 1, boardSizeFour);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.FRENCH, 1, boardSizeSix);
        g2 = new Sudoku(MODE.FRENCH, 1, boardSizeSix);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.FRENCH, 1, boardSizeTwelve);
        g2 = new Sudoku(MODE.FRENCH, 1, boardSizeTwelve);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        // testing spanish mode
        g = new Sudoku(MODE.SPANISH, 1, boardSizeFour);
        g2 = new Sudoku(MODE.SPANISH, 1, boardSizeFour);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.SPANISH, 1, boardSizeSix);
        g2 = new Sudoku(MODE.SPANISH, 1, boardSizeSix);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.SPANISH, 1, boardSizeTwelve);
        g2 = new Sudoku(MODE.SPANISH, 1, boardSizeTwelve);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        g = new Sudoku(MODE.SPANISH, 1, boardSizeNine);
        g2 = new Sudoku(MODE.SPANISH, 1, boardSizeNine);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        String[] tmpEList = wordListE(4);
        String[] tmpOList = wordListO(4);
        // testing manual mode
        g = new Sudoku(MODE.MANUAL, 1, boardSizeFour, tmpEList, tmpOList);
        g2 = new Sudoku(MODE.MANUAL, 1, boardSizeFour, tmpEList, tmpOList);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        tmpEList = wordListE(6);
        tmpOList = wordListO(6);
        g = new Sudoku(MODE.MANUAL, 1, boardSizeSix, tmpEList, tmpOList);
        g2 = new Sudoku(MODE.MANUAL, 1, boardSizeSix, tmpEList, tmpOList);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        tmpEList = wordListE(12);
        tmpOList = wordListO(12);
        g = new Sudoku(MODE.MANUAL, 1, boardSizeTwelve, tmpEList, tmpOList);
        g2 = new Sudoku(MODE.MANUAL, 1, boardSizeTwelve, tmpEList, tmpOList);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));

        tmpEList = wordListE(9);
        tmpOList = wordListO(9);
        g = new Sudoku(MODE.MANUAL, 1, boardSizeNine, tmpEList, tmpOList);
        g2 = new Sudoku(MODE.MANUAL, 1, boardSizeNine, tmpEList, tmpOList);
        assertFalse(equalArr(g.getEngWords(), g2.getEngWords()));
        assertFalse(equalArr(g.getNonEngWords(), g2.getNonEngWords()));


    }
    @Test
    public void testGameOverL() {
        testGameOver(MODE.FRENCH, 0, boardSizeFour);
        testGameOver(MODE.FRENCH, 1, boardSizeFour);
        testGameOver(MODE.FRENCH, 2, boardSizeSix);
        testGameOver(MODE.FRENCH, 3, boardSizeSix);

        testGameOver(MODE.FRENCH, 2, boardSizeTwelve);
        testGameOver(MODE.FRENCH, 2, boardSizeTwelve);
        testGameOver(MODE.FRENCH, 1, boardSizeNine);
        testGameOver(MODE.FRENCH, 3, boardSizeNine);
    }
    // Tests board generation and isWin function
    // a sudoku board generated with 'difficulty == 0' means board is filled
    // such that all numbers are without conflict
    // therefore, if we call isWin(), it should return true
    public void testGameOver(MODE mode, int diff, int[] boardSize) {

        // Tests board generation and isWin function
        // a sudoku board generated with 'difficulty == 0' means board is filled
        // such that all numbers are without conflict
        // therefore, if we call isWin(), it should return true
        if (mode == MODE.MANUAL) {
            ArrayList<Word> wordList = new ArrayList<>();
            for (int i = 0; i < 9; i++) {
                wordList.add(new Word("" + i, "" + i*2, i));
            }

            Sudoku wadup = new Sudoku(mode, diff, boardSize, wordList);
            wadup.isComplete();
            if (diff == 0) {
                assertTrue(wadup.gameOver());
            } else {
                assertFalse(wadup.gameOver());
            }
        } else {
            Sudoku wadup = new Sudoku(mode, diff, boardSize);
            wadup.isComplete();
            if (diff == 0) {
                assertTrue(wadup.gameOver());
            } else {
                assertFalse(wadup.gameOver());
            }

        }
    }

    // making sure the board generation algorithm is not the same each time
    @Test
    public void testBoardGen() {
        Sudoku a = new Sudoku(MODE.FRENCH, 1, boardSizeNine);
        Sudoku b = new Sudoku(MODE.FRENCH, 1, boardSizeNine);
        assertFalse(sameArr(a.currentBoard,b.currentBoard));

        Sudoku c = new Sudoku(MODE.SPANISH, 1, boardSizeNine);
        Sudoku d = new Sudoku(MODE.SPANISH, 1, boardSizeNine);
        assertFalse(sameArr(c.currentBoard,d.currentBoard));

        ArrayList<Word> wordList = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            wordList.add(new Word("" + i, "" + i*2, i));
        }

        Sudoku e = new Sudoku(MODE.MANUAL, 1, boardSizeNine, wordList);
        Sudoku f = new Sudoku(MODE.MANUAL, 1, boardSizeNine, wordList);
        assertFalse(sameArr(e.currentBoard,f.currentBoard));
    }

    // same 2d array helper
    private boolean sameArr(int[][] a, int[][] b) {
        if (a == null) {
            if (b != null) {
                return false;
            }
        }
        if (b == null) {
            if (a != null) {
                return false;
            }
        }
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (a[i].length != b[i].length) return false;
            for (int j = 0; j < a[i].length; j++) {
                if (a[i][j] != b[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }


    @Test
    public void testUpdateCell() {
        // we must find a spot that is not permanent (pre filled)
        // we test ALL the spots
        // 9x9 game
        Sudoku game = new Sudoku(MODE.FRENCH, 1, boardSizeNine);
        boolean[][] perms = game.getPerms();
        for (int i = 0; i < boardSizeNine[0]; i++) {
            for (int j = 0; j < boardSizeNine[0]; j++) {
                if (perms[i][j] == false) {
                    // add and then erase
                    game.setActiveValue(i);
                    assertTrue(game.getActiveValue() == i);
                    game.updateCell(i+1,j+1,0); // 0 = add mode
                    assertEquals(i, game.currentBoard[i][j]);
                    game.updateCell(i+1,j+1,1); // 1 = erase mode
                    assertEquals(0, game.currentBoard[i][j]);
                }
            }
        }

        // we test 4x4 game
        int[] boardSizeNine2 = boardSizeNine(4);
        game = new Sudoku(MODE.FRENCH, 1, boardSizeNine2);
        perms = game.getPerms();
        for (int i = 0; i < boardSizeNine2[0]; i++) {
            for (int j = 0; j < boardSizeNine2[0]; j++) {
                if (perms[i][j] == false) {
                    // add and then erase
                    game.setActiveValue(i);
                    game.updateCell(i+1,j+1,0); // 0 = add mode
                    assertEquals(i, game.currentBoard[i][j]);
                    game.updateCell(i+1,j+1,1); // 1 = erase mode
                    assertEquals(0, game.currentBoard[i][j]);
                }
            }
        }

        // we test 6x6 game
        boardSizeNine2 = boardSizeNine(6);
        game = new Sudoku(MODE.FRENCH, 1, boardSizeNine2);
        perms = game.getPerms();
        for (int i = 0; i < boardSizeNine2[0]; i++) {
            for (int j = 0; j < boardSizeNine2[0]; j++) {
                if (perms[i][j] == false) {
                    // add and then erase
                    game.setActiveValue(i);
                    game.updateCell(i+1,j+1,0); // 0 = add mode
                    assertEquals(i, game.currentBoard[i][j]);
                    game.updateCell(i+1,j+1,1); // 1 = erase mode
                    assertEquals(0, game.currentBoard[i][j]);
                }
            }
        }

        // we test 12x12 game
        boardSizeNine2 = boardSizeNine(12);
        game = new Sudoku(MODE.FRENCH, 1, boardSizeNine2);
        perms = game.getPerms();
        for (int i = 0; i < boardSizeNine2[0]; i++) {
            for (int j = 0; j < boardSizeNine2[0]; j++) {
                if (perms[i][j] == false) {
                    // add and then erase
                    game.setActiveValue(i);
                    game.updateCell(i+1,j+1,0); // 0 = add mode
                    assertEquals(i, game.currentBoard[i][j]);
                    game.updateCell(i+1,j+1,1); // 1 = erase mode
                    assertEquals(0, game.currentBoard[i][j]);
                }
            }
        }

    }

    // helper
    private boolean equalArr(String[] a, String[] b) {
        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) {
                return false;
            }
        }
        return true;
    }

    private int[] boardSizeNine(int sideLength) {
        int[] boardSizeNine = new int[3];
        boardSizeNine[0] = sideLength;
        if (sideLength == 4) {
            boardSizeNine[1] = boardSizeNine[2] = 2;
        } else if (sideLength == 6) {
            boardSizeNine[1] = 2;
            boardSizeNine[2] = 3;
        } else if (sideLength == 9) {
            boardSizeNine[1] = boardSizeNine[2] = 3;
        } else {
            boardSizeNine[1] = 3;
            boardSizeNine[2] = 4;
        }
        return boardSizeNine;
    }

    // helper
    private void outputArr(String[] a) {
        for (int i = 0; i < a.length; i++) {
            System.out.print(" "+ a[i]);
        }
    }

    private String[] wordListE(int n) {
        String[] tmp = new String[n];
        for (int i = 0; i < n; i++) {
            tmp[i] = Integer.toString(i);
        }
        return tmp;
    }

    private String[] wordListO(int n) {
        String[] tmp = new String[n];
        for (int i = 0; i < n; i++) {
            tmp[i] = Integer.toString(i) + Integer.toString(i);
        }
        return tmp;
    }

    private boolean contains(String[] arr, String str) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == str) {
                return true;
            }
        }
        return false;
    }
}