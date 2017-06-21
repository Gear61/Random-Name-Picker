package com.randomappsinc.studentpicker.Models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by alexanderchiou on 6/20/17.
 */

public class ListInfo {
    private Map<String, Integer> nameAmounts;
    private List<String> names;
    private int numPeople;

    public ListInfo(Map<String, Integer> nameAmounts, List<String> names, int numPeople) {
        this.nameAmounts = nameAmounts;
        this.names = names;
        this.numPeople = numPeople;
    }

    public Map<String, Integer> getNameAmounts() {
        return nameAmounts;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getLongList() {
        List<String> longList = new ArrayList<>();
        for (String name : names) {
            int amount = nameAmounts.get(name);
            for (int i = 0; i < amount; i++) {
                longList.add(name);
            }
        }
        return longList;
    }

    public void addNames(String name, int amount) {
        if (nameAmounts.containsKey(name)) {
            int currentAmount = nameAmounts.get(name);
            nameAmounts.put(name, amount + currentAmount);
        } else {
            nameAmounts.put(name, amount);
            names.add(name);
            Collections.sort(names);
        }
        numPeople += amount;
    }

    public void removeNames(String name, int amount) {
        if (nameAmounts.containsKey(name)) {
            int currentAmount = nameAmounts.get(name);
            if (currentAmount - amount <= 0) {
                nameAmounts.remove(name);
                names.remove(name);
            } else {
                nameAmounts.put(name, currentAmount - amount);
            }
        }
        numPeople -= amount;
    }

    public void renamePeople(String oldName, String newName, int amount) {
        removeNames(oldName, amount);
        addNames(newName, amount);
    }

    public String getName(int position) {
        return names.get(position);
    }

    public String getNameText(int position) {
        String name = names.get(position);
        int amount = nameAmounts.get(name);
        return amount == 1 ? name : name + " (" + String.valueOf(amount) + ")";
    }

    public int getNumNames() {
        return names.size();
    }

    public int getNumPeople() {
        return numPeople;
    }
}
