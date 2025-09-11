package com.android.alftendev.utils

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Context.CLIPBOARD_SERVICE
import android.graphics.Typeface
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.setMargins
import androidx.core.view.setPadding
import androidx.fragment.app.FragmentActivity
import com.android.alftendev.MyApplication
import com.android.alftendev.R
import com.android.alftendev.utils.UiUtils.showLoadingDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputLayout
import java.io.File


object DialogUtils {
    fun showExportedZipDialog(context: Context, zipPassword: String, zip: File) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(R.string.zip_password)
        builder.setMessage(context.getString(R.string.password_should_copy) + "\n${zipPassword}")
        builder.setPositiveButton(
            context.getString(R.string.copy_string)
        ) { _, _ ->
            val clipboard: ClipboardManager =
                context.getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                context.getString(R.string.zip_password),
                zipPassword
            )
            clipboard.setPrimaryClip(clip)

            if (zip.exists()) {
                zip.delete()
            }
        }
        builder.setOnCancelListener {
            if (zip.exists()) {
                zip.delete()
            }

            it.dismiss()
        }
        builder.create()
        builder.show()
    }

    fun askPasswordForZipDialog(context: Context, activity: FragmentActivity?) {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.getString(R.string.zip_file_password))

        val layout = LinearLayout(context)
        layout.setPadding(25)
        layout.orientation = LinearLayout.VERTICAL
        val layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        layout.layoutParams = layoutParams
        layout.gravity = Gravity.CENTER_HORIZONTAL

        val infoTextView = TextView(context)
        infoTextView.text = context.getString(R.string.enter_password_encrypt_zip_or_leave_empty)
        infoTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
        val infoLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        infoLayoutParams.setMargins(10)
        infoTextView.layoutParams = infoLayoutParams

        val textInputLayout = TextInputLayout(context)
        val textInputLayoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textInputLayout.layoutParams = textInputLayoutParams

        val input = EditText(context)
        input.hint = context.getString(R.string.enter_zip_password)
        input.inputType = InputType.TYPE_CLASS_TEXT
        input.typeface = Typeface.DEFAULT

        textInputLayout.addView(input)
        layout.addView(infoTextView)
        layout.addView(textInputLayout)

        builder.setView(layout)

        builder.setPositiveButton(context.getString(R.string.create_zip)) { dialog, _ ->
            val enteredPassword = input.text.toString()

            if (enteredPassword.isNotBlank() && !isPasswordValid(enteredPassword)) {
                activity?.runOnUiThread {
                    Toast.makeText(
                        context,
                        context.getString(R.string.password_invalid_alert), Toast.LENGTH_LONG
                    ).show()
                }
                return@setPositiveButton
            }

            dialog.dismiss()

            var loadingDialog: Dialog? = null

            activity?.runOnUiThread {
                loadingDialog = showLoadingDialog(context)
            }

            MyApplication.executor.submit {
                val result = ImportExport.exportDbZipEncrypted(context, enteredPassword)
                val zip = File(result.first)

                activity?.runOnUiThread {
                    loadingDialog?.dismiss()

                    showExportedZipDialog(context, result.second, zip)
                }
            }
        }

        builder.setNegativeButton(context.getString(R.string.cancel)) { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) {
            return false
        }
        if (!password.any { it.isDigit() }) {
            return false
        }
        return true
    }
}