package com.internet.taskwhiz

/* Okay, the purpose of this class is that it will
 * encapsulate ALL of the dat that will be associated with a task.
 * So, each task can be one task object.
*/

import androidx.lifecycle.ViewModel
import android.util.Log

// Because the Task class extends ViewModel, it should be exist all
// throughout program execution, even when the phone is turned,
// which is nice.
class Task constructor(
    // I'm using a primary constructor that declares
    // the instance variable "description". Also,
    // instance variables are public in Kotlin by default.
    var description: String = "*NO DESCRIPTION*",
    // A LocalDate follows the YYYY-MM-DD model. The default I have here
    // essentially means "unset due date."
    var dueDate: String  = "NOW!"
) : ViewModel(){
    // Remember, if you want to declare a secondary constructor,
    // you need to do: constructor(): this(params). This'll be a useful
    // hint for later.

    // This function will help me to see what an individual task is.
    // It's really just a convenience print.
    fun printMe(){
        Log.i("task", "description: $description | dueDate: $dueDate")
    }
}