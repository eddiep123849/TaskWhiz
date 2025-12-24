package com.internet.taskwhiz

import android.util.Log
import androidx.lifecycle.ViewModel
import android.content.Context

// This class will be able to track the same array list all throughout
// one complete lifecycle. It's very simple: it holds the arraylist of tasks
// and allows us to keep it without doing all of that bundle stuff.
class TaskListViewModel: ViewModel{
    public var tasklist: ArrayList<Task> = ArrayList<Task>()
    // To open up a db handler, you need a context. So, I'm just going
    // to use the context where this ViewModel us used. Simple, right?
    public lateinit var context: Context

    // When this ViewModel is created, it should get its data
    // from the database, which will have the stored tasks from
    // when the app was last closed.
    constructor(context: Context): super(){
        Log.i("me", "TaskListViewModel Created")
        this.context = context
        val dbHandler = TaskDatabaseHandler(context, null, null, TaskDatabaseHandler.DATABASE_VERSION)
        dbHandler.
    }

    // onCleared() is called right before the ViewModel is going to
    // be destroyed. I'll take advantage of this and update the database
    // when the persistent data will be lost.
    override fun onCleared(){
        super.onCleared()

    }

}