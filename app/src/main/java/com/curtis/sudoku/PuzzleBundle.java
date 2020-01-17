package com.curtis.sudoku;

import android.content.Context;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public class PuzzleBundle implements Serializable {
    public int cells[][];
    public int solution[][];
    public int user[][];
    public int regionLookup[][];
    public boolean finished;
    public ArrayList<Integer>[][] scribed;
    public long timer;

    public void Save(Context context) {
        String filename = "puzzlebundle";
        try {
            FileOutputStream fos;
            fos = context.openFileOutput(filename, Context.MODE_PRIVATE);
            try {
                ObjectOutputStream os;
                os = new ObjectOutputStream(fos);
                os.writeObject(this);
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static PuzzleBundle Load(Context context) {
        String filename = "puzzlebundle";
        try {
            FileInputStream fis = context.openFileInput(filename);
            ObjectInputStream is;
            is = new ObjectInputStream(fis);
            PuzzleBundle puzzleBundle = (PuzzleBundle) is.readObject();
            is.close();
            return puzzleBundle;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
