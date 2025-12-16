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
    val taskArray: ArrayList<Task> = ArrayList<Task>()

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

        taskArray.let {
            it.add(Task("Take out the trash"))
            it.add(Task("Pay taxes"))
            it.add(Task("Go to Publix"))
        }

        val taskAdapter = TaskAdapter(taskArray)

        recyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = taskAdapter

        // This is the first time I've done something like this NOT
        // following the standard view binding pattern. I need to
        // access a view, but it's in a different layout file, so I
        // think this should allow me to be able to access it.
        val dialogBinding = DialogLayoutBinding.inflate(layoutInflater)

        val builder: AlertDialog.Builder = AlertDialog.Builder(this)

        binding.addTask.setOnClickListener{
                builder
                    .setPositiveButton("Add Task") {dialog, which ->
                        addTask()
                    }
                    .setNegativeButton("Cancel"){dialog, which ->

                    }.create().show()
        }
    }


    private fun addTask(){
        // The below is my nifty way to add more tasks with unique descriptions.
        this.taskArray.add(Task("Additional task ${taskArray.size - 2}"))
        // If I reassign the adapter, then the recycler view readjusts what it displays.
        // Is there a better way to update the RecyclerView? Probably, but this is
        // how I have figured out how to do this.
        recyclerView.adapter = TaskAdapter(taskArray)
    }

}