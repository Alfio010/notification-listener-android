package com.android.alftendev.widget

import android.content.Intent
import android.widget.RemoteViewsService
import com.android.alftendev.utils.DBUtils.getChatNotificationsForWidget

class ChatNotiWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NotificationWidgetAdapter(this.applicationContext, getChatNotificationsForWidget())
    }
}