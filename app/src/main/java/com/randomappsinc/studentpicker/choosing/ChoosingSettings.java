package com.randomappsinc.studentpicker.choosing;

import com.randomappsinc.studentpicker.common.Language;

public class ChoosingSettings {

    private boolean presentationMode;
    private boolean withReplacement;
    private boolean automaticTts;
    private boolean showAsList;
    private int numNamesToChoose;
    private @Language int speechLanguage;
    private boolean preventDuplicates;

    public ChoosingSettings() {
        this.numNamesToChoose = 1;
    }

    public boolean isPresentationModeEnabled() {
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
        this.numNamesToChoose = Math.max(1, numNamesToChoose);
    }

    @Language
    public int getSpeechLanguage() {
        return speechLanguage;
    }

    public void setSpeechLanguage(@Language int speechLanguage) {
        this.speechLanguage = speechLanguage;
    }

    public boolean isPreventDuplicates() {
        return preventDuplicates;
    }

    public void setPreventDuplicates(boolean preventDuplicates) {
        this.preventDuplicates = preventDuplicates;
    }
}
