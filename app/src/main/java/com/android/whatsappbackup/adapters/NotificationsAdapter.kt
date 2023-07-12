package com.android.whatsappbackup.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.SpecificChatActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.UiUtils.dateFormatter
import com.android.whatsappbackup.utils.Utils
import com.android.whatsappbackup.utils.computables.AppIcon
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class NotificationsAdapter(
    private val notifications: List<Notifications>,
    private val context: Context
) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {
    companion object {
        private const val maxLength = 35

        @SuppressLint("InflateParams")
        fun setListener(
            view: View,
            notificationItem: Notifications,
            icon: Drawable?,
            context: Context
        ) {
            view.setOnClickListener {
                val customAlertDialogView = LayoutInflater.from(context)
                    .inflate(R.layout.custom_my_dialog, null, false)

                customAlertDialogView.findViewById<TextView>(R.id.tvDialogDate)
                    .text = dateFormatter(notificationItem.time)

                customAlertDialogView.findViewById<TextView>(R.id.tvDialogPackageName)
                    .text = if (notificationItem.packageName.target.name.isNullOrBlank()) {
                    notificationItem.packageName.target.pkg
                } else {
                    "${notificationItem.packageName.target.name} (${notificationItem.packageName.target.pkg})"
                }

                customAlertDialogView.findViewById<TextView>(R.id.tvDialogText)
                    .text = notificationItem.text

                if (!notificationItem.conversationTitle.isNullOrBlank()) {
                    customAlertDialogView.findViewById<TextView>(R.id.tvDialogConversationTitle)
                        .text = notificationItem.conversationTitle
                } else {
                    customAlertDialogView.findViewById<LinearLayout>(R.id.llConversationTitle)
                        .visibility = View.GONE
                }

                if (!notificationItem.titleBig.isNullOrBlank()) {
                    customAlertDialogView.findViewById<TextView>(R.id.tvDialogBigTitle)
                        .text = notificationItem.titleBig
                } else {
                    customAlertDialogView.findViewById<LinearLayout>(R.id.llBigTitle)
                        .visibility = View.GONE
                }

                if (!notificationItem.bigText.isNullOrBlank()) {
                    customAlertDialogView.findViewById<TextView>(R.id.tvDialogBigText)
                        .text = notificationItem.bigText
                } else {
                    customAlertDialogView.findViewById<LinearLayout>(R.id.llDialogBigText)
                        .visibility = View.GONE
                }

                if (!notificationItem.infoText.isNullOrBlank()) {
                    customAlertDialogView.findViewById<TextView>(R.id.tvDialogInfoText)
                        .text = notificationItem.infoText
                } else {
                    customAlertDialogView.findViewById<LinearLayout>(R.id.llInfoText)
                        .visibility = View.GONE
                }

                if (!notificationItem.peopleList.isNullOrBlank()) {
                    customAlertDialogView.findViewById<TextView>(R.id.tvDialogPeopleList)
                        .text = notificationItem.peopleList
                } else {
                    customAlertDialogView.findViewById<LinearLayout>(R.id.llPeopleList)
                        .visibility = View.GONE
                }

                customAlertDialogView.findViewById<TextView>(R.id.tvDialogIsDeleted)
                    .text = if (notificationItem.isDeleted) {
                    context.getString(R.string.yes)
                } else {
                    context.getString(R.string.no)
                }

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle(notificationItem.title)
                builder.setView(customAlertDialogView)

                if (icon != null) {
                    builder.setIcon(icon)
                } else {
                    builder.setIcon(R.mipmap.ic_launcher)
                }

                builder.setPositiveButton(
                    R.string.show_chat
                ) { _, _ ->
                    val intentChat = Intent(context, SpecificChatActivity::class.java)
                    intentChat.putExtra("pkgName", notificationItem.packageName.target.pkg)
                    intentChat.putExtra("title", notificationItem.title)
                    ContextCompat.startActivity(context, intentChat, null)
                }
                builder.setNegativeButton(R.string.back) { _, _ -> }
                builder.setNeutralButton(context.getString(R.string.open_app)) { _, _ ->
                    Utils.openApp(notificationItem.packageName.target.pkg, context)
                }
                builder.setOnCancelListener { it.dismiss() }
                builder.create()
                builder.show()
            }
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: MaterialTextView
        val tvDescription: MaterialTextView
        val tvDate: MaterialTextView
        val ivIcon: ImageView
        val linearLayout: LinearLayoutCompat

        init {
            tvName = view.findViewById(R.id.tvNome)
            tvDescription = view.findViewById(R.id.tvDescrizione)
            tvDate = view.findViewById(R.id.tvDate)
            ivIcon = view.findViewById(R.id.ivIcon)
            linearLayout = view.findViewById(R.id.llNotificationAdapter)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_notification_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val notifications = notifications[position]

        val title = if (notifications.title.trim() != "null") {
            notifications.title
        } else {
            context.getString(R.string.null_value)
        }

        val text = if (notifications.text.length > maxLength) {
            notifications.text.subSequence(0, maxLength).toString() + "..."
        } else if (notifications.text.trim() == "null") {
            context.getString(R.string.null_value)
        } else {
            notifications.text
        }

        viewHolder.tvName.text = title

        viewHolder.tvDescription.text = text

        viewHolder.tvDate.text = dateFormatter(notifications.time)

        val icon = AppIcon.compute(notifications.packageName.target.pkg)

        if (icon != null) {
            viewHolder.ivIcon.setImageDrawable(icon)
        } else {
            viewHolder.ivIcon.setImageResource(R.drawable.baseline_android_24)
        }

        setListener(viewHolder.tvName, notifications, icon, context)
        setListener(viewHolder.tvDescription, notifications, icon, context)
        setListener(viewHolder.linearLayout, notifications, icon, context)
    }

    override fun getItemCount() = notifications.size
}