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

        viewHolder.swIsChat.setOnCheckedChangeListener(null)

        viewHolder.swIsChat.isChecked = packageName.isChat

        viewHolder.swIsChat.setOnCheckedChangeListener { _, isChecked ->
            val entity = DBUtils.getPackageName(packageName.pkg)
            if (entity != null) {
                entity.isChat = isChecked
                MyApplication.packageNames.put(entity)

                DBUtils.getPackageName(packageName.pkg)
                    ?.let { viewHolder.swIsChat.isChecked = it.isChat }

                MyApplication.executor.submit { notifyItemChanged(position) }
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
