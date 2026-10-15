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
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.data.MoodEntry
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.viewmodel.MoodViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import java.util.Calendar
import androidx.compose.foundation.layout.width
import com.fit5046.health304.data.SensorReading
import com.fit5046.health304.viewmodel.SensorViewModel
import kotlin.math.roundToInt


@Composable
fun DataVisualizationScreen(

    onBackClick: () -> Unit = {},

    moodViewModel: MoodViewModel = viewModel(),

    sensorViewModel: SensorViewModel = viewModel()

) {

    // P3: Observe real Room data scoped to the Firebase account.
    val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
    val moodFlow = remember(moodViewModel, userId) {
        moodViewModel.getMoodEntries(userId)
    }
    val moodEntries by moodFlow.collectAsState(initial = emptyList())
    val sensorReadings by sensorViewModel.recentReadings.collectAsState()

    // P4: Time range applies to every report component.
    var selectedPeriod by rememberSaveable { mutableStateOf("All") }
    val periods = listOf("Daily", "Weekly", "Monthly", "All")
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val startMillis = when (selectedPeriod) {
        "Daily" -> calendar.timeInMillis
        "Weekly" -> {
            calendar.add(Calendar.DAY_OF_YEAR, -6)
            calendar.timeInMillis
        }
        "Monthly" -> {
            calendar.set(Calendar.DAY_OF_MONTH, 1)
            calendar.timeInMillis
        }
        else -> Long.MIN_VALUE
    }
    val nowMillis = System.currentTimeMillis()
    val filteredEntries = moodEntries.filter { entry ->
        entry.dateMillis in startMillis..nowMillis
    }
    val recentEntries = filteredEntries.sortedBy { it.dateMillis }.takeLast(7)


    val moodScores =
        filteredEntries.map { moodToScore(it.mood) }


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
                .verticalScroll(rememberScrollState())
                .padding(
                    horizontal = 20.dp,
                    vertical = 16.dp
                )
    ) {

        TextButton(
            onClick =
                onBackClick
        ) {

            Text(
                "← Back"
            )
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
                Modifier.height(
                    4.dp
                )
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
                Modifier.height(
                    20.dp
                )
        )


        Text(
            text = "Reporting Period",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            periods.forEach { period ->
                FilterChip(
                    selected = selectedPeriod == period,
                    onClick = { selectedPeriod = period },
                    label = { Text(period) }
                )
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        MoodSummaryCard(

            averageMood =
                averageMood,

            moodLabel =
                averageMoodLabel,

            hasData =
                moodScores
                    .isNotEmpty()
        )


        Spacer(
            modifier =
                Modifier.height(
                    16.dp
                )
        )


        MoodTrendCard(
            entries =
                recentEntries
        )

        Spacer(modifier = Modifier.height(16.dp))

        MoodDistributionCard(entries = filteredEntries)


        HeartRateCard(readings = sensorReadings)
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
                    .padding(
                        20.dp
                    ),

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
                        Modifier.height(
                            6.dp
                        )
                )


                Text(

                    text =
                        if (
                            hasData
                        ) {
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
                        if (
                            hasData
                        ) {

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
                    if (
                        hasData
                    ) {

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
    entries: List<MoodEntry>
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
                Modifier.padding(
                    20.dp
                )
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
                    Modifier.height(
                        4.dp
                    )
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
                    Modifier.height(
                        18.dp
                    )
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
                        .height(
                            150.dp
                        )
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
                 * Draw horizontal reference lines.
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
                                    alpha =
                                        0.35f
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
                                width =
                                    6f
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
                    Modifier.height(
                        8.dp
                    )
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



/** Heart-rate line chart drawn from the latest simulated wearable readings in Room. */
@Composable
private fun HeartRateCard(readings: List<SensorReading>) {
    // Room returns newest first; the chart reads oldest (left) to newest (right)
    val points = readings.reversed()
    val latest = readings.firstOrNull()
    val minHr = points.minOfOrNull { it.heartRate }
    val maxHr = points.maxOfOrNull { it.heartRate }
    val timeFormat = remember { SimpleDateFormat("HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Heart Rate", style = MaterialTheme.typography.titleMedium)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Simulated wearable data (Fitbit dataset), last ${points.size} readings",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = latest?.heartRate?.toString() ?: "--",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text("bpm", style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            if (points.size < 2 || minHr == null || maxHr == null) {
                Text(
                    text = "Waiting for sensor readings…",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Row(modifier = Modifier.fillMaxWidth()) {
                    // Y-axis labels: highest and lowest bpm in the window
                    Column(
                        modifier = Modifier.height(100.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("$maxHr", style = MaterialTheme.typography.bodySmall)
                        Text("$minHr", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Canvas(
                        modifier = Modifier
                            .weight(1f)
                            .height(100.dp)
                    ) {
                        val range = (maxHr - minHr).coerceAtLeast(1)
                        val stepX = size.width / (points.size - 1)
                        val path = Path()
                        points.forEachIndexed { index, reading ->
                            val x = index * stepX
                            val y = size.height * (1f - (reading.heartRate - minHr).toFloat() / range)
                            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFFD32F2F),
                            style = Stroke(width = 5f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // X-axis labels: time of the first and last reading
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(timeFormat.format(Date(points.first().timestamp)), style = MaterialTheme.typography.bodySmall)
                    Text("Time", style = MaterialTheme.typography.bodySmall)
                    Text(timeFormat.format(Date(points.last().timestamp)), style = MaterialTheme.typography.bodySmall)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Average ${points.map { it.heartRate }.average().roundToInt()} bpm · " +
                            "${points.sumOf { it.steps }} steps in this period",
                    style = MaterialTheme.typography.bodySmall
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

        "Very Good" ->
            5

        "Good" ->
            4

        "Neutral" ->
            3

        "Stressed" ->
            2

        "Low" ->
            1

        else ->
            3
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
        Date(
            dateMillis
        )
    )
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun DataVisualizationScreenPreview() {

    _304HealthTheme {

        DataVisualizationScreen()
    }
}