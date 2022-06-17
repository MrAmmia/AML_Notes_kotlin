package net.thebookofcode.www.amlnotes

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import android.widget.TextView.OnEditorActionListener
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.thebookofcode.www.amlnotes.Adapters.TodoAdapter
import net.thebookofcode.www.amlnotes.Entities.Note
import net.thebookofcode.www.amlnotes.Model.NoteViewModel
import net.thebookofcode.www.amlnotes.Model.NoteViewModelFactory
import net.thebookofcode.www.amlnotes.Repository.NoteRepository
import net.thebookofcode.www.amlnotes.databinding.FragmentAddEditNoteBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class AddEditNoteFragment : Fragment() {
    private var _binding:FragmentAddEditNoteBinding? = null
    private val binding get() = _binding!!
    private var REQUEST_CODE_READ_PERMISSION: Int = 1
    private var REQUEST_CODE_WRITE_PERMISSION: Int = 2
    private val args by navArgs<AddEditNoteFragmentArgs>()
    var note: Note? = null
    val noteViewModel: NoteViewModel by activityViewModels {
        NoteViewModelFactory((activity?.application as NoteApplication).repository)
    }
    lateinit var noteRepository: NoteRepository
    lateinit var adapter: TodoAdapter

    var imageUrl = ""
    var todo = ""
    var doneString = ""
    var reminder_date_time = ""
    private var todosArray = ArrayList<String>()
    private var boolArray = ArrayList<Int>()
    var isEditing = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentAddEditNoteBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.todoList.layoutManager = LinearLayoutManager(context)
        binding.editTextTodo.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.todoList.setHasFixedSize(true)
        adapter = TodoAdapter()
        binding.todoList.adapter = adapter
        if (args.currentNote != null) {
            note = args.currentNote!!
            isEditing = false
            binding.imgDone.setBackgroundResource(R.drawable.ic_delete)
            binding.editTextTitle.setText(note!!.title)
            binding.tvDateTime.text = note!!.dateTime
            binding.editTextContent.setText(note!!.content)
            if (note!!.imgPath.isNotEmpty()) {
                imageUrl = note!!.imgPath
                visibleImage(getImageFromPath(imageUrl)!!)
            }
            if (note!!.todo.isNotEmpty()) {
                todo = note!!.todo
                doneString = note!!.doneString
                val todos = todo.trim().split("\n").toTypedArray()
                val bools = doneString.trim().split("\n").toTypedArray()
                todosArray = ArrayList()
                boolArray = ArrayList()
                for (i in todos.indices) {
                    todosArray.add(todos[i])
                    Log.d("bools", bools[i].toString())
                    boolArray.add(bools[i].toInt())
                }
                adapter.setTodosArray(todosArray)
                adapter.setDoneArray(boolArray)
                binding.layoutTodo.visibility = View.VISIBLE
                binding.todoList.visibility = View.VISIBLE
            }
        } else {

        }
        if (isEditing) {
            binding.imgDone.setBackgroundResource(R.drawable.ic_check)
        } else {
            binding.imgDone.setBackgroundResource(R.drawable.ic_delete)
        }

        binding.linearLayout.setOnClickListener { binding.editTextContent.hasFocus() }

        binding.imgDone.setOnClickListener { v: View? ->
            if (isEditing) {
                saveNote()
                isEditing = false
                binding.imgDone.setBackgroundResource(R.drawable.ic_delete)
            } else {
                // delete note
                noteViewModel.delete(note!!)
                requireActivity().onBackPressed()
            }
        }

        binding.addTodo.setOnClickListener {
            binding.editTextTodo.visibility = View.VISIBLE
            binding.editTextTodo.hasFocus()
        }

        binding.imgBack.setOnClickListener { v: View? ->
            //saveNote()
            requireActivity().onBackPressed()
        }

        binding.llAlbum.setOnClickListener { v: View? ->
            context?.let {
                showBottomSheetDialog(
                    it
                )
            }
        }

        binding.llRemind.isEnabled = true

        binding.llRemind.setOnClickListener { v: View? ->
            // Set Reminder
            context?.let { showAlarmPicker(it) }
        }

        binding.llNote.setOnClickListener { binding.editTextContent.requestFocus() }

        binding.imgDelete.setOnClickListener { hideImage() }

        binding.llTodo.setOnClickListener {
            binding.layoutTodo.visibility = View.VISIBLE
            binding.editTextTodo.visibility = View.VISIBLE
        }

        binding.editTextTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isEditing = true
                binding.imgDone.setBackgroundResource(R.drawable.ic_check)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.editTextContent.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                isEditing = true
                binding.imgDone.setBackgroundResource(R.drawable.ic_check)
            }

            override fun afterTextChanged(s: Editable) {}
        })

        binding.editTextTodo.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == 6 && !v.text.toString().isEmpty()) {
                adapter.setTodoString(v.text.toString())
                adapter.setTodoBool(0)
                binding.todoList.visibility = View.VISIBLE
                isEditing = true
                binding.imgDone.setBackgroundResource(R.drawable.ic_check)
                v.text = ""
                return@OnEditorActionListener true
            } else if (actionId == KeyEvent.KEYCODE_DEL || actionId == KeyEvent.KEYCODE_FORWARD_DEL && v.text.toString()
                    .isEmpty()
            ) {
                binding.editTextTodo.visibility = View.GONE
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun saveNote() {
        var title = binding.editTextTitle.text.toString()
        val body = binding.editTextContent.text.toString()
        val dateTime = SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault()).format(Date())
        var numDone = 0
        var numTotal = 0

        // Checking if there's content in the note

        if (title.trim { it <= ' ' }.isEmpty() && body.trim { it <= ' ' }
                .isEmpty() && binding.imgNote.visibility == View.GONE && binding.layoutTodo.visibility == View.GONE && reminder_date_time.trim { it <= ' ' }
                .isEmpty()) {
            // No content
            requireActivity().onBackPressed();
        } else {
            // Content

            // Now check Title is empty
            if (title.trim { it <= ' ' }.isEmpty()) {
                if (binding.imgNote.visibility == View.VISIBLE) {
                    title = "Picture List"
                } else if (binding.layoutTodo.visibility == View.VISIBLE) {
                    title = "Todo List"
                }
            }

            // Checking if note contains to-do
            if (adapter.getTodoString()!!.isNotEmpty()) {
                doneString = adapter.getDoneString().toString()
                todo = adapter.getTodoString().toString()
                numDone = adapter.numDone()
                numTotal = adapter.itemCount
            }

            if (note != null) {
                val id = note!!.id
                note = Note(
                    title,
                    body,
                    dateTime,
                    imageUrl,
                    todo,
                    doneString,
                    numDone,
                    numTotal,
                    reminder_date_time
                )
                note!!.id = id
                noteViewModel.update(note!!)
            } else {
                note = Note(
                    title,
                    body,
                    dateTime,
                    imageUrl,
                    todo,
                    doneString,
                    numDone,
                    numTotal,
                    reminder_date_time
                )
                noteViewModel.insert(note!!)
            }
        }
        isEditing = false
        binding.imgDone.setBackgroundResource(R.drawable.ic_delete)
    }

    private fun deleteNote(note: Note) {
        noteViewModel.delete(note)
    }

    private fun showBottomSheetDialog(context: Context) {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout)
        val camera = bottomSheetDialog.findViewById<LinearLayout>(R.id.cameraLayout)
        val gallery = bottomSheetDialog.findViewById<LinearLayout>(R.id.galleryLayout)
        camera!!.setOnClickListener { v: View? ->
            val packageManager = requireActivity().packageManager
            if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
                if (Build.VERSION.SDK_INT <= 28) {
                    if (ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        bottomSheetDialog.cancel()
                        takePicture()
                        galleryAddPic(context, imageUrl)
                        isEditing = true
                        binding.imgDone.setBackgroundResource(R.drawable.ic_check)
                    } else {
                        askPermission(REQUEST_CODE_WRITE_PERMISSION)
                    }
                } else {
                    bottomSheetDialog.cancel()
                    takePicture()
                    galleryAddPic(context, imageUrl)
                    isEditing = true
                    binding.imgDone.setBackgroundResource(R.drawable.ic_check)
                }
            }
        }
        gallery!!.setOnClickListener { v: View? ->
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                bottomSheetDialog.cancel()
                selectImage()
                isEditing = true
                binding.imgDone.setBackgroundResource(R.drawable.ic_check)
            } else {
                askPermission(REQUEST_CODE_READ_PERMISSION)
            }
        }
        bottomSheetDialog.show()
    }

    private fun startAlarm(context: Context, c: Calendar) {
        val alarmMgr = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, AlarmReceiver::class.java)
        val alarmIntent = PendingIntent.getBroadcast(context, 1, intent, 0)
        alarmMgr.setExact(AlarmManager.RTC_WAKEUP, c.timeInMillis, alarmIntent)
    }

    private fun showAlarmPicker(context: Context) {
        val bottomSheetDialog = BottomSheetDialog(context)
        bottomSheetDialog.setContentView(R.layout.alarm_picker_dialog)
        val dateTimeSelectedTextView =
            bottomSheetDialog.findViewById<TextView>(R.id.dateTimeSelectedTextView)
        val datePicker = bottomSheetDialog.findViewById<DatePicker>(R.id.datePicker)
        val timePicker = bottomSheetDialog.findViewById<TimePicker>(R.id.timePicker)
        val llCancel = bottomSheetDialog.findViewById<LinearLayout>(R.id.llCancel)
        val llConfirm = bottomSheetDialog.findViewById<LinearLayout>(R.id.llConfirm)
        val c = Calendar.getInstance()
        datePicker!!.minDate = c.timeInMillis
        val setDateTime = SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault()).format(
            c.time.time
        )
        dateTimeSelectedTextView!!.text = setDateTime
        datePicker.setOnDateChangedListener { view, year, monthOfYear, dayOfMonth ->
            c[Calendar.YEAR] = year
            c[Calendar.MONTH] = monthOfYear
            c[Calendar.DAY_OF_MONTH] = dayOfMonth
            val setDateTime =
                SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault())
                    .format(c.time.time)
            dateTimeSelectedTextView.text = setDateTime
        }
        timePicker!!.setOnTimeChangedListener { view, hourOfDay, minute ->
            c[Calendar.HOUR_OF_DAY] = hourOfDay
            c[Calendar.MINUTE] = minute
            c[Calendar.SECOND] = 0
            val setDateTime =
                SimpleDateFormat("EEEE,dd MMM,yyyy HH:mm", Locale.getDefault())
                    .format(c.time.time)
            dateTimeSelectedTextView.text = setDateTime
        }
        llConfirm!!.setOnClickListener {
            startAlarm(context, c)
            isEditing = true
            binding.imgDone.setBackgroundResource(R.drawable.ic_check)
            bottomSheetDialog.cancel()
            reminder_date_time =
                SimpleDateFormat("MMM dd,yyyy HH:mm", Locale.getDefault())
                    .format(c.time)
        }
        llCancel!!.setOnClickListener {
            bottomSheetDialog.cancel()
            reminder_date_time = ""
        }
        bottomSheetDialog.setCanceledOnTouchOutside(true)
        bottomSheetDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        bottomSheetDialog.show()
    }

    private fun selectImage() {
        val showGalleryIntent =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        if (showGalleryIntent.resolveActivity(requireActivity().packageManager) != null) {
            galleryLauncher.launch(showGalleryIntent)
            //startActivityForResult(showGalleryIntent,REQUEST_SELECT_IMAGE);
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = context?.let {
                    FileProvider.getUriForFile(
                        it,
                        "net.thebookofcode.www.amlnotes.fileprovider",
                        photoFile
                    )
                }
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                //startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                cameraLauncher.launch(takePictureIntent)
            }
        }
    }

    private fun galleryAddPic(context: Context, url: String) {
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val f = File(url)
        val contentUri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        context.sendBroadcast(mediaScanIntent)
    }

    /*private val mPermissionResult = registerForActivityResult<String, Boolean>(
        ActivityResultContracts.RequestPermission()
    ) { result ->
        if (result) {
            showBottomSheetDialog(context)
        } else {
            Toast.makeText(context, "Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }*/

    var galleryLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> // Do your code from onActivityResult
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            if (data != null) {
                val selectedImageurl = data.data
                if (selectedImageurl != null) {
                    imageUrl = getImageUrl(selectedImageurl)
                    try {
                        val inputStream =
                            requireActivity().contentResolver.openInputStream(selectedImageurl)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        visibleImage(bitmap)
                    } catch (e: Exception) {
                        Toast.makeText(context, e.message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    imageUrl = ""
                }
            }
        }
    }

    var cameraLauncher = registerForActivityResult<Intent, ActivityResult>(
        ActivityResultContracts.StartActivityForResult()
    ) { result -> // Do your code from onActivityResult
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            context?.let { galleryAddPic(it, imageUrl) }
            val bitmap = BitmapFactory.decodeFile(imageUrl)
            visibleImage(bitmap)
            /*if (data != null) {
                            Uri extras = data.getData();
                            try {
                                visibleImage(MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), extras));
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            galleryAddPic(getContext(), imageUrl);
                            Bitmap bitmap = BitmapFactory.decodeFile(imageUrl);
                            visibleImage(bitmap);
                        }*/
        }
    }

    private fun getImageUrl(uri: Uri): String {
        val filePath: String?
        val cursor = requireActivity().contentResolver.query(uri, null, null, null)
        if (cursor == null) {
            filePath = uri.path
        } else {
            cursor.moveToFirst()
            val index = cursor.getColumnIndex("_data")
            filePath = cursor.getString(index)
            cursor.close()
        }
        return filePath!!
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        imageUrl = image.absolutePath
        return image
    }

    private fun visibleImage(bitmap: Bitmap) {
        binding.layoutImage.visibility = View.VISIBLE
        binding.imgNote.visibility = View.VISIBLE
        binding.imgDelete.visibility = View.VISIBLE
        binding.imgNote.setImageBitmap(bitmap)
    }

    fun hideImage() {
        binding.imgNote.visibility = View.GONE
        binding.imgDelete.visibility = View.GONE
        binding.layoutImage.visibility = View.GONE
        binding.imgNote.setImageDrawable(null)
        deleteImage(imageUrl)
        imageUrl = ""
        note!!.imgPath = ""
        isEditing = true
        binding.imgDone.setBackgroundResource(R.drawable.ic_check)
    }

    private fun deleteImage(path: String) {
        val imgFile = File(path)
        if (imgFile.exists()) {
            imgFile.delete()
        }
    }

    private fun getImageFromPath(path: String): Bitmap? {
        var myBitmap: Bitmap? = null
        val imgFile = File(path)
        if (imgFile.exists()) {
            myBitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
        }
        return myBitmap
    }

    private fun askPermission(requestCode: Int) {
        if (requestCode == REQUEST_CODE_READ_PERMISSION) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), REQUEST_CODE_READ_PERMISSION
            )
        } else if (requestCode == REQUEST_CODE_WRITE_PERMISSION) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), REQUEST_CODE_WRITE_PERMISSION
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}


