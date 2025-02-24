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
class BlacklistAdapter(private val packageNames: List<PackageName>) :
    RecyclerView.Adapter<BlacklistAdapter.ViewHolder>() {
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

        viewHolder.swIsBlackListed.setOnCheckedChangeListener(null)

        viewHolder.swIsBlackListed.isChecked = packageName.isBlackList

        viewHolder.swIsBlackListed.setOnCheckedChangeListener { _, isChecked ->
            val entity = DBUtils.getPackageName(packageName.pkg)
            if (entity != null) {
                entity.isBlackList = isChecked
                MyApplication.packageNames.put(entity)

                DBUtils.getPackageName(packageName.pkg)
                    ?.let { viewHolder.swIsBlackListed.isChecked = it.isBlackList }

                MyApplication.executor.submit { notifyItemChanged(position) }
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