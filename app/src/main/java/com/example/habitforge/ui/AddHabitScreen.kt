package com.example.habitforge.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.habitforge.viewmodel.HabitViewModel

@Composable
fun AddHabitScreen(
    navController: NavController,
    viewModel: HabitViewModel // <-- ViewModel is passed from NavGraph, no need to recreate
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Text(text = "Add New Habit")

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = title,
            onValueChange = { title = it },
            label = { Text("Habit Title") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Habit Description") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                if (title.isNotBlank()) {
                    viewModel.addHabit(title, description)
                    navController.popBackStack() // go back to Home
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Save Habit")
        }
    }
}
