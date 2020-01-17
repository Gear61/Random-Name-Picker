package com.randomappsinc.studentpicker.grouping;

class GroupingSettings {

    private int numOfNamesPerGroup;
    private int numOfGroups;

    GroupingSettings(int numOfNamesPerGroup, int numOfGroups) {
        this.numOfNamesPerGroup = numOfNamesPerGroup;
        this.numOfGroups = numOfGroups;
    }

    int getNumOfNamesPerGroup() {
        return numOfNamesPerGroup;
    }

    void setNumOfNamesPerGroup(int numNamesPerGroup) {
        this.numOfNamesPerGroup = numNamesPerGroup;
    }

    int getNumOfGroups() {
        return numOfGroups;
    }

    void setNumOfGroups(int numOfGroups) {
        this.numOfGroups = numOfGroups;
    }
}
