package com.android.alftendev.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.R
import com.android.alftendev.jetpackactivities.ChatUIActivity
import com.android.alftendev.models.Notifications
import com.android.alftendev.models.getParsedNoti
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.DateUtils.dateFormatter
import com.android.alftendev.utils.Utils
import com.android.alftendev.utils.computables.AppIcon
import com.google.android.material.checkbox.MaterialCheckBox
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView

class NotificationsAdapter(
    private val notifications: List<Notifications>,
    private val context: Context,
    private val selectionListener: OnSelectionChangeListener
) : RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {

    var isSelectionMode = false
    val selectedItemsIds = mutableSetOf<Long>()

    interface OnSelectionChangeListener {
        fun onSelectionModeChange(isSelectionMode: Boolean)
        fun onSelectionCountChange(count: Int)
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: MaterialTextView = view.findViewById(R.id.tvNome)
        val tvDescription: MaterialTextView = view.findViewById(R.id.tvDescrizione)
        val tvDate: MaterialTextView = view.findViewById(R.id.tvDate)
        val ivIcon: ImageView = view.findViewById(R.id.ivIcon)

        // val linearLayout: LinearLayoutCompat = view.findViewById(R.id.llNotificationAdapter)
        val cbSelect: MaterialCheckBox = view.findViewById(R.id.cbSelect)
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_notification_layout, viewGroup, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val notification = notifications[position]

        val parsedNoti = getParsedNoti(
            title = notification.title,
            text = notification.text,
            packageName = notification.packageName.target.pkg,
            context
        )

        viewHolder.tvName.text = parsedNoti.title
        viewHolder.tvDescription.text = parsedNoti.text
        viewHolder.tvDate.text = dateFormatter(notification.time)

        viewHolder.ivIcon.setImageIcon(parsedNoti.icon)

        viewHolder.cbSelect.visibility = if (isSelectionMode) View.VISIBLE else View.GONE
        viewHolder.cbSelect.isChecked = selectedItemsIds.contains(notification.entityId)

        viewHolder.itemView.setOnClickListener {
            if (isSelectionMode) {
                toggleSelection(notification.entityId)
            } else {
                showDialog(
                    notification,
                    AppIcon.compute(notification.packageName.target.pkg),
                    context
                )
            }
        }

        viewHolder.cbSelect.setOnClickListener {
            toggleSelection(notification.entityId)
        }

        viewHolder.itemView.setOnLongClickListener {
            if (!isSelectionMode) {
                isSelectionMode = true
                selectionListener.onSelectionModeChange(true)
                toggleSelection(notification.entityId)
                notifyDataSetChanged()
            }
            true
        }
    }

    override fun getItemCount() = notifications.size

    private fun toggleSelection(entityId: Long) {
        if (selectedItemsIds.contains(entityId)) {
            selectedItemsIds.remove(entityId)
        } else {
            selectedItemsIds.add(entityId)
        }
        selectionListener.onSelectionCountChange(selectedItemsIds.size)

        if (selectedItemsIds.isEmpty()) {
            exitSelectionMode()
        } else {
            notifyDataSetChanged()
        }
    }

    fun selectAll() {
        selectedItemsIds.clear()
        notifications.forEach { selectedItemsIds.add(it.entityId) }
        selectionListener.onSelectionCountChange(selectedItemsIds.size)
        notifyDataSetChanged()
    }

    fun deselectAll() {
        selectedItemsIds.clear()
        selectionListener.onSelectionCountChange(0)
        notifyDataSetChanged()
    }

    fun exitSelectionMode() {
        isSelectionMode = false
        selectedItemsIds.clear()
        selectionListener.onSelectionModeChange(false)
        selectionListener.onSelectionCountChange(0)
        notifyDataSetChanged()
    }

    @SuppressLint("InflateParams")
    private fun showDialog(notificationItem: Notifications, icon: Drawable?, context: Context) {
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
                .text = notificationItem.bigText!!.trim()
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

        customAlertDialogView.findViewById<Button>(R.id.bNotiAdapterDelete)
            .setOnClickListener {
                val builderDelete = MaterialAlertDialogBuilder(context)
                builderDelete.setTitle(context.getString(R.string.delete_notification))
                builderDelete.setMessage(context.getString(R.string.confirm_delete_noti_warning))

                if (icon != null) {
                    builderDelete.setIcon(icon)
                } else {
                    builderDelete.setIcon(R.mipmap.ic_launcher)
                }

                builderDelete.setPositiveButton(
                    context.getString(R.string.confirm)
                ) { _, _ ->
                    DBUtils.deleteNotificationById(notificationItem.entityId)
                }

                builderDelete.setNegativeButton(R.string.cancel) { _, _ -> }
                builderDelete.setOnCancelListener { it.dismiss() }
                builderDelete.create()
                builderDelete.show()
            }

        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(notificationItem.title)
        builder.setView(customAlertDialogView)

        if (icon != null) {
            builder.setIcon(icon)
        } else {
            builder.setIcon(R.mipmap.ic_launcher)
        }

        builder.setPositiveButton(R.string.show_chat) { _, _ ->
            val intentChat =
                Intent(context, ChatUIActivity::class.java).setAction(Intent.ACTION_MAIN)
            intentChat.putExtra("pkgName", notificationItem.packageName.target.pkg)
            intentChat.putExtra("title", notificationItem.title)
            context.startActivity(intentChat)
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