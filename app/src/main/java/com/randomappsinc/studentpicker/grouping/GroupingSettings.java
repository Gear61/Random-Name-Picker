package com.randomappsinc.studentpicker.grouping;

public class GroupingSettings {

    private int nameListSize;
    private int numOfNamesPerGroup;
    private int numOfGroups;

    public GroupingSettings(int nameListSize, int numOfNamesPerGroup, int numOfGroups) {
        this.nameListSize = nameListSize;
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

    public int getNameListSize() {
        return nameListSize;
    }

    public void setNameListSize(int nameListSize) {
        this.nameListSize = nameListSize;
    }
}
