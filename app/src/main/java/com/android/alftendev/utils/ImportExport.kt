package com.android.alftendev.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.android.alftendev.models.IJsonSerializable
import com.android.alftendev.utils.DBUtils.allNotifications
import com.android.alftendev.utils.DBUtils.allPackageNameLazy
import com.android.alftendev.utils.DBUtils.createNotificationFromJson
import com.android.alftendev.utils.DBUtils.createPackageNameFromJson
import io.objectbox.query.LazyList
import net.lingala.zip4j.ZipFile
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.model.enums.EncryptionMethod
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.security.SecureRandom
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Scanner
import java.util.TimeZone

object ImportExport {
    val LOGGER = CustomLog("noti-importer-export")
    //TODO
    private fun generateSecurePassword(length: Int = 16): String {
        val chars =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[]{}<>?"
        val secureRandom = SecureRandom()
        return (1..length)
            .map { chars[secureRandom.nextInt(chars.length)] }
            .joinToString("")
    }

    fun exportDbZipEncrypted(context: Context, enteredPassword: String): Pair<String, String> {
        val formatter = SimpleDateFormat("MM-dd-yyyy-HH-mm", Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        val timestamp = formatter.format(Date())

        val zipFileName = "$timestamp.zip"

        var notificationsJsonFile: File? = null
        var packageNameJsonFile: File? = null

        try {
            notificationsJsonFile = File(context.filesDir, "notifications-$timestamp.json")
            writeTmpJsonFile(notificationsJsonFile, allNotifications())

            packageNameJsonFile = File(context.filesDir, "packagename-$timestamp.json")
            writeTmpJsonFile(packageNameJsonFile, allPackageNameLazy())

            val password = enteredPassword.ifBlank {
                generateSecurePassword()
            }

            val zipFile = File(context.filesDir, zipFileName)
            ZipFile(zipFile, password.toCharArray()).use { zip ->
                val parameters = ZipParameters().apply {
                    isEncryptFiles = true
                    encryptionMethod = EncryptionMethod.AES
                }
                zip.addFile(notificationsJsonFile, parameters)
                zip.addFile(packageNameJsonFile, parameters)
            }

            shareZip(context, zipFile)

            return Pair(zipFile.absolutePath, password)
        } catch (e: Exception) {
            LOGGER.log("exportDbZip error: ${e.stackTraceToString()}")
            return Pair("error", e.stackTraceToString())
        } finally {
            notificationsJsonFile?.delete()
            packageNameJsonFile?.delete()
        }
    }

    fun importZipDecryptAndPrintStreaming(tmpZipFile: File, password: String): Boolean {
        var zipFile: ZipFile? = null
        var tempDir: File? = null
        var packageJsonFile: File? = null
        var notificationsJsonFile: File? = null

        try {
            LOGGER.log("importing zip")

            zipFile = ZipFile(tmpZipFile, password.toCharArray())
            tempDir = File(zipFile.file.parentFile, "temp_unzip")
            if (!tempDir.exists()) tempDir.mkdirs()
            zipFile.extractAll(tempDir.absolutePath)

            packageJsonFile = tempDir.listFiles()
                ?.firstOrNull { it.name.startsWith("package") && it.extension == "json" }
                ?: run {
                    LOGGER.log("File JSON packageJsonFile package not found")
                    return false
                }

            Scanner(packageJsonFile).use {
                while (it.hasNextLine()) {
                    val line = it.nextLine()

                    createPackageNamesFromJson(line)
                }
            }

            notificationsJsonFile = tempDir.listFiles()
                ?.firstOrNull { it.name.startsWith("notification") && it.extension == "json" }
                ?: run {
                    LOGGER.log("File JSON notificationsJsonFile package not found")
                    return false
                }

            Scanner(notificationsJsonFile).use {
                while (it.hasNextLine()) {
                    val line = it.nextLine()

                    createNotificationsFromJson(line)
                }
            }

            return true
        } catch (e: Exception) {
            LOGGER.log(e.stackTraceToString())
            return false
        } finally {
            tmpZipFile.delete()
            zipFile?.file?.delete()
            packageJsonFile?.delete()
            notificationsJsonFile?.delete()
            tempDir?.delete()
        }
    }

    fun createNotificationsFromJson(jsonString: String) {
        val json = JSONObject(jsonString)

        createNotificationFromJson(
            json.optString("packageName", ""),
            json.optString("title", ""),
            Date(json.optLong("time", 0)).time,
            json.optString("text", ""),
            json.optString("bigText", ""),
            json.optString("conversationTitle", ""),
            json.optString("infoText", ""),
            json.optString("peopleList", ""),
            json.optString("titleBig", ""),
            json.optBoolean("isDeleted", false)
        )
    }

    fun createPackageNamesFromJson(jsonString: String) {
        val json = JSONObject(jsonString)

        createPackageNameFromJson(
            json.optString("pkg", ""),
            json.optString("name", ""),
            json.optBoolean("isBlacklist", false),
            json.optBoolean("isWhiteList", false),
            json.optBoolean("isChat", false),
        )
    }

    fun shareZip(context: Context, file: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/zip"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        context.startActivity(Intent.createChooser(shareIntent, "Share zip via"))
    }

    fun <T : IJsonSerializable> writeTmpJsonFile(file: File, list: LazyList<T>) {
        FileWriter(file).use { writer ->
            var first = true
            for (element in list) {
                if (!first) writer.write("\n")

                var jsonString = element.toJson().toString()

                jsonString = jsonString
                    .replace("\u2028", "\\u2028")
                    .replace("\u2029", "\\u2029")

                writer.write(jsonString)
                first = false
            }
        }
    }

    fun copyUriToFile(context: Context, uri: Uri): File? {
        try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "zip")
            val outputStream = FileOutputStream(file)
            inputStream.copyTo(outputStream)
            inputStream.close()
            outputStream.close()
            return file
        } catch (e: Exception) {
            LOGGER.log("copyUriToFile " + e.stackTraceToString())
            return null
        }
    }
}
