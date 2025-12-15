package com.internet.taskwhiz

import java.time.LocalDate

// Okay, the purpose of this class is that it will
// encapsulate ALL of the dat that will be associated with a task.
// So, each task can be one task object.
class Task constructor(
    // I'm using a primary constructor that declares
    // the instance variable "description". Also,
    // instance variables are public in Kotlin by default.
    var description: String = "*NO DESCRIPTION*",
    // A LocalDate follows the YYYY-MM-DD model. The default I have here
    // essentially means "unset due date."
    var dueDate: LocalDate  = LocalDate.parse("0000-00-00")
) {
    // Remember, if you want to declare a secondary constructor,
    // you need to do: constructor(): this(params). This'll be a useful
    // hint for later.



}