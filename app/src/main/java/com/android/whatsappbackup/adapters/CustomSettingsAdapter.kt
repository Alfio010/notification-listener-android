package com.android.whatsappbackup.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.CustomLog
import com.android.whatsappbackup.utils.DBUtils
import com.google.android.material.textview.MaterialTextView

class CustomSettingsAdapter(
    context: Context,
    idRowCustom: Int,
    list: List<PackageName?>
) :
    ArrayAdapter<PackageName?>(context, idRowCustom, list) {
    companion object {
        val LOGGER = CustomLog("settings-adapter")
    }

    private var notificationItem: PackageName? = null

    @SuppressLint("SetTextI18n", "UseSwitchCompatOrMaterialCode", "ViewHolder", "InflateParams")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val convertView2: View?
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        convertView2 = inflater.inflate(R.layout.custom_blacklist_layout, null)

        val tvNamePackage = convertView2.findViewById<View>(R.id.tvNamePackage) as MaterialTextView
        val swIsBlackListed = convertView2.findViewById<View>(R.id.swIsBlackListed) as SwitchCompat
        val ivIcon = convertView2.findViewById<View>(R.id.ivBlacklist) as ImageView

        notificationItem = getItem(position)

        val name = notificationItem?.name
        val packageName = notificationItem?.pkg
        val icon: Drawable?

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

        tvNamePackage.text = if (name.isNullOrBlank()) {
            packageName
        } else {
            name
        }
        swIsBlackListed.isChecked = notificationItem?.isBlackList!!

        swIsBlackListed.setOnCheckedChangeListener { _, isChecked ->
            val entity = packageName?.let { DBUtils.packageNameExists(it) }
            if (entity != null) {
                entity.isBlackList = isChecked
                MyApplication.packagenames.put(entity)
            }
        }

        return convertView2
    }
}
