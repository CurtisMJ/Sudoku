package com.curtis.sudoku;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public class SudokuGen {
    private class Cell {
        protected int Row, Column, SuperCell, Value, Index;
        protected List<Integer> avail;

        public Cell(int _index) {
            Value = -1;
            avail = new ArrayList<Integer>();
            ResetValues();
            Index = _index;
            CalcRegions();
        }

        public void ResetValues() {
            for (int x = 1; x < 10; x++) {
                avail.add(x);
            }
            Value = -1;
        }

        private void CalcRegions() {
            int q = Index + 1;
            Column = GetColumn(q);
            Row = GetRow(q);
            SuperCell = GetSuperCell(q);
        }

        private int GetColumn(int n) {
            int k = n % 9;
            if (k == 0) { return 9; }
            else { return k; }
        }

        private int GetRow(int n) {
            int k;
            if (Column == 9) { k = (int)Math.floor(n / 9); }
            else { k = ((int)Math.floor(n / 9)) + 1; }
            return k;
        }

        private int GetSuperCell(int n) {
            int k = 0;
            int a = Column;
            int d = Row;
            if ((1 <= a) && (a < 4) && (1 <= d) && (d < 4)) { k = 1; }
            else if ((4 <= a) && (a < 7) && (1 <= d) && (d < 4)) { k = 2; }
            else if ((7 <= a) && (a < 10) && (1 <= d) && (d < 4)) { k = 3; }
            else if ((1 <= a) && (a < 4) && (4 <= d) && (d < 7)) { k = 4; }
            else if ((4 <= a) && (a < 7) && (4 <= d) && (d < 7)) { k = 5; }
            else if ((7 <= a) && (a < 10) && (4 <= d) && (d < 7)) { k = 6; }
            else if ((1 <= a) && (a < 4) && (7 <= d) && (d < 10)) { k = 7; }
            else if ((4 <= a) && (a < 7) && (7 <= d) && (d < 10)) { k = 8; }
            else if ((7 <= a) && (a < 19) && (7 <= d) && (d < 10)) { k = 9; }
            return k;
        }

        public boolean CheckConflict(Cell[] board) {
            for (int x = 0; x < board.length; x++) {
                if ((board[x].Row == this.Row) || (board[x].Column == this.Column) || (board[x].SuperCell == this.SuperCell)) {
                    if (board[x].Value == this.Value) { return true; }
                }
            }
            return false;
        }
    }

    Random rand;

    public PuzzleBundle Generate(int difficulty) {
        rand = new Random();
        Cell[] cells = new Cell[81];
        for (int x = 0; x < 81; x++) {
            cells[x] = new Cell(x);
        }
        int c = 0;
        while (c != 81) {
            if (!cells[c].avail.isEmpty()) {
                int i = rand.nextInt(cells[c].avail.size());
                int z = cells[c].avail.get(i);
                Cell testCell = new Cell(c);
                testCell.Value = z;
                if (!testCell.CheckConflict(cells)) {
                    cells[c].Value = z;
                    cells[c].avail.remove(i);
                    c++;
                } else {
                    cells[c].avail.remove(i);
                }
            } else {
                cells[c].ResetValues();
                cells[c - 1].Value = -1; // not full reset
                c--;
            }
        }

        ArrayList<Integer> indicesRemoved = new ArrayList<Integer>();
        ArrayList<Integer> indicesSafeRemoved = new ArrayList<Integer>();
        int i = 0;
        while (i < difficulty) { // goal is to now remove a specific amount of numbers
            int indice = -1;
            do {
                indice = rand.nextInt(81);
            }
            while (indicesRemoved.contains(indice));
            indicesRemoved.add(indice);
            int x = 0; // checking if the puzzle is still solvable after each
            int y = 0;
            SudokuSolve solver = new SudokuSolve();
            int[][] solverCells = new int[9][9];
            for (int n = 0; n < 81; n++) {
                if (x == 9) {
                    x = 0;
                    y++;
                }
                if ((n == indice) || indicesSafeRemoved.contains(n))
                    solverCells[y][x] = 0;
                else
                    solverCells[y][x] = cells[n].Value;

                x++;
            }
            // does solved puzzle match our solution?
            boolean matched = true;
            //int _i = 0;
            matched = (solver.solve(0,0,solverCells, 0) == 1);
            //for (int _x = 0; _x < 9; _x++) {
            //    if (!matched) break;
            //    for (int _y = 0; _y < 9; _y++) {
            //        int index = _x + (_y * 9);
            //        if (solver.cells[_y][_x] != cells[index].Value) {
            //            matched = false;
            //            break;
            //        }
            //    }
            //}

            if (matched) {
                i++;
                indicesSafeRemoved.add(indice);
            }

            if (indicesRemoved.size() > 79) break;
        }

        PuzzleBundle fin = new PuzzleBundle();
        fin.cells = new int[9][9];
        fin.solution = new int[9][9];
        fin.user = new int[9][9];
        fin.regionLookup = new int[9][9];
        fin.finished = false;
        fin.scribed = new ArrayList[9][9];
        int x = 0;
        int y = 0;
        for (int n = 0; n < 81; n++) {
            if (x == 9) {
                x = 0;
                y++;
            }
            if (indicesSafeRemoved.contains(n)) {
                fin.cells[y][x] = -1;
                fin.user[y][x] = -1;
            } else  { fin.cells[y][x] = cells[n].Value; fin.user[y][x] = 1; }
            fin.solution[y][x] = cells[n].Value;
            fin.regionLookup[y][x] = cells[n].SuperCell;
            x++;
        }

        return fin;
    }
}
