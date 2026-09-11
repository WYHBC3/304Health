package com.fit5046.health304.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fit5046.health304.ui.theme._304HealthTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DataVisualizationScreen(
    moodEntries: List<MoodDiaryEntry> = emptyList(),
    onBackClick: () -> Unit = {}
) {

    val recentEntries =
        moodEntries
            .sortedBy {
                it.dateMillis
            }
            .takeLast(7)

    val moodScores =
        recentEntries.map {
            moodToScore(
                it.mood
            )
        }

    val averageMood =
        if (
            moodScores.isEmpty()
        ) {

            0.0

        } else {

            moodScores.average()
        }

    val averageMoodLabel =
        moodLabelFromAverage(
            averageMood
        )

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
    ) {

        TextButton(
            onClick =
                onBackClick
        ) {

            Text("← Back")
        }

        Text(
            text =
                "Health Trends",

            style =
                MaterialTheme
                    .typography
                    .headlineMedium
        )

        Spacer(
            modifier =
                Modifier.height(4.dp)
        )

        Text(
            text =
                "Your recent wellness overview",

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )

        Spacer(
            modifier =
                Modifier.height(20.dp)
        )

        MoodSummaryCard(
            averageMood =
                averageMood,

            moodLabel =
                averageMoodLabel,

            hasData =
                moodScores.isNotEmpty()
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        MoodTrendCard(
            entries =
                recentEntries
        )

        Spacer(
            modifier =
                Modifier.height(16.dp)
        )

        HeartRateCard()
    }
}

@Composable
private fun MoodSummaryCard(
    averageMood: Double,
    moodLabel: String,
    hasData: Boolean
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {

        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(20.dp),

            verticalAlignment =
                Alignment.CenterVertically,

            horizontalArrangement =
                Arrangement
                    .SpaceBetween
        ) {

            Column {

                Text(
                    text =
                        "Average Mood",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                Spacer(
                    modifier =
                        Modifier.height(6.dp)
                )

                Text(
                    text =
                        if (hasData) {
                            moodLabel
                        } else {
                            "No data"
                        },

                    fontSize =
                        22.sp,

                    fontWeight =
                        FontWeight.Bold
                )

                Text(
                    text =
                        if (hasData) {

                            String.format(
                                Locale.getDefault(),
                                "%.1f / 5",
                                averageMood
                            )

                        } else {

                            "Add a mood record"
                        },

                    style =
                        MaterialTheme
                            .typography
                            .bodySmall
                )
            }

            Text(
                text =
                    if (hasData) {
                        moodEmoji(
                            averageMood
                        )
                    } else {
                        "—"
                    },

                fontSize =
                    44.sp
            )
        }
    }
}

