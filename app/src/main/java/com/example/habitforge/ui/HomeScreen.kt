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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitforge.data.Habit
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactoryProvider
import androidx.compose.runtime.collectAsState

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactoryProvider.provide())
) {
    // Collect StateFlow as State for Compose
    val habits by viewModel.habits.collectAsState()

    // Load habits when screen first opens
    LaunchedEffect(Unit) { viewModel.loadHabits() }

    Box(modifier = Modifier.fillMaxSize()) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            Text(
                text = "Your Habits",
                style = MaterialTheme.typography.headlineMedium
            )

            Spacer(modifier = Modifier.height(12.dp))

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

        FloatingActionButton(
            onClick = { navController.navigate("add") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Add Habit"
            )
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
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(6.dp),
        shape = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            // 🔥 Flame using emoji (no icon dependency)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = "🔥",
                    fontSize = MaterialTheme.typography.titleLarge.fontSize   // Big flame icon
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = habit.title,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "1 day",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }

            // ✔ Bigger Check Circle Button
// ✔ Bigger Check Circle Button (Green when done / Red when not)
            IconButton(
                onClick = { onCheckClick(!habit.isCompleted) },
                modifier = Modifier.size(52.dp) // Larger touch area
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Mark Completed",
                    tint = if (habit.isCompleted)
                        androidx.compose.ui.graphics.Color(0xFF4CAF50) // GREEN CHECK
                    else
                        androidx.compose.ui.graphics.Color(0xFFD32F2F), // RED CHECK
                    modifier = Modifier.size(40.dp)
                )
            }

        }
    }
}
