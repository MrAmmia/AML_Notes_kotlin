package net.thebookofcode.www.amlnotes

//import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import net.thebookofcode.www.amlnotes.Adapters.NoteAdapter
import net.thebookofcode.www.amlnotes.Entities.Note
import net.thebookofcode.www.amlnotes.Model.NoteViewModel
import net.thebookofcode.www.amlnotes.Model.NoteViewModelFactory


class NoteListFragment : Fragment() {
    lateinit var search_view: SearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_note_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val noteViewModel: NoteViewModel by viewModels {
            NoteViewModelFactory((activity?.application as NoteApplication).repository)
        }
        search_view = view.findViewById(R.id.search_view)
        search_view.imeOptions = EditorInfo.IME_ACTION_DONE
        val deleteNotes = view.findViewById<ImageView>(R.id.deleteNotes)
        val fab = view.findViewById<FloatingActionButton>(R.id.fab)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.setHasFixedSize(true)
        val adapter = NoteAdapter()
        recyclerView.adapter = adapter


        noteViewModel!!.getAllNotes.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })



        deleteNotes.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setMessage("Do you want to delete all notes?")
                .setTitle("Warning!")
                .setCancelable(true)
                .setPositiveButton(
                    "Yes"
                ) { dialog, which -> noteViewModel!!.deleteAllNotes() }.setNegativeButton(
                    "No"
                ) { dialog, which -> dialog.cancel() }.create().show()
        }

        ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                noteViewModel.delete(adapter.getNote(viewHolder.adapterPosition)!!)
                Toast.makeText(context, "Note Deleted!", Toast.LENGTH_SHORT).show()
            }
        }).attachToRecyclerView(recyclerView)

        fab.setOnClickListener { v: View? ->
            findNavController(
                v!!
            ).navigate(R.id.action_noteListFragment_to_addEditNoteFragment)
        }


        adapter.setOnItemClickListener(object : NoteAdapter.NoteItemClickListener {
            override fun onItemClick(note: Note?) {
                val action: NavDirections =
                    NoteListFragmentDirections.actionNoteListFragmentToAddEditNoteFragment(note!!)
                findNavController().navigate(action)
            }

        })
    }
}