package net.thebookofcode.www.amlnotes.Entities

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "note_table")
data class Note(
    var title: String,

    var content: String,


    @ColumnInfo(name = "date_time")
    var dateTime: String,

    @ColumnInfo(name = "img_path")
    var imgPath: String,

    var todo: String,

    @ColumnInfo(name = "done_string")
    var doneString: String,

    @ColumnInfo(name = "done_todo")
    var doneTodo: Int,

    @ColumnInfo(name = "total_todo")
    var totalTodo: Int,

    @ColumnInfo(name = "reminder_date_time")
    var reminderDateTime: String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null
}