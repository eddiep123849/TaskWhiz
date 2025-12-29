package com.internet.taskwhiz

/* Okay, this class should handle ALL operations associated with
 * accessing the literal sqlite database file. If I want to access
 * my database, then I should use this file: adding, deleting,
 * and querying should all be taken care of by this file.
 */

import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.Context
import java.util.LinkedList

class TaskDatabaseHandler(context: Context, name: String?,
                          factory: SQLiteDatabase.CursorFactory?, version: Int):
    SQLiteOpenHelper(context, ACTUAL_SQL_FILE_NAME,
        factory, DATABASE_VERSION) {
    companion object {
        // This object will store the basic information about my database.
        // Here, we see the actual file's name, the one table's name that
        // it contains, and the two column names.
        const val DATABASE_VERSION = 1
        const val ACTUAL_SQL_FILE_NAME = "dataBruh.db"
        const val TASK_TABLE_NAME = "tasks"
        // Not sure if I'll use the below column, but it's here.
        val COLUMN_PRIMARY_KEY = arrayOf("id", "INTEGER PRIMARY KEY")
        // The below stores the name and type of column 1, respectively.
        val COLUMN_1_DETAILS = arrayOf("description", "TEXT")

        // The below stores the name and type of column 2, respectively.
        val COLUMN_2_DETAILS = arrayOf("due_date", "TEXT")
    }

    override fun onCreate(db: SQLiteDatabase): Unit{
        // Technically, in SQL, case doesn't matter for keywords.
        val CREATE_TASK_DATABASE = "creaTe tABle $TASK_TABLE_NAME (" +
                // Sometimes, I like to mix syntaxes :)
                "${COLUMN_PRIMARY_KEY[0]} ${COLUMN_PRIMARY_KEY[1]},"   +
                "${COLUMN_1_DETAILS[0]} ${COLUMN_1_DETAILS.get(1)}," +
                "${COLUMN_2_DETAILS.get(0)} ${COLUMN_2_DETAILS[1]}"  +
                ")"
        db.execSQL(CREATE_TASK_DATABASE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int){
        // I'm not really sure what to do in this scenario. I think
        // I'm only going to use one database version.
        for (i in 1..1000)
            android.util.Log.wtf("uh oh", "Wait, why did this happen")
    }








    // This marks the methods I actually use to INTERACT with my database.






    fun addTask(task: Task){
        val ADD_TASK = "insert into $TASK_TABLE_NAME " +
                "(${COLUMN_1_DETAILS[0]}, ${COLUMN_2_DETAILS[0]})" +
                " VALUES " +
                // Remember, string literals in SQL HAVE QUOTES "!
                "(\"${task.description}\", \"${task.dueDate}\")"
        val db = this.writableDatabase
        db.execSQL(ADD_TASK)
    }

    fun deleteTask(task: Task): Boolean {
        var result = false
        val QUERY = "select * from $TASK_TABLE_NAME whERe " +
                "${COLUMN_1_DETAILS[0]} = ${task.description}" +
                " AND " +
                "${COLUMN_2_DETAILS[0]} = ${task.dueDate}"
        val db = this.writableDatabase
        val cursor: android.database.Cursor = db.rawQuery(QUERY, null)

        if (cursor.moveToFirst()){
            db.delete(TASK_TABLE_NAME,
                "${COLUMN_1_DETAILS[0]} = ${task.description}" +
                        " AND " +
                        "${COLUMN_2_DETAILS[0]} = ${task.dueDate}",
                null)
            cursor.close()
            result = true
        }
        db.close()
        return result
    }

    // This deletes all tasks. I bet you didn't need this comment to know that.
    fun deleteAllTasks() {
        val DELETE_ALL_QUERY = "DELETE fRoM $TASK_TABLE_NAME"
        val db = this.writableDatabase
        // Goodbye, rows
        db.execSQL(DELETE_ALL_QUERY)
        db.close()
    }

    // Yeah, we return a list of all tasks in the database.
    fun getAllTasks(): LinkedList<Task> {
        // The below list will store the tasks from the db, if there are any.
        val taskList = LinkedList<Task>()
        // The below query should return ALL rows in the table.
        val GET_ALL_QUERY = "SELECT * FROM $TASK_TABLE_NAME"
        val db = this.writableDatabase
        val cursor = db.rawQuery(GET_ALL_QUERY, null)

        // We'll go through all of the rows in the cursor object, which
        // should be all of the rows in the table because of the above query.
        for (i in 0 until cursor.count)
            // I have to type cursor a lot here, so I decided to use the "let" method.
            // It's a lot easier to type "it" than "cursor" every time.
            cursor.let {
                // Point to the current row
                it.moveToPosition(i)
                // Get the correct indexes for each column based off their names.
                val descriptionColumnIndex: Int = it.getColumnIndex(COLUMN_1_DETAILS[0])
                val dueDateColumnIndex: Int = it.getColumnIndex(COLUMN_2_DETAILS[0])
                // Get the column data for the row we're on.
                val currentDescription: String = it.getString(descriptionColumnIndex)
                val currentDueDate: String = it.getString(dueDateColumnIndex)
                // Make a task with the data we got from the db and add it to out list
                val currentTask = Task(currentDescription, currentDueDate)
                taskList.add(currentTask)

                // Debugging: let's see what it is! Hopefully, it's good!
                currentTask.printMe()
            }
        // Now, taskList should contain all of the tasks stored in the database.
        return taskList
    }

    // Takes a list of tasks and makes the db hold ONLY these tasks. It's
    // like we're "setting" the database (hehe).
    fun setDB(tasks: List<Task>){
        // Empty the db
        deleteAllTasks()
        // Add all of the tasks in the list to the db.
        for (task in tasks) addTask(task)
    }
}