package com.randomappsinc.studentpicker.utils

import android.text.TextUtils
import com.randomappsinc.studentpicker.models.ListDO
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
}