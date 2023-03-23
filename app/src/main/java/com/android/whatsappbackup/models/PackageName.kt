package com.android.whatsappbackup.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

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
) {
    override fun toString(): String {
        return "PackageName(entityId=$entityId, name=$name, pkg='$pkg', isBlackList=$isBlackList, isChat=$isChat)"
    }
}
