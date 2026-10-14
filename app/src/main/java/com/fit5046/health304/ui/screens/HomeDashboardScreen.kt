package com.fit5046.health304.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.data.WellnessProgress
import com.fit5046.health304.ui.components.RecommendationCard
import com.fit5046.health304.ui.theme._304HealthTheme
import com.fit5046.health304.viewmodel.MoodViewModel
import com.fit5046.health304.viewmodel.WellnessProgressViewModel
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun HomeDashboardScreen(

    onMoodClick: () -> Unit = {},

    onViewAllMoodClick: () -> Unit = {},

    onProfileClick: () -> Unit = {},

    onSearchClick: () -> Unit = {},

    onHealthTrendsClick: () -> Unit = {},

    moodViewModel: MoodViewModel = viewModel(),

    wellnessProgressViewModel:
    WellnessProgressViewModel = viewModel()

) {

    /*
     * Current Firebase user.
     */
    val userId =
        FirebaseAuth
            .getInstance()
            .currentUser
            ?.uid
            ?: return


    /*
     * Observe this user's Mood Diary records from Room.
     */
    val moodEntries by
    moodViewModel
        .getMoodEntries(userId)
        .collectAsState(
            initial = emptyList()
        )


    /*
     * One daily WellnessProgress row per user.
     */
    val todayDateKey =
        remember {

            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(
                Date()
            )
        }


    val savedProgress by
    wellnessProgressViewModel
        .getProgress(
            userId,
            todayDateKey
        )
        .collectAsState(
            initial = null
        )


    /*
     * If no progress row exists yet today,
     * use an empty default object.
     */
    val currentProgress =
        savedProgress
            ?: WellnessProgress(
                userId = userId,
                dateKey = todayDateKey
            )


    /*
     * Mood completion is calculated directly
     * from today's real MoodEntry records.
     *
     * Adding today's first Mood -> true.
     * Deleting today's last Mood -> false.
     */
    val moodCompleted =
        moodEntries.any {

            isSameDay(
                it.dateMillis,
                System.currentTimeMillis()
            )
        }


    /*
     * Medication and Exercise currently do not have
     * separate completion records elsewhere in the app,
     * so these two states are stored in WellnessProgress.
     */
    val medicationCompleted =
        currentProgress
            .medicationCompleted


    val exerciseCompleted =
        currentProgress
            .exerciseCompleted


    /*
     * Daily progress.
     */
    val completedTasks =
        listOf(
            moodCompleted,
            medicationCompleted,
            exerciseCompleted
        ).count {
            it
        }


    val progress =
        completedTasks / 3f


    /*
     * Real Mood data for Home.
     */
    val latestMoodEntry =
        moodEntries
            .maxByOrNull {
                it.dateMillis
            }


    val recentMoodEntries =
        moodEntries
            .sortedByDescending {
                it.dateMillis
            }
            .take(3)


    LazyColumn(

        modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 24.dp,
                    vertical = 24.dp
                ),

        verticalArrangement =
            Arrangement.spacedBy(
                16.dp
            )
    ) {


        /*
         * Header
         */
        item {

            Box(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(

                    text =
                        "304Health",

                    style =
                        MaterialTheme
                            .typography
                            .headlineMedium,

                    modifier =
                        Modifier.align(
                            Alignment.Center
                        ),

                    textAlign =
                        TextAlign.Center
                )


                Row(

                    modifier =
                        Modifier.align(
                            Alignment.CenterEnd
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    IconButton(
                        onClick =
                            onSearchClick
                    ) {

                        Icon(

                            imageVector =
                                Icons.Filled.Search,

                            contentDescription =
                                "Search",

                            modifier =
                                Modifier.size(
                                    28.dp
                                ),

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }


                    IconButton(
                        onClick =
                            onProfileClick
                    ) {

                        Icon(

                            imageVector =
                                Icons.Filled
                                    .AccountCircle,

                            contentDescription =
                                "Profile",

                            modifier =
                                Modifier.size(
                                    36.dp
                                ),

                            tint =
                                MaterialTheme
                                    .colorScheme
                                    .primary
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text =
                    "Your wellness at a glance",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium,

                modifier =
                    Modifier.fillMaxWidth(),

                textAlign =
                    TextAlign.Center
            )
        }


        /*
         * Welcome
         */
        item {

            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Text(

                text =
                    "Welcome back!",

                style =
                    MaterialTheme
                        .typography
                        .titleLarge
            )


            Text(

                text =
                    "Take care of yourself today.",

                style =
                    MaterialTheme
                        .typography
                        .bodyMedium
            )
        }


        /*
         * P1 context-aware recommendation.
         */
        item {

            RecommendationCard()
        }


        /*
         * Today's Progress
         */
        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            16.dp
                        )
                ) {

                    Text(

                        text =
                            "Today's Progress",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                12.dp
                            )
                    )


                    LinearProgressIndicator(

                        progress = {
                            progress
                        },

                        modifier =
                            Modifier.fillMaxWidth()
                    )


                    Spacer(
                        modifier =
                            Modifier.height(
                                8.dp
                            )
                    )


                    Text(

                        text =
                            "$completedTasks of 3 daily tasks completed",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall
                    )
                }
            }
        }


        /*
         * Search shortcut
         */
        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                16.dp
                            ),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment
                            .CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(

                            text =
                                "Search Health Content",

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
                                "Search mood records and access health features.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }


                    TextButton(
                        onClick =
                            onSearchClick
                    ) {

                        Text(
                            "Search"
                        )
                    }
                }
            }
        }


        /*
         * Health Trends shortcut
         */
        item {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                16.dp
                            ),

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween,

                    verticalAlignment =
                        Alignment
                            .CenterVertically
                ) {

                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(

                            text =
                                "Health Trends",

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
                                "View your mood and health trends over time.",

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }


                    TextButton(
                        onClick =
                            onHealthTrendsClick
                    ) {

                        Text(
                            "View"
                        )
                    }
                }
            }
        }


        /*
         * Latest Mood
         */
        item {

            Text(

                text =
                    "Latest Mood",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )


            Spacer(
                modifier =
                    Modifier.height(
                        8.dp
                    )
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Row(

                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(
                                16.dp
                            ),

                    verticalAlignment =
                        Alignment
                            .CenterVertically,

                    horizontalArrangement =
                        Arrangement
                            .SpaceBetween
                ) {

                    Column(
                        modifier =
                            Modifier.weight(
                                1f
                            )
                    ) {

                        Text(

                            text =
                                latestMoodEntry
                                    ?.mood
                                    ?: "No record",

                            style =
                                MaterialTheme
                                    .typography
                                    .titleMedium
                        )


                        Text(

                            text =
                                if (
                                    latestMoodEntry
                                    != null
                                ) {

                                    formatDate(
                                        latestMoodEntry
                                            .dateMillis
                                    )

                                } else {

                                    "Add your first mood record."
                                },

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall
                        )
                    }


                    Button(
                        onClick =
                            onMoodClick
                    ) {

                        Text(

                            if (
                                latestMoodEntry
                                == null
                            ) {

                                "Add Mood"

                            } else {

                                "Update Mood"
                            }
                        )
                    }
                }
            }
        }


        /*
         * Daily checklist
         */
        item {

            Text(

                text =
                    "Today's Checklist",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )


            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(
                            8.dp
                        )
                ) {


                    /*
                     * Mood is automatic.
                     *
                     * It is disabled because the value
                     * comes directly from today's Mood records.
                     */
                    ChecklistItem(

                        text =
                            "Mood diary completed",

                        checked =
                            moodCompleted,

                        enabled =
                            false,

                        onCheckedChange = {}
                    )


                    /*
                     * Medication is manually completed
                     * and persisted in Room.
                     */
                    ChecklistItem(

                        text =
                            "Take medication",

                        checked =
                            medicationCompleted,

                        onCheckedChange = {
                                checked ->

                            wellnessProgressViewModel
                                .saveProgress(

                                    currentProgress.copy(

                                        medicationCompleted =
                                            checked,

                                        updatedAt =
                                            System.currentTimeMillis()
                                    )
                                )
                        }
                    )


                    /*
                     * Exercise is manually completed
                     * and persisted in Room.
                     */
                    ChecklistItem(

                        text =
                            "Home exercise completed",

                        checked =
                            exerciseCompleted,

                        onCheckedChange = {
                                checked ->

                            wellnessProgressViewModel
                                .saveProgress(

                                    currentProgress.copy(

                                        exerciseCompleted =
                                            checked,

                                        updatedAt =
                                            System.currentTimeMillis()
                                    )
                                )
                        }
                    )
                }
            }
        }


        /*
         * Recent Mood Records
         */
        item {

            Text(

                text =
                    "Recent Mood Records",

                style =
                    MaterialTheme
                        .typography
                        .titleMedium
            )
        }


        if (
            recentMoodEntries
                .isEmpty()
        ) {

            item {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Text(

                        text =
                            "No mood records yet.",

                        modifier =
                            Modifier.padding(
                                16.dp
                            ),

                        style =
                            MaterialTheme
                                .typography
                                .bodyMedium
                    )
                }
            }

        } else {

            items(

                items =
                    recentMoodEntries,

                key = {
                    it.id
                }

            ) {
                    entry ->

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Row(

                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    16.dp
                                ),

                        horizontalArrangement =
                            Arrangement
                                .SpaceBetween,

                        verticalAlignment =
                            Alignment
                                .CenterVertically
                    ) {

                        Column {

                            Text(

                                text =
                                    formatDate(
                                        entry.dateMillis
                                    ),

                                style =
                                    MaterialTheme
                                        .typography
                                        .bodyMedium
                            )


                            Text(

                                text =
                                    entry.mood,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleMedium
                            )
                        }


                        TextButton(
                            onClick =
                                onViewAllMoodClick
                        ) {

                            Text(
                                "View"
                            )
                        }
                    }
                }
            }
        }


        item {

            TextButton(

                onClick =
                    onViewAllMoodClick,

                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Text(
                    "View All Mood Records"
                )
            }


            Spacer(
                modifier =
                    Modifier.height(
                        16.dp
                    )
            )
        }
    }
}


@Composable
private fun ChecklistItem(

    text: String,

    checked: Boolean,

    enabled: Boolean = true,

    onCheckedChange:
        (Boolean) -> Unit

) {

    Row(

        modifier =
            Modifier.fillMaxWidth(),

        verticalAlignment =
            Alignment.CenterVertically
    ) {

        Checkbox(

            checked =
                checked,

            enabled =
                enabled,

            onCheckedChange =
                onCheckedChange
        )


        Text(

            text =
                text,

            style =
                MaterialTheme
                    .typography
                    .bodyMedium
        )
    }
}


/*
 * Used to determine whether a MoodEntry
 * belongs to today.
 */
private fun isSameDay(

    firstMillis: Long,

    secondMillis: Long

): Boolean {

    val formatter =
        SimpleDateFormat(
            "yyyyMMdd",
            Locale.getDefault()
        )


    return formatter.format(
        Date(
            firstMillis
        )
    ) ==
            formatter.format(
                Date(
                    secondMillis
                )
            )
}


@Preview(
    showBackground = true,
    showSystemUi = true
)
@Composable
private fun HomeDashboardScreenPreview() {

    _304HealthTheme {

        HomeDashboardScreen()
    }
}