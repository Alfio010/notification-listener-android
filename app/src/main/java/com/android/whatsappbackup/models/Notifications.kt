package com.android.whatsappbackup.models

import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id
import io.objectbox.relation.ToOne
import java.util.*

@Entity
data class Notifications(
    @Id
    var entityId: Long,
    var title: String,
    var time: Date,
    var text: String,
    var bigText: String? = null,
    var conversationTitle: String? = null,
    var infoText: String? = null,
    var peopleList: String? = null,
    var titleBig: String? = null,
    var isDeleted: Boolean = false
) {
    lateinit var packageName: ToOne<PackageName>

    override fun toString(): String {
        return "Notifications(entityId=$entityId, packageName='$packageName', title='$title', time=$time, text='$text', bigText=$bigText, conversationTitle=$conversationTitle, infoText=$infoText, peopleList=$peopleList, titleBig=$titleBig, isDeleted=$isDeleted)"
    }
}
