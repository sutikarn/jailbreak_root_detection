package com.anish.trust_fall.externalstorage

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.os.Build

object ExternalStorageCheck {
    /**
     * Checks if the application is installed on the SD card.
     *
     * @return `true` if the application is installed on the sd card
     */
    @SuppressLint("ObsoleteSdkInt", "SdCardPath")
    fun isOnExternalStorage(context: Context?): Boolean {
        // context อาจเป็น null
        val ctx = context ?: return false

        // API 8+ : เช็กจาก ApplicationInfo.flags (ทำแบบปลอดภัยต่อ null)
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.ECLAIR_MR1) {
            val pm = ctx.packageManager
            val pi = try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    pm.getPackageInfo(
                        ctx.packageName,
                        PackageManager.PackageInfoFlags.of(0)
                    )
                } else {
                    @Suppress("DEPRECATION")
                    pm.getPackageInfo(ctx.packageName, 0)
                }
            } catch (_: PackageManager.NameNotFoundException) {
                null
            } catch (_: Throwable) {
                null
            }

            val ai: ApplicationInfo? = pi?.applicationInfo
            val flags = ai?.flags ?: 0
            if ((flags and ApplicationInfo.FLAG_EXTERNAL_STORAGE) == ApplicationInfo.FLAG_EXTERNAL_STORAGE) {
                return true
            }
        }

        // fallback : เช็กจาก path ของ filesDir (กัน null และ exception)
        return try {
            val filesDirPath = ctx.filesDir?.absolutePath ?: return false
            when {
                filesDirPath.startsWith("/data/") -> false
                filesDirPath.contains("/mnt/") || filesDirPath.contains("/sdcard/") -> true
                else -> false
            }
        } catch (_: Throwable) {
            false
        }
    }
}
