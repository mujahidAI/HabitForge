package com.example.habitforge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactoryProvider
import com.example.habitforge.data.Habit
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment

@Composable
fun HabitDetailsScreen(
    navController: NavController,
    habitId: Int,
    viewModel: HabitViewModel = viewModel(factory = HabitViewModelFactoryProvider.provide())
) {
    // Collect state safely for Compose
    val habits by viewModel.habits.collectAsState()

    // Load habits (only once)
    LaunchedEffect(Unit) { viewModel.loadHabits() }

    // Look up habit by id
    val habit = habits.find { it.id == habitId }

    // If still loading or not yet found
    if (habit == null) {
        Text("Loading habit...", modifier = Modifier.padding(16.dp))
        return
    }

    var title by remember { mutableStateOf(habit.title) }
    var description by remember { mutableStateOf(habit.description) }
    var isCompleted by remember { mutableStateOf(habit.isCompleted) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Edit Habit", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Habit Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Habit Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                checked = isCompleted,
                onCheckedChange = {
                    isCompleted = it
                    viewModel.setCompleted(habitId, it) // update instantly
                }
            )
            Text("Completed?")
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Update Habit
        Button(
            onClick = {
                viewModel.updateHabit(
                    Habit(
                        id = habitId,
                        title = title,
                        description = description,
                        isCompleted = isCompleted
                    )
                )
                navController.popBackStack()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Update Habit")
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Delete Habit
        Button(
            onClick = {
                viewModel.deleteHabit(habit)
                navController.popBackStack()
            },
            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Delete Habit")
        }
    }
}
