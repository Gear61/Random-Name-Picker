package com.randomappsinc.studentpicker.choosing;

public class ChoosingSettings {

    private boolean presentationMode;
    private boolean withReplacement;
    private boolean automaticTts;
    private boolean showAsList;
    private int numNamesToChoose;

    public ChoosingSettings() {
        this.numNamesToChoose = 1;
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

    public void setWithReplacement(boolean withReplacement) {
        this.withReplacement = withReplacement;
    }

    public boolean getAutomaticTts() {
        return automaticTts;
    }

    public void setAutomaticTts(boolean automaticTts) {
        this.automaticTts = automaticTts;
    }

    public boolean getShowAsList() {
        return showAsList;
    }

    public void setShowAsList(boolean showAsList) {
        this.showAsList = showAsList;
    }

    public int getNumNamesToChoose() {
        return numNamesToChoose;
    }

    public void setNumNamesToChoose(int numNamesToChoose) {
        this.numNamesToChoose = numNamesToChoose;
    }
}
