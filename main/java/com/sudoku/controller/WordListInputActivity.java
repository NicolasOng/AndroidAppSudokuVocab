package com.sudoku.controller;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.sudoku.R;

import java.util.ArrayList;
import java.util.List;

// Activity to allow user to manually input their own word lists


//public class MainActivity extends AppCompatActivity implements View.OnClickListener {
public class WordListInputActivity extends AppCompatActivity {

    private static final String TAG = "WordListInputActivity";

    private static final int[] n_word_ids = {
            R.id.n_word1, R.id.n_word2, R.id.n_word3,
            R.id.n_word4, R.id.n_word5, R.id.n_word6,
            R.id.n_word7, R.id.n_word8, R.id.n_word9,
            R.id.n_word10, R.id.n_word11, R.id.n_word12
    };

    private static final int[] nn_word_ids = {
            R.id.nn_word1, R.id.nn_word2, R.id.nn_word3,
            R.id.nn_word4, R.id.nn_word5, R.id.nn_word6,
            R.id.nn_word7, R.id.nn_word8, R.id.nn_word9,
            R.id.nn_word10, R.id.nn_word11, R.id.nn_word12
    };

    // board size chosen from main menu
    // default 9x9 (3x3 boxes)
    // 4x4 (2x2 box), 6x6 (2x3 box), 12x12 (3x4 box)
    private int[] boardSize;

    // current set of words
    private String[] manEngList;
    private String[] manNonEngList;

    private Button save_button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //set the layout and the saved Instance State.
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.input_word_list);
        Log.d(TAG, "set content view to ");

        if (savedInstanceState != null) {

        }

        // retrieve previous word list
        // allows for pesistent word lists so user doesnt have to input every time
        Bundle extras = getIntent().getExtras();
        boardSize = extras.getIntArray("boardSize");
        manEngList = new String[boardSize[0]];
        manNonEngList = new String[boardSize[0]];
        if (getIntent().hasExtra("manEngList")) {
            manEngList = extras.getStringArray("manEngList");
            if (getIntent().hasExtra("manNonEngList")) {
                manNonEngList = extras.getStringArray("manNonEngList");
            }
        }
        setText();

        save_button = (Button) findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save textfield text into containers
                getText();
                Toast.makeText(WordListInputActivity.this, "Saved!", Toast.LENGTH_SHORT).show();
            }
        });


    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "backpresser");
        Intent intent= new Intent();
        intent.putExtra("engList", manEngList);
        intent.putExtra("nonEngList", manNonEngList);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }

    // Set the textfields to values in current word lists
    private void setText() {
        if (manEngList == null || manNonEngList == null) {return;}
//        n_words = new ArrayList<EditText>();
//        nn_words = new ArrayList<EditText>();
        //for each input,
        Log.d(TAG, "setText(): Board size is = " + boardSize[0]);
        for (int v = 0; v < manEngList.length; v++) {
            //get the input from the layout,
            int id = n_word_ids[v];
            int id2 = nn_word_ids[v];
            EditText nField = (EditText) findViewById(id);
            EditText nnField = (EditText) findViewById(id2);
            if (manEngList[v] != null) {
                nField.setText(manEngList[v]);
            } else {
                nField.setText("");
            }
            if (manNonEngList[v] != null) {
                nnField.setText(manNonEngList[v]);
            } else {
                nnField.setText("");
            }

        }

    }

    // Get text from textfield and put into array containers
    private void getText() {
//        n_words = new ArrayList<EditText>();
        //for each input,
        Log.d(TAG, "getText(): Board size is = " + boardSize[0]);
        for (int v = 0; v < boardSize[0]; v++) {
            //get the input from the layout,
            int id = n_word_ids[v];
            int id2 = nn_word_ids[v];
            EditText nField = (EditText) findViewById(id);
            EditText nnField = (EditText) findViewById(id2);
            String tmp1 = nField.getText().toString();
            String tmp2 = nnField.getText().toString();
            if (tmp1 == "" || tmp2 == "") {
                Toast.makeText(this, "Field missing", Toast.LENGTH_SHORT).show();
            }
            manEngList[v] = tmp1;
            manNonEngList[v] = tmp2;
        }
        // logs for testing
        //outputArrToLog();
    }

    private void outputArrToLog() {
        for (int v = 0; v < boardSize[0]; v++) {
            Log.d(TAG, v + " manEngList = " + manEngList[v] + " | nonManEngList = " + manNonEngList[v]);
        }
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
