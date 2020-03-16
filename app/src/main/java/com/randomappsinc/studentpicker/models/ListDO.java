package com.randomappsinc.studentpicker.models;

public class ListDO {

    private int id;
    private String name;

    public ListDO(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public ListDO(ListDO listDO) {
        this.id = listDO.id;
        this.name = listDO.name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
