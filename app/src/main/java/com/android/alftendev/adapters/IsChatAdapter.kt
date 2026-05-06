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
        val tvPackageName: MaterialTextView = view.findViewById(R.id.tvIsChatPkgName)
        val swIsChat: SwitchCompat = view.findViewById(R.id.swIsChat)
        val ivPackage: ImageView = view.findViewById(R.id.ivIsChat)
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

        viewHolder.swIsChat.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) return@setOnCheckedChangeListener

            packageName.isChat = isChecked

            MyApplication.executor.execute {
                val entity = DBUtils.getPackageName(packageName.pkg) ?: packageName
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