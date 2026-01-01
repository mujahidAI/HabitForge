package com.example.habitforge.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.habitforge.data.Habit
import com.example.habitforge.ui.theme.*
import com.example.habitforge.viewmodel.HabitViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HabitViewModel
) {
    val habits by viewModel.habits.collectAsState()
    val quote by viewModel.quote.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadHabits()
        viewModel.loadDailyQuote()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(LavenderBackground)
    ) {
        // Main content container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 32.dp)
        ) {
            // Inspirational Quote - directly on background
            Text(
                text = "\"$quote\"",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontSize = 20.sp,
                    lineHeight = 28.sp,
                    fontStyle = FontStyle.Italic,
                    fontFamily = FontFamily.Serif
                ),
                color = Color(0xFF2C2C2C),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Habit List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(habits) { habit ->
                    HabitItem(
                        habit = habit,
                        onClick = { navController.navigate("details/${habit.id}") },
                        onCheckClick = { isChecked -> viewModel.setCompleted(habit.id, isChecked) }
                    )
                }
            }
        }

        // Floating Action Button (unchanged as requested)
        FloatingActionButton(
            onClick = { navController.navigate("add") },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
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
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(24.dp))
            .clickable { onClick() },
        color = CreamCard,
        shape = RoundedCornerShape(24.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left side: Habit title and streak
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    ),
                    color = TextPrimary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${habit.streak} day streak",
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontSize = 14.sp
                    ),
                    color = LightGray
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Right side: Custom checkbox
            CustomCheckbox(
                isChecked = habit.isCompleted,
                onCheckedChange = { onCheckClick(!habit.isCompleted) }
            )
        }
    }
}

@Composable
fun CustomCheckbox(
    isChecked: Boolean,
    onCheckedChange: () -> Unit
) {
    Surface(
        modifier = Modifier
            .size(40.dp)
            .clickable { onCheckedChange() },
        shape = CircleShape,
        color = if (isChecked) SageGreen else ErrorRed
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Icon(
                imageVector = if (isChecked) Icons.Default.Check else Icons.Default.Close,
                contentDescription = if (isChecked) "Completed" else "Not Completed",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
