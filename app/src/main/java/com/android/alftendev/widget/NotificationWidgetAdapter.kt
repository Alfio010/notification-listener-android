package com.android.alftendev.widget

import android.content.Context
import android.graphics.drawable.Icon
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.android.alftendev.R
import com.android.alftendev.models.Notifications
import com.android.alftendev.utils.UiUtils.dateFormatter
import com.android.alftendev.utils.Utils.getIconFromDrawable
import com.android.alftendev.utils.computables.AppIcon

class NotificationWidgetAdapter(
    private val context: Context,
    val notifications: List<Notifications>
) :
    RemoteViewsService.RemoteViewsFactory {
    companion object {
        private const val MAX_LENGTH = 35
    }

    private val itemList = mutableListOf<Notifications>()

    override fun onCreate() {}

    override fun onDataSetChanged() {
        itemList.clear()
        itemList.addAll(notifications)
    }

    override fun onDestroy() {}

    override fun getCount(): Int = itemList.size

    override fun getViewAt(position: Int): RemoteViews {
        val notification = itemList[position]
        val views = RemoteViews(context.packageName, R.layout.widget_list_item)

        val title = if (notification.title.trim() != "null") {
            notification.title
        } else {
            context.getString(R.string.null_value)
        }

        val text = if (notification.text.length > MAX_LENGTH) {
            notification.text.subSequence(0, MAX_LENGTH).toString() + "..."
        } else if (notification.text.trim() == "null") {
            context.getString(R.string.null_value)
        } else {
            notification.text
        }

        views.setTextViewText(R.id.item_text, title)

        views.setTextViewText(R.id.item_description, text)

        views.setTextViewText(R.id.tvDate, dateFormatter(notification.time))

        val icon = AppIcon.compute(notification.packageName.target.pkg)

        if (icon != null) {
            views.setImageViewIcon(R.id.item_image, getIconFromDrawable(icon))
        } else {
            views.setImageViewIcon(
                R.id.item_image,
                Icon.createWithResource(context, R.drawable.baseline_android_24)
            )
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? = null

    override fun getViewTypeCount(): Int = 1

    override fun getItemId(position: Int): Long = position.toLong()

    override fun hasStableIds(): Boolean = true
}