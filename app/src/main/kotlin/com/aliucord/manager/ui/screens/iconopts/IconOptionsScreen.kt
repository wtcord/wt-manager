package com.aliucord.manager.ui.screens.iconopts

import android.net.Uri
import android.os.Build
import android.os.Parcelable
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import com.aliucord.manager.ui.components.*
import com.aliucord.manager.ui.screens.iconopts.components.*
import com.aliucord.manager.ui.screens.patchopts.PatchOptions
import com.aliucord.manager.ui.screens.patchopts.PatchOptionsScreen
import com.aliucord.manager.ui.util.ColorSaver
import com.aliucord.manager.ui.util.throttledState
import com.aliucord.manager.util.back
import dev.wintry.manager.R
import dev.zt64.compose.pipette.CircularColorPicker
import dev.zt64.compose.pipette.HsvColor
import kotlinx.parcelize.IgnoredOnParcel
import kotlinx.parcelize.Parcelize

@Parcelize
class IconOptionsScreen : Screen, Parcelable {
    @IgnoredOnParcel
    override val key = "IconOptions"

    @Composable
    override fun Content() {
        // Retrieves a global model owned by the navigator
        val navigator = LocalNavigator.currentOrThrow
        val modelScreen by remember {
            derivedStateOf {
                navigator.items.lastOrNull { it is PatchOptionsScreen }
                    ?: error("No PatchOptionsScreen in stack")
            }
        }
        val model = modelScreen.koinScreenModel<IconOptionsModel>() // Parameters are already injected in PatchOptionsScreen

        IconOptionsScreenContent(
            mode = model.mode,
            setMode = model::changeMode,
            selectedColor = model.selectedColor,
            setSelectedColor = model::changeSelectedColor,
            selectedImage = { model.selectedImage },
            setSelectedImage = model::changeSelectedImageUri,
            onBackPressed = { navigator.back(currentActivity = null) },
        )
    }
}

@Composable
fun IconOptionsScreenContent(
    mode: IconOptionsMode,
    setMode: (IconOptionsMode) -> Unit,
    selectedColor: HsvColor,
    setSelectedColor: (HsvColor) -> Unit,
    selectedImage: () -> ByteArray?,
    setSelectedImage: (Uri) -> Unit,
    onBackPressed: () -> Unit,
) {
    val isAdaptiveIconsAvailable = Build.VERSION.SDK_INT >= 26

    Scaffold(
        topBar = { IconOptionsAppBar() },
    ) { paddingValues ->
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
                .padding(horizontal = 20.dp, vertical = 20.dp)
        ) {
            IconPreview(
                mode = mode,
                selectedColor = selectedColor,
                selectedImage = selectedImage,
            )

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            RadioSelectorItem(
                name = stringResource(R.string.discord),
                description = stringResource(R.string.iconopts_variant_desc_discord),
                selected = mode == IconOptionsMode.Original,
                onClick = remember { { setMode(IconOptionsMode.Original) } },
            )
            RadioSelectorItem(
                name = stringResource(R.string.iconopts_variant_title_old_discord),
                description = stringResource(R.string.iconopts_variant_desc_old_discord),
                selected = mode == IconOptionsMode.OldDiscord,
                onClick = remember { { setMode(IconOptionsMode.OldDiscord) } },
            )
            RadioSelectorItem(
                name = stringResource(R.string.wintry),
                description = stringResource(R.string.iconopts_variant_desc_wintry),
                selected = mode == IconOptionsMode.Wintry,
                onClick = remember { { setMode(IconOptionsMode.Wintry) } },
            )
            RadioSelectorItem(
                name = stringResource(R.string.aliucord),
                description = stringResource(R.string.iconopts_variant_desc_aliucord),
                selected = mode == IconOptionsMode.Aliucord,
                onClick = remember { { setMode(IconOptionsMode.Aliucord) } },
            )
            RadioSelectorItem(
                name = stringResource(R.string.iconopts_variant_title_color),
                description = stringResource(R.string.iconopts_variant_desc_color),
                selected = mode == IconOptionsMode.CustomColor,
                onClick = remember { { setMode(IconOptionsMode.CustomColor) } },
            )
            if (isAdaptiveIconsAvailable) {
                RadioSelectorItem(
                    name = stringResource(R.string.iconopts_variant_title_image),
                    description = stringResource(R.string.iconopts_variant_desc_image),
                    selected = mode == IconOptionsMode.CustomImage,
                    onClick = remember { { setMode(IconOptionsMode.CustomImage) } },
                )
            }

            HorizontalDivider(Modifier.padding(vertical = 16.dp))

            AnimatedVisibility(
                visible = mode == IconOptionsMode.CustomColor,
                enter = fadeIn(tween(delayMillis = 250)),
                exit = fadeOut(tween(durationMillis = 200)),
            ) {
                CustomColorOptions(
                    color = selectedColor,
                    setColor = setSelectedColor,
                )
            }

            AnimatedVisibility(
                visible = mode == IconOptionsMode.CustomImage,
                enter = fadeIn(tween(delayMillis = 250)),
                exit = fadeOut(tween(durationMillis = 200)),
            ) {
                CustomImageOptions(
                    selectedImage = selectedImage,
                    setSelectedImage = setSelectedImage,
                )
            }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                FilledTonalButton(
                    onClick = onBackPressed,
                ) {
                    Text(stringResource(R.string.action_confirm))
                }
            }
        }
    }
}

