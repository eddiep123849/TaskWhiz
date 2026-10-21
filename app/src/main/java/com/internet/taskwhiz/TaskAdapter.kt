package com.internet.taskwhiz

/* This class handles what a RecyclerView will display.
 * HOW these items are displayed will be handled by the
 * LayoutManager of the individual RecyclerView, but WHAT will
 * be displayed is handled by this class.
 */

import androidx.lifecycle.MutableLiveData
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import java.util.LinkedList

/* This class has three instance variables: 1) a linked list that contains the details of a bunch of tasks,
 * 2) a referenceBackToTheMainActivity so that when this TaskAdapter scrolls to a new view and creates something new,
 * it can let the MainActivty know by calling a function in it to update the lonClickDeletes (the Observable class is
 * now DEPRECATED! I need to take matters into my own hands then to keep the long click deletes updated),
 * and 3) a list of ViewHolderReferences so that every time a new viewHolder is created, we can reference it again to see what
 * task it is currently displaying.
 */
class TaskAdapter(val listOfTasks: LinkedList<Task>, val referenceBackToMainActivity: MainActivity): RecyclerView.Adapter<TaskAdapter.MyViewHolder>() {
    /* I'd like to maintain a list of references to the ViewHolder objects so I
     * can reference them later on in MainActivity when I set up the LongClick
     * deletes with the correct dialog description. However, this list can change,
     * so I'd like to be able to "observe" it to react to its changes, so I'm going
     * to put the list of references in a LiveData wrapper!
     */
    val listOfViewHolderReferences: LinkedList<MyViewHolder>

    init {
        // We need to make sure we initialize the LinkedList within the LiveData wrapper!
        // Because of this, we should be able to use the !!. assert non-null operator because
        // with this init block, the value of the LiveData wrapper should NEVER BE NULL!!
        listOfViewHolderReferences = LinkedList<MyViewHolder>()
    }

    // Make a MyViewHolder instance. Also, I believe the parent parameter is the RecyclerView into which
    // we'll be putting our ViewHolders! That's what that parameter is, I'm pretty sure!
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskAdapter.MyViewHolder {
        Log.i("TaskAdapter", "onCreateViewHolder called")
        // I've decided to use below the not-so-safe assert not null operator below
        // BECAUSE, in this instance, the LiveData object should ALWAYS have an ACTUAL
        // OBJECT as its value because of the initialization block, so if it IS null,
        // then I did something wrong.
        listOfViewHolderReferences.addLast(
            MyViewHolder(
                // I have a sample layout of what a task should look like.
            LayoutInflater.from(parent.context).inflate(R.layout.task_card, parent, false)
            )
        )
        Log.i("TaskAdapter", "size: ${listOfViewHolderReferences.size}")

        // To make sure that the newly created ViewHolder is kept track of in the
        // MainActivity, we'll let MainActivity know it needs to add another longClickListener to
        // a newly created viewHolder by calling its update long click delete function from here.
        // This should remove the need for the "setup longClickDeletes" option in the add task button.
        referenceBackToMainActivity.setLongClickDeletes()

        // Return the reference to the last ViewHolder in the list.
        return listOfViewHolderReferences.last()
    }

    // Set the properties of a MyViewHolder instance.
    override fun onBindViewHolder(holder: TaskAdapter.MyViewHolder, position: Int) {
        val cardView: CardView = holder.itemView as CardView
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

    public class MyViewHolder(layoutView: View): RecyclerView.ViewHolder(layoutView) {
        // The super class already holds a view, so I don't need to declare any more instance variables
        // to be able to access the view in the constructor.
        init {
            Log.i("TaskAdapter", "MyViewHolder object created")
        }
    }
}