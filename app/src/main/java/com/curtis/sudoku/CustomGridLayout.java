package com.curtis.sudoku;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Curtis Jones on 06/01/2015.
 */
public class CustomGridLayout extends LinearLayout {
    private Paint mLineMainPaint;
    private Paint mLineThickMainPaint;
    private Paint mBackgroundSelect;
    private Paint mLineupSelect;
    private Paint mRegionSelect;
    private Paint mInvalid;
    private Paint mCheck;
    private Paint mScribe;
    private Paint mHighlight;
    private Paint mFirework;
    private float CellWidth = 0;
    private float ScribeWidth;
    private float ScribeWidthO2;
    private float ScribeTextHeight;
    private float ScribeTextHeightO2;
    private LightingColorFilter[] filters;
    public SudokuBoardUI uiPiece;
    private ArrayList<coord> InvalidCells;
    private float boardLength, regionLength;
    public Context context;
    public boolean displayWrongCells;
    private Bitmap particle;
    Random r = new Random();

    private class coord {
        int x, y;
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        return (int)((dp * displayMetrics.density) + 0.5);
    }

    public CustomGridLayout(Context context, UIPiece ui) {
        super(context);
        InvalidCells = new ArrayList<coord>();
        particle = BitmapFactory.decodeResource(context.getResources(), R.drawable.particle);
        displayWrongCells = false;
        uiPiece = (SudokuBoardUI) ui;
        CellWidth = getResources().getDimension(R.dimen.cellwidth);
        ScribeTextHeight = getResources().getDimension(R.dimen.textScribeSize);
        ScribeTextHeightO2 = (ScribeTextHeight / 2) - getResources().getDimension(R.dimen.textScribeUpPad);
        ScribeWidth = CellWidth / 2;
        ScribeWidthO2 = ScribeWidth / 2;
        boardLength = CellWidth * 9;
        regionLength = CellWidth * 3;
        mLineMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineMainPaint.setColor(Color.WHITE);
        mLineMainPaint.setStrokeWidth(dpToPx(1));
        mLineThickMainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineThickMainPaint.setColor(Color.WHITE);
        mLineThickMainPaint.setStrokeWidth(dpToPx(3));
        mBackgroundSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBackgroundSelect.setColor(Color.CYAN);
        mBackgroundSelect.setAlpha(128);
        mBackgroundSelect.setStyle(Paint.Style.FILL);
        mLineupSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLineupSelect.setColor(Color.rgb(255, 106, 0)); //Reddish Orange
        mLineupSelect.setAlpha(64);
        mLineupSelect.setStyle(Paint.Style.FILL);
        mRegionSelect = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRegionSelect.setColor(Color.YELLOW);
        mRegionSelect.setAlpha(48);
        mRegionSelect.setStyle(Paint.Style.FILL);
        mInvalid = new Paint(Paint.ANTI_ALIAS_FLAG);
        mInvalid.setColor(Color.RED);
        mInvalid.setStyle(Paint.Style.STROKE);
        mInvalid.setStrokeWidth(dpToPx(3));
        mCheck = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCheck.setColor(Color.rgb(190, 0, 0));
        mCheck.setAlpha(190);
        mCheck.setStyle(Paint.Style.FILL);
        mScribe = new Paint(Paint.ANTI_ALIAS_FLAG);
        mScribe.setColor(Color.rgb(255, 255, 180));
        mScribe.setStyle(Paint.Style.STROKE);
        mScribe.setTextSize(ScribeTextHeight);
        mScribe.setTextAlign(Paint.Align.CENTER);
        mHighlight = new Paint(Paint.ANTI_ALIAS_FLAG);
        mHighlight.setColor(Color.BLUE); //Reddish Orange
        mHighlight.setAlpha(76);
        mHighlight.setStyle(Paint.Style.FILL);
        mFirework = new Paint(Paint.ANTI_ALIAS_FLAG);
        mFirework.setAlpha(255);
        mFirework.setStyle(Paint.Style.FILL);

        filters = new LightingColorFilter[] {
                new LightingColorFilter(Color.RED, 1),
                new LightingColorFilter(Color.BLUE, 1),
                new LightingColorFilter(Color.CYAN, 1),
                new LightingColorFilter(Color.YELLOW, 1),
                new LightingColorFilter(Color.GREEN, 1),
                new LightingColorFilter(Color.MAGENTA, 1)
        };

        setWillNotDraw(false);
        setOnTouchListener(new OverOnTouchListener());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float xOff = getChildAt(0).getX();
        float yOff = getChildAt(0).getY();
        if (!MainActivity.puzzle.finished) {
            // draw selected cell
            if (uiPiece.selected != null) {
                float sX = uiPiece.selected.x * CellWidth;
                float sY = uiPiece.selected.y * CellWidth;
                // draw lineup
                canvas.drawRect(xOff, sY + yOff, xOff + boardLength, sY + CellWidth + yOff, mLineupSelect);
                canvas.drawRect(sX + xOff, yOff, sX + CellWidth + xOff, yOff + boardLength, mLineupSelect);
                // draw its selection square
                canvas.drawRect(sX + xOff, sY + yOff, sX + CellWidth + xOff, sY + CellWidth + yOff, mBackgroundSelect);
                // draw supercell
                int region = MainActivity.puzzle.regionLookup[uiPiece.selected.y][uiPiece.selected.x] - 1;
                int regionX = region % 3;
                int regionY = (int) Math.floor(region / 3);
                float rsX = regionX * regionLength;
                float rsY = regionY * regionLength;
                canvas.drawRect(rsX + xOff, rsY + yOff, rsX + xOff + regionLength, rsY + yOff + regionLength, mRegionSelect);
                if (MainActivity.puzzle.cells[uiPiece.selected.y][uiPiece.selected.x] != -1) {
                    for (int x = 0; x < 9; x++) {
                        for (int y = 0; y < 9; y++) {
                            if ((x != uiPiece.selected.x) && (y != uiPiece.selected.y)) {
                                if (MainActivity.puzzle.cells[y][x] == MainActivity.puzzle.cells[uiPiece.selected.y][uiPiece.selected.x]) {
                                    float s_X = x * CellWidth;
                                    float s_Y = y * CellWidth;
                                    canvas.drawRect(s_X + xOff, s_Y + yOff, s_X + CellWidth + xOff, s_Y + CellWidth + yOff, mHighlight);
                                }
                            }
                        }
                    }
                }
            }
        }

        // draw grid
        for (int x = 0; x < 10; x++) {
            float PosX = x * CellWidth;
            float PosY = CellWidth * 9;
            if (!(((x) % 3) == 0)) {
                canvas.drawLine(PosX + xOff, yOff, PosX + xOff, PosY + yOff, mLineMainPaint);
                canvas.drawLine(xOff, PosX + yOff, PosY + xOff, PosX + yOff, mLineMainPaint);
            } else {
                canvas.drawLine(PosX + xOff, yOff, PosX + xOff, PosY + yOff, mLineThickMainPaint);
                canvas.drawLine(xOff, PosX + yOff, PosY + xOff, PosX + yOff, mLineThickMainPaint);
            }
        }

        if (!MainActivity.puzzle.finished) {
            int s = InvalidCells.size();
            for (int n = 0; n < s; n++) {
                coord cord = InvalidCells.get(n);
                float sX = cord.x * CellWidth;
                float sY = cord.y * CellWidth;
                canvas.drawRect(sX + xOff, sY + yOff, sX + CellWidth + xOff, sY + CellWidth + yOff, mInvalid);
            }

            if (displayWrongCells) {
                for (int x = 0; x < 9; x++) {
                    for (int y = 0; y < 9; y++) {
                        if ((MainActivity.puzzle.cells[y][x] != MainActivity.puzzle.solution[y][x]) && (MainActivity.puzzle.cells[y][x] != -1)) {
                            float sX = x * CellWidth;
                            float sY = y * CellWidth;
                            canvas.drawRect(sX + xOff, sY + yOff, sX + CellWidth + xOff, sY + CellWidth + yOff, mCheck);
                        }
                    }
                }
            }

            // scribed cells
            for (int x = 0; x < 9; x++) {
                for (int y = 0; y < 9; y++) {
                    if (MainActivity.puzzle.scribed[y][x] != null) {
                        // cell has scribed values!
                        // draw 'em
                        // assume scribe will only ever have four numbers
                        float sX = x * CellWidth;
                        float sY = y * CellWidth;
                        ArrayList<Integer> scribe = MainActivity.puzzle.scribed[y][x];
                        int scribeSize = scribe.size();
                        for (int n = 0; n < scribeSize; n++) {
                            int _x_ = n % 2;
                            int _y_ = (int) Math.floor(n / 2);
                            String text = Integer.toString(scribe.get(n));

                            canvas.drawText(text, (_x_ * ScribeWidth) + sX + xOff + ScribeWidthO2, (_y_ * ScribeWidth) + sY + yOff + ScribeTextHeightO2 + ScribeWidthO2, mScribe);
                        }
                    }
                }
            }
        }

        if (MainActivity.puzzle.finished) {
            // celebrate!
            if (fireworks != null) {
                ArrayList<FireWorksTask.FireWork> safeClone = (ArrayList<FireWorksTask.FireWork>) fireworks.fireWorks.clone();
                int fS = safeClone.size();
                for (int n = 0; n < fS; n++) {
                    FireWorksTask.FireWork fireWork = safeClone.get(n);
                    if (!fireWork.detonated) {
                        // SO FLY
                        mFirework.setColor(Color.WHITE);
                        mFirework.setColorFilter(null);
                        canvas.drawRect(fireWork.currX - 4, fireWork.currY - 8,fireWork.currX + 4, fireWork.currY + 8, mFirework );
                    } else {
                        // SO BYE
                        for (int n_ = 0; n_ < fireWork.parts.length; n_++) {
                            mFirework.setColorFilter(filters[fireWork.parts[n_].Color]);
                            //canvas.drawCircle(fireWork.parts[n_].x, fireWork.parts[n_].y, 4, mFirework);
                            canvas.drawBitmap(particle, fireWork.parts[n_].x - 8, fireWork.parts[n_].y - 8, mFirework);
                        }
                    }
                }
            } else {
                fireworks = new FireWorksTask(getWidth(), getHeight(), this);
                fireworks.execute();
            }
        }

        super.onDraw(canvas);
    }

