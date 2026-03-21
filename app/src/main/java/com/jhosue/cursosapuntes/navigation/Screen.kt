package com.jhosue.cursosapuntes.navigation

sealed class Screen(val route: String) {
    object Main : Screen("main")
    object Settings : Screen("settings")
    object Section : Screen("section/{sectionId}/{sectionName}") {
        fun createRoute(sectionId: String, sectionName: String) = "section/$sectionId/$sectionName"
    }
    object NoteDetail : Screen("noteDetail/{noteId}/{sectionName}/{noteIndex}/{totalNotes}") {
        fun createRoute(noteId: String, sectionName: String, noteIndex: Int, totalNotes: Int) = "noteDetail/$noteId/$sectionName/$noteIndex/$totalNotes"
    }
}
