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
        if (numOfGroups != 0) {
            this.numOfGroups = numOfGroups;
        } else {
            int numGroupsToFill = nameListSize / DEFAULT_NAMES_PER_GROUP;
            this.numOfGroups = (nameListSize % DEFAULT_NAMES_PER_GROUP) > 0
                    ? numGroupsToFill + 1
                    : numGroupsToFill;
        }
    }

    int getNameListSize() {
        return nameListSize;
    }
}
