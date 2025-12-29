package com.internet.taskwhiz

import android.content.Context
import java.util.LinkedList

/* The purpose of this class is to clear up this whole context problem I've been having with
 * my TaskListViewModel class. I figure that having a separate class through which I HANDLE
 * my ViewModel will make things easier and will also be closer in alignment with the Jetpack
 * development guidelines.
 *
 * This class is intended (as of now) to be used within an activity to manage one of my
 * TaskListViewModels. It will do ALL of the updating for me, and it will make sure that
 * the dynamic data in the ViewModel and the persistent data in the database sqlite file
 * are up-to-date and synchronized.
 *
 * If I need to use a ViewModel, then I should make an instance of this class to handle it.
 */

// Once an instance of ViewModelHandler is created, you cannot directly access its members.
// I'll control what can be used here.
class ViewModelHandler(private val context: Context,
                       private val viewModel: TaskListViewModel) {

    // The below block will make sure that the list in the ViewModel is
    // up-to-date when this handler is created.
    init { setList() }

    // Set our viewModel list to have all of the tasks in the db and NOTHING else.
    fun setList() {
        val db = TaskDatabaseHandler(context, null, null, TaskDatabaseHandler.DATABASE_VERSION)
        viewModel.tasklist = db.getAllTasks()
        // Does the below even do anything?
        db.close()
    }

    // Set our db to have all of the tasks in the viewModel list and NOTHING else.
    fun setDB() {
        val db = TaskDatabaseHandler(context, null, null, TaskDatabaseHandler.DATABASE_VERSION)
        db.setDB(viewModel.tasklist)
        // Again, why should I even do the below?
        db.close()
    }

    fun getTasks(): LinkedList<Task> { return viewModel.tasklist }

    fun addTask(task: Task) { viewModel.tasklist.add(task) }

    // It's possible that the list is empty, which is why
    // I'll provide some feedback in the form of a Boolean.
    fun deleteLastTask(): Boolean {
        var gotDeleted = false
        // Once I found out what scope functions were,
        // I have since found them to be very useful.
        viewModel.tasklist.let {
            if (it.isEmpty()) ; // We can't do anything in this case.
            else {
                // There's actually something to remove.
                it.removeLast()
                gotDeleted = true
            }
        }
        return gotDeleted
    }

    fun getSize() = viewModel.tasklist.size


    
    
}