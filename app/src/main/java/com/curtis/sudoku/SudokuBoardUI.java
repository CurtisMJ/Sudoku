package com.curtis.sudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public class SudokuBoardUI implements UIPiece {
    private CustomGridLayout root;
    private LinearLayout noPuzzleRoot;
    public CellTag selected;
    public CellTag[][] tags;
    public TextView timerText;
    Context context;

    public void updateContext(Context context_) {
        context = context_;
        if (root != null)
            root.context = context;
    }

    public SudokuBoardUI(Context context_) {
        //init(context);
          context = context_;
    }

    private void init() {
        root = new CustomGridLayout(context, this);
        root.context = context;
        root.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.addView(inflater.inflate(R.layout.board, null));
        tags = new CellTag[9][9];
        // fill in cells
        TableLayout table = (TableLayout) root.findViewById(R.id.mainBoard);
        for (int y = 0; y < 9; y++) {
            TableRow row = (TableRow) table.getChildAt(y);
            for (int x = 0; x < 9; x++) {
                TextView t = (TextView) row.getChildAt(x);
                if (MainActivity.puzzle.user[y][x] == -1) {
                    t.setText("");
                    t.setTextColor(Color.rgb(255, 255, 180));
                }
                int value = MainActivity.puzzle.cells[y][x];
                if (value != -1)
                    t.setText(Integer.toString(value));
                CellTag tag = new CellTag();
                tag.x = x; tag.y = y; tag.user = (value == -1); tag.selected = false;
                tag.t = t;
                tags[y][x] = tag;
            }
        }
        root.invalidCellCheck();
        Button checkBtn = (Button) root.findViewById(R.id.btnCheck);
        checkBtn.setOnClickListener(new OnClickCheckListener());
        timerText = (TextView) root.findViewById(R.id.timerTextView);
    }

    @Override
    public View GetRootView() {
        if (MainActivity.puzzle != null)
            return root;
        else
            return noPuzzleRoot;
    }

    Timer timer;
    TimerTask timeTask;
    final long timerInterval = 250;
    final Runnable timeUpdate = new Runnable() {
        @Override
        public void run() {
            long millis = MainActivity.puzzle.timer;
            String formatted = String.format("%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis) -
                            TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millis)));
            timerText.setText(formatted);
        }
    };

    @Override
    public void Wakeup() {
        if (MainActivity.puzzle != null) {
            init();
            if (timer == null) {
                timer = new Timer();
            }
            if (!MainActivity.puzzle.finished) {
                timeTask = new TimerTask() {
                    @Override
                    public void run() {
                        MainActivity.puzzle.timer += timerInterval;
                        if (root != null)
                            root.post(timeUpdate);
                    }
                };
                timer.scheduleAtFixedRate(timeTask, 0, timerInterval);
            }
            root.post(timeUpdate);
        } else {
            noPuzzleRoot = new LinearLayout(context);
            noPuzzleRoot.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            noPuzzleRoot.addView(inflater.inflate(R.layout.no_puzzle, null));
        }

    }

    public void stopClock() {
        if ((timer != null) && (timeTask != null)) {
            timeTask.cancel();
            timeTask = null;
        }

    }

    @Override
    public void Destroy() {
        if (root != null) {
            if (root.fireworks != null) {
                root.fireworks.cancel(false);
            }
            stopClock();
        }
    }

    public class CellTag {
        protected int x, y;
        protected boolean user, selected;
        protected TextView t;
    }

    public class OnClickCheckListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            DisplayWrongCellsTask task = new DisplayWrongCellsTask(root, (android.widget.Button) view);
            task.execute();
        }
    }
}
