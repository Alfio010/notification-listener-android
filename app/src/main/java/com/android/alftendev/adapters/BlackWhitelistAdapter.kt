package com.android.alftendev.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.widget.SwitchCompat
import androidx.recyclerview.widget.RecyclerView
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.models.PackageName
import com.android.alftendev.utils.DBUtils
import com.android.alftendev.utils.computables.AppIcon
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
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
        val llCustomBlacklistCard: LinearLayout

        init {
            tvNamePackage = view.findViewById(R.id.tvPackageName)
            swIsBlackWhiteListed = view.findViewById(R.id.swIsBlackListed)
            ivBlackWhitelist = view.findViewById(R.id.ivBlacklist)
            llCustomBlacklistCard = view.findViewById(R.id.llCustomBlacklistCard)
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

        val showSettingDialog = {
            val customAlertDialogView = LayoutInflater.from(viewHolder.itemView.context)
                .inflate(R.layout.custom_packagecard_dialog, null, false)

            val chatSwitch = customAlertDialogView.findViewById<MaterialSwitch>(R.id.packageDialogIsChat)

            val entity = DBUtils.getPackageName(packageName.pkg)

            if (entity != null) {
                chatSwitch.isChecked = entity.isChat
            }

            chatSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                if (entity != null) {
                    entity.isChat = isChecked
                    MyApplication.packageNames.put(entity)
                }
            }

            val builder = MaterialAlertDialogBuilder(viewHolder.itemView.context)
            builder.setTitle(viewHolder.itemView.context.getString(R.string.settings))
            builder.setView(customAlertDialogView)
            builder.setIcon(R.mipmap.ic_launcher)
            builder.setNeutralButton(R.string.back) { _, _ -> }
            builder.setOnCancelListener { it.dismiss() }
            builder.create()
            builder.show()
        }

        viewHolder.llCustomBlacklistCard.setOnClickListener {
            showSettingDialog()
        }

        viewHolder.ivBlackWhitelist.setOnClickListener {
            showSettingDialog()
        }
    }

    override fun getItemCount() = packageNames.size
}