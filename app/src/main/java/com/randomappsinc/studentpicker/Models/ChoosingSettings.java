package com.randomappsinc.studentpicker.Models;

/**
 * Created by alexanderchiou on 2/15/16.
 */
public class ChoosingSettings {
    private boolean presentationMode;
    private boolean withReplacement;
    private int numNamesToChoose;

    public ChoosingSettings(boolean presentationMode, boolean withReplacement, int numNamesToChoose) {
        this.presentationMode = presentationMode;
        this.withReplacement = withReplacement;
        this.numNamesToChoose = numNamesToChoose;
    }

    public boolean getPresentationMode() {
        return presentationMode;
    }

    public void setPresentationMode(boolean presentationMode) {
        this.presentationMode = presentationMode;
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
