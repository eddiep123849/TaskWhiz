package com.internet.taskwhiz

import android.util.Log
import androidx.lifecycle.ViewModel
import android.content.Context
import java.util.LinkedList

// This class will be able to track the same array list all throughout
// one complete lifecycle. It's very simple: it holds the arraylist of tasks
// and allows us to keep it without doing all of that bundle stuff.
class TaskListViewModel: ViewModel{

    // Y'know, I almost NEVER use linked lists, but here we are!
    public var tasklist: LinkedList<Task> = LinkedList<Task>()

    constructor(): super(){
        Log.i("TaskListViewModel", "TaskListViewModel Created")
    }

    // onCleared() is called right before the ViewModel is going to
    // be destroyed. That's a nice lifecycle fact to know.
}