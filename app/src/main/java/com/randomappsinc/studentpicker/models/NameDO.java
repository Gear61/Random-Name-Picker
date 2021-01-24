package com.randomappsinc.studentpicker.models;

import androidx.annotation.Nullable;

public class NameDO {

    private int id;
    private String name;
    private int amount;
    private @Nullable String photoUri;

    public NameDO() {}

    public NameDO(int id, String name, int amount, @Nullable String photoUri) {
        this.id = id;
        this.name = name;
        this.amount = amount;
        this.photoUri = photoUri;
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

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    @Nullable
    public String getPhotoUri() {
        return photoUri;
    }

    public void setPhotoUri(@Nullable String photoUri) {
        this.photoUri = photoUri;
    }
}
