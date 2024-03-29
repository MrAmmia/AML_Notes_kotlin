package net.thebookofcode.www.amlnotes.Adapters

import android.annotation.SuppressLint
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
import net.thebookofcode.www.amlnotes.NoteListFragment
import net.thebookofcode.www.amlnotes.databinding.NoteItemBinding
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class NoteAdapter : RecyclerView.Adapter<NoteAdapter.NoteViewHolder>(), Filterable {
    private var notes = ArrayList<Note>()
    private var mNotesFull: ArrayList<Note>? = null
    private var listener: NoteItemClickListener? = null
    private var longClickListener: NoteItemLongClickListener? = null
    var isLongClickEnabled: Boolean = false
    var isSelectAll: Boolean = false
    private var selectedNotes = ArrayList<Note>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val itemBinding =
            NoteItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NoteViewHolder(itemBinding)
    }

    override fun onBindViewHolder(
        holder: NoteViewHolder,
        @SuppressLint("RecyclerView") position: Int
    ) {
        val currentNote = notes[position]
        if (isLongClickEnabled) {
            holder.itemBinding.check.visibility = View.VISIBLE
        } else {
            holder.itemBinding.check.visibility = View.GONE
        }
        holder.itemBinding.itemLayout.setOnClickListener() {
            if (isLongClickEnabled) {
                if (holder.itemBinding.check.visibility == View.VISIBLE) {
                    holder.itemBinding.check.visibility = View.GONE
                    selectedNotes.remove(notes[position])
                } else {
                    holder.itemBinding.check.visibility = View.VISIBLE
                    selectedNotes.add(notes[position])
                }
            } else {
                if (listener != null && position != RecyclerView.NO_POSITION) {
                    listener!!.onItemClick(notes[position])
                }
            }
        }
        holder.itemBinding.itemLayout.setOnLongClickListener(object : View.OnLongClickListener {
            override fun onLongClick(v: View?): Boolean {
                holder.itemBinding.check.visibility = View.VISIBLE
                isLongClickEnabled = true
                if (longClickListener != null && position != RecyclerView.NO_POSITION) {
                    longClickListener!!.onItemLongClick(notes[position])
                    if (selectedNotes.contains(notes[position])) {

                    }
                    selectedNotes!!.add(notes[position])

                    if (selectedNotes.size == 0) {
                        isLongClickEnabled = false
                    }
                }
                return true
            }
        })
        holder.bind(currentNote)
        if (isSelectAll) {
            holder.itemBinding.check.visibility = View.VISIBLE

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
        mNotesFull = ArrayList(notes)
        notifyDataSetChanged()
    }

    fun tickAll() {
        this.isSelectAll = true
        selectedNotes.clear()
        selectedNotes.addAll(notes)
        notifyDataSetChanged()
    }

    fun unTickAll() {
        this.isSelectAll = false
        selectedNotes.clear()
        notifyDataSetChanged()
    }

    fun getNote(position: Int): Note {
        return notes[position]
    }

    fun getSelectedNotes(): ArrayList<Note> {
        return selectedNotes!!
    }

    interface NoteItemClickListener {
        fun onItemClick(note: Note?)
    }

    interface NoteItemLongClickListener {
        fun onItemLongClick(note: Note?)
    }

    fun setOnLongClickListener(longClickListener: NoteItemLongClickListener) {
        this.longClickListener = longClickListener
    }

    fun setOnItemClickListener(listener: NoteItemClickListener?) {
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return notes.size
    }

    inner class NoteViewHolder(val itemBinding: NoteItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(note: Note) = with(itemBinding) {
            if (note.title.isNotEmpty()) {
                tvTitle.visibility = View.VISIBLE
                tvTitle.text = note.title
            }
            if (note.content.isNotEmpty()) {
                tvBody.visibility = View.VISIBLE
                tvBody.text = note.content
            }
            tvDateTime.text = note.dateTime
            if (note.reminderDateTime.isNotEmpty()) {
                reminderLayout.visibility = View.VISIBLE
                imgReminder.visibility = View.VISIBLE
                reminderTime.text = note.reminderDateTime
            }
            if (note.totalTodo != 0) {
                val total = note.totalTodo.toString()
                val done = note.doneTodo.toString()
                todoLayout.visibility = View.VISIBLE
                tvTodoDone.text = done
                tvTodoTotal.text = total
            }
            if (note.imgPath.isNotEmpty()) {
                val path: String = note.imgPath
                imageNote.visibility = View.VISIBLE
                imageNote.setImageBitmap(getImageFromPath(path))
            }

        }
    }

    private val mNotesFilter = object : Filter() {
        override fun performFiltering(constraint: CharSequence?): FilterResults {
            val filteredList = ArrayList<Note>()
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

    fun selectAll(): ArrayList<Note> {
        selectedNotes!!.clear()
        selectedNotes!!.addAll(notes)
        return selectedNotes!!
    }
}