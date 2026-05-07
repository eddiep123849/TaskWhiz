package com.internet.taskwhiz

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.animation.AnimationUtils
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
import com.internet.taskwhiz.databinding.DialogLayoutBinding
import com.internet.taskwhiz.databinding.ActivityMainBinding
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
    private var count = 0
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
        val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)

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

        Log.i("trouble", "size of referencelist: ${taskAdapter.listOfViewHolderReferences.size}")
        val useMe = recyclerView.adapter as TaskAdapter
        Log.i("trouble", "size of referencelist: ${useMe.listOfViewHolderReferences.size}")
    }

    // This is called when the the app is no longer in the foreground/is
    // about to no longer be on the screen, I believe. This will make sure that
    // the db is updated to hold the current tasks in the ViewModel.
    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        taskmodelhandler.setDB()
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

    // This will delete a specific task out of the linked list and will update the RecyclerView accordingly.
    private fun deleteSpecificTask(index: Int): Boolean{
        val boolAnswer: Boolean = taskmodelhandler.deleteTaskAt(index)
        recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
        return boolAnswer
    }

    // This will set a longClickListener on each Task in the RecyclerView so that it can
    // be deleted by a longClick.
    private fun setLongClickDeletes() {
        Log.i("longClick", "setLongClickDeletes() called")
        val taskAdapter: TaskAdapter = recyclerView.adapter as TaskAdapter
        val listOfViewHolderReferences: LinkedList<TaskAdapter.MyViewHolder> = taskAdapter.listOfViewHolderReferences

        // It's been a LONG time since I've done the below in Android.
        var i = 0
        for (viewHolder in listOfViewHolderReferences){
            // To take a view out of the recyclerView with a longClick, we need to do two things:
            // 1: remove that task from the dynamic list of tasks to show the update.
            // 2: make sure the TaskAdapter of the recyclerView is updated to reflect this change.
            // If every time we change our TaskList data we make sure to update our recyclerView,
            // then we will successfully make sure that the recyclerView, dynamic and persistent
            // data are synchronized.
            Log.i("longClick", "Setting up a listener.")
            val searchForThisTask = taskmodelhandler.getTask(i)
            searchForThisTask.printMe()

            viewHolder.view.setOnLongClickListener {
                // Delete the task from the dynamic task list.
                Log.i("longClick", "longClick listener code is running. i: $i")
                taskmodelhandler.deleteSpecifiedTask(searchForThisTask)
                // Make sure the change is reflected
                binding.recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
                // Keep the click action from going to other listeners.
                true
            }
            i++
        }
    }

    fun setOnLongCLickDeletesRevised() {

    }


}