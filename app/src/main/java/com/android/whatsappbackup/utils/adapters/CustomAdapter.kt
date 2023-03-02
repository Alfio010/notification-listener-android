package com.android.whatsappbackup.utils.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import com.android.whatsappbackup.R
import com.android.whatsappbackup.activities.SpecificChatActivity
import com.android.whatsappbackup.models.Notifications
import com.android.whatsappbackup.utils.CustomLog
import com.android.whatsappbackup.utils.SomeUtils.dateFormatter
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textview.MaterialTextView
import com.google.gson.Gson
import org.json.JSONObject

class CustomAdapter(
    context: Context,
    idRowCustom: Int,
    list: List<Notifications?>
) :
    ArrayAdapter<Notifications?>(context, idRowCustom, list) {
    companion object {
        val LOGGER = CustomLog("custom-adapter")
    }

    private var notificationItem: Notifications? = null

    @SuppressLint("SetTextI18n", "ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView2: View?
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        convertView2 = inflater.inflate(R.layout.custom_notification_layout, null)

        val spacesToIndentEachLevel = 2
        val gson = Gson()

        val tvName = convertView2!!.findViewById<View>(R.id.tvNome) as MaterialTextView
        val tvDescription = convertView2.findViewById<View>(R.id.tvDescrizione) as MaterialTextView
        val tvDate = convertView2.findViewById<View>(R.id.tvDate) as MaterialTextView
        val ivIcon = convertView2.findViewById<View>(R.id.ivIcon) as ImageView

        notificationItem = getItem(position)
        val title = notificationItem?.title
        var text = notificationItem?.text
        val date = notificationItem?.time
        val packageName = notificationItem?.packageName
        var icon: Drawable? = null

        try {
            if (packageName != null) {
                icon = context.packageManager.getApplicationIcon(packageName)
                ivIcon.setImageDrawable(icon)
            } else {
                ivIcon.visibility = View.GONE
            }
        } catch (e: PackageManager.NameNotFoundException) {
            LOGGER.doLog(e.toString())
        }

        if (title.isNullOrBlank()) {
            tvName.visibility = View.GONE
        } else {
            tvName.text = title
        }

        if (text.isNullOrBlank()) {
            tvDescription.visibility = View.GONE
        } else {
            val length = 35
            if (text.length > length) {
                text = text.subSequence(0, length).toString() + "..."
            }
            tvDescription.text = text
        }

        tvDate.text = date?.let { dateFormatter(it) }

        fun customAdapterButtonListener(textView: TextView) {
            textView.setOnClickListener {
                notificationItem = getItem(position)
                val gJson = gson.toJson(notificationItem)

                val view: View = inflater.inflate(R.layout.scrollable_popup, null)
                val textview = view.findViewById(R.id.textmsg) as MaterialTextView
                textview.text = JSONObject(gJson).toString(spacesToIndentEachLevel)

                val builder = MaterialAlertDialogBuilder(context)
                builder.setTitle(title)
                builder.setView(view)
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
                    intentChat.putExtra("pkgName", packageName)
                    intentChat.putExtra("title", title)
                    startActivity(context, intentChat, null)
                }
                builder.setNegativeButton(R.string.back) { _, _ -> }
                builder.create()
                builder.show()
            }
        }

        customAdapterButtonListener(tvName)
        customAdapterButtonListener(tvDescription)

        return convertView2
    }
}
