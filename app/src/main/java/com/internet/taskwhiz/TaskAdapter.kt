package com.internet.taskwhiz

/* This class handles what a RecyclerView will display.
 * HOW these items are displayed will be handled by the
 * LayoutManager of the individual RecyclerView, but WHAT will
 * be displayed is handled by this class.
 */

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

class TaskAdapter(val listOfTasks: LinkedList<Task>): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    // I'm having a problem with setting on click listeners to the cardView objects here,
    // so I decided to add this in to be able to reference them after they're created. I don't
    // know what happens after they're created and then passed to the RecyclerView, and I also
    // don't know how to access these objects after their creation here, which is why I added
    // the below list. It's an Eddie-fix, but shoot, if it works, it works!
    val listOfViewHolderReferences: LinkedList<MyViewHolder> = LinkedList<MyViewHolder>()

    // Make a MyViewHolder instance. Also, I believe the parent parameter is the RecyclerView into which
    // we'll be putting our ViewHolders! That's what that parameter is, I'm pretty sure!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.MyViewHolder {
        Log.i("TaskAdapter", "onCreateViewHolder called")

        listOfViewHolderReferences.addLast(
            MyViewHolder(
                // I have a sample layout of what a task should look like.
            LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
            )
        )
        Log.i("TaskAdapter", "size: ${listOfViewHolderReferences.size}")
        // Return the reference to the last ViewHolder in the list.
        return listOfViewHolderReferences.last()
    }

    // Set the properties of a MyViewHolder instance.
    override fun onBindViewHolder(holder: TaskAdapter.MyViewHolder, position: Int) {
        val cardView: CardView = holder.view as CardView
        // Because I specifically created a layout file to use, I can be confident
        // that the second and third children will always be the same. The CardView has
        // a linear layout, and the linear layout will always have three TextViews inside:
        // a task number, a description, and a due date.
        val linearLayout: LinearLayout = cardView.getChildAt(0) as LinearLayout
        val taskNumberText: TextView = linearLayout.getChildAt(0) as TextView
        val descriptionText: TextView = linearLayout.getChildAt(1) as TextView
        val localDateText: TextView = linearLayout.getChildAt(2) as TextView

        // Position starts at 0, so we need an offset to start at 1.
        taskNumberText.text  = "Task Number: ${position + 1}"
        descriptionText.text = listOfTasks.get(position).description
        localDateText.text   = listOfTasks.get(position).dueDate

        Log.i("TaskAdapter","position: $position; onBindViewHolder holder: ${descriptionText.text}, ${localDateText.text}")

    }

    // Determines how many times we make and set the properties of a MyViewHolder instance.
    override fun getItemCount(): Int {
        // This function gets called a LOT, so I commented out the below message.
        // Log.i("TaskAdapter", "getItemCount called")
        return listOfTasks.size;
    }


    public class MyViewHolder: RecyclerView.ViewHolder {
        // Yeah, my viewholder class holds a single view. That's what it does.
        // New addition, it's possible that the superclass already holds a variable for the view,
        // so it may be that me adding in a View instance variable is unnecessary.
        val view: View
        constructor(layoutView: View): super(layoutView){
            view = layoutView
        }
    }
}