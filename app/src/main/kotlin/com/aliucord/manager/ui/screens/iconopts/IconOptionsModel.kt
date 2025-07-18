package com.aliucord.manager.ui.screens.iconopts

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.aliucord.manager.ui.screens.patchopts.PatchOptions.IconReplacement
import com.aliucord.manager.util.*
import dev.wintry.manager.BuildConfig
import dev.wintry.manager.R
import dev.zt64.compose.pipette.HsvColor
import java.io.IOException

class IconOptionsModel(
    prefilledOptions: IconReplacement,
    private val application: Application,
) : ScreenModel {
    // ---------- Icon patching mode ---------- //
    var mode by mutableStateOf(IconOptionsMode.Original)
        private set

    fun changeMode(mode: IconOptionsMode) {
        this.mode = mode
    }

    // ---------- Replacement color ---------- //
    var selectedColor by mutableStateOf(HsvColor(IconReplacement.AliucordColor))
        private set

    fun changeSelectedColor(color: HsvColor) {
        selectedColor = color
    }

    private fun initSelectedColor(color: Color) {
        selectedColor = HsvColor(color)
    }

    // ---------- Replacement color ---------- //
    var selectedImage by mutableStateOf<ByteArray?>(null)

    fun changeSelectedImageUri(uri: Uri) = screenModelScope.launchIO {
        try {
            // Check file size first
            val query = application.contentResolver.query(uri, null, null, null, null)
                ?: throw IOException("Failed to query selected image uri")

            val size = query.use { cursor ->
                cursor.moveToFirst()
                cursor.getLong(cursor.getColumnIndexOrThrow(OpenableColumns.SIZE))
            }

            if (size > 1024 * 256) { // 256KiB
                mainThread { application.showToast(R.string.iconopts_failed_image_too_big) }
                return@launchIO
            }

            // Read file bytes
            val bytes = application.contentResolver
                .openInputStream(uri)
                ?.use { it.readBytes() }
                ?: throw IOException("Failed to open input stream")

            mainThread { selectedImage = bytes }
        } catch (t: Throwable) {
            Log.w(BuildConfig.TAG, "Failed to open selected foreground replacement image", t)
            mainThread { application.showToast(R.string.iconopts_failed_image) }
        }
    }

    // ---------- Other ---------- //
    init {
        when (prefilledOptions) {
            is IconReplacement.CustomColor if prefilledOptions.color == IconReplacement.WintryColor -> {
                changeMode(IconOptionsMode.Wintry)
                initSelectedColor(IconReplacement.WintryColor)
            }

            is IconReplacement.CustomColor if prefilledOptions.color == IconReplacement.AliucordColor -> {
                changeMode(IconOptionsMode.Aliucord)
                initSelectedColor(IconReplacement.AliucordColor)
            }


            is IconReplacement.CustomColor -> {
                changeMode(IconOptionsMode.CustomColor)
                initSelectedColor(prefilledOptions.color)
            }

            is IconReplacement.CustomImage -> {
                changeMode(IconOptionsMode.CustomImage)
                selectedImage = prefilledOptions.imageBytes
            }

            IconReplacement.Original -> changeMode(IconOptionsMode.Original)
            IconReplacement.OldDiscord -> changeMode(IconOptionsMode.OldDiscord)
        }
    }

    fun generateConfig(): IconReplacement? {
        return when (mode) {
            IconOptionsMode.Original -> IconReplacement.Original
            IconOptionsMode.OldDiscord -> IconReplacement.OldDiscord
            IconOptionsMode.Wintry -> IconReplacement.CustomColor(IconReplacement.WintryColor)
            IconOptionsMode.Aliucord -> IconReplacement.CustomColor(IconReplacement.AliucordColor)
            IconOptionsMode.CustomColor -> IconReplacement.CustomColor(color = selectedColor.toColor())
            IconOptionsMode.CustomImage -> IconReplacement.CustomImage(
                imageBytes = selectedImage ?: return null
            )
        }
    }
}
