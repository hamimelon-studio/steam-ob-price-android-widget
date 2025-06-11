package com.mike.steamob.release.ui.addwidget.mainflow

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.mike.steamob.release.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddWidgetIntroScreen(
    onProceed: (String) -> Unit,
    onSkip: () -> Unit
) {
    val keyword = remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalContext.current.getString(R.string.add_widget_title)) },
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
                text = LocalContext.current.getString(R.string.add_widget_message),
                style = MaterialTheme.typography.bodyLarge
            )

            // Search bar
            OutlinedTextField(
                value = keyword.value,
                onValueChange = { keyword.value = it },
                label = { Text(LocalContext.current.getString(R.string.add_widget_text_input_hint)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = { onProceed.invoke(keyword.value) }
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Search"
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            // Skip text with link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = LocalContext.current.getString(R.string.add_widget_skip_text1),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = LocalContext.current.getString(R.string.add_widget_skip_text_link),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.primary,
                        textDecoration = TextDecoration.Underline
                    ),
                    modifier = Modifier.clickable { onSkip() }
                )
                Text(
                    text = LocalContext.current.getString(R.string.add_widget_skip_text2),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

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
                    text = LocalContext.current.getString(R.string.add_widget_intro_option1),
                    style = MaterialTheme.typography.bodyLarge
                )
                Image(
                    painter = painterResource(id = R.drawable.option1),
                    contentDescription = "Option 1",
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = LocalContext.current.getString(R.string.add_widget_intro_option2),
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
                    Text(LocalContext.current.getString(R.string.add_widget_proceed_button))
                }
            }
        }
    }
}
