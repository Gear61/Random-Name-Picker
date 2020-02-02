package com.randomappsinc.studentpicker.grouping;

public class GroupingSettings {

    private int listSize;
    private int numOfNamesPerGroup;
    private int numOfGroups;

    public GroupingSettings(int listSize, int numOfNamesPerGroup, int numOfGroups) {
        this.listSize = listSize;
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

    public int getListSize() {
        return listSize;
    }

    public void setListSize(int listSize) {
        this.listSize = listSize;
    }
}
