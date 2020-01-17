package com.curtis.sudoku;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Curtis Jones on 09/01/2015.
 */
public class FireWorksTask extends AsyncTask {
    Random r = new Random();
    public float maxX, maxY, maxYO2;
    private View invalRoot;

    public FireWorksTask(float maxX, float maxY, View invalRoot) {
        this.maxX = maxX;
        this.maxY = maxY;
        maxYO2 = maxY / 2;
        this.invalRoot = invalRoot;
        fireWorks = new ArrayList<FireWork>();
    }

    public class FireWork {
        // 'CAUSE BABY YOU'RE A FIIIIIIREWOOORK!
        private final float upSpeed = 25f;
        private final float deltaTweak = 80f;
        private final float deltaTweakO2 = 40f;
        private final int maxPartCycles = 32;
        public float maxY;
        public float currX;
        public float currY;
        public int partCycles;

        public FireWork(float maxX, float currX) {
            this.maxY = maxX;
            this.currX = currX;
            detonated = false;
        }

        public class Particle {
            private final float gravity = 0.81f;
            private final float frictionRatio = 0.95f;
            public float x, y, xDelta, yDelta;
            public int Color;
            public void runSim() {
                x += xDelta; y += yDelta;
                yDelta += gravity;
                xDelta *= frictionRatio;
            }
        }

        public Particle[] parts;
        public boolean detonated;

        public void runSim() {
            if (!detonated) {
                currY -= upSpeed;
                if (currY < this.maxY) {
                    parts = new Particle[36];
                    for (int n= 0; n < 36; n++) {
                        Particle part = new Particle();
                        part.x = currX; part.y = currY;
                        part.xDelta = (r.nextFloat() * deltaTweak) - deltaTweakO2;
                        part.yDelta = (r.nextFloat() * deltaTweak) - deltaTweakO2;
                        part.Color = r.nextInt(6);
                        parts[n] = part;
                    }
                    detonated = true;
                }
            } else {
                for (int n = 0; n < parts.length; n++) {
                    parts[n].runSim();
                }
                partCycles++;
            }
        }
    }

    public ArrayList<FireWork> fireWorks;

    @Override
    protected Object doInBackground(Object[] objects) {
        InvalRunnable invalRunnable = new InvalRunnable();
        while (!isCancelled()) {
            if (r.nextFloat() < 0.18f) {
                FireWork fireWork = new FireWork(r.nextFloat() * maxYO2, r.nextFloat() * maxX);
                fireWork.currY = maxY;

                fireWorks.add(fireWork);
            }
            int fS = fireWorks.size();
            for (int n = 0; n < fS; n++) {
                FireWork current = fireWorks.get(n);
                current.runSim();
                if (current.partCycles > current.maxPartCycles) {
                    fireWorks.remove(n);
                    n--; // removed object so step back to ensure entire array is still processed
                    fS--; // also adjust size
                }
            }
            // update ui
            invalRoot.post(invalRunnable);
            try {
                Thread.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        fireWorks.clear();
        return null;
    }

    public class InvalRunnable implements Runnable {

        @Override
        public void run() {
            invalRoot.invalidate();
        }
    }
}
