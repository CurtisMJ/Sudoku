package com.curtis.sudoku;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

/**
 * Created by Curtis Jones on 07/01/2015.
 */
public class PuzzleGenerateTask extends AsyncTask<Integer, Integer, Boolean> {
    MainActivity context;

    public PuzzleGenerateTask(Context context_) {
        context = (MainActivity) context_;
    }

    @Override
    protected Boolean doInBackground(Integer... integers) {
        try {
            SudokuGen gen = new SudokuGen();
            MainActivity.puzzle = gen.Generate(integers[0]);
            MainActivity.puzzle.Save(context);
        }
        catch (Exception e) {
            return false;
        }
        return true;
    }
    ProgressDialog dlg;
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dlg = new ProgressDialog(context);
        dlg.setMessage("Generating new puzzle...");
        dlg.setCanceledOnTouchOutside(false);
        dlg.setCancelable(false);
        dlg.show();

    }


    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        // activity can be hidden, it it doesn't really matter if we fail at anything here
        try {
            dlg.dismiss();
            context.drawerSwitch(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
