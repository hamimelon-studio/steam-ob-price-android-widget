package com.mike.steamob.ui.home

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun GameCard(
    backgroundColor: Color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f)
) {
    val cardShape = RoundedCornerShape(4.dp)

    Card(
        modifier = Modifier
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSecondary.copy(alpha = 0.3f),
                shape = cardShape
            ),
        shape = cardShape,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            val primaryColor = MaterialTheme.colorScheme.primary

            Canvas(
                modifier = Modifier
                    .width(2.dp)
                    .fillMaxHeight()
                    .align(Alignment.CenterStart)
            ) {
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
    }
}
