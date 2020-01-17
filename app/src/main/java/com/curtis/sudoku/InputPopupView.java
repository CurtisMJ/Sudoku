package com.curtis.sudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TableLayout;

/**
 * Created by Curtis Jones on 07/01/2015.
 */
public class InputPopupView extends LinearLayout {
    public final float CellWidth;
    TableLayout root;
    Button i1, i2, i3, i4, i5, i6, i7, i8, i9, iX, iC;
    public CheckBox scribeCheck;
    int result;
    boolean scribe;
    PopupWindow parent;

    private class MicroTag {
        protected int no;
        protected MicroTag(int no_) {
            no = no_;
        }
    }

    public InputPopupView(Context context, PopupWindow parent_) {
        super(context);
        CellWidth = getResources().getDimension(R.dimen.buttwidth);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root = (TableLayout) inflater.inflate(R.layout.input_box, null);
        addView(root);
        i1 = (Button) root.findViewById(R.id.input1);
        i2 = (Button) root.findViewById(R.id.input2);
        i3 = (Button) root.findViewById(R.id.input3);
        i4 = (Button) root.findViewById(R.id.input4);
        i5 = (Button) root.findViewById(R.id.input5);
        i6 = (Button) root.findViewById(R.id.input6);
        i7 = (Button) root.findViewById(R.id.input7);
        i8 = (Button) root.findViewById(R.id.input8);
        i9 = (Button) root.findViewById(R.id.input9);
        iX = (Button) root.findViewById(R.id.inputC);
        iC = (Button) root.findViewById(R.id.inputCancel);
        scribeCheck = (CheckBox) root.findViewById(R.id.scribeCheck);
        i1.setTag(new MicroTag(1));
        i2.setTag(new MicroTag(2));
        i3.setTag(new MicroTag(3));
        i4.setTag(new MicroTag(4));
        i5.setTag(new MicroTag(5));
        i6.setTag(new MicroTag(6));
        i7.setTag(new MicroTag(7));
        i8.setTag(new MicroTag(8));
        i9.setTag(new MicroTag(9));
        iX.setTag(new MicroTag(-2));
        iC.setTag(new MicroTag(-1));
        InputOnTouchListener listen = new InputOnTouchListener();
        i1.setOnClickListener(listen);
        i2.setOnClickListener(listen);
        i3.setOnClickListener(listen);
        i4.setOnClickListener(listen);
        i5.setOnClickListener(listen);
        i6.setOnClickListener(listen);
        i7.setOnClickListener(listen);
        i8.setOnClickListener(listen);
        i9.setOnClickListener(listen);
        iX.setOnClickListener(listen);
        iC.setOnClickListener(listen);
        parent = parent_;
        parent.setContentView(this);
        parent.setWidth((int) (CellWidth * 3));
        parent.setHeight((int) (CellWidth * 4));
        result = -1;
    }

    private class InputOnTouchListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            result = ((MicroTag) view.getTag()).no;
            scribe = scribeCheck.isChecked();
            parent.dismiss();
        }
    }
}
