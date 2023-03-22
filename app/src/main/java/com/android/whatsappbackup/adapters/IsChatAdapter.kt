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

class IsChatAdapter(private val packageNames: List<PackageName>) :
    RecyclerView.Adapter<IsChatAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPackageName: MaterialTextView
        val swIsChat: SwitchCompat
        val ivPackage: ImageView

        init {
            tvPackageName = view.findViewById(R.id.tvIsChatPkgName)
            swIsChat = view.findViewById(R.id.swIsChat)
            ivPackage = view.findViewById(R.id.ivIsChat)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.custom_is_chat, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        val packageName = packageNames[position]

        viewHolder.tvPackageName.text = if (packageName.name.isNullOrBlank()) {
            packageName.pkg
        } else {
            packageName.name
        }

        viewHolder.swIsChat.isChecked = packageName.isChat

        viewHolder.swIsChat.setOnCheckedChangeListener { _, isChecked ->
            val entity = DBUtils.packageNameExists(packageName.pkg)
            if (entity != null) {
                entity.isChat = isChecked
                MyApplication.packageNames.put(entity)
            }
        }

        val icon = AppIcon.compute(packageName.pkg)

        if (icon != null) {
            viewHolder.ivPackage.setImageDrawable(icon)
        } else {
            viewHolder.ivPackage.setImageResource(R.drawable.baseline_android_24)
        }
    }

    override fun getItemCount() = packageNames.size
}
