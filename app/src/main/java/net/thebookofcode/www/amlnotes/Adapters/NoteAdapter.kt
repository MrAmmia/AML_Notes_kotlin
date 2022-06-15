package net.thebookofcode.www.amlnotes.Adapters

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.os.Build
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import net.thebookofcode.www.amlnotes.Entities.Note
import net.thebookofcode.www.amlnotes.R
import java.io.File
import java.io.IOException
import java.util.*

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {
    private var notes = ArrayList<Note>()
    private var mNotesFull: ArrayList<Note>? = null
    private var listener: NoteItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.NoteViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.note_item,parent,false)
        return NoteViewHolder(v)
    }

    override fun onBindViewHolder(holder: NoteAdapter.NoteViewHolder, position: Int) {
        val currentNote = notes[position]
        if (currentNote.title.isNotEmpty()) {
            holder.tvTitle.visibility = View.VISIBLE
            holder.tvTitle.text = currentNote.title
        }
        if (currentNote.content!!.isNotEmpty()) {
            holder.tvBody.visibility = View.VISIBLE
            holder.tvBody.text = currentNote.content
        }
        holder.tvDateTime.text = currentNote.dateTime!!
        if (currentNote.reminderDateTime!!.isNotEmpty()) {
            holder.reminderLayout.visibility = View.VISIBLE
            holder.imgReminder.visibility = View.VISIBLE
            holder.reminderTime.text = currentNote.reminderDateTime
        }
        if (currentNote.totalTodo != 0) {
            val total = currentNote.totalTodo.toString()
            val done = currentNote.doneTodo.toString()
            holder.todoLayout.visibility = View.VISIBLE
            holder.tvTodoDone.text = done
            holder.tvTodoTotal.text = total
        }
        if (currentNote.imgPath!!.isNotEmpty()) {
            val path: String = currentNote.imgPath!!
            holder.imageNote.visibility = View.VISIBLE
            holder.imageNote.setImageBitmap(getImageFromPath(path))
        }
    }

    private fun getImageFromPath(path: String): Bitmap? {
        var myBitmap: Bitmap? = null
        val imgFile = File(path)

        val size = Size(100, 100)
        if (imgFile.exists()) {
            if (Build.VERSION.SDK_INT >= 29) {
                try {
                    myBitmap = ThumbnailUtils.createImageThumbnail(imgFile, size, null)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            } else {
                myBitmap = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(path), 100, 100)
            }
        }
        return myBitmap
    }

    fun setNotes(notes: List<Note>?) {
        this.notes = notes as ArrayList<Note>
        mNotesFull = java.util.ArrayList(notes)
        notifyDataSetChanged()
    }

    fun getNote(position: Int): Note? {
        return notes[position]
    }

    interface NoteItemClickListener {
        fun onItemClick(note: Note?)
    }

    fun setOnItemClickListener(listener: NoteItemClickListener?) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    inner class NoteViewHolder(itemView:View) : RecyclerView.ViewHolder(itemView){
        var tvTitle: TextView
        var tvBody: TextView
        var tvDateTime: TextView
        var tvTodoDone: TextView
        var tvTodoTotal: TextView
        var imgReminder: ImageView
        var imgTodo: ImageView
        var todoLayout: LinearLayout
        var reminderLayout: LinearLayout
        var reminderTime: TextView
        var imageNote: ImageView

        init {
            tvTitle = itemView.findViewById(R.id.tvTitle)
            tvBody = itemView.findViewById(R.id.tvBody)
            tvDateTime = itemView.findViewById(R.id.note)
            tvTodoDone = itemView.findViewById(R.id.tvTodoDone)
            tvTodoTotal = itemView.findViewById(R.id.tvTodoTotal)
            imgReminder = itemView.findViewById(R.id.imgReminder)
            imgTodo = itemView.findViewById(R.id.imgTodo)
            todoLayout = itemView.findViewById(R.id.todoLayout)
            reminderLayout = itemView.findViewById(R.id.reminderLayout)
            reminderTime = itemView.findViewById(R.id.reminderTime)
            imageNote = itemView.findViewById(R.id.imageNote)

            itemView.setOnClickListener {
                val position = adapterPosition
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(notes[position])
                }
            }
        }
    }

    private val mNotesFilter = object : Filter(){
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = java.util.ArrayList<Note>()
            if (constraint == null || constraint.isEmpty() || constraint == "") {
                filteredList.addAll(mNotesFull!!)
            } else {
                val filterPattern =
                    constraint.toString().lowercase(Locale.getDefault()).trim { it <= ' ' }
                for (item in mNotesFull!!) {
                    if (item.title!!.lowercase(Locale.getDefault())
                            .contains(filterPattern) || item.content!!.lowercase(
                            Locale.getDefault()
                        ).contains(filterPattern)
                        || item.todo!!.lowercase(Locale.getDefault()).contains(filterPattern)
                    ) {
                        filteredList.add(item)
                    }
                }
            }
            val results = FilterResults()
            results.values = filteredList
            return results
        }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {

            notes.clear()
            notes.addAll(results!!.values as Collection<Note>)
            notifyDataSetChanged()
        }
    }

    override fun getFilter(): Filter {
        return mNotesFilter
    }
}