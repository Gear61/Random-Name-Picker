package com.randomappsinc.studentpicker.models;

import com.randomappsinc.studentpicker.choosing.ChoosingSettings;
import com.randomappsinc.studentpicker.utils.NameUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Represents the choosing state of a name list */
public class ListInfo {

    private Map<String, NameDO> nameInformation;
    private List<String> names;
    private int numInstances;
    private List<String> nameHistory;

    public ListInfo(Map<String, NameDO> nameInformation, List<String> names, int numInstances, List<String> history) {
        this.nameInformation = nameInformation;
        this.names = names;
        this.numInstances = numInstances;
        this.nameHistory = history;
    }

    public Map<String, NameDO> getNameInformation() {
        return nameInformation;
    }

    public List<String> getNames() {
        return names;
    }

    public List<String> getNameHistory() {
        return nameHistory;
    }

    public void clearNameHistory() {
        nameHistory.clear();
    }

    public String getNameHistoryFormatted() {
        StringBuilder namesHistory = new StringBuilder();
        for (int i = 0; i < nameHistory.size(); i++) {
            if (i != 0) {
                namesHistory.append("\n");
            }
            namesHistory.append(nameHistory.get(i));
        }
        return namesHistory.toString();
    }

    private List<String> getLongList() {
        List<String> longList = new ArrayList<>();
        for (String name : names) {
            int amount = nameInformation.get(name).getAmount();
            for (int i = 0; i < amount; i++) {
                longList.add(name);
            }
        }
        return longList;
    }

    public void addNames(int nameId, String name, int amount) {
        if (nameInformation.containsKey(name)) {
            NameDO nameDO = nameInformation.get(name);
            int currentAmount = nameDO.getAmount();
            nameDO.setAmount(currentAmount + amount);
        } else {
            NameDO newName = new NameDO(nameId, name, amount);
            nameInformation.put(name, newName);
            names.add(name);
            Collections.sort(names);
        }
        numInstances += amount;
    }

    public void removeNames(String name, int amount) {
        if (nameInformation.containsKey(name)) {
            NameDO nameInfo = nameInformation.get(name);
            int currentAmount = nameInfo.getAmount();
            if (currentAmount - amount <= 0) {
                nameInformation.remove(name);
                names.remove(name);

                // If we're removing more instances than there currently are, make sure
                // we remove the current amount instead so we don't go negative
                numInstances -= currentAmount;
            } else {
                nameInfo.setAmount(currentAmount - amount);
                numInstances -= amount;
            }
        }
    }

    public void renamePeople(String oldName, String newName, int amount) {
        removeNames(oldName, amount);
        // addNames(nameId, newName, amount);
    }

    public String getName(int position) {
        return names.get(position);
    }

    public String getNameText(int position) {
        String name = names.get(position);
        int amount = nameInformation.get(name).getAmount();
        return amount == 1 ? name : name + " (" + amount + ")";
    }

    public int getNumNames() {
        return names.size();
    }

    public int getNumInstances() {
        return numInstances;
    }

    public void removeAllInstancesOfName(int position) {
        String name = getName(position);
        int amount = nameInformation.get(name).getAmount();
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

    public List<List<String>> groupNamesList(List<List<Integer>> listOfGroups) {
        List<List<String>> listOfGroupsOfNames = new ArrayList<>();
        List<String> allNames = getLongList();
        for (List<Integer> listOfIndicesPerGroup : listOfGroups) {
            List<String> listOfNames = new ArrayList<>();
            for (int i = 0; i < listOfIndicesPerGroup.size(); i++) {
                listOfNames.add(allNames.get(listOfIndicesPerGroup.get(i)));
            }
            listOfGroupsOfNames.add(listOfNames);
        }
        return listOfGroupsOfNames;
    }
}
