package com.jhosue.apuntes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.view.WindowCompat
import com.jhosue.apuntes.navigation.AppNavigation
import com.jhosue.apuntes.ui.theme.CursosApuntesTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge()
        setContent {
            val sharedPreferences = remember { getSharedPreferences("settings", MODE_PRIVATE) }
            var darkMode by remember { mutableStateOf(sharedPreferences.getBoolean("dark_mode", false)) }

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
