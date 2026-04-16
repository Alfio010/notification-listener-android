package com.android.alftendev.widget

import android.content.Intent
import android.widget.RemoteViewsService

class LastNotiWidgetService : RemoteViewsService() {
    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return NotificationWidgetAdapter(this.applicationContext)
    }
}