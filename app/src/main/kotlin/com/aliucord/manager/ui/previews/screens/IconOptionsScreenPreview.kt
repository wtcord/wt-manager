package com.aliucord.manager.ui.previews.screens

import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.*
import androidx.core.graphics.drawable.toBitmap
import com.aliucord.manager.ui.screens.iconopts.IconOptionsMode
import com.aliucord.manager.ui.screens.iconopts.IconOptionsScreenContent
import com.aliucord.manager.ui.screens.patchopts.PatchOptions.IconReplacement
import com.aliucord.manager.ui.theme.ManagerTheme
import dev.wintry.manager.R
import dev.zt64.compose.pipette.HsvColor
import java.io.ByteArrayOutputStream

// This preview has scrollable/interactable content that cannot be properly tested from an IDE preview

@Composable
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_NO)
private fun IconOptionsScreenPreview(
    @PreviewParameter(IconOptionsParametersProvider::class)
    parameters: IconOptionsParameters,
) {
    var mode by remember { mutableStateOf(parameters.mode) }
    var color by remember { mutableStateOf(HsvColor(parameters.selectedColor)) }

    DisposableEffect(parameters) {
        mode = parameters.mode
        color = HsvColor(parameters.selectedColor)

        onDispose {}
    }

    val image = parameters.selectedImage()
    ManagerTheme {
        IconOptionsScreenContent(
            mode = mode,
            setMode = { mode = it },
            selectedColor = color,
            setSelectedColor = { color = it },
            selectedImage = { image },
            setSelectedImage = {},
            onBackPressed = {},
        )
    }
}

private data class IconOptionsParameters(
    val mode: IconOptionsMode,
    val selectedColor: Color,
    val selectedImage: @Composable () -> ByteArray?,
)

private class IconOptionsParametersProvider : PreviewParameterProvider<IconOptionsParameters> {
    override val values = sequenceOf(
        IconOptionsParameters(
            mode = IconOptionsMode.Original,
            selectedColor = IconReplacement.AliucordColor,
            selectedImage = { null },
        ),
        IconOptionsParameters(
            mode = IconOptionsMode.OldDiscord,
            selectedColor = Color.Black,
            selectedImage = { null },
        ),
        IconOptionsParameters(
            mode = IconOptionsMode.CustomColor,
            selectedColor = Color(0xFFFFB6C1),
            selectedImage = { null },
        ),
        IconOptionsParameters(
            mode = IconOptionsMode.CustomImage,
            selectedColor = Color.Black,
            selectedImage = { null },
        ),
        IconOptionsParameters(
            mode = IconOptionsMode.CustomImage,
            selectedColor = Color.Black,
            selectedImage = {
                val context = LocalContext.current
                remember(context) { getMipmapBytes(context, R.mipmap.ic_launcher) }
            },
        ),
    )

    private fun getMipmapBytes(context: Context, @DrawableRes id: Int): ByteArray {
        val drawable = AppCompatResources.getDrawable(context, id)!!
        val bitmap = drawable.toBitmap()
        return ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, it)
            it.toByteArray()
        }
    }
}
