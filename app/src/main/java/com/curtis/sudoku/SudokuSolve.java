package com.curtis.sudoku;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public class SudokuSolve {
    public int[][] cells_;

    // i = y and j = x
    public int solve(int i, int j, int[][] cells, int count /*initailly called with 0*/) {
        if (i == 9) {
            i = 0;
            if (++j == 9)
                return (1+count);
        }
        if (cells[i][j] != 0)  // skip filled cells
            return solve(i+1,j,cells, count);
        // search for 2 solutions instead of 1
        // break, if 2 solutions are found
        for (int val = 1; val <= 9 && count < 2; ++val) {
            if (legal(i,j,val,cells)) {
                cells[i][j] = val;
                // add additional solutions
                count = solve(i+1,j,cells, count);
            }
        }
        cells[i][j] = 0; // reset on backtrack
        return count;
    }

    boolean legal(int i, int j, int val, int[][] vals) {
        cells_ = vals;
        return (!containedIn3x3Box(i,j,val) && !containedInRowCol(i,j,val));
    }

    /**
     * Check if a value contains in its 3x3 box for a cell.
     * @param row current row index.
     * @param col current column index.
     * @return true if this cell is incorrect or duplicated in its 3x3 box.
     */
    private boolean containedIn3x3Box(int row, int col, int value) {
        // Find the top left of its 3x3 box to start validating from
        int startRow = row / 3 * 3;
        int startCol = col / 3 * 3;

        // Check within its 3x3 box except its cell
        for (int i = startRow; i < startRow + 3; i++)
            for (int j = startCol; j < startCol + 3; j++) {
                if (!(i == row && j == col)) {
                    if (cells_[i][j] == value){
                        return true;
                    }
                }
            }

        return false;
    }

    /**
     * Check if a value is contained within its row and column.
     * Used when solving the puzzle.
     * @param row current row index.
     * @param col current column index.
     * @param value value in this cell.
     * @return true if this value is duplicated in its row and column.
     */
    private boolean containedInRowCol(int row, int col, int value) {
        for (int i = 0; i < 9; i++) {
            // Don't check the same cell
            if (i != col)
                if (cells_[row][i] == value)
                    return true;
            if (i != row)
                if (cells_[i][col] == value)
                    return true;
        }

        return false;
    }
}
