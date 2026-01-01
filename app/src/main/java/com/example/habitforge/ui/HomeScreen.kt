package com.example.habitforge.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.habitforge.data.Habit
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactoryProvider

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactoryProvider.provide())
) {
    val habits by viewModel.habits.collectAsState()
    val quote by viewModel.quote.collectAsState()

    // Load habits + quote on first open
    LaunchedEffect(Unit) {
        viewModel.loadHabits()
        viewModel.loadDailyQuote()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {

            // 📌 Daily Quote Section
            Text(
                text = "Daily Motivation",
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(4.dp))

            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth(),
                tonalElevation = 4.dp
            ) {
                Text(
                    text = quote,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 📌 Habit Header
            Text(
                text = "Your Habits",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // 📌 Habit List
            LazyColumn {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        onClick = { navController.navigate("details/${habit.id}") },
                        onCheckClick = { isChecked ->
                            viewModel.setCompleted(habit.id, isChecked)
                        }
                    )
                }
            }
        }

        // ➕ FAB Button
        FloatingActionButton(
            onClick = { navController.navigate("add") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "Add Habit")
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onClick: () -> Unit,
    onCheckClick: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔥 Flame emoji instead of Whatshot icon
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🔥", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(text = habit.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = "${habit.streak} day streak", style = MaterialTheme.typography.bodySmall)
                }
            }

            // ✔ / ❌ Check Button
            IconButton(
                onClick = { onCheckClick(!habit.isCompleted) },
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Complete Habit",
                    tint = if (habit.isCompleted)
                        MaterialTheme.colorScheme.primary // Green = Completed
                    else
                        MaterialTheme.colorScheme.error, // Red = Not done yet
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
