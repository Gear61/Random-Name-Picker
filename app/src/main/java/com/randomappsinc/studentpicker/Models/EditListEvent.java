package com.randomappsinc.studentpicker.Models;

/**
 * Created by alexanderchiou on 10/18/15.
 */
public class EditListEvent
{
    public static final String ADD = "add";
    public static final String REMOVE = "remove";

    private String eventType;
    private String name;

    public EditListEvent(String eventType, String name)
    {
        this.eventType = eventType;
        this.name = name;
    }

    public String getEventType()
    {
        return eventType;
    }

    public String getName()
    {
        return name;
    }
}
