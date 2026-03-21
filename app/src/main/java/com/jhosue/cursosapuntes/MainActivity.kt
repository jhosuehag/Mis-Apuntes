package com.jhosue.cursosapuntes

import android.os.Bundle
import android.content.SharedPreferences
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.jhosue.cursosapuntes.navigation.AppNavigation
import com.jhosue.cursosapuntes.ui.theme.CursosApuntesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE)
        
        enableEdgeToEdge()
        setContent {
            var darkMode by remember { mutableStateOf(sharedPreferences.getBoolean("dark_mode", false)) }

            // Escuchar cambios en las preferencias
            LaunchedEffect(Unit) {
                sharedPreferences.registerOnSharedPreferenceChangeListener { _, key ->
                    if (key == "dark_mode") {
                        darkMode = sharedPreferences.getBoolean("dark_mode", false)
                    }
                }
            }
            
            CursosApuntesTheme(darkTheme = darkMode) {
                AppNavigation(
                    darkMode = darkMode,
                    onDarkModeChanged = { enabled ->
                        sharedPreferences.edit().putBoolean("dark_mode", enabled).apply()
                        darkMode = enabled
                    }
                )
            }
        }
    }
}