    public FireWorksTask fireworks;

    private class OverOnTouchListener implements View.OnTouchListener {

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if ((motionEvent.getAction() != MotionEvent.ACTION_DOWN) || displayWrongCells)
                return false;
            float xOff = getChildAt(0).getX();
            float yOff = getChildAt(0).getY();
            float xPos = motionEvent.getX() - xOff;
            float yPos = motionEvent.getY() - yOff;
            int x = (int) Math.floor(xPos / CellWidth);
            int y = (int) Math.floor(yPos / CellWidth);
            if ((x > 8) || (y > 8) || (x < 0) || (y < 0)) { return false; }
            SudokuBoardUI.CellTag tag = uiPiece.tags[y][x];
            if (uiPiece.selected != null) {
                if (uiPiece.selected != tag) {
                    uiPiece.selected.selected = false;
                    uiPiece.selected = tag;
                    uiPiece.selected.selected = true;
                } else {
                    if ((MainActivity.puzzle.user[y][x] == -1) && !MainActivity.puzzle.finished) {
                        // two taps means input is requested
                        PopupWindow inputPop = new PopupWindow();
                        inputPop.setBackgroundDrawable(new BitmapDrawable());
                        inputPop.setOutsideTouchable(true);
                        inputPop.setFocusable(true);
                        InputPopupView inputView = new InputPopupView(getContext(), inputPop);
                        TableLayout boardLay = (TableLayout) CustomGridLayout.this.getChildAt(0);
                        TableRow row = (TableRow) boardLay.getChildAt(y);
                        inputPop.setOnDismissListener(new InputFinishListener(inputView, x, y));
                        if(MainActivity.puzzle.scribed[y][x] != null) {
                            inputView.scribeCheck.setChecked(true);
                        }
                        inputPop.showAsDropDown(row.getChildAt(x), (int) (inputView.CellWidth * -1.5), (int) (inputView.CellWidth * -1.5));
                    }
                }
            } else {
                uiPiece.selected = tag;
                uiPiece.selected.selected = true;
            }
            invalidate();
            return true;
        }
    }

    protected void invalidCellCheck() {
        InvalidCells.clear();
        boolean solved = true;
        for (int x = 0; x < 9; x++) {
            for (int y = 0; y < 9; y++) {
                if (MainActivity.puzzle.cells[y][x] != MainActivity.puzzle.solution[y][x]) {
                    solved = false;
                }
                if (MainActivity.puzzle.cells[y][x] != -1) {
                    int value = MainActivity.puzzle.cells[y][x];
                    boolean valid = true;
                    // check across
                    for (int x_ = 0; x_ < 9; x_++) {
                        if (x_ != x) {
                            if (value == MainActivity.puzzle.cells[y][x_]) {
                                // this in an invalid cell
                                valid = false;
                                break;
                            }
                        }
                    }
                    if (valid) {
                        // check down
                        for (int y_ = 0; y_ < 9; y_++) {
                            if (y_ != y) {
                                if (value == MainActivity.puzzle.cells[y_][x]) {
                                    // this in an invalid cell
                                    valid = false;
                                    break;
                                }
                            }
                        }
                    }
                    if (valid) {
                        // check supercells
                        for (int x_ = 0; x_ < 9; x_++) {
                            for (int y_ = 0; y_ < 9; y_++) {
                                if ((x != x_) && (y != y_)) {
                                    if (MainActivity.puzzle.regionLookup[y][x] == MainActivity.puzzle.regionLookup[y_][x_]) {
                                        if (value == MainActivity.puzzle.cells[y_][x_]) {
                                            valid = false;
                                            break;
                                        }
                                    }
                                }
                            }
                            if (!valid)
                                break;
                        }
                    }
                    if (!valid) {
                        // its an invalid cell!
                        coord cord = new coord();
                        cord.x = x;
                        cord.y = y;
                        InvalidCells.add(cord);
                    }
                } // else its not worth checking this cell
            }
        }
        if (solved) {
            // sweep through user[][] and switch all to 1 to avoid further input
            //for (int x = 0; x < 9; x++) {
            //    for (int y = 0; y < 9; y++) {
            //        MainActivity.puzzle.user[y][x] = 1;
            //    }
            //}
            if (!MainActivity.puzzle.finished) {
                new AlertDialog.Builder(context).setTitle("").setMessage("Puzzle Solved!").setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        }).create().show();
            }
            MainActivity.puzzle.finished = true;
            MainActivity.BoardUI.stopClock();
        }
    }

    private class InputFinishListener implements PopupWindow.OnDismissListener {
        InputPopupView v;
        int x, y;

        private InputFinishListener(InputPopupView v_, int x_, int y_) {
            v = v_;
            x = x_;
            y = y_;
        }

        @Override
        public void onDismiss() {
            if (v.result != -1) {
                TableLayout boardLay = (TableLayout) CustomGridLayout.this.getChildAt(0);
                TableRow row = (TableRow) boardLay.getChildAt(y);
                TextView t = (TextView) row.getChildAt(x);
                if (v.result != -2) {
                    if (!v.scribe) {
                        t.setText(Integer.toString(v.result));
                        // clear scribed values
                        MainActivity.puzzle.scribed[y][x] = null;
                        MainActivity.puzzle.cells[y][x] = v.result;
                    } else {
                        if (MainActivity.puzzle.scribed[y][x] == null) {
                            MainActivity.puzzle.scribed[y][x] = new ArrayList<Integer>();
                            t.setText("");
                            MainActivity.puzzle.cells[y][x] = -1;
                        }
                        if (MainActivity.puzzle.scribed[y][x].size() < 4)
                            MainActivity.puzzle.scribed[y][x].add(v.result);
                    }
                } else {
                    t.setText("");
                    MainActivity.puzzle.cells[y][x] = -1;
                    // clear scribed values
                    MainActivity.puzzle.scribed[y][x] = null;
                }

                // stuff changed, perform invalid visual cell check
                invalidCellCheck();
                invalidate();
            } // else input was cancelled
        }
    }
}
