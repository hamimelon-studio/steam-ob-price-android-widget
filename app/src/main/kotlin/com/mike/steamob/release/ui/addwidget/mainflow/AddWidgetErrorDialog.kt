package com.mike.steamob.release.ui.addwidget.mainflow

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.mike.steamob.release.R

@Composable
fun AddWidgetErrorDialog(
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(300.dp)
            .padding(16.dp)
            .background(
                MaterialTheme.colorScheme.surface, shape = MaterialTheme.shapes.medium
            )
            .border(1.dp, MaterialTheme.colorScheme.onSurface)
            .padding(16.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = LocalContext.current.getString(R.string.add_widget_error_title),
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = LocalContext.current.getString(R.string.add_widget_error_message),
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(onClick = onClick) {
                Text(LocalContext.current.getString(R.string.okay))
            }
        }
    }
}