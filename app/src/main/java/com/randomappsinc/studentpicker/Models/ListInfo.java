package com.randomappsinc.studentpicker.Models;

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

    public void addName(String name, int amount) {
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

    public void removeName(String name, int amount) {
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
        removeName(oldName, amount);
        addName(newName, amount);
    }

    public String getName(int position) {
        return names.get(position);
    }

    public int getAmount(int position) {
        return nameAmounts.get(getName(position));
    }

    public int getNumPeople() {
        return numPeople;
    }
}
