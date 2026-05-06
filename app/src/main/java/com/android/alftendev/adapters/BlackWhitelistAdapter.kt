package com.android.alftendev.adapters

import android.os.Handler
import android.os.Looper
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
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.materialswitch.MaterialSwitch
import com.google.android.material.textview.MaterialTextView

// TODO specificGraph in alert
class BlackWhitelistAdapter(
    private val packageNames: List<PackageName>,
    private val blacklistMode: Boolean
) : RecyclerView.Adapter<BlackWhitelistAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNamePackage: MaterialTextView = view.findViewById(R.id.tvPackageName)
        val swIsBlackWhiteListed: SwitchCompat = view.findViewById(R.id.swIsBlackListed)
        val ivBlackWhitelist: ImageView = view.findViewById(R.id.ivBlacklist)
        val llCustomBlacklistCard: LinearLayout = view.findViewById(R.id.llCustomBlacklistCard)
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

        viewHolder.swIsBlackWhiteListed.setOnCheckedChangeListener { buttonView, isChecked ->
            if (!buttonView.isPressed) return@setOnCheckedChangeListener

            if (blacklistMode) {
                packageName.isBlackList = isChecked
            } else {
                packageName.isWhiteList = isChecked
            }

            MyApplication.executor.execute {
                val entity = DBUtils.getPackageName(packageName.pkg) ?: packageName

                if (blacklistMode) {
                    entity.isBlackList = isChecked
                } else {
                    entity.isWhiteList = isChecked
                }

                MyApplication.packageNames.put(entity)
            }
        }

        val icon = AppIcon.compute(packageName.pkg)
        if (icon != null) {
            viewHolder.ivBlackWhitelist.setImageDrawable(icon)
        } else {
            viewHolder.ivBlackWhitelist.setImageResource(R.drawable.baseline_android_24)
        }

        val showSettingDialog = {
            val context = viewHolder.itemView.context
            val customAlertDialogView = LayoutInflater.from(context)
                .inflate(R.layout.custom_packagecard_dialog, null, false)

            val chatSwitch =
                customAlertDialogView.findViewById<MaterialSwitch>(R.id.packageDialogIsChat)

            MyApplication.executor.execute {
                val entity = DBUtils.getPackageName(packageName.pkg) ?: packageName

                // Main thread
                Handler(Looper.getMainLooper()).post {
                    chatSwitch.isChecked = entity.isChat

                    chatSwitch.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (!buttonView.isPressed) return@setOnCheckedChangeListener

                        packageName.isChat = isChecked
                        entity.isChat = isChecked

                        MyApplication.executor.execute {
                            MyApplication.packageNames.put(entity)
                        }
                    }
                }
            }

            MaterialAlertDialogBuilder(context)
                .setTitle(context.getString(R.string.settings))
                .setView(customAlertDialogView)
                .setIcon(R.mipmap.ic_launcher)
                .setNeutralButton(R.string.back) { _, _ -> }
                .setOnCancelListener { it.dismiss() }
                .show()
        }

        viewHolder.llCustomBlacklistCard.setOnClickListener { showSettingDialog() }
        viewHolder.ivBlackWhitelist.setOnClickListener { showSettingDialog() }
    }

    override fun getItemCount() = packageNames.size
}