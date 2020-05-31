package com.randomappsinc.studentpicker.grouping;

public class GroupMakingSettings {

    private static final int DEFAULT_NAMES_PER_GROUP = 2;

    private int nameListSize;
    private int numOfNamesPerGroup;
    private int numOfGroups;

    public GroupMakingSettings(int nameListSize) {
        this.nameListSize = nameListSize;
        this.numOfNamesPerGroup = DEFAULT_NAMES_PER_GROUP;
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

    int getNameListSize() {
        return nameListSize;
    }
}
