package com.sudoku.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.util.Log;

import com.sudoku.R;
import com.sudoku.model.Sudoku;

import static android.content.ContentValues.TAG;

public class ButtonGridAdapter extends BaseAdapter {
    private final Context mContext;
    public int numRows;
    public Sudoku grid;
    public String[] engWords;
    public String[] nonEngWords;
    public boolean listening = false;
    // 1
    public ButtonGridAdapter(Context context) {
        this.mContext = context;
    }
    // 2
    @Override
    public int getCount() {
        return grid.getSideLength();
    }

    // 3
    @Override
    public long getItemId(int position) {
        return 0;
    }

    // 4
    @Override
    public Object getItem(int position) {
        return null;
    }

    // 5
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int value = position + 1;
        Log.d(TAG, "position = " + position);
        //get the view elements of the cell:
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.value_btn, null);
        }
        final TextView buttonValue = (TextView) convertView.findViewById(R.id.button_value);
        final LinearLayout buttonForeground = (LinearLayout) convertView.findViewById(R.id.button_foreground);
        final LinearLayout buttonBackground = (LinearLayout) convertView.findViewById(R.id.button_background);

        //set the height of the cell:
        // if want two rows, do divide by two, etc.
        int height = parent.getHeight();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height/numRows);
        buttonBackground.setLayoutParams(params);

        //change background:
        int activeValue = grid.getActiveValue();
        if (value == activeValue) {
            buttonForeground.setBackgroundColor(Color.parseColor("#00ffff"));
        } else {
            buttonForeground.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        //set text:
        buttonValue.setText(grid.getWordForButtonsAt(value));

        return convertView;
    }

    public void isListening(boolean listening) {
        this.listening = listening;
    }

}
