package com.internet.taskwhiz

import android.graphics.Color
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(val listOfTasks: Array<Task>): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.MyViewHolder {
        Log.i("me", "onCreateViewHolder called")
        val cardView: CardView = CardView(parent.context)

        return MyViewHolder(
            cardView
        )
    }

    override fun onBindViewHolder(holder: TaskAdapter.MyViewHolder, position: Int) {
        Log.i("me","onBindViewHolder called")
        val cardView: CardView = holder.view as CardView
        val textView: TextView = TextView(cardView.context)
        textView.text = listOfTasks.get(position).description
        textView.height = 30
        textView.setBackgroundColor(Color.BLACK)
        textView.setTextColor(Color.CYAN)

        cardView.addView(textView)
    }

    override fun getItemCount(): Int {
        Log.i("me", "getItemCount called")
        return listOfTasks.size;
    }


    public class MyViewHolder: RecyclerView.ViewHolder {
        val view: View
        constructor(itemView: View): super(itemView){
            view = itemView
        }
    }


}