package com.aliucord.manager.ui.screens.settings

import android.app.Application
import androidx.compose.runtime.*
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.aliucord.manager.manager.PathManager
import com.aliucord.manager.manager.PreferencesManager
import com.aliucord.manager.ui.theme.Theme
import com.aliucord.manager.util.*
import dev.wintry.manager.BuildConfig
import dev.wintry.manager.R

class SettingsModel(
    private val application: Application,
    private val paths: PathManager,
    val preferences: PreferencesManager,
) : ScreenModel {
    val installInfo = InstallInfo

    var patchedApkExists by mutableStateOf(paths.patchedApk().exists())
        private set

    var showThemeDialog by mutableStateOf(false)
        private set

    fun showThemeDialog() {
        showThemeDialog = true
    }

    fun hideThemeDialog() {
        showThemeDialog = false
    }

    fun setTheme(theme: Theme) {
        preferences.theme = theme
    }

    fun setKeepPatchedApks(value: Boolean) {
        preferences.keepPatchedApks = value
    }

    fun clearCache() = screenModelScope.launchIO {
        paths.clearCache()

        mainThread {
            patchedApkExists = false
            application.showToast(R.string.action_cleared_cache)
        }
    }

    fun copyInstallInfo() {
        application.copyToClipboard(installInfo)
        application.showToast(R.string.action_copied)
    }

    fun shareApk() {
        // TODO(Wintry): Implement this
        application.showToast(R.string.settings_export_apk_not_implemented)
        // val file = paths.patchingWorkingDir().resolve("patched.apk")
        // val fileUri = FileProvider.getUriForFile(
        //     /* context = */ application,
        //     /* authority = */ "${BuildConfig.APPLICATION_ID}.provider",
        //     /* file = */ file,
        //     /* displayName = */ "Aliucord.apk",
        // )
        //
        // val intent = Intent(Intent.ACTION_SEND)
        //     .setType("application/vnd.android.package-archive")
        //     .putExtra(Intent.EXTRA_STREAM, fileUri)
        //     .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        //     .let {
        //         Intent.createChooser(
        //             /* target = */ it,
        //             /* title = */ application.getString(R.string.log_action_export_apk),
        //         )
        //     }
        //     .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        //
        // try {
        //     application.startActivity(intent)
        // } catch (t: Throwable) {
        //     Log.w(BuildConfig.TAG, "Failed to share APK", t)
        //     application.showToast(R.string.status_failed)
        // }
    }

    companion object {
        @Suppress("KotlinConstantConditions")
        private val InstallInfo: String = """
            ${BuildConfig.APPLICATION_NAME}
            Version: ${BuildConfig.VERSION_NAME}
            Version Code: ${BuildConfig.VERSION_CODE}
            Release: ${if (BuildConfig.RELEASE) "Yes" else "No"}
            Git Branch: ${BuildConfig.GIT_BRANCH}
            Git Commit: ${BuildConfig.GIT_COMMIT}
            Git Changes: ${if (BuildConfig.GIT_LOCAL_CHANGES) "Yes" else "No"}
        """.trimIndent()
    }
}
