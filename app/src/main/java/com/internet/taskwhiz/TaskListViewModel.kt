package com.internet.taskwhiz

import android.util.Log
import androidx.lifecycle.ViewModel

// This class will be able to track the same array list all throughout
// one complete lifecycle. It's very simple: it holds the arraylist of tasks
// and allows us to keep it without doing all of that bundle stuff.
class TaskListViewModel: ViewModel{
    public var tasklist: ArrayList<Task> = ArrayList<Task>()

    constructor(): super(){
        Log.i("me", "TaskListViewModel Created")
    }
}