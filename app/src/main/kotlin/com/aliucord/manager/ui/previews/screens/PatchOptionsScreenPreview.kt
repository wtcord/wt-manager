package com.aliucord.manager.ui.previews.screens

import android.content.res.Configuration
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.*
import com.aliucord.manager.ui.screens.patchopts.*
import com.aliucord.manager.ui.theme.ManagerTheme

// This preview has scrollable/interactable content that cannot be tested from an IDE preview

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun PatchOptionsScreenPreview(
    @PreviewParameter(PatchOptionsParametersProvider::class)
    parameters: PatchOptionsParameters,
) {
    ManagerTheme {
        PatchOptionsScreenContent(
            isUpdate = parameters.isUpdate,
            isDevMode = parameters.isDevMode,
            debuggable = parameters.debuggable,
            setDebuggable = {},
            oldLogo = parameters.oldLogo,
            selectedColor = parameters.selectedColor,
            selectedImage = { parameters.selectedImage },
            onOpenIconOptions = {},
            appName = parameters.appName,
            appNameIsError = parameters.appNameIsError,
            setAppName = {},
            packageName = parameters.packageName,
            packageNameState = parameters.packageNameState,
            setPackageName = {},
            versionPreference = parameters.versionPreference,
            setVersionPreference = {},
            customVersionCode = parameters.customVersionCode,
            customVersionCodeIsError = parameters.customVersionCodeIsError,
            setCustomVersionCode = {},
            isConfigValid = parameters.isConfigValid,
            onInstall = {},
        )
    }
}

@Suppress("ArrayInDataClass")
private data class PatchOptionsParameters(
    val isUpdate: Boolean,
    val isDevMode: Boolean,
    val debuggable: Boolean,
    val oldLogo: Boolean,
    val selectedColor: Color?,
    val selectedImage: ByteArray?,
    val appName: String,
    val appNameIsError: Boolean,
    val packageName: String,
    val packageNameState: PackageNameState,
    val versionPreference: VersionPreference,
    val customVersionCode: String,
    val customVersionCodeIsError: Boolean,
    val isConfigValid: Boolean,
)

private class PatchOptionsParametersProvider : PreviewParameterProvider<PatchOptionsParameters> {
    override val values = sequenceOf(
        // Default initial install
        PatchOptionsParameters(
            isUpdate = false,
            isDevMode = false,
            debuggable = false,
            oldLogo = false,
            selectedColor = PatchOptions.IconReplacement.AliucordColor,
            selectedImage = null,
            appName = PatchOptions.Default.appName,
            appNameIsError = false,
            packageName = PatchOptions.Default.packageName,
            packageNameState = PackageNameState.Ok,
            versionPreference = VersionPreference.Stable,
            customVersionCode = "",
            customVersionCodeIsError = false,
            isConfigValid = true,
        ),
        PatchOptionsParameters(
            isUpdate = true,
            isDevMode = false,
            debuggable = false,
            oldLogo = false,
            selectedColor = null,
            selectedImage = null,
            appName = "an invalid app name.",
            appNameIsError = true,
            packageName = "a b",
            packageNameState = PackageNameState.Invalid,
            versionPreference = VersionPreference.Stable,
            customVersionCode = "",
            customVersionCodeIsError = false,
            isConfigValid = false,
        ),
        PatchOptionsParameters(
            isUpdate = false,
            isDevMode = true,
            debuggable = true,
            oldLogo = false,
            selectedColor = Color.Magenta,
            selectedImage = null,
            appName = PatchOptions.Default.appName,
            appNameIsError = false,
            packageName = PatchOptions.Default.packageName,
            packageNameState = PackageNameState.Taken,
            versionPreference = VersionPreference.Stable,
            customVersionCode = "",
            customVersionCodeIsError = false,
            isConfigValid = true,
        ),
    )
}
