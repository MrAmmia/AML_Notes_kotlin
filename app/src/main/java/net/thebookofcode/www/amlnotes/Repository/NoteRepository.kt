package net.thebookofcode.www.amlnotes.Repository

import androidx.annotation.WorkerThread
import kotlinx.coroutines.flow.Flow
import net.thebookofcode.www.amlnotes.DAO.NoteDao
import net.thebookofcode.www.amlnotes.Entities.Note

class NoteRepository(private val noteDao: NoteDao) {

    val getAllNotes: Flow<List<Note>> = noteDao.getAllNotes()

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(note: Note) {
        noteDao.insert(note)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun update(note: Note) {
        noteDao.update(note)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(note: Note) {
        noteDao.delete(note)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun deleteAllNotes() {
        noteDao.deleteAllNotes()
    }
}