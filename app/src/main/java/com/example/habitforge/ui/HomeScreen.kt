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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.data.Habit

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HabitViewModel // <-- ViewModel now passed in
) {
    val habits by viewModel.habits.collectAsState()
    val quote by viewModel.quote.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
        viewModel.loadDailyQuote()
    }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp)
        ) {

            // Daily Quote
            Surface(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = MaterialTheme.shapes.medium,
                shadowElevation = 4.dp
            ) {
                Text(
                    text = quote,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Habit List
            LazyColumn {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        onClick = { navController.navigate("details/${habit.id}") },
                        onCheckClick = { isChecked -> viewModel.setCompleted(habit.id, isChecked) }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate("add") },
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Habit")
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
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(habit.title, style = MaterialTheme.typography.titleMedium)
                Text("${habit.streak} day streak", style = MaterialTheme.typography.bodySmall)
            }

            IconButton(onClick = { onCheckClick(!habit.isCompleted) }) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Complete Habit",
                    tint = if (habit.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(36.dp)
                )
            }
        }
    }
}
