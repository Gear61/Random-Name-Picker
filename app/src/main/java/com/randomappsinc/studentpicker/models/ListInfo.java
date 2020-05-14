package com.randomappsinc.studentpicker.models;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.utils.NameUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/** Represents the choosing state of a name list */
public class ListInfo {

    private Map<String, Integer> nameAmounts;
    private List<String> uniqueNames;
    private int numInstances;
    private List<String> nameHistory;

    public ListInfo(
            Map<String, Integer> nameToAmount,
            List<String> uniqueNames,
            int numInstances,
            List<String> history) {
        this.nameAmounts = nameToAmount;
        this.uniqueNames = uniqueNames;
        this.numInstances = numInstances;
        this.nameHistory = history;
    }

    public Map<String, Integer> getNameAmounts() {
        return nameAmounts;
    }

    public List<String> getNameHistory() {
        return nameHistory;
    }

    public void clearNameHistory() {
        nameHistory.clear();
    }

    public List<String> getLongList() {
        List<String> longList = new ArrayList<>();
        for (String name : uniqueNames) {
            int amount = nameAmounts.get(name);
            for (int i = 0; i < amount; i++) {
                longList.add(name);
            }
        }
        return longList;
    }

    private void removeNames(String name, int amount) {
        if (nameAmounts.containsKey(name)) {
            int currentAmount = nameAmounts.get(name);
            if (currentAmount - amount <= 0) {
                nameAmounts.remove(name);
                uniqueNames.remove(name);

                // If we're removing more instances than there currently are, make sure
                // we remove the current amount instead so we don't go negative
                numInstances -= currentAmount;
            } else {
                nameAmounts.put(name, currentAmount - amount);
                numInstances -= amount;
            }
        }
    }

    public String getName(int position) {
        return uniqueNames.get(position);
    }

    public String getNameText(int position) {
        String name = uniqueNames.get(position);
        int amount = nameAmounts.get(name);
        return amount == 1 ? name : name + " (" + amount + ")";
    }

    public int getNumNames() {
        return uniqueNames.size();
    }

    public int getNumInstances() {
        return numInstances;
    }

    public void removeAllInstancesOfName(int position) {
        String name = getName(position);
        int amount = nameAmounts.get(name);
        removeNames(name, amount);
    }

    public String chooseNames(List<Integer> indexes, ChoosingSettings settings) {
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
            nameHistory.add(chosenName);
            if (!settings.getWithReplacement()) {
                removeNames(chosenName, 1);
            }
        }
        return namesText.toString();
    }
}
