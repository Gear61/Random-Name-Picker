package com.randomappsinc.studentpicker.models;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Represents the choosing state of a name list */
public class ListInfo {

    private Map<String, NameDO> nameMap;
    private List<String> uniqueNames;
    private int numInstances;
    private List<String> nameHistory;

    public ListInfo(
            Map<String, NameDO> nameMap,
            List<String> uniqueNames,
            int numInstances,
            List<String> history) {
        this.nameMap = nameMap;
        this.uniqueNames = uniqueNames;
        this.numInstances = numInstances;
        this.nameHistory = history;
    }

    public Map<String, NameDO> getNameMap() {
        return nameMap;
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
            int amount = nameMap.get(name).getAmount();
            for (int i = 0; i < amount; i++) {
                longList.add(name);
            }
        }
        return longList;
    }

    private void removeNames(String name, int amount) {
        if (nameMap.containsKey(name)) {
            int currentAmount = nameMap.get(name).getAmount();
            if (currentAmount - amount <= 0) {
                nameMap.remove(name);
                uniqueNames.remove(name);

                // If we're removing more instances than there currently are, make sure
                // we remove the current amount instead so we don't go negative
                numInstances -= currentAmount;
            } else {
                int newAmount = currentAmount - amount;
                nameMap.get(name).setAmount(newAmount);
                numInstances -= amount;
            }
        }
    }

    public NameDO getNameDO(int position) {
        return nameMap.get(uniqueNames.get(position));
    }

    public NameDO getNameDO(String name) {
        return nameMap.get(name);
    }

    public int getNumNames() {
        return uniqueNames.size();
    }

    public int getNumInstances() {
        return numInstances;
    }

    public void removeAllInstancesOfName(int position) {
        String name = uniqueNames.get(position);
        int amount = nameMap.get(name).getAmount();
        removeNames(name, amount);
    }

    public List<String> chooseNamesLegacy(ChoosingSettings settings) {
        List<String> allNames = settings.getPreventDuplicates() ? new ArrayList<>(uniqueNames) : getLongList();
        Collections.shuffle(allNames);
        List<String> chosenNames = new ArrayList<>();

        int namesToAdd = Math.min(settings.getNumNamesToChoose(), allNames.size());
        for (int i = 0; i < namesToAdd; i++) {
            String chosenName = allNames.get(i);
            chosenNames.add(chosenName);
            nameHistory.add(chosenName);
            if (!settings.getWithReplacement()) {
                removeNames(chosenName, 1);
            }
        }
        return chosenNames;
    }

    public List<NameDO> chooseNames(ChoosingSettings settings) {
        List<String> allNames = settings.getPreventDuplicates() ? new ArrayList<>(uniqueNames) : getLongList();
        Collections.shuffle(allNames);
        List<NameDO> chosenNames = new ArrayList<>();

        int namesToAdd = Math.min(settings.getNumNamesToChoose(), allNames.size());
        for (int i = 0; i < namesToAdd; i++) {
            String chosenName = allNames.get(i);
            chosenNames.add(nameMap.get(chosenName));
            nameHistory.add(chosenName);
            if (!settings.getWithReplacement()) {
                removeNames(chosenName, 1);
            }
        }
        return chosenNames;
    }
}
