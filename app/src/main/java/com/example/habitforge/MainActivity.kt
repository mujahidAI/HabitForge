package com.example.habitforge

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.lifecycle.ViewModelProvider
import com.example.habitforge.navigation.AppNavGraph
import com.example.habitforge.viewmodel.HabitViewModel
import com.example.habitforge.viewmodel.HabitViewModelFactoryProvider

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Get ViewModel through factory provider
        val factory = HabitViewModelFactoryProvider.provide(application)
        val habitViewModel = ViewModelProvider(this, factory)
            .get(HabitViewModel::class.java)

        setContent {
            MaterialTheme {
                // Pass shared ViewModel into navigation
                AppNavGraph(habitViewModel)
            }
        }
    }
}
