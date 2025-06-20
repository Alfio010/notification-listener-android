package com.android.alftendev.models

import org.json.JSONObject

interface IJsonSerializable {
    fun toJson(): JSONObject
}
