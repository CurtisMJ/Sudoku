package com.curtis.sudoku;

import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;

/**
 * Created by Curtis Jones on 08/01/2015.
 */
public class DisplayWrongCellsTask extends AsyncTask {
    CustomGridLayout gridLayout;
    Button comingView;

    public DisplayWrongCellsTask(CustomGridLayout gridLayout, Button comingView) {
        this.gridLayout = gridLayout;
        this.comingView = comingView;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        comingView.setEnabled(false);
        gridLayout.displayWrongCells = true;
        gridLayout.invalidate();

    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        gridLayout.displayWrongCells = false;
        gridLayout.invalidate();
        comingView.setEnabled(true);
    }
}
