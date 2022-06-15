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

class TodoAdapter : RecyclerView.Adapter<TodoAdapter.TodoHolder>() {
    private var todosArray = ArrayList<String>()
    private var boolArray = ArrayList<Int>()
    private var doneCount = 0

    inner class TodoHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTodo: TextView
        val check: ImageView
        val delete: ImageView

        init {
            tvTodo = itemView.findViewById(R.id.tvTodo)
            check = itemView.findViewById(R.id.check)
            delete = itemView.findViewById(R.id.delete)

            check.setOnClickListener {
                val position = adapterPosition
                if (boolArray[position] == 0) {
                    boolArray[position] = 1
                    check.setBackgroundResource(R.drawable.ic_done)
                } else {
                    boolArray[position] = 0
                    check.setBackgroundResource(R.drawable.ic_undone)
                }
            }

            delete.setOnClickListener {
                val position = adapterPosition
                boolArray.removeAt(position)
                todosArray.removeAt(position)
                notifyDataSetChanged()
            }

            tvTodo.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                    if (s.toString().isEmpty()) {
                        delete.visibility = View.GONE
                    } else {
                        delete.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable) {}
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.todo_item, parent, false)
        return TodoHolder(v)
    }

    override fun onBindViewHolder(holder: TodoHolder, position: Int) {
        holder.tvTodo.text = todosArray[position]
        val status: Int = boolArray[position]
        if (status == 0) {
            holder.check.setImageResource(R.drawable.ic_undone)
            boolArray[position] = 0
        } else if (status == 1) {
            holder.check.setImageResource(R.drawable.ic_done)
            boolArray[position] = 1
        }
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