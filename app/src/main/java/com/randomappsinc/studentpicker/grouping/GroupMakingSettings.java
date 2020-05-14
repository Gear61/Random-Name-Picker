package com.randomappsinc.studentpicker.grouping;

class GroupMakingSettings {

    private static final int DEFAULT_NAMES_PER_GROUP = 2;

    private int nameListSize;
    private int numOfNamesPerGroup;
    private int numOfGroups;

    GroupMakingSettings(int nameListSize) {
        this.nameListSize = nameListSize;
        this.numOfNamesPerGroup = DEFAULT_NAMES_PER_GROUP;

        int numGroupsToFill = nameListSize / DEFAULT_NAMES_PER_GROUP;
        numOfGroups = (nameListSize % DEFAULT_NAMES_PER_GROUP) > 0
                ? numGroupsToFill + 1
                : numGroupsToFill;
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
}
