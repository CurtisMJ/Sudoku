package com.curtis.sudoku;

import android.app.ActionBar;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

/**
 * Created by Curtis Jones on 07/01/2015.
 */
public class GenPuzzleUI  implements UIPiece {
    private LinearLayout root;
    public Context context_;
    private Button beg, easy, med, hard, vhard;
    public GenPuzzleUI(Context context) {
        root = new LinearLayout(context);
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        root.addView(inflater.inflate(R.layout.generation_config, null));
        root.getChildAt(0).setLayoutParams(new LinearLayout.LayoutParams((int) context.getResources().getDimension(R.dimen.conwidth), LinearLayout.LayoutParams.WRAP_CONTENT));
        root.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        beg = (Button) root.findViewById(R.id.btnBeg);
        easy = (Button) root.findViewById(R.id.btnEasy);
        med = (Button) root.findViewById(R.id.btnMed);
        hard = (Button) root.findViewById(R.id.btnHard);
        vhard = (Button) root.findViewById(R.id.btnVHard);
        beg.setTag(new MicroTag(15));
        easy.setTag(new MicroTag(35));
        med.setTag(new MicroTag(42));
        hard.setTag(new MicroTag(50));
        vhard.setTag(new MicroTag(64));
        OnClickListener_ listener_ = new OnClickListener_();
        beg.setOnClickListener(listener_);
        easy.setOnClickListener(listener_);
        med.setOnClickListener(listener_);
        hard.setOnClickListener(listener_);
        vhard.setOnClickListener(listener_);
        context_ = context;
    }

    @Override
    public View GetRootView() {
        return root;
    }

    @Override
    public void Wakeup() {

    }

    @Override
    public void Destroy() {

    }

    private class MicroTag {
        int diff;

        private MicroTag(int diff) {
            this.diff = diff;
        }
    }

    private class OnClickListener_ implements View.OnClickListener {

        @Override
        public void onClick(View view) {
            MicroTag diffTag = (MicroTag) view.getTag();
            int diff = diffTag.diff;

            PuzzleGenerateTask generateTask = new PuzzleGenerateTask(context_);
            generateTask.execute(diff);
        }
    }
}
