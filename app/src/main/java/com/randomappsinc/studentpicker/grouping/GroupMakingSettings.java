package com.randomappsinc.studentpicker.grouping;

class GroupMakingSettings {

    private int nameListSize;
    private int numOfNamesPerGroup;
    private int numOfGroups;

    GroupMakingSettings(int nameListSize, int numOfNamesPerGroup, int numOfGroups) {
        this.nameListSize = nameListSize;
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

    int getNameListSize() {
        return nameListSize;
    }

    void setNameListSize(int nameListSize) {
        this.nameListSize = nameListSize;
    }
}
