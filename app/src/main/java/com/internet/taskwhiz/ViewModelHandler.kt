package com.internet.taskwhiz

import android.content.Context

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

class ViewModelHandler(val context: Context,
                       val dynamicData: TaskListViewModel) {

}