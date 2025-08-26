package com.android.alftendev.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.computables.AppIcon
import com.google.android.material.textview.MaterialTextView

// TODO specificGraph in alert
class BlackWhitelistAdapter(
    private val packageNames: List<PackageName>,
    private val blacklistMode: Boolean
) :
    RecyclerView.Adapter<BlackWhitelistAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamePackage: MaterialTextView
        val swIsBlackWhiteListed: SwitchCompat
        val ivBlackWhitelist: ImageView

        init {
            tvNamePackage = view.findViewById(R.id.tvPackageName)
            swIsBlackWhiteListed = view.findViewById(R.id.swIsBlackListed)
            ivBlackWhitelist = view.findViewById(R.id.ivBlacklist)
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

        viewHolder.swIsBlackWhiteListed.setOnCheckedChangeListener(null)

        viewHolder.swIsBlackWhiteListed.isChecked = if (blacklistMode) {
            packageName.isBlackList
        } else {
            packageName.isWhiteList
        }

        viewHolder.swIsBlackWhiteListed.setOnCheckedChangeListener { _, isChecked ->
            val entity = DBUtils.getPackageName(packageName.pkg)
            if (entity != null) {
                if (blacklistMode) {
                    entity.isBlackList = isChecked
                    MyApplication.packageNames.put(entity)

                    DBUtils.getPackageName(packageName.pkg)
                        ?.let { viewHolder.swIsBlackWhiteListed.isChecked = it.isBlackList }
                } else {
                    entity.isWhiteList = isChecked
                    MyApplication.packageNames.put(entity)

                    DBUtils.getPackageName(packageName.pkg)
                        ?.let { viewHolder.swIsBlackWhiteListed.isChecked = it.isWhiteList }
                }

                MyApplication.executor.submit { notifyItemChanged(position) }
            }
        }

        val icon = AppIcon.compute(packageName.pkg)

        if (icon != null) {
            viewHolder.ivBlackWhitelist.setImageDrawable(icon)
        } else {
            viewHolder.ivBlackWhitelist.setImageResource(R.drawable.baseline_android_24)
        }
    }

    override fun getItemCount() = packageNames.size
}