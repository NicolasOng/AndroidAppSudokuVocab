package com.sudoku.model;

import android.content.Context;
import android.renderscript.ScriptGroup;
import android.util.Log;

import com.sudoku.utils.CSVFile;
import com.sudoku.utils.MODE;
import com.sudoku.model.Word;
import com.sudoku.utils.CSVFile;
import com.sudoku.utils.MODE;

import java.util.ArrayList;
import java.io.InputStream;
import java.util.Random;

// Defines a game of Sudoku!
// Creates a sudoku board (supporting 4x4, 6x6, 9x9, 12x12 as of now)
// Randomly generates a sudoku board as 2D  array
// Default languages are FRENCH and SPANISH
// Otherwise, manual word lists and or CSV file required

public class Sudoku {
    // sideLength + sideLength/2
    // sideLength + sideLength/2
    private static final String TAG = "Sudoku";
    public int[][] currentBoard;
    private boolean[][] permanenceGrid;
    //defines the size of the grid.
    //possible values: 6x6 (2x3 rec), 4x4 (2x2 rec), 12x12 (3x4 rec) 9x9 (3x3 rec)

    int subGridHeight;
    int subGridWidth;
    int sideLength;

    //sets what items go into the grid:
    //options are engligh (3), nonEnglish (2), and number (0).
    public int permVals = 1;
    public int nonPermVals = 2;

    // english version of default word list for French mode
    private String[] eFrenchList = {"cat", "dog", "boy", "girl", "rat", "pig", "I", "bad", "yes", "hello", "five", "red"};
    // french version of default word list for French mode
    private String[] oFrenchList = {"chat", "chien", "garcon", "fille", "rat", "porc", "je", "mal", "oui", "bonjour", "cinq", "rouge"};

    // english version of default word list for Spanish mode
    private String[] eSpanishList = {"cat", "dog", "boy", "girl", "rat", "pig", "I", "bad", "yes", "hello", "five", "red"};
    // spanish version of default word list for Spanish mode
    private String[] oSpanishList = {"gata", "perra", "niño", "niña", "rata", "cerda", "yo", "mala", "sí", "hola", "cinco", "roja"};

    // actual word lists to be utilized while game is playing
    private String[] eWords;
    private String[] oWords;

    // game difficulty, sets number of cells to remove between 2 numbers (low, high)
    private int difficulty;
    private int diffHi, diffLo;

    private boolean gameComplete = false;

    // the game mode includes 4 variants
    // FRENCH, SPANISH, CSV FILE MODE, MANUAL WORD INPUT MODE
    private MODE mode = MODE.FRENCH;

    // currently selected word by player, used to index the wordlists, eWords, and oWords
    int activeValue = 1;

    // to be utilized for generating priority lists
    public ArrayList<Word> csvFullList;
    public String[] csvFullListEng;


    // temp array of lists of possible numbers that can go in a square
    // must be called before board begins generation
    private ArrayList<Integer>[][] availableNums;


