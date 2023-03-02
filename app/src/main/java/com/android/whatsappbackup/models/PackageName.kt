package com.android.whatsappbackup.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import io.objectbox.annotation.Unique

@Entity
data class PackageName(
    @Id
    var entityId: Long,

    var name: String?,

    @Index
    @Unique
    var pkg: String?,

    var isBlackList: Boolean = false
) {
    override fun toString(): String {
        return "PackageName(entityId=$entityId, name=$name, pkg=$pkg, isBlackList=$isBlackList)"
    }
}
