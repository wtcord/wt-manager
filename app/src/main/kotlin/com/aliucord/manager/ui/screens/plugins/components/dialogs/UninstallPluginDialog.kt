package com.aliucord.manager.ui.screens.plugins.components.dialogs

import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.wintry.manager.R

@Composable
fun UninstallPluginDialog(
    pluginName: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                painter = painterResource(R.drawable.ic_delete_forever),
                contentDescription = null,
                modifier = Modifier.size(32.dp),
            )
        },
        title = {
            Text(stringResource(R.string.plugins_delete_plugin, pluginName))
        },
        text = {
            Text(
                text = stringResource(R.string.plugins_delete_plugin_body, pluginName),
                textAlign = TextAlign.Center,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
            ) {
                Text(stringResource(R.string.action_confirm))
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                Text(stringResource(R.string.action_cancel))
            }
        }
    )
}
