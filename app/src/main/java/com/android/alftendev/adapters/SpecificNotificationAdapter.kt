package com.android.alftendev.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.R
import com.android.alftendev.models.NotificationStatsItem
import com.android.alftendev.utils.computables.AppIcon
import com.google.android.material.textview.MaterialTextView

class SpecificNotificationAdapter(
    private val notificationStatsItemsList: List<NotificationStatsItem>,
    val packageName: String
) :
    RecyclerView.Adapter<SpecificNotificationAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSpecificNotification: MaterialTextView
        val tvCounter: MaterialTextView
        val ivBlacklist: ImageView

        init {
            tvSpecificNotification = view.findViewById(R.id.tvSpecificNotification)
            tvCounter = view.findViewById(R.id.tvSpecificNotificationCount)
            ivBlacklist = view.findViewById(R.id.ivSpecificNotification)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.specific_notification_adapter, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val notificationStatsItem = notificationStatsItemsList[position]

        viewHolder.tvSpecificNotification.text = notificationStatsItem.title

        viewHolder.tvCounter.text = if (notificationStatsItem.percentage == null) {
            notificationStatsItem.number.toString()
        } else {
            "${notificationStatsItem.percentage}% (${notificationStatsItem.number})"
        }

        if (packageName.isNotBlank()) {
            val icon = AppIcon.compute(packageName)

            if (icon != null) {
                viewHolder.ivBlacklist.setImageDrawable(icon)
            } else {
                viewHolder.ivBlacklist.setImageResource(R.drawable.baseline_android_24)
            }
        }
    }

    override fun getItemCount() = notificationStatsItemsList.size
}