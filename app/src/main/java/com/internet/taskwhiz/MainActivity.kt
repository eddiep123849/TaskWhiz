package com.internet.taskwhiz

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
// view binding imports
import com.internet.taskwhiz.databinding.AddDialogLayoutBinding
import com.internet.taskwhiz.databinding.ActivityMainBinding
import com.internet.taskwhiz.databinding.DeleteDialogLayoutBinding
import java.io.File
import java.io.FileInputStream
// file IO imports
import java.io.FileReader
import java.io.FileWriter
import java.io.InputStream
import java.util.LinkedList

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var binding: ActivityMainBinding
    // The below is my way of handling my ViewModel. I originally came up with it to solve my
    // context problem I was having with my ViewModel implementation.
    private lateinit var taskmodelhandler: ViewModelHandler

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // view binding setup code insertion. I hope I remembered everything,
        // because it's been a while since I've done this.
        binding = ActivityMainBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        taskmodelhandler = ViewModelHandler(
            this,
            ViewModelProvider(this)[com.internet.taskwhiz.TaskListViewModel::class.java]
        )

        val taskAdapter = TaskAdapter(taskmodelhandler.getTasks())

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // This is the first time I've done something like this NOT
        // following the standard view binding pattern. I need to
        // access a view, but it's in a different layout file, so I
        // think this should allow me to be able to access it.
        val dialogBinding = AddDialogLayoutBinding.inflate(layoutInflater)

        // The below sets up the Add Task dialog to specify the description and due date
        // for a new task to be displayed. It has a title, icon, positive button
        // to make a new task from user input, and a negative button to cancel.
        val addBuilder: AlertDialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.eddie_icon)
            .setTitle("Wassup?")
            .setView(dialogBinding.root)
            // In Kotlin, unused lambda parameters can be substituted with _.
            .setPositiveButton("Add Task") {_, _ -> addSpecificTask(
                Task(
                    dialogBinding.editDescription.text.toString(),
                    dialogBinding.editDueDate.text.toString()
                )
            ) }
            .setNegativeButton("Cancel"){_, _ -> }
            .setNeutralButton("Setup Click Deletes! (Experimental)"){_,_ -> setLongClickDeletes()}
            .create()

        binding.addTask.setOnClickListener{
            addBuilder.show()
        }

        // This sets up the Delete Task dialog to be able to delete the last added task or to cancel.
        val deleteBuilder: AlertDialog = AlertDialog.Builder(this)
            .setIcon(R.mipmap.eddie_icon)
            .setTitle("Wassup Dawg?")
            .setPositiveButton("Delete Last Task"){dialog, which -> deleteLastTask()}
            .setNegativeButton("Cancel"){dialog, which -> }
            .create()

        binding.deleteTask.setOnClickListener{
            deleteBuilder.show()
        }

        Log.i("onCreate", "size of referencelist: ${taskAdapter.listOfViewHolderReferences.size}")
        val useMe = recyclerView.adapter as TaskAdapter
        Log.i("onCreate", "size of referencelist: ${useMe.listOfViewHolderReferences.size}")
    }

    /* When onDestroy is finally called for this activity, that's when the
     * TaskListViewModel instance will actually be destroyed, which means that
     * the dynamic data will be lost. So, when that happens, it's a good time to
     * store the current state of the tasks to the database so that when we call
     * onCreate again to see which tasks to put in the TaskListViewModel, we pull
     * the saved tasks from the database. This should be slightly more efficient
     * than the onSaveInstanceState implementation because this will use the
     * database less but will still synchronize the dynamic data and persistent data.
     */
    override fun onDestroy(){
        taskmodelhandler.setDB()
        super.onDestroy()
    }

    private fun addGenericTask(){
        // The below is my nifty way to add more tasks with unique descriptions.
        this.taskmodelhandler.addTask(Task("Additional task ${taskmodelhandler.getSize() + 1}"))
        // If I reassign the adapter, then the recycler view readjusts what it displays.
        // Is there a better way to update the RecyclerView? Probably, but this is
        // how I have figured out how to do this.
        recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
    }

    // The below will add a specific task to the linked list and will update the RecyclerView accordingly.
    private fun addSpecificTask(task: Task) {
        taskmodelhandler.addTask(task)
        // When you update the adapter, the recyclerView updates what it displays.
        recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
    }

    // Returns true if we found it and deleted it and false otherwise.
    private fun deleteSpecificTask(task: Task) : Boolean {
        val result = taskmodelhandler.deleteSpecifiedTask(task)
        if (result){
            recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
        }
        return result
    }

    private fun deleteLastTask(){
        // If we're going to delete a task, we better be sure that it actually has
        // something we can remove.
        if (taskmodelhandler.getSize() == 0){
            AlertDialog.Builder(this).setTitle("You can't remove anything!")
                .setNeutralButton("C'mon! (Click here)"){dialog, which -> }.create().show()
        }
        else {
            this.taskmodelhandler.deleteLastTask()
            recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
        }
    }

    // This will set a longClickListener on each Task in the RecyclerView so that it can
    // be deleted by a longClick.
    private fun setLongClickDeletes() {
        Log.i("setLongClickDeletes", "setLongClickDeletes() called")
        val taskAdapter: TaskAdapter = recyclerView.adapter as TaskAdapter
        val listOfViewHolderReferences: LinkedList<TaskAdapter.MyViewHolder> = taskAdapter.listOfViewHolderReferences

        for (viewHolder in listOfViewHolderReferences){
            // To take a view out of the recyclerView with a longClick, we need to do two things:
            // 1: remove that task from the dynamic list of tasks to show the update.
            // 2: make sure the TaskAdapter of the recyclerView is updated to reflect this change.
            // If every time we change our TaskList data we make sure to update our recyclerView,
            // then we will successfully make sure that the recyclerView, dynamic and persistent
            // data are synchronized.
            val cardView = viewHolder.view as CardView
            cardView.let{
                // One of my CardViews in the adapter holds a LinearLayout,
                // which holds 3 TextViews.
                it.setOnLongClickListener{_ ->
                    /* Now, because there are a limited number (I think 5 maximum at a
                     * time) of ViewHolders that get created, we need to see WHICH
                     * task is currently being displayed in THIS particular ViewModel.
                     * So, here are my new steps for the longClick delete:
                     * 1) See which task is currently being displayed on a specific
                     *    ViewHolder.
                     * 2) Once we figure out which task is being displayed in the
                     *    particular ViewHolder that was clicked, we then search for it
                     *    in the dynamic list of tasks and delete it.
                     * 3) Once we delete the task from the dynamic list of tasks, we
                     *    then update the adapter to reflect this change.
                     */
                    val linearLayout = it.getChildAt(0) as LinearLayout
                    val description = (linearLayout.getChildAt(1) as TextView).text.toString()
                    val dueDate = (linearLayout.getChildAt(2) as TextView).text.toString()
                    val clickedTask = Task(description, dueDate)
                    // Now, we try to see if such a task is in our list. We SHOULD
                    // find such a task in our list
                    deleteTaskDialog(clickedTask)
                    if (-1 < taskmodelhandler.hasTask(clickedTask)){
                        // The task is in the list.
                        deleteTaskDialog(clickedTask)
                    }
                    else {
                        // The task is not in the list.
                        Log.i("setLongClickDeletes", "We didn't find task:")
                        clickedTask.printMe()
                    }
                    // Don't pass this event to other listeners
                    true
                }
            }
        }
    }

    // The below function will take a task as input and will
    // 1) search for it with the taskmodelhandler,
    // 2), display the option to delete said task in a dialog,
    // 3) and then actually delete the task if the user clicks yes.
    fun deleteTaskDialog(task: Task){
        val deleteDialogLayout = DeleteDialogLayoutBinding.inflate(layoutInflater)
        // Currently, the dialog_layout is a LinearLayout with
        // 2 child views, so these can be hardcoded, but may need to
        // be changed later if I change the layout file.
        val linearLayout = deleteDialogLayout.root
        val descriptionTextView = linearLayout.getChildAt(0) as TextView
        val dueDateText = linearLayout.getChildAt(1) as TextView
        descriptionTextView.text = task.description
        dueDateText.text = task.dueDate

        AlertDialog.Builder(this)
            .setIcon(R.mipmap.eddie_icon)
            .setTitle("Do you want to delete the below task?")
            .setView(deleteDialogLayout.root)
            .setPositiveButton("Delete Task"){dialog, which ->
                deleteSpecificTask(task)
                Log.i("deleteTaskDialog", "We successfully found and deleted task:")
                task.printMe()
                Log.i("deleteTaskDialog", "")
                dialog.dismiss()
                Log.i("deleteTaskDialog", "We dismissed the dialog!")
            }
            .setNegativeButton("Cancel"){dialog, which ->
                dialog.dismiss()
                Log.i("deleteTaskDialog", "We dismissed the dialog!")
            }
            .create()
            .show()


    }
}