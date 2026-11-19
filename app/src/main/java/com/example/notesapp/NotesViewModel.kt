package com.example.notesapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NotesViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).noteDao()

    // READ: Flow dari Room diubah menjadi StateFlow untuk UI
    val notes: StateFlow<List<Note>> = dao.getAll()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    // CREATE: sekarang menerima title dan note
    fun addNote(title: String, note: String) {
        val t = title.trim()
        val n = note.trim()
        if (t.isEmpty() || n.isEmpty()) return
        viewModelScope.launch { dao.insert(Note(title = t, note = n)) }
    }

    // UPDATE: title dan note bisa diubah
    fun updateNote(item: Note, newTitle: String, newNote: String) {
        val t = newTitle.trim()
        val n = newNote.trim()
        if (t.isEmpty() || n.isEmpty()) return
        viewModelScope.launch { dao.update(item.copy(title = t, note = n)) }
    }

    // DELETE
    fun deleteNote(item: Note) {
        viewModelScope.launch { dao.delete(item) }
    }
}