package com.example.habitforge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habitforge.ui.theme.CreamCard
import com.example.habitforge.ui.theme.LavenderBackground
import com.example.habitforge.viewmodel.HabitViewModel

@Composable
fun HabitDetailsScreen(
    navController: NavController,
    habitId: Int,
    viewModel: HabitViewModel
) {
    LaunchedEffect(Unit) {
        viewModel.loadHabits()
    }

    val habits = viewModel.habits.collectAsState().value
    val habit = habits.find { it.id == habitId }

    if (habit == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LavenderBackground),
            contentAlignment = Alignment.Center
        ) {
            Text("Habit not found")
        }
        return
    }

    var title by remember { mutableStateOf(habit.title) }
    var description by remember { mutableStateOf(habit.description) }
    var isCompleted by remember { mutableStateOf(habit.isCompleted) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(16.dp))
                .background(CreamCard, RoundedCornerShape(16.dp))
                .padding(20.dp)
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
                        viewModel.setCompleted(habitId, it)
                    }
                )
                Text(text = "Completed?")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    viewModel.updateHabit(
                        habit.copy(
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
}
