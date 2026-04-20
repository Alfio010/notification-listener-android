package com.android.alftendev.widget

import android.content.Context
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.android.alftendev.R
import com.android.alftendev.models.Notifications
import com.android.alftendev.models.getParsedNoti
import com.android.alftendev.utils.CustomLog
import com.android.alftendev.utils.DBUtils.lastNotificationWithLimit
import com.android.alftendev.utils.DateUtils.dateFormatter
import java.util.concurrent.Executors

class NotificationWidgetAdapter(private val context: Context) :
    RemoteViewsService.RemoteViewsFactory {

    companion object {
        const val MAX_LENGTH = 35
        val LOGGER = CustomLog("noti-widget-provider")
    }

    private val widgetExecutor = Executors.newSingleThreadExecutor()

    @Volatile
    private var itemList: List<Notifications> = emptyList()

    override fun onCreate() {
    }

    override fun onDataSetChanged() {
        val future = widgetExecutor.submit<List<Notifications>> {
            lastNotificationWithLimit()
        }

        try {
            itemList = future.get()
        } catch (e: Exception) {
            LOGGER.log("Error while loading widget ${e.stackTraceToString()}")
            itemList = emptyList()
        }
    }

    override fun onDestroy() {
        itemList = emptyList()
        widgetExecutor.shutdown()
    }

    override fun getCount(): Int = itemList.size

    override fun getViewAt(position: Int): RemoteViews {
        val notification = itemList[position]
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)

        val parsedNoti = getParsedNoti(
            title = notification.title,
            text = notification.text,
            packageName = notification.packageName.target.pkg,
            context
        )

        views.setTextViewText(R.id.item_text, parsedNoti.title)
        views.setTextViewText(R.id.item_description, parsedNoti.text)
        views.setTextViewText(R.id.noti_date, dateFormatter(notification.time))

        views.setImageViewIcon(R.id.item_image, parsedNoti.icon)

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long {
        return itemList.getOrNull(position)?.entityId ?: position.toLong()
    }

    override fun hasStableIds(): Boolean = true
}