package com.internet.taskwhiz

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(val listOfTasks: ArrayList<Task>): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.MyViewHolder {
        Log.i("me", "onCreateViewHolder called")


        // I have a sample layout of what a task should look like.
        return MyViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
        )
    }

    override fun onBindViewHolder(holder: TaskAdapter.MyViewHolder, position: Int) {
        Log.i("me","onBindViewHolder called")

        val cardView: CardView = holder.view as CardView
        // Because I specifically created a layout file to use, I can be confident
        // that the first and second children will always be the same. The CardView has
        // a linear layout, and the linear layout will always have two TextViews inside.
        val linearLayout: LinearLayout = cardView.getChildAt(0) as LinearLayout
        val descriptionText: TextView = linearLayout.getChildAt(0) as TextView
        val localDateText: TextView = linearLayout.getChildAt(1) as TextView

        descriptionText.text = listOfTasks.get(position).description
        localDateText.text   = listOfTasks.get(position).dueDate.toString()

    }

    override fun getItemCount(): Int {
        Log.i("me", "getItemCount called")
        return listOfTasks.size;
    }


    public class MyViewHolder: RecyclerView.ViewHolder {
        val view: View
        constructor(layoutView: View): super(layoutView){
            view = layoutView
        }
    }


}