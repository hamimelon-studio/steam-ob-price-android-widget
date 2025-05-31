package com.mikeapp.steamob.ui.about

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mikeapp.steamob.R
import org.koin.androidx.compose.koinViewModel
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

@Composable
fun AboutScreen(navController: NavController, innerPadding: PaddingValues) {
    val viewModel: AboutViewModel = koinViewModel()
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Header section with tech accent
        Box(modifier = Modifier.fillMaxWidth()) {
//            Canvas(modifier = Modifier.matchParentSize()) {
//                drawLine(
//                    color = primaryColor.copy(alpha = 0.5f),
//                    start = Offset(0f, size.height - 2.dp.toPx()),
//                    end = Offset(size.width * 0.7f, size.height - 2.dp.toPx()),
//                    strokeWidth = 2.dp.toPx()
//                )
//            }
            Text(
                text = LocalContext.current.getString(R.string.about_app_name),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                ),
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Links section
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Author button
            TechButton(
                onClick = {
                    val url = "https://github.com/hongwei-bai"
                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                    navController.navigate("github/$encodedUrl/0")
                },
                icon = null,
                text = "${LocalContext.current.getString(R.string.about_author)}: bhw8412@gmail.com"
            )

            // Github button
            TechButton(
                onClick = {
                    val url = "https://github.com/hamimelon-studio/steam-ob-price-android-widget"
                    val encodedUrl = URLEncoder.encode(url, StandardCharsets.UTF_8.toString())
                    navController.navigate("github/$encodedUrl/2400")
                },
                icon = null,
                text = "Github"
            )

            // Feedback button
            TechButton(
                onClick = {
                    val newIssueUrl = "https://github.com/hamimelon-studio/steam-ob-price-android-widget/issues/new"
                    viewModel.openUrlInBrowser(newIssueUrl)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_open_in_new_24),
                        contentDescription = "Open in browser",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = LocalContext.current.getString(R.string.feedback)
            )

            // Report Issue button
            TechButton(
                onClick = {
                    val newIssueUrl = "https://github.com/hamimelon-studio/steam-ob-price-android-widget/issues/new"
                    viewModel.openUrlInBrowser(newIssueUrl)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_open_in_new_24),
                        contentDescription = "Open in browser",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                text = LocalContext.current.getString(R.string.report_issue)
            )
        }
    }
}

@Composable
private fun TechButton(
    onClick: () -> Unit,
    icon: @Composable (() -> Unit)?,
    text: String,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.surface

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = primaryColor.copy(alpha = 0.3f),
                shape = RoundedCornerShape(4.dp)
            ),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = surfaceColor.copy(alpha = 0.7f)
        ),
        onClick = onClick
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left accent line
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(24.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    drawRect(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.8f),
                                primaryColor.copy(alpha = 0.2f)
                            )
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )

            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                icon()
            }
        }
    }
}