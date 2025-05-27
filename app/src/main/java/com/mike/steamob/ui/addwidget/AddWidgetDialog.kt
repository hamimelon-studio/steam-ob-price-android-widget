package com.mike.steamob.ui.addwidget

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mike.steamob.R

@Composable
fun AddWidgetDialog(
    appId0: String,
    threshold0: Float,
    onDismiss: () -> Unit,
    onConfirm: (String, Float) -> Unit
) {
    var appId by remember { mutableStateOf(appId0) }
    var priceThreshold by remember { mutableStateOf(threshold0.toString()) }

    AlertDialog(
        containerColor = MaterialTheme.colorScheme.secondaryContainer,
        onDismissRequest = { onDismiss() },
        title = {
            Text(
                text = LocalContext.current.getString(R.string.input_appid),
//                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.titleSmall
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = appId,
                    onValueChange = { appId = it },
                    label = {
                        Text(
                            text = LocalContext.current.getString(R.string.input_enter_appid),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                OutlinedTextField(
                    value = priceThreshold,
                    onValueChange = { priceThreshold = it },
                    label = {
                        Text(
                            text = LocalContext.current.getString(R.string.input_notify_me),
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val thresholdValue = priceThreshold.toFloatOrNull() ?: 0f
                onConfirm(appId, thresholdValue)
            }) {
                Text(
                    LocalContext.current.getString(R.string.okay)
                )
            }
        },
        dismissButton = {
            Button(onClick = { onDismiss() }) {
                Text(
                    LocalContext.current.getString(R.string.cancel)
                )
            }
        }
    )
}