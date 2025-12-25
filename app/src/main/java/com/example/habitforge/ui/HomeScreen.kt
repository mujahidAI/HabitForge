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
            .padding(vertical = 6.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Column {
                Text(text = habit.title, style = MaterialTheme.typography.titleMedium)
                Text(text = habit.description, style = MaterialTheme.typography.bodySmall)
            }

            IconButton(
                onClick = { onCheckClick(!habit.isCompleted) }
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = "Mark Completed",
                    tint = if (habit.isCompleted)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
    }
}
