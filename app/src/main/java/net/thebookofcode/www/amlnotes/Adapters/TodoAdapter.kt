package net.thebookofcode.www.amlnotes.Adapters

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import net.thebookofcode.www.amlnotes.R
import net.thebookofcode.www.amlnotes.databinding.TodoItemBinding

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoHolder>() {
    private var todosArray = ArrayList<String>()
    private var boolArray = ArrayList<Int>()
    private var doneCount = 0

    inner class TodoHolder(private val itemBinding: TodoItemBinding) : RecyclerView.ViewHolder(itemBinding.root) {

        fun bind(position: Int)=with(itemBinding){
            tvTodo.text = todosArray[position]
            val status: Int = boolArray[position]
            if (status == 0) {
                check.setImageResource(R.drawable.ic_undone)
                boolArray[position] = 0
            } else if (status == 1) {
                check.setImageResource(R.drawable.ic_done)
                boolArray[position] = 1
            }
        }

        init {

            itemBinding.check.setOnClickListener {
                val position = adapterPosition
                if (boolArray[position] == 0) {
                    boolArray[position] = 1
                    itemBinding.check.setBackgroundResource(R.drawable.ic_done)
                } else {
                    boolArray[position] = 0
                    itemBinding.check.setBackgroundResource(R.drawable.ic_undone)
                }
            }

            itemBinding.delete.setOnClickListener {
                val position = adapterPosition
                boolArray.removeAt(position)
                todosArray.removeAt(position)
                notifyDataSetChanged()
            }

            itemBinding.tvTodo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().isEmpty()) {
                        itemBinding.delete.visibility = View.GONE
                    } else {
                        itemBinding.delete.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        val itemBinding = TodoItemBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return TodoHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount(): Int {
        return todosArray.size
    }

    fun getDoneString(): String? {
        var doneString = ""
        for (num in boolArray) {
            doneString = doneString + num.toString() + "\n"
        }
        return doneString
    }

    fun getTodoString(): String? {
        var todoString = ""
        return try {
            for (todo in todosArray) {
                todoString = todoString + todo + "\n"
            }
            todoString
        } catch (e: NullPointerException) {
            ""
        }
    }

    fun setTodosArray(todosArray: ArrayList<String>?) {
        this.todosArray = todosArray!!
        notifyDataSetChanged()
    }

    fun setDoneArray(boolArray: ArrayList<Int>?) {
        this.boolArray = boolArray!!
        notifyDataSetChanged()
    }

    fun setTodoBool(done: Int) {
        boolArray.add(done)
        notifyDataSetChanged()
    }

    fun setTodoString(todoString: String?) {
        todosArray.add(todoString!!)
        notifyDataSetChanged()
    }

    fun numDone(): Int {
        for (i in boolArray.indices) {
            if (boolArray[i] == 1) {
                doneCount++
            }
        }
        return doneCount
    }
}