package com.curtis.sudoku;

import android.app.Activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    public NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    public boolean gameInitSequence = false;

    public static PuzzleBundle puzzle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

    }

    protected void initGame() {
        puzzle = PuzzleBundle.Load(this);
        if (puzzle == null) {
           // PuzzleGenerateTask generateTask = new PuzzleGenerateTask(this);
           // generateTask.execute(5);
           drawerSwitch(1);
        }
        gameInitSequence = true;
    }

    @Override
    protected void onPause() {
        if (puzzle != null)
            puzzle.Save(getApplicationContext());
        if (BoardUI != null) {
            BoardUI.stopClock();
        }
        super.onPause();
    }


    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();

    }


    public void onSectionAttached(int number) {

                mTitle = "Sudoku";

    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    protected static SudokuBoardUI BoardUI;
    protected static GenPuzzleUI GenUI;

    public void drawerSwitch(int pos) {
        mNavigationDrawerFragment.selectItem(pos);

    }



    @Override
    protected void onResume() {
        super.onResume();
        if ( puzzle != null)
            drawerSwitch(0);

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private int sectionId;
        private UIPiece ui;


        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            fragment.sectionId = sectionNumber;
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {

        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
           View rootView = new LinearLayout(getActivity());

           switch (sectionId) {
               case 1 : {
                  if (BoardUI == null) { BoardUI = new SudokuBoardUI(getActivity()); ((MainActivity)getActivity()).initGame(); }
                   ui = BoardUI;
                   BoardUI.updateContext(getActivity());
                   ui.Wakeup();
                   rootView = ui.GetRootView();
                   break;
               }
               case 2 : {
                   if (GenUI == null) { GenUI = new GenPuzzleUI(getActivity());  }
                   ui = GenUI;
                   GenUI.context_ = getActivity();
                   ui.Wakeup();
                   rootView = ui.GetRootView();
                   break;
               }
               case 3: {
                   Resources r = getActivity().getResources();
                   String aboutTxt = "Error retrieving";
                   try {
                       aboutTxt = "Sudoku\nCopyright© Curtis Jones 2015\nVersion " + getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
                   } catch (PackageManager.NameNotFoundException e) {
                       e.printStackTrace();
                   }

                   new AlertDialog.Builder(getActivity()).setTitle("About").setPositiveButton("OK", new DialogInterface.OnClickListener() {
                       @Override
                       public void onClick(DialogInterface dialogInterface, int i) {
                           dialogInterface.dismiss();
                       }
                   }).setMessage(aboutTxt).create().show();
                   ((MainActivity)getActivity()).drawerSwitch(0);
                   break;
               }
           }
            return rootView;
        }

        @Override
        public void onDetach() {
            if (ui != null) {
                ui.Destroy();
                ui = null;
            }
            super.onDetach();
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }

}
