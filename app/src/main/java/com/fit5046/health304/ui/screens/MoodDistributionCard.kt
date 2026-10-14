package com.fit5046.health304.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit5046.health304.data.MoodEntry

/** P4: dynamic bar chart based on P3's account-scoped Room mood records. */
@Composable
fun MoodDistributionCard(entries: List<MoodEntry>) {
    val moods = listOf("Low", "Stressed", "Neutral", "Good", "Very Good")
    val counts = moods.map { name -> entries.count { it.mood == name } }
    val maxCount = (counts.maxOrNull() ?: 0).coerceAtLeast(1)
    val barColor = MaterialTheme.colorScheme.primary
    val axisColor = MaterialTheme.colorScheme.outline

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Mood Distribution", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(8.dp))
            Text("Number of records by mood", style = MaterialTheme.typography.bodySmall)
            Spacer(Modifier.height(20.dp))

            if (entries.isEmpty()) {
                Text("No mood records available.")
            } else {
                Row(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    Column(
                        modifier = Modifier.width(30.dp).fillMaxHeight(),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$maxCount", fontSize = 11.sp)
                        Text("0", fontSize = 11.sp)
                    }
                    Canvas(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        val chartHeight = size.height * 0.9f
                        val slotWidth = size.width / moods.size
                        val barWidth = slotWidth * 0.55f
                        drawLine(
                            color = axisColor,
                            start = Offset(0f, chartHeight),
                            end = Offset(size.width, chartHeight),
                            strokeWidth = 2f
                        )
                        counts.forEachIndexed { index, count ->
                            val barHeight = chartHeight * count / maxCount
                            val x = index * slotWidth + (slotWidth - barWidth) / 2f
                            drawRect(
                                color = barColor,
                                topLeft = Offset(x, chartHeight - barHeight),
                                size = Size(barWidth, barHeight)
                            )
                        }
                    }
                }
                Row(modifier = Modifier.fillMaxWidth().padding(start = 30.dp)) {
                    moods.forEach { mood ->
                        Text(
                            text = mood,
                            modifier = Modifier.weight(1f),
                            fontSize = 9.sp,
                            lineHeight = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
                Text("Y-axis: Record count", style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
