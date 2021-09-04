package com.example.dailymirror.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dailymirror.R
import com.example.dailymirror.model.TodoModel
import kotlinx.android.synthetic.main.todo_item.view.*
import java.text.SimpleDateFormat
import java.util.*

class TodoAdapter(val list : List<TodoModel>): RecyclerView.Adapter<TodoAdapter.TodoViewHolder>(){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TodoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.todo_item,parent,false)
        return TodoViewHolder(view)
    }

    override fun onBindViewHolder(holder: TodoViewHolder, position: Int) {

        holder.itemView.title.text = list[position].title
        holder.itemView.description.text = list[position].description
        holder.itemView.category.text = list[position].category
        updateTime(list[position].time,holder)
        updateDate(list[position].date,holder)
    }

    override fun getItemId(position: Int): Long {
        return list[position].id
    }

    private fun updateDate(date: Long, holder: TodoAdapter.TodoViewHolder) {
        val myFormat = "d-mm-yyyy"
        val sdf = SimpleDateFormat(myFormat)
        holder.itemView.showDate.text = sdf.format(Date(date))
    }

    private fun updateTime(time: Long,holder: TodoViewHolder) {
        val myFormat = "h:mm a"
        val stf = SimpleDateFormat(myFormat)
        holder.itemView.showTime.text = stf.format(Date((time)))
    }
    override fun getItemCount() : Int{
        Log.d("SIZE: ",list.size.toString())
        return list.size
    }

    class TodoViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    }

}