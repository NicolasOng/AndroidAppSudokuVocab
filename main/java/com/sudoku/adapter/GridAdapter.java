package com.sudoku.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sudoku.R;
import com.sudoku.model.Sudoku;

import static android.content.ContentValues.TAG;

public class GridAdapter extends BaseAdapter {
    private final Context mContext;
    public Sudoku grid;
    public String[] nonEngWords;
    public String[] engWords;
    String number_val;
    String number_val2;
    boolean listening = false;

    // 1
    public GridAdapter(Context context) {
        this.mContext = context;
    }

    // 2
    @Override
    public int getCount() {
        return grid.getCount();
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
        int col = position % grid.getSideLength();
        int row = (position - col) / grid.getSideLength();
        //get the view elements of the cell:
        if (convertView == null) {
            final LayoutInflater layoutInflater = LayoutInflater.from(mContext);
            convertView = layoutInflater.inflate(R.layout.cell, null);
        }
        final TextView cellValue = (TextView) convertView.findViewById(R.id.cell_value);
        final LinearLayout cellBackground = (LinearLayout) convertView.findViewById(R.id.cell_background);
        final LinearLayout GVcellBackground = (LinearLayout) convertView.findViewById(R.id.gridview_cell_background);

        //set the height of the cell:
        int height = parent.getHeight();
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height / grid.getSideLength());
        GVcellBackground.setLayoutParams(params);

        //change background:
        int value = grid.getValueAt(row, col);
        if (value == grid.getActiveValue()) {
            cellBackground.setBackgroundColor(Color.parseColor("#00ffff"));
        } else {
            cellBackground.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        //bold if permanent:
        boolean permanence = grid.getPermanenceAt(row, col);
        if (permanence) {
            cellValue.setTypeface(null, Typeface.BOLD);
        } else {
            cellValue.setTypeface(null, Typeface.NORMAL);
        }

        cellValue.setText(grid.getWordAt(row, col));

        //change color to red if there is a conflict:
        /*
        boolean isConflict = grid.conflictSpot(row, col, value, false);
       if (isConflict) {
            //change word color to red
            cellValue.setTextColor(Color.parseColor("#ff0000"));
        } else {
            //change to black.
            cellValue.setTextColor(Color.parseColor("#000000"));
        }
        */

        return convertView;
    }

    public void isListening(boolean listening) {
        this.listening = listening;
    }

}