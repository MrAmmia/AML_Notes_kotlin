package net.thebookofcode.www.amlnotes.Model

import android.app.Application
import androidx.lifecycle.*
import kotlinx.coroutines.launch
import net.thebookofcode.www.amlnotes.Entities.Note
import net.thebookofcode.www.amlnotes.Repository.NoteRepository

class NoteViewModel(private val noteRepository: NoteRepository) :
    ViewModel() {

    val getAllNotes: LiveData<List<Note>> = noteRepository.getAllNotes.asLiveData()

    fun insert(note: Note) = viewModelScope.launch {
        noteRepository.insert(note)
    }

    fun update(note: Note) = viewModelScope.launch {
        noteRepository.update(note)
    }

    fun delete(note: Note) = viewModelScope.launch {
        noteRepository.delete(note)
    }

    fun deleteAllNotes() = viewModelScope.launch {
        noteRepository.deleteAllNotes()
    }

}

class NoteViewModelFactory(private val noteRepository: NoteRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NoteViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return NoteViewModel(noteRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }

}
