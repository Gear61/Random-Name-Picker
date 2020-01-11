package com.randomappsinc.studentpicker.grouping;

public class GroupingSettings {

    private int numOfNamesInGroup;
    private int numOfGroups;

    public GroupingSettings() {
        this.numOfNamesInGroup = 2;
        this.numOfGroups = 1;
    }

    public int getNumOfNamesInGroup() {
        return numOfNamesInGroup;
    }

    public void setNumOfNamesInGroup(int numNamesInGroup) {
        this.numOfNamesInGroup = numNamesInGroup;
    }

    public int getNumOfGroups() {
        return numOfGroups;
    }

    public void setNumOfGroups(int numOfGroups) {
        this.numOfGroups = numOfGroups;
    }
}
