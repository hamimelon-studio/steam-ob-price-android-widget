package com.mike.steamob.ui.commonui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.mike.steamob.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetIntroScreen(
    isFromHomeScreen: Boolean,
    onProceed: (String) -> Unit
) {
    val keyword = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("How to Add New Widget?") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors()
            )
        },
        bottomBar = {

        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp)
                .padding(bottom = 72.dp)
                .verticalScroll(scrollState)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Guide text
            Text(
                text = "Follow the steps below to search and proceed.",
                style = MaterialTheme.typography.bodyLarge
            )

            // Warning section
            if (!isFromHomeScreen) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.padding(top = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Warning",
                        tint = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Note: Widgets can only be added from the Home Screen, not from inside the app.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.error)
                    )
                }
                Text(
                    text = "However, you can still follow this guide to find your game and copy the App ID to use when adding the widget from the Home Screen.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(start = 32.dp, top = 4.dp) // indent under icon
                )
            }

            // Search bar
            OutlinedTextField(
                value = keyword.value,
                onValueChange = { keyword.value = it },
                label = { Text("Search game name keyword") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Image area with option1 and option2
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outline,
                        shape = RoundedCornerShape(8.dp)
                    )
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Option 1 - You can either grab this number and fill by yourself",
                    style = MaterialTheme.typography.bodyLarge
                )
                Image(
                    painter = painterResource(id = R.drawable.option1),
                    contentDescription = "Option 1",
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Option 2 - Or click into the tile and auto fill the app id.",
                    style = MaterialTheme.typography.bodyLarge
                )
                Image(
                    painter = painterResource(id = R.drawable.option2),
                    contentDescription = "Option 2",
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = { onProceed.invoke(keyword.value) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("Understand & Proceed")
                }
            }
        }
    }
}
