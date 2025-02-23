package com.android.alftendev.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.android.alftendev.R

class LastNotiWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context?,
        appWidgetManager: AppWidgetManager?,
        appWidgetId: Int,
        newOptions: Bundle?
    ) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions)
        context?.let {
            appWidgetManager?.let {
                updateAppWidget(context, appWidgetManager, appWidgetId)
            }
        }
    }
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.widget_layout)
    val intent = Intent(context, LastNotiWidgetService::class.java)
    views.setRemoteAdapter(R.id.widget_list, intent)

    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_list)

    appWidgetManager.updateAppWidget(appWidgetId, views)
}