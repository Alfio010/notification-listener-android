package com.android.whatsappbackup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.whatsappbackup.MyApplication
import com.android.whatsappbackup.R
import com.android.whatsappbackup.models.PackageName
import com.android.whatsappbackup.utils.DBUtils
import com.android.whatsappbackup.utils.computables.AppIcon
import com.google.android.material.textview.MaterialTextView

class SettingsAdapter(private val packageNames: List<PackageName>) :
    RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamePackage: MaterialTextView
        val swIsBlackListed: SwitchCompat
        val ivBlacklist: ImageView

        init {
            tvNamePackage = view.findViewById(R.id.tvPackageName)
            swIsBlackListed = view.findViewById(R.id.swIsBlackListed)
            ivBlacklist = view.findViewById(R.id.ivBlacklist)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_blacklist_layout, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val packageName = packageNames[position]

        viewHolder.tvNamePackage.text = if (packageName.name.isNullOrBlank()) {
            packageName.pkg
        } else {
            packageName.name
        }

        viewHolder.swIsBlackListed.isChecked = packageName.isBlackList

        viewHolder.swIsBlackListed.setOnCheckedChangeListener { _, isChecked ->
            val entity = DBUtils.packageNameExists(packageName.pkg)
            if (entity != null) {
                entity.isBlackList = isChecked
                MyApplication.packageNames.put(entity)
            }
        }

        val icon = AppIcon.compute(packageName.pkg)

        if (icon != null) {
            viewHolder.ivBlacklist.setImageDrawable(icon)
        } else {
            viewHolder.ivBlacklist.setImageResource(R.drawable.baseline_android_24)
        }
    }

    override fun getItemCount() = packageNames.size
}