    // default constructor, almost never used
    // only really useful for testing purposes!
    public Sudoku() {
        this.difficulty = 0;
        sideLength = 9;
        subGridWidth = 3;
        subGridHeight = 3;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        eWords = eFrenchList;
        oWords = oFrenchList;
        randomizeWordList(); // word list is not the same each game
        // generate sudoku board
        Log.d(TAG, "This happened");
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells(); // should remove nothing
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }
    }

    // For french / spanish mode
    // Difficulty = number of cells to remove upon new game
    public Sudoku(MODE mode, int difficulty, int[] boardSize) {
        if (mode == MODE.CSV) {
            throw new IllegalArgumentException("For CSV Mode, input stream argument is required");
        } else if (mode == MODE.MANUAL) {
            throw new IllegalArgumentException("For Manual Mode, word list argument is required");
        } else if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty must be greater than 0");
        } else if (difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be < 4");
        }
        this.difficulty = difficulty;
        sideLength = boardSize[0];
        subGridHeight = boardSize[1];
        subGridWidth = boardSize[2];
        this.mode = mode;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        if (mode == MODE.FRENCH || mode == MODE.SPANISH) {
            makeWordList();
            randomizeWordList();
        }
        Log.d(TAG, "This happened");
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells();
        Log.d(TAG, "Removed cells");
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }
        Log.d(TAG, "cells removed");
    }


    // Designed for games where user wants to use their CSV file
    public Sudoku(MODE mode, int difficulty, int[] boardSize, InputStream inputStream) {
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty must be greater than 0");
        } else if (difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be < 4");
        } else {
            this.difficulty = difficulty;
        }
        // setup board size attributes
        sideLength = boardSize[0];
        subGridHeight = boardSize[1];
        subGridWidth = boardSize[2];
        if (inputStream != null) {
            makeWordList(inputStream);
            randomizeWordList();
        } else {
            throw new IllegalArgumentException();
        }

        this.mode = mode;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        Log.d(TAG, "This happened");
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells();
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }

    }

    // Designed for games where user wants to use their CSV file
    public Sudoku(MODE mode, int difficulty, int[] boardSize, InputStream inputStream, int[] priority) {
        if (inputStream != null) {
            makeWordList(inputStream, priority);
            randomizeWordList();
        } else {
            throw new IllegalArgumentException();
        }
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty must be greater than 0");
        } else if (difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be < 4");
        } else {
            this.difficulty = difficulty;
        }
        // setup board size attributes
        sideLength = boardSize[0];
        subGridHeight = boardSize[1];
        subGridWidth = boardSize[2];
        this.mode = mode;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        Log.d(TAG, "This happened");
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells();
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }
    }

    // Designed for games where user wants to use manually inputted words
    // Two static array version
    public Sudoku(MODE mode, int difficulty, int[] boardSize, String[] engList, String[] notEngList) {
        // setup board size attributes
        sideLength = boardSize[0];
        subGridHeight = boardSize[1];
        subGridWidth = boardSize[2];
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty must be greater than 0");
        } else if (difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be < 4");
        } else {
            this.difficulty = difficulty;
        }
        this.mode = mode;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        makeWordList(engList, notEngList);
        randomizeWordList();
        Log.d(TAG, "This happened");
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells();
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }
    }

    // Designed for games where user wants to use manually inputted words
    // ArrayList version
    public Sudoku(MODE mode, int difficulty, int[] boardSize, ArrayList<Word> wordList) {
        // setup board size attributes
        sideLength = boardSize[0];
        subGridHeight = boardSize[1];
        subGridWidth = boardSize[2];
        if (wordList.size() != sideLength) {
            throw new IllegalArgumentException("word list size is incorrect = " + wordList.size() + " should be " + sideLength);
        }
        if (difficulty < 0) {
            throw new IllegalArgumentException("Difficulty must be greater than 0");
        } else if (difficulty > 3) {
            throw new IllegalArgumentException("Difficulty must be < 4");
        } else {
            this.difficulty = difficulty;
        }

        this.mode = mode;
        currentBoard = new int[sideLength][sideLength];
        permanenceGrid = new boolean[sideLength][sideLength];
        if (wordList.size() != sideLength) {
            throw new IllegalArgumentException("word list size is incorrect = " + wordList.size());
        } else {
            setWordList(wordList);
            randomizeWordList();
        }
        availableNums = new ArrayList[sideLength][sideLength];
        generate();
        while (hasZero()) {
            Log.d(TAG, "HAS ZERO");
            generate();
        }
        Log.d(TAG, "Sudoku board is filled");
        Log.d(TAG, "Removing cells");
        this.calcDifficulty();
        this.removeCells();
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                if (currentBoard[row][col] != 0) {
                    permanenceGrid[row][col] = true;
                }
            }
        }
    }

    // default word list generators
    private void makeWordList() {
        String[] tmp = new String[sideLength];
        String[] tmp2 = new String[sideLength];
        if (mode == MODE.FRENCH) {
            // cuts default list (size 12) to fit current game size
            System.arraycopy(eFrenchList, 0, tmp, 0, sideLength);
            System.arraycopy(oFrenchList, 0, tmp2, 0, sideLength);
        } else if (mode == MODE.SPANISH) {
            System.arraycopy(eSpanishList, 0, tmp, 0, sideLength);
            System.arraycopy(oSpanishList, 0, tmp2, 0, sideLength);
        } else {
            throw new RuntimeException("Uh, the mode is wrong... Try the other makeWordList functions");
        }
        eWords = tmp;
        oWords = tmp2;
    }

    // CSV MODE
    private void makeWordList(InputStream inputStream) {
        CSVFile csvFile = new CSVFile(inputStream, sideLength);
        csvFullList = csvFile.read(); // gets the entire word list
        for (int i = 0; i < csvFullList.size(); i++) {
            csvFullListEng = new String[csvFullList.size()];
            csvFullListEng[i] = csvFullList.get(i).getEnglish();
        }
        // if word list is greater than necessary, cut
        if (csvFullList.size() > sideLength) {
            Log.d(TAG, "csvFullList.size > sideLength:" + sideLength);
            setWordList(csvFile.cut());
        } else {
            Log.d(TAG, "derpaderpa");
            setWordList(csvFullList);
        }
        Log.d(TAG, "word list generated o-o");

    }

    // CSV MODE WITH PRIORITY
    private void makeWordList(InputStream inputStream, int[] priority) {
        if (mode == MODE.CSV) {
            CSVFile csvFile = new CSVFile(inputStream, sideLength);
            csvFullList = csvFile.read();
            if (csvFullList.size() > sideLength) {
                setWordList(csvFile.cut(priority));
            } else {
                setWordList(csvFullList);
            }
        } else {
            throw new RuntimeException("Uh, the mode is wrong...");
        }
    }

    // Copies word lists to use for game
    private void makeWordList(String[] engList, String[] notEngList) {
        if (engList.length < sideLength || notEngList.length < sideLength) {
            throw new IllegalArgumentException(
                    "array length should be size: " + sideLength + ". engList size = " + engList.length + ", notEngLish size = " +notEngList.length);
        } else if (engList == notEngList) {
            throw new IllegalArgumentException(
                    "The word lists are the same. No.");
        }
        eWords = engList;
        oWords = notEngList;
        String[] tmp = new String[sideLength];
        String[] tmp2 = new String[sideLength];
        System.arraycopy(engList, 0, tmp, 0, sideLength);
        System.arraycopy(notEngList, 0, tmp2, 0, sideLength);
        eWords = tmp;
        oWords = tmp2;
    }

    // helper for array lists....
    private void setWordList(ArrayList<Word> wordList) {
        eWords = new String[sideLength];
        oWords = new String[sideLength];
        for (int i = 0; i < sideLength; i++) {
            eWords[i] = wordList.get(i).getEnglish();
            oWords[i] = wordList.get(i).getNotEnglish();
        }
    }

    // set everything up before filling the board
    private void generate() {
        // initialize available numbers array
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                availableNums[i][j] = new ArrayList<>();
            }
        }

        // fill each list of available numbers with 1-9
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                for (int x = 1; x < sideLength+1; x++) {
                    availableNums[i][j].add(x);
                }
            }
        }

        boolean error = true;
        while (error == true) {
            try {
                //start solving the board at square (0,0)
                solve(0, 0);
                // there weren't any errors, yay!
                error = false;
            } catch (StackOverflowError e) {
                //there was some sort of error
                //clear puzzle and keep trying until it works
                for (int i = 0; i < sideLength; i++) {
                    for (int j = 0; j < sideLength; j++) {
                        currentBoard[i][j] = 0;
                    }
                }
            }
        }

    }

    private void solve(int row, int col) {

        Random gen = new Random();
        boolean numFound = false;

        // set current square to empty
        currentBoard[row][col] = 0;

        // while we still have numbers to try and have not found a valid number
        while (!availableNums[row][col].isEmpty() && !numFound) {

            // pick random number from list of available numbers
            int num = availableNums[row][col].get(gen
                    .nextInt(availableNums[row][col].size()));

            // check if generated number is valid
            if (!conflictSpot(row, col, num)) {

                // add number to square
                currentBoard[row][col] = num;
                numFound = true;

                // remove added number from list of available numbers
                availableNums[row][col].remove(Integer.valueOf(num));

                break;

            } else {
                // remove number from list of available numbers
                availableNums[row][col].remove(Integer.valueOf(num));
                // if we are out of numbers, stop trying to find a number
                if (availableNums[row][col].isEmpty()) {
                    break;
                }
            }

        }

        // if out of numbers, replenish this squares numbers
        // go back 1 square
        if (availableNums[row][col].isEmpty() == true) {

            for (int x = 1; x < sideLength+1; x++) {
                availableNums[row][col].add(x);
            }

            back(row, col);
        } // if a number was added and there are still more empty squares
        // go forward 1 square
        else if (numFound == true && (emptyCheck() == true)) {
            next(row, col);
        }
    }

    // move to the next square
    private void next(int row, int col) {
        if (col == (sideLength-1)) {
            solve(row + 1, 0);
        } else {
            solve(row, col + 1);
        }
    }

    // move to the previous square
    private void back(int row, int col) {
        if (col == 0 && row != 0) {
            solve(row - 1, sideLength-1);
        } else if (col != 0){
            solve(row, col - 1);
        }
    }

    // searches puzzle for empty squares
    // 0 denotes empty
    private boolean emptyCheck() {

        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (currentBoard[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }

    // Remove cells based on difficulty
    // To be used AFTER board has been generated
    private void removeCells() {
        if (difficulty == 0) return; // difficulty = 0 is for testing purposes...
        // generate a random number between diff.low to diff.high
        Random rand = new Random();
        Log.d(TAG, "diffhi = " + diffHi);
        Log.d(TAG, "diffLo = " + diffLo);
        int count = rand.nextInt(diffHi) + diffLo;
        int col, row;
        for (int i = 0; i < count ; i++) {
            row = rand.nextInt(sideLength);
            col = rand.nextInt(sideLength);
            while (currentBoard[row][col] == 0) {
                row = rand.nextInt(sideLength);
                col = rand.nextInt(sideLength);
            }
            currentBoard[row][col] = 0;
            permanenceGrid[row][col] = false;
        }
        Log.d(TAG, "Removed cells");
    }

    // returns true if the number generates a conflict
    // changed the 9s and 3s to their appropriate counterparts, but doesn't seem to work RIP
    private boolean conflictSpot(int row, int col, int num) {
        return conflictBox(row - row  % subGridHeight, col - col % subGridWidth, num) || conflictRow(row, num) || conflictCol(col, num);
    }

    // finds a spot on the board that the value can be placed into
    public int[] getHint(int value) {
        int[] hint = new int[2];
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (!permanenceGrid[i][j] && !conflictSpot(i,j,value+1)) {
                    hint[0] = i;
                    hint[1] = j;
                    return hint;
                }
            }
        }
        return hint;
    }

    // return true if theres a equal number in box
    // Depending on board size:
    // 4x4, box = 2x2
    // 6x6, box = 2x3
    // 9x9, box = 3x3
    // 12x12, box = 3x4
    private boolean conflictBox(int rowStart, int colStart, int num) {
        //make both given parameters -4 for it to function as normal
        for (int i = 0; i < subGridHeight; i++) {
            for (int j = 0; j < subGridWidth; j++) {
                //Log.d(TAG, "conflictBox:" + rowStart + "col" + colStart);
                if (currentBoard[rowStart + i][colStart + j] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    // returns true if theres an equal number in row
    private boolean conflictRow(int row, int num) {
        //make givenCol -1 to function as normal
        for (int col = 0; col < sideLength; col++) {
            if (currentBoard[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    // returns true if theres an equal num in col
    private boolean conflictCol(int col, int num) {
        //make givenRow -1 for the function to function as normal.
        for (int row = 0; row < sideLength; row++) {
            if (currentBoard[row][col] == num) {
                return true;
            }
        }
        return false;
    }

    // this temporarily removes the current value
    // then checks if inputting the value will cause a conflict
    // if the value causes a conflict return false
    // otherwise, return true (game is win)
    public boolean isComplete() {
        int val;
        for (int row = 0; row < sideLength; row++) {
            for (int col = 0; col < sideLength; col++) {
                val = currentBoard[row][col];
                currentBoard[row][col] = 0;
                if (conflictSpot(row, col, val)) {
                    currentBoard[row][col] = val; // put value back
                    gameComplete = false;
                    return false;
                }
                currentBoard[row][col] = val; // put value back
            }
        }
        // board completed
        gameComplete = true;
        return true;
    }

    // returns true if game is over (board is complete)
    // otherwise, false
    public boolean gameOver() {
        return gameComplete;
    }

    public MODE getMode() { return mode; }

    public String[] getEngWords() {
        return eWords;
    }

    public String[] getNonEngWords() {
        return oWords;
    }

    //updates the given cell.
    //row and col describe which cell. (1-gamelength)
    //value is what value the player currently has active. (1-gamelength)
    //mode can be 0 (add) or 1 (remove)
    //in add mode, the given value is added to the cell. in remove mode, the value in the cell is removed (doesn't matter the value).
    public void updateCell(int row, int col, int mode) {
        if (gameComplete) {
            Log.d(TAG, "Game is done. Stop it");
            return;
        }
        row--;
        col--;
        //if the cell is not permanent,
        if (!permanenceGrid[row][col]) {
            //if the mode is add mode,
            if (mode == 0) {
                //give the cell the value:
                currentBoard[row][col] = activeValue;
            //if the mode is erase mode,
            } else if (mode == 1) {
                //remove the value from the cell (set to 0):
                currentBoard[row][col] = 0;
            }
        }
        // check if game is done
        // updates gameComplete boolean var
        if (isComplete()) {
            gameComplete = true;
        }
    }

    // Randomizes the game's word lists to counteract repetitiveness
    private void randomizeWordList() {
        Random rand = new Random();
        for (int i = eWords.length - 1; i > 0; i--) {
            int index = rand.nextInt(i+1);
            String tmp = eWords[index];
            eWords[index] = eWords[i];
            eWords[i] = tmp;

            tmp = oWords[index];
            oWords[index] = oWords[i];
            oWords[i] = tmp;
        }
    }

    // returns 2d array of the permanence grid
    public boolean[][] getPerms() {
        return permanenceGrid;
    }

    // returns the current difficulty
    public int getDifficulty() {
        return difficulty;
    }

    // returns number of cells in sudoku grid
    public int getCount() {return sideLength*sideLength;}

    // returns size of sudoku game
    public int getSideLength() {return sideLength;}

    // returns the value at a cell
    public int getValueAt(int row, int col) {
        return currentBoard[row][col];
    }

    // returns true if a certain cell is permanent
    public boolean getPermanenceAt(int row, int col) {
        return permanenceGrid[row][col];
    }

    public String getWordAt(int row, int col) {
        //get the word to be displayed in the grid at this position.
        int val = getValueAt(row, col);
        if (val == 0) {
            return "";
        }
        boolean perm = getPermanenceAt(row, col);
        int type = 0;
        if (perm) {
            type = permVals;
        } else {
            type = nonPermVals;
        }

        if (type == 0) {
            return Integer.toString(val);
        } else if (type == 1) {
            return eWords[val - 1];
        } else if (type == 2) {
            return oWords[val - 1];
        }
        return "";
    }

    public String getWordForButtonsAt(int val) {
        //get the word to be displayed in the grid at this position.
        if (nonPermVals == 0) {
            return Integer.toString(val);
        } else if (nonPermVals == 1) {
            return eWords[val - 1];
        } else if (nonPermVals == 2) {
            return oWords[val - 1];
        }
        return "";
    }


    public void setActiveValue(int value) {
        if (value <= sideLength && value >= 0) {
            activeValue = value;
        } else {
            throw new IllegalArgumentException("value must be between 1-"+sideLength);
        }
    }

    public int getActiveValue() {
        return activeValue;
    }

    // generates difficulty ranges (diffHi, diffLo)
    // difficulty = number of cells to remove
    // as of now, there are only 3 difficulties available
    private void calcDifficulty() {
        diffHi = sideLength;
        diffLo = sideLength/2;
        if (difficulty > 1) {
            for (int i = 2; i < 4; i++) {
                // make sure diffHi is not greater than total number of cells
                if ((diffHi + diffHi/2) > sideLength*sideLength) {
                    return;
                }
                diffLo = diffHi;
                diffHi = diffHi + (diffLo/2);
            }
        }
    }

    // returns true if the board contains a zero
    private boolean hasZero() {
        for (int i = 0; i < sideLength; i++) {
            for (int j = 0; j < sideLength; j++) {
                if (currentBoard[i][j] == 0) {
                    return true;
                }
            }
        }
        return false;
    }
}