@Composable
private fun IconPreview(
    mode: IconOptionsMode,
    selectedColor: HsvColor,
    selectedImage: () -> ByteArray?,
) {
    Label(
        name = stringResource(R.string.iconopts_icon_preview_title),
        description = stringResource(R.string.iconopts_icon_preview_desc),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 14.dp),
        ) {
            val drawable = if (mode == IconOptionsMode.CustomImage) {
                val image = selectedImage()

                if (image != null && Build.VERSION.SDK_INT >= 26) {
                    customIconDrawable(
                        foregroundIcon = image,
                    )
                } else {
                    discordIconDrawable(
                        backgroundColor = Color.Black,
                        oldLogo = false,
                        size = 72.dp,
                    )
                }
            } else {
                val color = when (mode) {
                    IconOptionsMode.Original -> PatchOptions.IconReplacement.BlurpleColor
                    IconOptionsMode.OldDiscord -> PatchOptions.IconReplacement.OldBlurpleColor
                    IconOptionsMode.Aliucord -> PatchOptions.IconReplacement.AliucordColor
                    IconOptionsMode.Wintry -> PatchOptions.IconReplacement.WintryColor
                    IconOptionsMode.CustomColor -> selectedColor.toColor()
                    IconOptionsMode.CustomImage -> error("unreachable")
                }
                val throttledColor by throttledState(value = color, throttleMs = 75)

                discordIconDrawable(
                    backgroundColor = throttledColor,
                    oldLogo = mode == IconOptionsMode.OldDiscord,
                    size = 72.dp,
                )
            }

            Drawable(
                drawable = drawable,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
            )
            Drawable(
                drawable = drawable,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape),
            )
            Drawable(
                drawable = drawable,
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            Drawable(
                drawable = drawable,
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape),
            )
        }
    }
}

