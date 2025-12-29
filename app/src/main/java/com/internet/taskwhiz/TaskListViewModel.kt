package com.internet.taskwhiz

import android.util.Log
import androidx.lifecycle.ViewModel
import android.content.Context
import java.util.LinkedList

// This class will be able to track the same array list all throughout
// one complete lifecycle. It's very simple: it holds the arraylist of tasks
// and allows us to keep it without doing all of that bundle stuff.
class TaskListViewModel: ViewModel{
    public var tasklist: LinkedList<Task> = LinkedList<Task>()
    // To open up a db handler, you need a context. So, I'm just going
    // to use the context where this ViewModel is used. Simple, right?
    public var context: Context

    // When this ViewModel is created, it should get its data
    // from the database, which will have the stored tasks from
    // when the app was last closed.
    constructor(context: Context): super(){
        Log.i("me", "TaskListViewModel Created")
        this.context = context
        val dbHandler = TaskDatabaseHandler(context, null, null, TaskDatabaseHandler.DATABASE_VERSION)
        // The below sets our dynamic data to be what is stored in the database.
        tasklist = dbHandler.getAllTasks()
        // Does the below even do anything? Why should I call close?
        dbHandler.close()
    }

    // onCleared() is called right before the ViewModel is going to
    // be destroyed. I'll take advantage of this and update the database
    // when the persistent data will be lost.
    override fun onCleared(){
        super.onCleared()
        val dbHandler = TaskDatabaseHandler(context, null, null, TaskDatabaseHandler.DATABASE_VERSION)
        // The below will set the database to have all of our dynamic data.
        dbHandler.setDB(tasklist)
    }

}