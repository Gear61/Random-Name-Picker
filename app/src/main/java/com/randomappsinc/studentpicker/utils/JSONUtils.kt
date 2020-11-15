package com.randomappsinc.studentpicker.utils

import android.text.TextUtils
import com.randomappsinc.studentpicker.models.ListDO
import com.randomappsinc.studentpicker.models.NameDO
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.util.*

object JSONUtils {

    private const val NAME_LIST = "nameList"
    private const val LIST_NAME = "listName"
    private const val NAME = "name"
    private const val AMOUNT = "amount"

    @JvmStatic
    fun namesArrayToJsonString(chosenNames: List<String>): String {
        val alreadyChosenNamesArray = JSONArray()
        for (alreadyChosenName in chosenNames) {
            alreadyChosenNamesArray.put(alreadyChosenName)
        }
        return alreadyChosenNamesArray.toString()
    }

    @JvmStatic
    fun extractNamesHistory(namesArrayText: String?): List<String> {
        if (TextUtils.isEmpty(namesArrayText)) {
            return emptyList()
        }
        val alreadyChosenNames: MutableList<String> = ArrayList()
        try {
            val namesArray = JSONArray(namesArrayText)
            for (i in 0 until namesArray.length()) {
                alreadyChosenNames.add(namesArray.getString(i))
            }
        } catch (ignored: JSONException) {
        }
        return alreadyChosenNames
    }

    fun serializeNameList(listDOs: List<ListDO>): String {
        val nameListArray = JSONArray()
        for (listDO in listDOs) {
            val listDOJson: JSONObject? = createNameListJson(listDO)
            if (listDOJson != null) {
                nameListArray.put(listDOJson)
            }
        }
        return nameListArray.toString()
    }

    private fun createNameListJson(listDO: ListDO): JSONObject? {
        try {
            val nameListJsonObject = JSONObject().apply {
                put(LIST_NAME, listDO.name)
            }

            val names = JSONArray()
            for (nameDO in listDO.namesInList) {
                val nameJson = JSONObject().apply {
                    put(NAME, nameDO.name)
                    put(AMOUNT, nameDO.amount)
                }
                names.put(nameJson)
            }
            nameListJsonObject.put(NAME_LIST, names)

            return nameListJsonObject
        } catch (e: JSONException) {
            return null
        }
    }

    fun deserializeNameListsJson(nameListsJson: String?): List<ListDO> {
        val listDOs = ArrayList<ListDO>()
        try {
            val nameListsArray = JSONArray(nameListsJson)
            for (i in 0 until nameListsArray.length()) {
                val nameListJson = nameListsArray.getJSONObject(i)
                listDOs.add(getNameListFromJson(nameListJson))
            }
        } catch (ignored: JSONException) {}
        return listDOs
    }

    private fun getNameListFromJson(nameListJson: JSONObject): ListDO {
        val listDO = ListDO()
        try {
            listDO.name = nameListJson.getString(LIST_NAME)

            val nameDOs = ArrayList<NameDO>()
            val nameList = nameListJson.getJSONArray(NAME_LIST)
            for (i in 0 until nameList.length()) {
                val nameJson = nameList.getJSONObject(i)
                val nameDo = NameDO().apply {
                    name = nameJson.getString(NAME)
                    amount = nameJson.getInt(AMOUNT)
                }
                nameDOs.add(nameDo)
            }
            listDO.namesInList = nameDOs
        }
        catch (ignored: JSONException) {}
        return listDO
    }
}