@Composable
private fun CustomColorOptions(
    color: HsvColor,
    setColor: (HsvColor) -> Unit,
) {
    val color by rememberUpdatedState(color)

    // This color is separated from the live color and intentionally lags behind while the RGBTextField is being edited.
    // When this changes, then the text inside the RGBTextField is reset to the fully formatted color.
    // As such, this only happens when the color is changed via the other color pickers.
    var initialRGBFieldColor by rememberSaveable(stateSaver = ColorSaver) { mutableStateOf(color.toColor()) }

    Column(
        verticalArrangement = Arrangement.spacedBy(30.dp),
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        Label(
            name = stringResource(R.string.iconopts_colorpicker_title),
            description = stringResource(R.string.iconopts_colorpicker_desc),
            modifier = Modifier.fillMaxWidth(),
        ) {
            CircularColorPicker(
                hue = color.hue,
                saturation = color.saturation,
                value = color.value,
                onColorChange = { hue, saturation ->
                    color.copy(hue = hue, saturation = saturation).let {
                        setColor(it)
                        initialRGBFieldColor = it.toColor()
                    }
                },
                modifier = Modifier
                    .padding(top = 12.dp)
                    .size(260.dp)
                    .align(Alignment.CenterHorizontally)
            )
        }

        Label(
            name = stringResource(R.string.iconopts_lightness_title),
            description = stringResource(R.string.iconopts_lightness_desc),
        ) {
            InteractiveSlider(
                value = color.value,
                onValueChange = { value ->
                    color.copy(value = value).let {
                        setColor(it)
                        initialRGBFieldColor = it.toColor()
                    }
                },
                valueRange = 0f..1f,
                brush = Brush.horizontalGradient(
                    listOf(
                        Color.Black,
                        Color.hsv(color.hue, color.saturation, 1f)
                    ),
                ),
                thumbColor = color.toColor(),
            )
        }

        Label(
            name = stringResource(R.string.iconopts_hex_title),
            description = stringResource(R.string.iconopts_hex_desc),
        ) {
            RGBTextField(
                initialColor = initialRGBFieldColor,
                setColor = { setColor(HsvColor(it)) },
                modifier = Modifier.padding(top = 12.dp),
            )
        }
    }
}

@Composable
private fun CustomImageOptions(
    selectedImage: () -> ByteArray?,
    setSelectedImage: (Uri) -> Unit,
) {
    val pickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { it?.let(setSelectedImage) }
    )

    Column(
        verticalArrangement = Arrangement.spacedBy(15.dp),
        modifier = Modifier.padding(horizontal = 15.dp)
    ) {
        MainActionButton(
            text = stringResource(R.string.iconopts_btn_open_selection),
            icon = painterResource(R.drawable.ic_launch),
            onClick = { pickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
        )

        val uriHandler = LocalUriHandler.current
        MainActionButton(
            text = stringResource(R.string.iconopts_btn_open_example_image),
            icon = painterResource(R.drawable.ic_launch),
            onClick = { uriHandler.openUri("https://github.com/Aliucord/Aliucord/blob/fe8b17002ec1ee66ac3eeb2855c9ca2f2f307410/installer/android/app/src/main/assets/icon1.png") },
            colors = IconButtonDefaults.filledTonalIconButtonColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            ),
        )

        Label(
            name = stringResource(R.string.iconopts_image_preview_title),
            description = stringResource(R.string.iconopts_image_preview_desc),
            modifier = Modifier.padding(top = 20.dp),
        ) {
            Crossfade(
                targetState = selectedImage(),
                label = "Selected image preview fade",
                animationSpec = tween(durationMillis = 300, delayMillis = 250),
            ) { image ->
                if (image == null) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                            .aspectRatio(1.2f)
                            .clip(MaterialTheme.shapes.large)
                            .background(MaterialTheme.colorScheme.surfaceContainerLow)
                    ) {
                        Text(
                            text = stringResource(R.string.iconopts_image_preview_none),
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.alpha(.5f),
                        )
                    }
                } else {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .padding(top = 8.dp)
                            .fillMaxWidth()
                    ) {
                        var size by remember { mutableStateOf(Size.Zero) }
                        val dpSize = with(LocalDensity.current) { size.toDpSize() }

                        TransparencyGrid(
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .size(dpSize)
                        )

                        AsyncImage(
                            model = image,
                            contentDescription = null,
                            modifier = Modifier
                                .onSizeChanged { size = it.toSize() }
                        )
                    }
                }
            }
        }
    }
}
