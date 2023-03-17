package com.android.whatsappbackup.adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.SpecificChatActivity
import com.android.whatsappbackup.models.NotificationJsonSerializer
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.Utils
import com.android.whatsappbackup.utils.computables.AppIcon
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject

class NotificationsAdapter(
    private val notifications: List<Notifications>,
    private val context: Context
) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    companion object {
        private const val spacesToIndentEachLevel = 2
        private const val maxLength = 35
        private val gson: Gson = GsonBuilder().registerTypeAdapter(
            Notifications::class.java,
            NotificationJsonSerializer()
        ).create()

        fun customAdapterButtonListener(
            textView: MaterialTextView,
            notificationItem: Notifications,
            icon: Drawable?,
            context: Context
        ) {
            textView.setOnClickListener {
                val gJson = gson.toJson(notificationItem)

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle(notificationItem.title)
                builder.setMessage(JSONObject(gJson).toString(spacesToIndentEachLevel))

                if (icon != null) {
                    builder.setIcon(icon)
                } else {
                    builder.setIcon(com.android.whatsappbackup.utils.icon)
                }

                builder.setPositiveButton(
                    R.string.show_chat
                ) { _, _ ->
                    val intentChat = Intent(context, SpecificChatActivity::class.java)
                    intentChat.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                    intentChat.putExtra("pkgName", notificationItem.packageName)
                    intentChat.putExtra("title", notificationItem.title)
                    ContextCompat.startActivity(context, intentChat, null)
                }
                builder.setNegativeButton(R.string.back) { _, _ -> }
                builder.setNeutralButton(context.getString(R.string.open_app)) { _, _ ->
                    Utils.openApp(notificationItem.packageName, context)
                }
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

        init {
            tvName = view.findViewById(R.id.tvNome)
            tvDescription = view.findViewById(R.id.tvDescrizione)
            tvDate = view.findViewById(R.id.tvDate)
            ivIcon = view.findViewById(R.id.ivIcon)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_notification_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val notifications = notifications[position]

        val title = notifications.title
        val text = if (notifications.text.length > maxLength) {
            notifications.text.subSequence(0, maxLength).toString() + "..."
        } else {
            notifications.text
        }

        viewHolder.tvName.text = title

        viewHolder.tvDescription.text = text

        viewHolder.tvDate.text = Utils.dateFormatter(notifications.time)

        val icon = AppIcon.compute(notifications.packageName)

        if (icon != null) {
            viewHolder.ivIcon.setImageDrawable(icon)
        } else {
            viewHolder.ivIcon.setImageResource(R.drawable.baseline_android_24)
        }

        customAdapterButtonListener(viewHolder.tvName, notifications, icon, context)
        customAdapterButtonListener(viewHolder.tvDescription, notifications, icon, context)
    }

    override fun getItemCount() = notifications.size
}