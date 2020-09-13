package com.randomappsinc.studentpicker.models;

import java.util.List;

public class ListDO {

    private int id;
    private String name;
    private List<NameDO> nameDOList;

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

    public List<NameDO> getNameDOList() {
        return nameDOList;
    }

    public void setNameDOList(List<NameDO> nameDOList) {
        this.nameDOList = nameDOList;
    }
}
