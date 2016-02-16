package com.randomappsinc.studentpicker.Models;

/**
 * Created by alexanderchiou on 2/15/16.
 */
public class ChoosingSettings {
    private boolean withReplacement;
    private int numNamesToChoose;

    public ChoosingSettings(boolean withReplacement, int numNamesToChoose) {
        this.withReplacement = withReplacement;
        this.numNamesToChoose = numNamesToChoose;
    }

    public boolean getWithReplacement() {
        return withReplacement;
    }

    public int getNumNamesToChoose() {
        return numNamesToChoose;
    }

    public void setWithReplacement(boolean withReplacement) {
        this.withReplacement = withReplacement;
    }

    public void setNumNamesToChoose(int numNamesToChoose) {
        this.numNamesToChoose = numNamesToChoose;
    }
}
