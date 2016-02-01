package com.randomappsinc.studentpicker.Models;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class EditListEvent {
    public static final String ADD = "add";
    public static final String REMOVE = "remove";
    public static final String RENAME = "rename";
    public static final String RENAME_LIST = "renameList";

    private String eventType;
    private String name;
    private String newName;

    public String getEventType() {
        return eventType;
    }

    public String getName() {
        return name;
    }

    public String getNewName() {
        return newName;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
