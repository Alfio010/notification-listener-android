package com.android.whatsappbackup.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.annotation.Index
import java.util.*

@Entity
data class Notifications(
    @Id
    var entityId: Long,
    @Index
    var packageName: String,
    var title: String,
    var time: Date,
    var text: String,
    var bigText: String? = null,
    var conversationTitle: String? = null,
    var infoText: String? = null,
    var peopleList: String? = null,
    var titleBig: String? = null,
    var isDeleted: Boolean = false,
    var isChat: Boolean = false
) {
    override fun toString(): String {
        return "Notifications(entityId=$entityId, packageName='$packageName', title='$title', time=$time, text='$text', " +
                "isDeleted=$isDeleted, bigText='$bigText', conversationTitle='$conversationTitle', infoText='$infoText', " +
                "peopleList='$peopleList', titleBig='$titleBig')"
    }
}
