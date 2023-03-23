package com.android.whatsappbackup.models

import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.google.gson.JsonSerializer
import java.lang.reflect.Type

class NotificationJsonSerializer : JsonSerializer<Notifications> {
    override fun serialize(
        src: Notifications,
        typeOfSrc: Type?,
        context: JsonSerializationContext
    ): JsonElement {
        val jsonObject = JsonObject()

        jsonObject.add("entityId", context.serialize(src.entityId))
        jsonObject.add("packageName", context.serialize(src.packageName.target?.pkg))
        jsonObject.add("time", context.serialize(src.time))

        jsonObject.add("title", context.serialize(src.title))
        jsonObject.add("conversationTitle", context.serialize(src.conversationTitle))
        jsonObject.add("titleBig", context.serialize(src.titleBig))
        jsonObject.add("text", context.serialize(src.text))
        jsonObject.add("bigText", context.serialize(src.bigText))
        jsonObject.add("infoText", context.serialize(src.infoText))
        jsonObject.add("peopleList", context.serialize(src.peopleList))

        jsonObject.add("isDeleted", context.serialize(src.isDeleted))

        return jsonObject
    }
}
