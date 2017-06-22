package com.randomappsinc.studentpicker.Models;

import com.randomappsinc.studentpicker.Utils.NameUtils;

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
    private int numInstances;

    public ListInfo(Map<String, Integer> nameAmounts, List<String> names, int numInstances) {
        this.nameAmounts = nameAmounts;
        this.names = names;
        this.numInstances = numInstances;
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
        numInstances += amount;
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
        numInstances -= amount;
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

    public int getNumInstances() {
        return numInstances;
    }

    public String chooseNames(List<Integer> indexes, ChoosingSettings settings, List<String> alreadyChosenNames) {
        StringBuilder namesText = new StringBuilder();
        List<String> allNames = getLongList();
        for (int i = 0; i < indexes.size(); i++) {
            if (i != 0) {
                namesText.append("\n");
            }
            if (settings.getShowAsList()) {
                namesText.append(NameUtils.getPrefix(i));
            }

            String chosenName = allNames.get(indexes.get(i));
            namesText.append(chosenName);
            alreadyChosenNames.add(chosenName);
            if (!settings.getWithReplacement()) {
                removeNames(chosenName, 1);
            }
        }
        return namesText.toString();
    }
}
