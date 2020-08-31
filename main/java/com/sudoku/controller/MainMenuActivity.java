package com.sudoku.controller;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoku.R;
import com.sudoku.utils.CSVFile;
import com.sudoku.utils.MODE;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

// Main Menu

//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class MainMenuActivity extends AppCompatActivity {

    private static final String TAG = "MainMenuActivity";

    //NG button:
    private Button playBtn;
    private Button listenBtn;
    private Button regularBtn;
    // listening mode btn
    private boolean listening = false;
    private boolean regular = false;
    // button to determine game mode
    private Button frenchBtn;
    private Button spanishBtn;
    private Button csvBtn;
    private Button wordListBtn;

    // button to enter word list input activity
    private Button inputWordListBtn;

    // buttons for difficulty selection
    private Button beginnerBtn;
    private Button intermediateBtn;
    private Button expertBtn;
    private int difficulty;

    // buttons for board size
    private Button sizeFourBtn;
    private Button sizeSixBtn;
    private Button sizeNineBtn;
    private Button sizeTwelveBtn;
    private int[] boardSize; // board size, 0 = sideLength, 1 = rows, 2 = columns

    // store word list passed from WordListInputActivity
    // to be passed either to MainActivity to be used for the game
    // or to be passed to WordListInputActivity for current view
    private String[] manEngList;
    private String[] manNonEngList;

    private ArrayList<String> listOfCsvFiles;
    private Button importCsvBtn;
    private Spinner csvListSpinner;
    private Button removeCsvBtn;
    private MODE mode;
    private static final int WORDLIST_ACTIVITY_REQUEST_CODE = 43;
    private static final int READ_REQUEST_CODE = 42;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the layout and the saved Instance State.
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.main_menu);
        Log.d(TAG, "set content view to ");

        if (savedInstanceState != null) {

        }

        playBtn = (Button) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity:
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                //to put info into the intent:
                intent.putExtra("selectedMode", mode);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("boardSize", boardSize);
                // if CSV was selected, send chosen csv file from spinner
                if (mode == MODE.CSV) {
                    if (csvListSpinner.getSelectedItemPosition() < 1) {
                        Toast.makeText(MainMenuActivity.this, "No CSV File Selected", Toast.LENGTH_LONG).show();
                    } else {
                        intent.putExtra("fileName", csvListSpinner.getSelectedItem().toString());
                        startActivity(intent);
                    }
                } else if (mode == MODE.MANUAL) {
                    // Check if manually inputted word list is valid before starting activity
                    if (manWordListValid()) {
                        intent.putExtra("engList", manEngList);
                        intent.putExtra("nonEngList", manNonEngList);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "manWordListInvalid");
                        Toast.makeText(MainMenuActivity.this, "Inputted word list is invalid! Please fix it.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivity(intent);
                }
                Log.d(TAG, "Main Menu Play btn pressed");

            }
        });
        listenBtn = (Button) findViewById(R.id.listenBtn);
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity:
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                //to put info into the intent:
                listening = true;

                //GridAdapter.isListening(listening);
                intent.putExtra("selectedMode", mode);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("listening", listening);
                intent.putExtra("regular", regular);
                intent.putExtra("boardSize", boardSize);

                // if CSV was selected, send chosen csv file from spinner
                if (mode == MODE.CSV) {
                    if (csvListSpinner.getSelectedItemPosition() < 1) {
                        Toast.makeText(MainMenuActivity.this, "No CSV File Selected", Toast.LENGTH_LONG).show();
                    } else {
                        intent.putExtra("fileName", csvListSpinner.getSelectedItem().toString());
                        startActivity(intent);
                    }
                } else if (mode == MODE.MANUAL) {
                    // Check if manually inputted word list is valid before starting activity!
                    if (manWordListValid()) {
                        intent.putExtra("engList", manEngList);
                        intent.putExtra("nonEngList", manNonEngList);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "manWordListInvalid");
                        Toast.makeText(MainMenuActivity.this, "Inputted word list is invalid! Please fix it.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivity(intent);
                }
                Log.d(TAG, "Main Menu Play btn pressed");

            }
        });
        regularBtn = (Button) findViewById(R.id.regularBtn);
        regularBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start MainActivity:
                Intent intent = new Intent(MainMenuActivity.this, MainActivity.class);
                //to put info into the intent:
                regular = true;

                //GridAdapter.isListening(listening);
                intent.putExtra("selectedMode", mode);
                intent.putExtra("difficulty", difficulty);
                intent.putExtra("listening", listening);
                intent.putExtra("regular", regular);
                intent.putExtra("boardSize", boardSize);

                // if CSV was selected, send chosen csv file from spinner
                if (mode == MODE.CSV) {
                    if (csvListSpinner.getSelectedItemPosition() < 1) {
                        Toast.makeText(MainMenuActivity.this, "No CSV File Selected", Toast.LENGTH_LONG).show();
                    } else {
                        intent.putExtra("fileName", csvListSpinner.getSelectedItem().toString());
                        startActivity(intent);
                    }
                } else if (mode == MODE.MANUAL) {
                    // Check if manually inputted word list is valid before starting activity!
                    if (manWordListValid()) {
                        intent.putExtra("engList", manEngList);
                        intent.putExtra("nonEngList", manNonEngList);
                        startActivity(intent);
                    } else {
                        Log.d(TAG, "manWordListInvalid");
                        Toast.makeText(MainMenuActivity.this, "Inputted word list is invalid! Please fix it.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    startActivity(intent);
                }
                Log.d(TAG, "Main Menu Play rglr btn pressed");

            }
        });


        frenchBtn = (Button) findViewById(R.id.frenchBtn);
        frenchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE.FRENCH;
                frenchBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                spanishBtn.setBackgroundColor(Color.WHITE);
                csvBtn.setBackgroundColor(Color.WHITE);
                wordListBtn.setBackgroundColor(Color.WHITE);
            }
        });

        spanishBtn = (Button) findViewById(R.id.spanishBtn);
        spanishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE.SPANISH;
                spanishBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                frenchBtn.setBackgroundColor(Color.WHITE);
                csvBtn.setBackgroundColor(Color.WHITE);
                wordListBtn.setBackgroundColor(Color.WHITE);
            }
        });

        csvBtn = (Button) findViewById(R.id.csvBtn);
        csvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE.CSV;
                csvBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                spanishBtn.setBackgroundColor(Color.WHITE);
                frenchBtn.setBackgroundColor(Color.WHITE);
                wordListBtn.setBackgroundColor(Color.WHITE);
            }
        });

        wordListBtn = (Button)findViewById(R.id.wordListBtn);
        wordListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mode = MODE.MANUAL;
                wordListBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                csvBtn.setBackgroundColor(Color.WHITE);
                spanishBtn.setBackgroundColor(Color.WHITE);
                frenchBtn.setBackgroundColor(Color.WHITE);
            }
        });

        inputWordListBtn = (Button)findViewById(R.id.inputWordListBtn);
        inputWordListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start WordListInputActivity:
                Intent intent = new Intent(MainMenuActivity.this, WordListInputActivity.class);
                intent.putExtra("boardSize", boardSize);
                if (manEngList != null && manNonEngList != null) {
                    intent.putExtra("manEngList", manEngList);
                    intent.putExtra("manNonEngList", manNonEngList);
                }
                startActivityForResult(intent, WORDLIST_ACTIVITY_REQUEST_CODE);
                // look at onActivityResult(..)

            }
        });


        beginnerBtn = (Button)findViewById(R.id.beginnerBtn);
        beginnerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 1;
                beginnerBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                intermediateBtn.setBackgroundColor(Color.WHITE);
                expertBtn.setBackgroundColor(Color.WHITE);


            }
        });

        intermediateBtn = (Button)findViewById(R.id.intermediateBtn);
        intermediateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 2;
                intermediateBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                beginnerBtn.setBackgroundColor(Color.WHITE);
                expertBtn.setBackgroundColor(Color.WHITE);

            }
        });

        expertBtn = (Button)findViewById(R.id.expertBtn);
        expertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                difficulty = 3;
                expertBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                beginnerBtn.setBackgroundColor(Color.WHITE);
                intermediateBtn.setBackgroundColor(Color.WHITE);

            }
        });


        sizeFourBtn = (Button)findViewById(R.id.fourBtn);
        sizeFourBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardSize[0] = 4;
                boardSize[1] = boardSize[2] = 2;
                sizeFourBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                sizeSixBtn.setBackgroundColor(Color.WHITE);
                sizeNineBtn.setBackgroundColor(Color.WHITE);
                sizeTwelveBtn.setBackgroundColor(Color.WHITE);
            }
        });

        sizeSixBtn = (Button)findViewById(R.id.sixBtn);
        sizeSixBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardSize[0] = 6;
                boardSize[1] = 2;
                boardSize[2] = 3;
                sizeFourBtn.setBackgroundColor(Color.WHITE);
                sizeSixBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                sizeNineBtn.setBackgroundColor(Color.WHITE);
                sizeTwelveBtn.setBackgroundColor(Color.WHITE);

            }
        });

        sizeNineBtn = (Button)findViewById(R.id.nineBtn);
        sizeNineBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardSize[0] = 9;
                boardSize[1] = boardSize[2] = 3;
                sizeFourBtn.setBackgroundColor(Color.WHITE);
                sizeSixBtn.setBackgroundColor(Color.WHITE);
                sizeNineBtn.setBackgroundColor(Color.rgb(153, 255, 153));
                sizeTwelveBtn.setBackgroundColor(Color.WHITE);

            }
        });

        sizeTwelveBtn = (Button)findViewById(R.id.twelveBtn);
        sizeTwelveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boardSize[0] = 12;
                boardSize[1] = 3;
                boardSize[2] = 4;
                sizeFourBtn.setBackgroundColor(Color.WHITE);
                sizeSixBtn.setBackgroundColor(Color.WHITE);
                sizeNineBtn.setBackgroundColor(Color.WHITE);
                sizeTwelveBtn.setBackgroundColor(Color.rgb(153, 255, 153));

            }
        });

        // Initializing all the default selections
        // word list
        mode = MODE.FRENCH;
        frenchBtn.setBackgroundColor(Color.rgb(153, 255, 153));
        // difficulty
        difficulty = 1;
        beginnerBtn.setBackgroundColor(Color.rgb(153, 255, 153));
        // board size
        boardSize = new int[3];
        boardSize[0] = 9;
        boardSize[1] = boardSize[2] = 3;
        sizeNineBtn.setBackgroundColor(Color.rgb(153, 255, 153));


        importCsvBtn = (Button) findViewById(R.id.importCsvBtn);
        importCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileBrowser(); // allow user to browse for a csv file adds file to internal storage
            }
        });

        // setup spinner
        // fileNames start at array listOfCsvFiles[1]
        csvListSpinner = (Spinner) findViewById(R.id.csvList);
        listOfCsvFiles = new ArrayList<String>();
        csvListAdd("Select A CSV File");
        File[] files = getFilesDir().listFiles();
        for (File f : files) {
            csvListAdd(cutFileName(Uri.fromFile(f)));
        }

        removeCsvBtn = (Button) findViewById(R.id.removeCsvBtn);
        removeCsvBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                csvListRemove();
            }
        });

    }

    // adds a string to spinner
    private void csvListAdd(String fileName) {
        listOfCsvFiles.add(fileName);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfCsvFiles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        csvListSpinner.setAdapter(dataAdapter);
    }


    // removes currently selected spinner item
    private void csvListRemove() {
        this.deleteFile(csvListSpinner.getSelectedItem().toString());
        if (csvListSpinner.getSelectedItemPosition() > 0) {
            listOfCsvFiles.remove(csvListSpinner.getSelectedItem().toString());
        } else {
            Toast.makeText(MainMenuActivity.this, "No CSV File To Remove", Toast.LENGTH_LONG).show();
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, listOfCsvFiles);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        csvListSpinner.setAdapter(dataAdapter);
    }

    // Let user select file from browser...
    // Storage Access Framework
    // https://developer.android.com/guide/topics/providers/document-provider#java
    public void openFileBrowser() {
        Intent browserIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        browserIntent.addCategory(Intent.CATEGORY_OPENABLE);
        browserIntent.setType("text/csv");
        startActivityForResult(browserIntent, READ_REQUEST_CODE);
    }

    // After user selects file...
    // Storage Access Framework
    // https://developer.android.com/guide/topics/providers/document-provider#java
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        Log.d(TAG, "RequestCode == " + requestCode);
        if (requestCode == READ_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = resultData.getData();
            if (resultData != null) {
                uri = resultData.getData();
            }
            String fileName = cutFileName(uri);
            // if file is duplicate, do not add
            if (listOfCsvFiles.contains(fileName)) {
                Toast.makeText(MainMenuActivity.this, "Duplicate Error", Toast.LENGTH_LONG).show();
                return;
            }
            csvListAdd(fileName);
            // copy file to internal storage (app folder)
            try {
                storeCSV(uri, fileName);
            } catch (Exception e) {
                e.printStackTrace();
            }

            // on back press from word list input activity
        } else if (requestCode == WORDLIST_ACTIVITY_REQUEST_CODE) {
            Log.d(TAG, "word list retrieving");
            Bundle extras = resultData.getExtras();
            manEngList = extras.getStringArray("engList");
            manNonEngList = extras.getStringArray("nonEngList");
            Log.d(TAG, "word list size = " + manEngList.length);
            Log.d(TAG, "board size = " + boardSize[0]);

            Log.d(TAG, "word list retrieved");
        }
    }

    // makes a copy of csv in internal storage
    // for use after app shut down
    private void storeCSV(Uri uri, String fileName) throws IOException {
        checkCSV(getContentResolver().openInputStream(uri));
        InputStream inputStream = getContentResolver().openInputStream(uri);
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int n;
        byte[] data = new byte[16384];
        while ((n = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, n);
        }
        try {
            FileOutputStream outputStream = openFileOutput(fileName, MODE_PRIVATE);
            outputStream.write(buffer.toByteArray());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Returns an error if the CSV File is not valid
    // CSV File is not valid if:
    // length < 9, or if reading process generated an exception
    private void checkCSV(InputStream inputStream) {
        CSVFile csvFile = new CSVFile(inputStream, boardSize[0]);
        try {
            csvFile.read();
        } catch (Exception e) {
            Toast.makeText(MainMenuActivity.this, "CSV File error " + e.toString(), Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    // Returns the file name (String) of a uri
    private String cutFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    // run before playing in manual mode
    // checks if the word lists are valid
    private boolean manWordListValid() {
        if (manEngList == null || manNonEngList == null) {
            Log.d(TAG, "null  word list");
            return false;
        }
        if (manEngList.length < boardSize[0] || manNonEngList.length < boardSize[0]) {
            Log.d(TAG, "inputted word list is too small!");
            return false;
        }

        // check if there are empty values
        for (int i = 0; i < boardSize[0]; i++) {
            if (manEngList[i] != null) {
                if (manEngList[i].length() == 0) {
                    return false;
                }
            }

            if (manNonEngList[i] != null) {
                if (manNonEngList[i].length() == 0) {
                    return false;
                }
            }
            if (manEngList[i] == "" || manNonEngList[i] == "") {
                Log.d(TAG, "manual word list empty at index " + i);
                return false;
            }
        }
        Log.d(TAG, "board size = " + boardSize[0]);
        Log.d(TAG, "man word list size = " + manEngList.length);
        Log.d(TAG, "man word is valid");
        return true;
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

}