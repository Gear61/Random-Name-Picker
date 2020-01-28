package com.randomappsinc.studentpicker.grouping;

public class GroupingSettings {

    private int numOfNamesPerGroup;
    private int numOfGroups;

    public GroupingSettings() {
    }

    public GroupingSettings(int numOfNamesPerGroup, int numOfGroups) {
        this.numOfNamesPerGroup = numOfNamesPerGroup;
        this.numOfGroups = numOfGroups;
    }

    public int getNumOfNamesPerGroup() {
        return numOfNamesPerGroup;
    }

    public void setNumOfNamesPerGroup(int numNamesPerGroup) {
        this.numOfNamesPerGroup = numNamesPerGroup;
    }

    public int getNumOfGroups() {
        return numOfGroups;
    }

    public void setNumOfGroups(int numOfGroups) {
        this.numOfGroups = numOfGroups;
    }
}
