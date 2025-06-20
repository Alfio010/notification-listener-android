package com.android.alftendev.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique
import org.json.JSONObject

@Entity
data class PackageName(
    @Id
    var entityId: Long = 0,
    var name: String? = null,
    @Index
    @Unique
    var pkg: String = String(),
    var isBlackList: Boolean = false,
    var isChat: Boolean = false
) : IJsonSerializable {
    override fun toString(): String {
        return "PackageName(entityId=$entityId, name=$name, pkg='$pkg', " +
                "isBlackList=$isBlackList, isChat=$isChat)"
    }

    override fun toJson(): JSONObject {
        val json = JSONObject()

        json.put("entityId", entityId)
        json.put("name", name)
        json.put("pkg", pkg)
        json.put("isBlackList", isBlackList)
        json.put("isChat", isChat)

        return json
    }
}
