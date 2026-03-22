package com.jhosue.apuntes.navigation

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jhosue.apuntes.ui.screens.MainScreen
import com.jhosue.apuntes.ui.screens.NoteDetailScreen
import com.jhosue.apuntes.ui.screens.SectionScreen
import com.jhosue.apuntes.ui.screens.SettingsScreen
import com.jhosue.apuntes.viewmodel.MainViewModel
import com.jhosue.apuntes.viewmodel.SettingsViewModel
import com.jhosue.apuntes.viewmodel.NoteDetailViewModel
import com.jhosue.apuntes.viewmodel.SectionViewModel

@Composable
fun AppNavigation(
    darkMode: Boolean,
    onDarkModeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Main.route) {
        composable(Screen.Main.route) {
            val viewModel: MainViewModel = hiltViewModel()
            MainScreen(
                viewModel = viewModel,
                onNavigateToSection = { sectionId, sectionName ->
                    navController.navigate(Screen.Section.createRoute(sectionId, sectionName))
                },
                onNavigateToSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(Screen.Settings.route) {
            val viewModel: SettingsViewModel = hiltViewModel()
            SettingsScreen(
                viewModel = viewModel,
                darkMode = darkMode,
                onDarkModeChanged = onDarkModeChanged,
                onNavigateBack = { navController.popBackStack() },
                onRestoreSuccess = {
                    navController.popBackStack(Screen.Main.route, inclusive = false)
                }
            )
        }
        
        composable(
            route = Screen.Section.route,
            arguments = listOf(
                navArgument("sectionId") { type = NavType.StringType },
                navArgument("sectionName") { type = NavType.StringType }
            )
        ) {
            val viewModel: SectionViewModel = hiltViewModel()
            SectionScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToNoteDetail = { noteId, sectionName, noteIndex, totalNotes ->
                    navController.navigate(Screen.NoteDetail.createRoute(noteId, sectionName, noteIndex, totalNotes))
                }
            )
        }
        
        composable(
            route = Screen.NoteDetail.route,
            arguments = listOf(
                navArgument("noteId") { type = NavType.StringType },
                navArgument("sectionName") { type = NavType.StringType },
                navArgument("noteIndex") { type = NavType.IntType },
                navArgument("totalNotes") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val viewModel: NoteDetailViewModel = hiltViewModel()
            val noteIndex = backStackEntry.arguments?.getInt("noteIndex") ?: 0
            val totalNotes = backStackEntry.arguments?.getInt("totalNotes") ?: 0
            NoteDetailScreen(
                viewModel = viewModel,
                noteIndex = noteIndex,
                totalNotes = totalNotes,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
