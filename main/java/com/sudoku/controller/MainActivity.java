package com.sudoku.controller;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.os.Build;

import com.sudoku.adapter.ButtonGridAdapter;
import com.sudoku.adapter.GridAdapter;
import com.sudoku.utils.MODE;
import com.sudoku.R;
import com.sudoku.model.Sudoku;
import com.sudoku.utils.TextToSpeak;

import java.util.Locale;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Activity for the sudoku game itself


//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private TextToSpeak textToSpeech;
    //AE button:
    private TextView addEraseButton;
    private TextView listenBtn;
    private TextView hintBtn;

    private Chronometer timer;
    private int hintOnOff = 0;
    private int listenOnOff = 0; // 0 = off, 1 = on
    //NG button:
    private TextView newGameButton;
    //activeValue is 1-9
    // active WORD
    private int activeValue = 1;
    //mode is 0-1
    //0 is "add" mode
    //1 is "subtract" mode
    private int mode = 1;

    MODE gameMode = MODE.FRENCH;
    //the grid for sudoku:
    Sudoku theGrid;
    boolean alreadyWon; // used so the game complete functionality does not repeat after onclick
    // vars to be used every time the new game button is pressed
    // for gameMode = MODE.CSV
    String selectedCSV;
    // for gameMode = MODE.MANUAL
    String[] manEngList;
    String[] manNonEngList;

    // listening mode true / false
    boolean listening = false;
    boolean regular = false;
    // priority list array, integer position represents index in word list, values represent priority
    int[] priorityArr;

    // difficulty - get from main menu
    // used to calculate number of cells to remove
    int difficulty = 0;
    int[] boardSize;
    final GridAdapter gridAdapter = new GridAdapter(this);
    final ButtonGridAdapter buttonAdapter = new ButtonGridAdapter(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the layout and the saved Instance State.
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);
        Log.d(TAG, "set content view to ");

        if (savedInstanceState != null) {

        }

        // retrieve game mode from main menu
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            gameMode = (MODE)extras.getSerializable("selectedMode");
            Log.d(TAG, "Difficulty == " + difficulty);
            difficulty = extras.getInt("difficulty");
            Log.d(TAG, "Difficulty == " + difficulty);
            boardSize = extras.getIntArray("boardSize");
            listening = extras.getBoolean("listening");
            regular = extras.getBoolean("regular");
        }
        gridAdapter.isListening(listening);
        buttonAdapter.isListening(listening);

        // retrieve filename to parse
        if (gameMode == MODE.CSV) {
            selectedCSV = extras.getString("fileName");
            Log.d(TAG, "onCreate: csv mode");
            if (priorityArr != null) {
                theGrid = new Sudoku(gameMode, difficulty, boardSize, getInputStream(selectedCSV), priorityArr);
            } else {
                theGrid = new Sudoku(gameMode, difficulty, boardSize, getInputStream(selectedCSV));
            }
            priorityArr = new int[theGrid.csvFullList.size()];
        } else if (gameMode == MODE.MANUAL) {
            Log.d(TAG, "onCreate: manual mode");
            String[] engList = extras.getStringArray("engList");
            String[] nonEngList = extras.getStringArray("nonEngList");
            Log.d(TAG, "Creating boardSize = " + boardSize[0] + " " + boardSize[1] + " " + boardSize[2]);
            theGrid = new Sudoku(gameMode, difficulty, boardSize, engList, nonEngList);
        } else {
            Log.d(TAG, "onCreate: else mode");
            Log.d(TAG, "Creating boardSize = " + boardSize[0] + " " + boardSize[1] + " " + boardSize[2]);
            theGrid = new Sudoku(gameMode, difficulty, boardSize);
        }
        alreadyWon = false;

        Log.d(TAG, "setting up the gridview");
        GridView gridView = (GridView)findViewById(R.id.gridview);
        //set how many columns gridview should have
        gridView.setNumColumns(theGrid.getSideLength());
        //put the grid in the grid adapter
        gridAdapter.grid = theGrid;
        gridView.setAdapter(gridAdapter);
        //put the grid in the grid adapter, so we don't have to pass values into the GA constantly
        configureGridForDifferentModes();

        timer = findViewById(R.id.timer);
        resetTimer();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //do stuff on item click
                Log.d(TAG, "position = " + position);
                int col = position % theGrid.getSideLength();
                int row = (position - col) / theGrid.getSideLength();
                //
                theGrid.updateCell(row + 1, col + 1, mode);
                Log.d(TAG, "active value = " + activeValue + " row = " + row + " col + " + col);

                // tells grid adapter to redraw itself
                gridAdapter.notifyDataSetChanged();

                if (listenOnOff == 1) {
                    if (theGrid.currentBoard[row][col] != 0) {
                        //grab string from the selected word in the oWords array
                        //pass word to speak function
                        String editText = theGrid.getNonEngWords()[theGrid.currentBoard[row][col] - 1];
                        textToSpeech = new TextToSpeak(getApplicationContext(), Locale.CANADA, editText);


                    }
                }

                // check if game is over and prompt the player to play new game
                // if the user does not listen to prompt
                // the alreadyWon boolean value, which is now set to True, will prompt the user
                // to play new game again.
                if (theGrid.gameOver()) {
                    if (alreadyWon) {
                        Toast.makeText(MainActivity.this, "Game is Over! Please Press New Game!", Toast.LENGTH_SHORT).show();
                    } else {
                        newGameButton.setBackgroundColor(Color.GREEN);
                        Toast.makeText(MainActivity.this, "Board Complete!", Toast.LENGTH_LONG).show();
                        playWow();
                        alreadyWon = true;
                    }

                }
            }
        });

        //check if in landscape. needed to know how many columns in buttonsView.
        int bvCols = 1;
        int orientation = getResources().getConfiguration().orientation;
        if (!(orientation == Configuration.ORIENTATION_LANDSCAPE)) {
            // In portrait
            int sl = theGrid.getSideLength();
            if (sl % 2 == 0) {
                bvCols = sl/2;
            } else if (sl % 3 == 0) {
                bvCols = sl/3;
            }
        }

        GridView buttonView = (GridView)findViewById(R.id.buttonsView);
        //set how many columns gridview should have
        buttonView.setNumColumns(bvCols);
        //put the grid in the grid adapter
        buttonAdapter.grid = theGrid;
        buttonAdapter.numRows = theGrid.getSideLength()/bvCols;
        buttonView.setAdapter(buttonAdapter);
        //put the grid in the grid adapter, so we don't have to pass values into the GA constantly

        buttonView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                //do stuff on item click
                activeValue = position + 1;
                changeActiveValue(activeValue);
                // in turn calling your gridAdapter's getView method again for each cell
                Log.d(TAG, "active value = " + activeValue);
            }
        });


        //create the new game button:
        newGameButton = (TextView) findViewById(R.id.new_game);
        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameMode == MODE.CSV) {
                    theGrid = new Sudoku(gameMode, difficulty, boardSize, getInputStream(selectedCSV));
                } else if (gameMode == MODE.MANUAL) {
                    theGrid = new Sudoku(gameMode, difficulty, boardSize, manEngList, manNonEngList);
                } else {
                    theGrid = new Sudoku(gameMode, difficulty, boardSize);
                }
                configureGridForDifferentModes();
                resetTimer();
                alreadyWon = false;
                newGameButton.setBackgroundColor(Color.WHITE);
                Log.d(TAG, "gameMode " + gameMode.name());
