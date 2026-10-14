package com.fit5046.health304.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fit5046.health304.viewmodel.RecommendationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/** Personalised suggestion produced by the context-aware WorkManager task. */
@Composable
fun RecommendationCard(
    viewModel: RecommendationViewModel = viewModel()
) {
    val recommendation by viewModel.recommendation.collectAsStateWithLifecycle()

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "Suggested for you",
                    style = MaterialTheme.typography.labelLarge,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = { viewModel.refresh() }) {
                    Text("Refresh")
                }
            }

            val current = recommendation
            if (current == null) {
                Text(
                    text = "Personalising your suggestion…",
                    style = MaterialTheme.typography.bodyMedium
                )
            } else {
                Text(
                    text = current.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = current.message,
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "${current.category} · Based on: ${current.reason}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Updated " + SimpleDateFormat("HH:mm", Locale.getDefault())
                        .format(Date(current.createdAt)),
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}