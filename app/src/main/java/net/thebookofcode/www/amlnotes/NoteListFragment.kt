package net.thebookofcode.www.amlnotes

//import androidx.fragment.app.viewModels
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.fragment_note_list.*
import net.thebookofcode.www.amlnotes.Adapters.NoteAdapter
import net.thebookofcode.www.amlnotes.Entities.Note
import net.thebookofcode.www.amlnotes.Model.NoteViewModel
import net.thebookofcode.www.amlnotes.Model.NoteViewModelFactory
import net.thebookofcode.www.amlnotes.databinding.FragmentNoteListBinding


class NoteListFragment : Fragment() {
    private var _binding: FragmentNoteListBinding? = null
    private val binding get() = _binding!!
    private var isLongClickEnabled = false


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentNoteListBinding.inflate(inflater, container, false)
        binding.searchView
        val noteViewModel: NoteViewModel by activityViewModels {
            NoteViewModelFactory((activity?.application as NoteApplication).repository)
        }
        binding.searchView.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.setHasFixedSize(true)
        val adapter = NoteAdapter()
        binding.recyclerView.adapter = adapter


        noteViewModel.getAllNotes.observe(viewLifecycleOwner, Observer { notes ->
            notes?.let { adapter.setNotes(it) }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.trim { it <= ' ' }.isNotEmpty()) {
                    adapter.filter.filter(newText)
                } else {
                    if (binding.searchView.hasFocus()) {
                        adapter.filter.filter("")
                    }
                }
                return false
            }
        })

        binding.selectAll.setOnCheckedChangeListener { buttonView, isChecked ->
            if (buttonView.isChecked) {
                adapter.tickAll()
            } else {
                adapter.unTickAll()
            }
        }

        binding.deleteNotes.setOnClickListener {
            if (isLongClickEnabled) {
                AlertDialog.Builder(requireContext())
                    .setMessage("Do you want to delete selected note(s)?")
                    .setTitle("Warning!")
                    .setCancelable(true)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which ->
                        var deleteNotes: ArrayList<Note> = adapter.getSelectedNotes()
                        for (note in deleteNotes) {
                            noteViewModel.delete(note)
                        }
                        isLongClickEnabled = false
                        adapter.isLongClickEnabled = false
                        binding.selectAll.visibility = View.GONE
                        adapter.unTickAll()

                    }.setNegativeButton(
                        "No"
                    ) { dialog, which -> dialog.cancel() }.create().show()

            } else {
                AlertDialog.Builder(requireContext())
                    .setMessage("Do you want to delete all notes?")
                    .setTitle("Warning!")
                    .setCancelable(true)
                    .setPositiveButton(
                        "Yes"
                    ) { dialog, which -> noteViewModel!!.deleteAllNotes() }.setNegativeButton(
                        "No"
                    ) { dialog, which -> dialog.cancel() }.create().show()
                isLongClickEnabled = false
            }
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
        }).attachToRecyclerView(binding.recyclerView)

        binding.fab.setOnClickListener { v: View? ->
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

        adapter.setOnLongClickListener(object : NoteAdapter.NoteItemLongClickListener {
            override fun onItemLongClick(note: Note?) {
                adapter.isLongClickEnabled = true
                isLongClickEnabled = true
                if (isLongClickEnabled) {
                    //Toast.makeText(context, "Long Clicked", Toast.LENGTH_SHORT).show()
                    binding.selectAll.visibility = View.VISIBLE
                }
            }
        })
        requireActivity().onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                if(isLongClickEnabled){
                    adapter.unTickAll()
                    isLongClickEnabled=!isLongClickEnabled
                }else{
                    requireActivity().onBackPressed()
                }
            }

        })
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}