//                readCSV(); // must be called again
                initializeGridView();
            }
        });

        //create the mode-changing button
        addEraseButton = (TextView) findViewById(R.id.add_erase);
        addEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleAddErase();
            }
        });



        //create the mode-changing button
        listenBtn = (TextView) findViewById(R.id.listenBtn);
        if (listening) {
            listenBtn.setBackgroundColor(Color.GREEN);
        }
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleListenBtn();
            }
        });

        //create the mode-changing button
        hintBtn = (TextView) findViewById(R.id.hintBtn);
        hintBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int[] hints = theGrid.getHint(activeValue-1);
                // INCREMENT PRIORITY FOR THIS WORD
                if (hints[0] != 0 && hints[1] != 0) {
                    Toast.makeText(MainActivity.this, "Hint: There is a spot for " + theGrid.getNonEngWords()[activeValue-1] + " at row " + (hints[0]+1) + " and column " + (hints[1]+1), Toast.LENGTH_SHORT).show();
                }

            }
        });

        //update the view:
        initializeGridView();
        toggleAddErase();
    }

    // return input stream for the selected csv file
    private InputStream getInputStream(String selectedCSV) {
        try {
            return this.openFileInput(selectedCSV);
        } catch (FileNotFoundException e) {
            Log.d(TAG, "file not found XD");
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }
    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.i(TAG, "onSaveInstanceState");
        //savedInstanceState.putInt(KEY_INDEX, mCurrentIndex);
        //savedInstanceState.putIntArray(KEY_ANSWERS, userAnswers);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    // setup all the values required for the layouts
    private void initializeGridView() {
        //this function initializes the view.
        gridAdapter.nonEngWords = theGrid.getNonEngWords();
        gridAdapter.engWords = theGrid.getEngWords();
        buttonAdapter.engWords = theGrid.getEngWords();
        buttonAdapter.nonEngWords = theGrid.getNonEngWords();
        gridAdapter.grid = theGrid;
        buttonAdapter.grid = theGrid;
        //tintAllCellsWhite();
        changeActiveValue(1);
        //createLegend(18);
        //updateGridView(theGrid.getWords(), theGrid.getPerms(), theGrid.getConflictGrid(1, 1), 24, 10);
    }

    // toggles listen button (on/off)
    private void toggleListenBtn() {
        if (listenOnOff == 1) {
            listenOnOff = 0;
            if (!listening) {
                listenBtn.setBackgroundColor(Color.WHITE);
            } else {
                listenOnOff = 1;
                listenBtn.setBackgroundColor(Color.GREEN);
            }

        } else {
            listenOnOff = 1;
            if (!listening) {
                listenBtn.setBackgroundColor(Color.WHITE);
            } else {
                listenOnOff = 1;
                listenBtn.setBackgroundColor(Color.GREEN);
            }
        }
    }

    // toggle add or erase mode
    // add = player inputs values into cells
    // erase = player removes values from cells
    private void toggleAddErase() {
        mode++;
        mode = mode % 2;
        if (mode == 0) {
            addEraseButton.setText(R.string.add);
            addEraseButton.setBackgroundColor(Color.GREEN);
        } else if (mode == 1) {
            addEraseButton.setText(R.string.erase);
            addEraseButton.setBackgroundColor(Color.RED);
        }
    }

    //runs whenever the active value is changed:
    //change the BG of the current button to normal,
    //highlightCellsWithValue(activeValue, android.R.color.white);
    //ViewCompat.setBackgroundTintList(b1, ContextCompat.getColorStateList(this, android.R.color.white));
    //update the value,
    private void changeActiveValue(int v) {
        theGrid.setActiveValue(v);
        buttonAdapter.notifyDataSetChanged();
        gridAdapter.notifyDataSetChanged();

    }

    // Plays end game music - Wow
    private void playWow() {
        MediaPlayer mPlayer = MediaPlayer.create(MainActivity.this, R.raw.wow);
        mPlayer.start();
    }

    private void configureGridForDifferentModes() {
        if (listening) {
            theGrid.permVals = 0;
            theGrid.nonPermVals = 1;
        } else if (regular) {
            theGrid.permVals = 0;
            theGrid.nonPermVals = 0;
        } else {
            theGrid.permVals = 1;
            theGrid.nonPermVals = 2;
        }
    }

    private void resetTimer() {
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
    }
}
