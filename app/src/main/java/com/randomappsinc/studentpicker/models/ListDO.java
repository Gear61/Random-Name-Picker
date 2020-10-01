package com.randomappsinc.studentpicker.models;

import java.io.Serializable;
import java.util.List;

public class ListDO implements Serializable {

    private int id;
    private String name;
    private List<NameDO> nameDOs;

    public ListDO(int id, String name) {
        this.id = id;
        this.name = name;
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

    public List<NameDO> getNamesInList() {
        return nameDOs;
    }

    public void setNamesInList(List<NameDO> nameDOs) {
        this.nameDOs = nameDOs;
    }
}
