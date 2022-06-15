package net.thebookofcode.www.amlnotes.DAO

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import net.thebookofcode.www.amlnotes.Entities.Note

@Dao
interface NoteDao {
    @Insert()
    suspend fun insert(note: Note)

    @Update
    suspend fun update(note: Note)

    @Delete
    suspend fun delete(note: Note)

    @Query("SELECT * FROM note_table ORDER BY id DESC")
    fun getAllNotes(): Flow<List<Note>>

    @Query("DELETE FROM note_table")
    suspend fun deleteAllNotes()
}