@Composable
private fun MoodTrendCard(
    entries: List<MoodDiaryEntry>
) {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            Text(
                text =
                    "Mood Trend",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )

            Spacer(
                modifier =
                    Modifier.height(4.dp)
            )

            Text(
                text =
                    "Latest ${entries.size} mood records",

                style =
                    MaterialTheme
                        .typography
                        .bodySmall
            )

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            if (
                entries.isEmpty()
            ) {

                Text(
                    text =
                        "No mood records available.",

                    style =
                        MaterialTheme
                            .typography
                            .bodyMedium
                )

                return@Column
            }

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
            ) {

                val leftPadding =
                    size.width * 0.04f

                val rightPadding =
                    size.width * 0.04f

                val topPadding =
                    size.height * 0.10f

                val bottomPadding =
                    size.height * 0.10f

                val usableWidth =
                    size.width -
                            leftPadding -
                            rightPadding

                val usableHeight =
                    size.height -
                            topPadding -
                            bottomPadding

                /*
                 * Draw subtle horizontal
                 * reference lines.
                 */
                for (
                level in 1..5
                ) {

                    val y =
                        topPadding +
                                (
                                        (5 - level) /
                                                4f
                                        ) *
                                usableHeight

                    drawLine(
                        color =
                            Color.LightGray
                                .copy(
                                    alpha = 0.35f
                                ),

                        start =
                            Offset(
                                leftPadding,
                                y
                            ),

                        end =
                            Offset(
                                size.width -
                                        rightPadding,
                                y
                            ),

                        strokeWidth =
                            2f
                    )
                }

                val points =
                    entries.mapIndexed {
                            index,
                            entry ->

                        val score =
                            moodToScore(
                                entry.mood
                            )

                        val x =
                            if (
                                entries.size == 1
                            ) {

                                size.width / 2f

                            } else {

                                leftPadding +
                                        (
                                                usableWidth /
                                                        (
                                                                entries.size -
                                                                        1
                                                                )
                                                ) *
                                        index
                            }

                        val y =
                            topPadding +
                                    (
                                            (5 - score) /
                                                    4f
                                            ) *
                                    usableHeight

                        Offset(
                            x,
                            y
                        )
                    }

                if (
                    points.size >= 2
                ) {

                    val path =
                        Path()

                    path.moveTo(
                        points.first().x,
                        points.first().y
                    )

                    points
                        .drop(1)
                        .forEach {
                                point ->

                            path.lineTo(
                                point.x,
                                point.y
                            )
                        }

                    drawPath(
                        path =
                            path,

                        color =
                            Color(
                                0xFF6750A4
                            ),

                        style =
                            Stroke(
                                width = 6f
                            )
                    )
                }

                points.forEach {
                        point ->

                    drawCircle(
                        color =
                            Color(
                                0xFF6750A4
                            ),

                        radius =
                            8f,

                        center =
                            point
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(8.dp)
            )

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                entries.forEach {
                        entry ->

                    Text(
                        text =
                            formatShortDate(
                                entry.dateMillis
                            ),

                        fontSize =
                            9.sp,

                        style =
                            MaterialTheme
                                .typography
                                .labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun HeartRateCard() {

    Card(
        modifier =
            Modifier.fillMaxWidth(),

        shape =
            RoundedCornerShape(
                20.dp
            )
    ) {

        Column(
            modifier =
                Modifier.padding(20.dp)
        ) {

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                Column {

                    Text(
                        text =
                            "Heart Rate",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )

                    Spacer(
                        modifier =
                            Modifier.height(4.dp)
                    )

                    Text(
                        text =
                            "Simulated sensor data",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }

                Column(
                    horizontalAlignment =
                        Alignment.End
                ) {

                    Text(
                        text =
                            "72",

                        fontSize =
                            26.sp,

                        fontWeight =
                            FontWeight.Bold
                    )

                    Text(
                        text =
                            "bpm",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier.height(18.dp)
            )

            Canvas(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(80.dp)
            ) {

                val path =
                    Path()

                path.moveTo(
                    0f,
                    size.height * 0.60f
                )

                path.lineTo(
                    size.width * 0.15f,
                    size.height * 0.60f
                )

                path.lineTo(
                    size.width * 0.25f,
                    size.height * 0.20f
                )

                path.lineTo(
                    size.width * 0.35f,
                    size.height * 0.75f
                )

                path.lineTo(
                    size.width * 0.50f,
                    size.height * 0.50f
                )

                path.lineTo(
                    size.width * 0.65f,
                    size.height * 0.55f
                )

                path.lineTo(
                    size.width * 0.78f,
                    size.height * 0.25f
                )

                path.lineTo(
                    size.width,
                    size.height * 0.58f
                )

                drawPath(
                    path =
                        path,

                    color =
                        Color(
                            0xFFD32F2F
                        ),

                    style =
                        Stroke(
                            width = 5f
                        )
                )
            }
        }
    }
}

private fun moodToScore(
    mood: String
): Int {

    return when (
        mood
    ) {

        "Very Good" -> 5
        "Good" -> 4
        "Neutral" -> 3
        "Stressed" -> 2
        "Low" -> 1

        else -> 3
    }
}

private fun moodLabelFromAverage(
    average: Double
): String {

    return when {

        average >= 4.5 ->
            "Very Good"

        average >= 3.5 ->
            "Good"

        average >= 2.5 ->
            "Neutral"

        average >= 1.5 ->
            "Stressed"

        average > 0 ->
            "Low"

        else ->
            "No data"
    }
}

private fun moodEmoji(
    average: Double
): String {

    return when {

        average >= 4.5 ->
            "😄"

        average >= 3.5 ->
            "🙂"

        average >= 2.5 ->
            "😐"

        average >= 1.5 ->
            "😟"

        else ->
            "😞"
    }
}

private fun formatShortDate(
    dateMillis: Long
): String {

    return SimpleDateFormat(
        "d MMM",
        Locale.getDefault()
    ).format(
        Date(dateMillis)
    )
}

@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun DataVisualizationScreenPreview() {

    val previewEntries =
        listOf(
            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        5
                    ),

                mood = "Neutral",

                factors =
                    emptyList(),

                notes = ""
            ),

            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        6
                    ),

                mood = "Stressed",

                factors =
                    listOf("Study / Work"),

                notes = ""
            ),

            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        7
                    ),

                mood = "Good",

                factors =
                    listOf("Exercise"),

                notes = ""
            ),

            MoodDiaryEntry(
                dateMillis =
                    createDateMillis(
                        2026,
                        9,
                        8
                    ),

                mood = "Very Good",

                factors =
                    listOf("Exercise"),

                notes = ""
            )
        )

    _304HealthTheme {

        DataVisualizationScreen(
            moodEntries =
                previewEntries
        )
    }
}