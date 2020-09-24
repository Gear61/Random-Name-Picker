package com.randomappsinc.studentpicker.utils

import java.io.*
import kotlin.experimental.and

object ObjectSerializer {

    @Throws(IOException::class)
    fun serialize(obj: Serializable): String {
        val serialObj = ByteArrayOutputStream()
        val objStream = ObjectOutputStream(serialObj)
        objStream.writeObject(obj)
        objStream.close()
        return encodeBytes(serialObj.toByteArray())
    }

    @Throws(IOException::class, ClassNotFoundException::class)
    fun deserialize(str: String?): Any? {
        if (str == null || str.isEmpty()) return null
        val serialObj = ByteArrayInputStream(decodeBytes(str))
        val objStream = ObjectInputStream(serialObj)
        return objStream.readObject()
    }

    private fun encodeBytes(bytes: ByteArray): String {
        val strBuf = StringBuffer()
        for (i in bytes.indices) {
            strBuf.append(((bytes[i].toInt() shr 4 and 0xF) + 'a'.toInt()).toChar())
            strBuf.append(((bytes[i] and 0xF) + 'a'.toInt()).toChar())
        }
        return strBuf.toString()
    }

    private fun decodeBytes(str: String): ByteArray {
        val bytes = ByteArray(str.length / 2)
        var i = 0
        while (i < str.length) {
            var c = str[i]
            bytes[i / 2] = (c - 'a' shl 4).toByte()
            c = str[i + 1]
            bytes[i / 2] = bytes[i / 2].plus(((c - 'a'))).toByte()
            i += 2
        }
        return bytes
    }
}