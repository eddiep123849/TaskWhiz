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
        val view = binding.root
        enableEdgeToEdge()
        setContentView(view)
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

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            // Look, I set an icon! However, it only shows up if you set a title.
            .setIcon(R.mipmap.eddie_icon)
            .setTitle("Wassup?")

        binding.addTask.setOnClickListener{
            builder
                .setView(dialogBinding.root)
                .setPositiveButton("Add Task") {dialog, which -> addSpecificTask(
                    Task(
                        dialogBinding.editDescription.text.toString(),
                        dialogBinding.editDueDate.text.toString()
                    )
                ) }
                .setNegativeButton("Cancel"){dialog, which -> }
                    .create().show()

        }

        binding.deleteTask.setOnClickListener{
            builder
                .setView(null) // The null view resets it.
                .setPositiveButton("Delete Task"){dialog, which -> deleteLastTask()}
                .setNegativeButton("Cancel") {dialog, which -> }
                    .create().show()

        }
    }

    // This is called when the the app is no longer in the foreground/is
    // about to no longer be on the screen, I believe. This will make sure that
    // the db is updated to hold the current tasks in the ViewModel.
    override fun onSaveInstanceState(outState: Bundle){
        super.onSaveInstanceState(outState)
        taskmodelhandler.setDB()
    }


    private fun addGenericTask(){
        // The below is my nifty way to add more tasks
        // with unique descriptions.
        this.taskmodelhandler.addTask(Task("Additional task ${taskmodelhandler.getSize() + 1}"))
        // If I reassign the adapter, then the recycler view readjusts what it displays.
        // Is there a better way to update the RecyclerView? Probably, but this is
        // how I have figured out how to do this.
        recyclerView.adapter = TaskAdapter(taskmodelhandler.getTasks())
    }

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

    private fun deleteSpecificTask(index: Int){
        taskmodelhandler.deleteTaskAt(index)
    }

}