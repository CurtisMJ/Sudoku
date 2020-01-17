package com.curtis.sudoku;

import android.view.View;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public interface UIPiece {
    public View GetRootView();
    public void Wakeup();
    public void Destroy();
}
