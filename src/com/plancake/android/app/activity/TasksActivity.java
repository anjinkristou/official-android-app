/*************************************************************************************
* ===================================================================================*
* Software by: Danyuki Software Limited                                              *
* This file is part of Plancake.                                                     *
*                                                                                    *
* Copyright 2009-2010-2011 by:     Danyuki Software Limited                          *
* Support, News, Updates at:  http://www.plancake.com                                *
* Licensed under the AGPL version 3 license.                                         *                                                       *
* Danyuki Software Limited is registered in England and Wales (Company No. 07554549) *
**************************************************************************************
* Plancake is distributed in the hope that it will be useful,                        *
* but WITHOUT ANY WARRANTY; without even the implied warranty of                     *
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the                      *
* GNU Affero General Public License for more details.                                *
*                                                                                    *
* You should have received a copy of the GNU Affero General Public License           *
* along with this program.  If not, see <http://www.gnu.org/licenses/>.              *
*                                                                                    *
**************************************************************************************/

package com.plancake.android.app.activity;

import java.util.ArrayList;

import com.plancake.android.app.DbAdapter;
import com.plancake.android.app.PlancakeList;
import com.plancake.android.app.PlancakeTag;
import com.plancake.android.app.PlancakeTask;
import com.plancake.android.app.R;
import com.plancake.android.app.R.id;
import com.plancake.android.app.R.layout;
import com.plancake.android.app.R.string;
import com.plancake.android.app.arrayAdapter.TaskItemAdapter;
import com.plancake.api.client.PlancakeTaskForApi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class TasksActivity extends PlancakeFullActivity {

	private DbAdapter dbAdapter;
	
	private ArrayList<PlancakeTask> tasks = null;
	
	public static final int FILTERED_BY_LIST_MODE = 1;	
	public static final int FILTERED_BY_TAG_MODE = 2;
	public static final int IN_CALENDAR_MODE = 3;
	public static final int STARRED_MODE = 4;
	
	// it is good to put 'view note' first in the case you are marking as done
	// because you didn't notice the task has got a note
	public static final int CONTEXT_MENU_ITEM_VIEW_NOTE = ContextMenu.FIRST + 10;		
	public static final int CONTEXT_MENU_ITEM_MARK_AS_DONE = ContextMenu.FIRST + 20;
	public static final int CONTEXT_MENU_ITEM_MARK_AS_INCOMPLETE = ContextMenu.FIRST + 21;
	public static final int CONTEXT_MENU_ITEM_EDIT = ContextMenu.FIRST + 30;	
	public static final int CONTEXT_MENU_ITEM_DELETE = ContextMenu.FIRST + 40;
	
	public int listId = 0;
	public int tagId = 0;
	public int mode = 0;
	
    private TaskItemAdapter taskItemAdapter;
    ListView tasksSetView = null;
    PlancakeTask selectedTaskForEditing = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tasks);
       
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
        {
        	listId = extras.getInt("listId");
        	tagId = extras.getInt("tagId");
        	int starred = extras.getInt("starred");        	

            if (listId > 0)
            {
            	mode = TasksActivity.FILTERED_BY_LIST_MODE;
            }
            else if (tagId > 0)
            {
            	mode = TasksActivity.FILTERED_BY_TAG_MODE;        	
            }
            else
            {
            	mode = TasksActivity.STARRED_MODE;               	
            }
        }
        else
        {
        	mode = TasksActivity.IN_CALENDAR_MODE;
        }

        this.dbAdapter = new DbAdapter(this);
		this.dbAdapter.open();        
        
        Resources resources = this.getResources();
        TextView titleView = (TextView)findViewById(R.id.tasksSetTitle);		
		
		PlancakeList list = null; 
		PlancakeTag tag = null;
		String activityTitle = null;
		
        if (mode == FILTERED_BY_LIST_MODE)
        {
			list = this.dbAdapter.getListById(listId);
			activityTitle = resources.getString(R.string.tasks_in_list_view_title) + " " + list.name; 
        }
        else if (mode == FILTERED_BY_TAG_MODE)
        {
			tag = this.dbAdapter.getTagById(tagId);
			activityTitle = resources.getString(R.string.starred_tasks_title);         	
        }
        else if (mode == STARRED_MODE)
        {
			activityTitle = resources.getString(R.string.starred_tasks_title);         	
        }
        else // calendar mode
        {
			activityTitle = resources.getString(R.string.tasks_in_calendar_title);         	
        }
        titleView.setText(activityTitle);
        
        if ( (mode == FILTERED_BY_LIST_MODE) && (list.isTodoList()) ) // we don't display items with dueDate for Todo list
        {    	
            TextView todoListAlertView = (TextView)findViewById(R.id.noTasksWithDueDateForTodoList);
            todoListAlertView.setVisibility(TextView.VISIBLE);
        }
        
        
        tasks = new ArrayList<PlancakeTask>();        
        tasksSetView = (ListView)findViewById(R.id.tasksSet);   
        
        Cursor tasksCursor = null;
        
        if (mode == FILTERED_BY_LIST_MODE)
        {
	        if (! list.isTodoList()) // we don't display items with dueDate for Todo list
	        {
		        tasksCursor = this.dbAdapter.getTasksWithDueDateByListId(listId);                      
		        if (tasksCursor.moveToFirst())
		        {
		            do
		        	{   
		        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
		        	} while(tasksCursor.moveToNext());
		        }
	        }
	        
	        tasksCursor = this.dbAdapter.getTasksWithoutDueDateByListId(listId);                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }        
        }
        else if (mode == FILTERED_BY_TAG_MODE)
        {
	        tasksCursor = this.dbAdapter.getTasksWithDueDateByTagId(tagId);                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }
	        
	        tasksCursor = this.dbAdapter.getTasksWithoutDueDateByTagId(tagId);                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }           	
        }
        else if (mode == STARRED_MODE)
        {
	        tasksCursor = this.dbAdapter.getStarredTasksWithDueDate();                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }
	        
	        tasksCursor = this.dbAdapter.getStarredTasksWithoutDueDate();                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }           	
        }        
        else // calendar mode
        {
	        tasksCursor = this.dbAdapter.getTasksWithDueDate();                      
	        if (tasksCursor.moveToFirst())
	        {
	            do
	        	{          	
	        		tasks.add(dbAdapter.getTaskFromCursor(tasksCursor));           		
	        	} while(tasksCursor.moveToNext());
	        }           	
        }
	         
        if (tasksCursor != null)
        {
        	tasksCursor.close();
        }
        
        taskItemAdapter = new TaskItemAdapter(this, R.layout.task_item, tasks, mode, dbAdapter);        
        tasksSetView.setAdapter(taskItemAdapter);
        
        registerForContextMenu(tasksSetView);
    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	// we get here only after the EditInboxActivity
    	
    	// we need to refresh the Inbox after the user add/edit a task in the Inbox
    	
    	int inboxListId = dbAdapter.getInboxListId();    	
    	if (listId == inboxListId)
    	{
    		if (data != null) // data is null if you push the 'back' button while editing a task
    		{
        		data.getData();
	    		long taskId = data.getLongExtra(EditInboxActivity.EXTRA_RESULT_TASK_ID, 0);
	    		boolean isNew = data.getBooleanExtra(EditInboxActivity.EXTRA_RESULT_IS_NEW, true);
	    		PlancakeTask task = dbAdapter.getTaskById(taskId);
	    		
	    		if (isNew)
	    		{
	        		taskItemAdapter.add(task);    			
	    		}
	    		else // the task was edited
	    		{
	    			int position = taskItemAdapter.getPosition(selectedTaskForEditing);
	    			taskItemAdapter.remove(selectedTaskForEditing);
	    			taskItemAdapter.insert(task, position);
	    		}
	    		taskItemAdapter.notifyDataSetChanged();
    		} 		
    	}
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu,
    								View v,
    								ContextMenu.ContextMenuInfo menuInfo)
    {
    	super.onCreateContextMenu(menu, v, menuInfo);
    	
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
    	
    	PlancakeTask task = taskItemAdapter.getItem(info.position);
    	
    	menu.setHeaderTitle(R.string.context_menu_title);
    	
    	int inboxListId = dbAdapter.getInboxListId();
 
    	// it is good to put 'view note' first in the case you are marking as done
    	// because you didn't notice the task has got a note
    	if ( (task.note != null) && (task.note.length() > 0) )
    	{
    		menu.add(CONTEXT_MENU_ITEM_VIEW_NOTE, CONTEXT_MENU_ITEM_VIEW_NOTE, Menu.NONE, R.string.view_note);    		
    	}      	
    	
    	if ( (!task.isCompleted) && (!task.isModifiedLocally) )
    	{    	
    		menu.add(CONTEXT_MENU_ITEM_MARK_AS_DONE, CONTEXT_MENU_ITEM_MARK_AS_DONE, Menu.NONE, R.string.mark_as_done);
    	}
    	
    	if ( task.isCompleted )
    	{    	
    		menu.add(CONTEXT_MENU_ITEM_MARK_AS_INCOMPLETE, CONTEXT_MENU_ITEM_MARK_AS_INCOMPLETE, Menu.NONE, R.string.mark_as_incomplete);
    	}        	
    	
    	// the user can edit tasks that have been added locally to the inbox
    	if ( (listId == inboxListId) &&
    		  task.isModifiedLocally &&
    		  !task.isCompleted)
    	{
    		menu.add(CONTEXT_MENU_ITEM_EDIT, CONTEXT_MENU_ITEM_EDIT, Menu.NONE, R.string.edit);
    		menu.add(CONTEXT_MENU_ITEM_DELETE, CONTEXT_MENU_ITEM_DELETE, Menu.NONE, R.string.delete);
    	}
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // Here's how you can get the correct item in onContextItemSelected()
    	AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
    	PlancakeTask task = taskItemAdapter.getItem(info.position);
    	
    	switch(item.getItemId())
    	{
	    	case (CONTEXT_MENU_ITEM_VIEW_NOTE):
	    	{
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(TasksActivity.this);
            	alertDialog.setMessage(task.note);
        		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {					
    				}
    			});
        		alertDialog.show();
	    		
	    		return true;
	    	}
	    	case (CONTEXT_MENU_ITEM_EDIT):
	    	{
				Intent intent = new Intent(this, EditInboxActivity.class);
				// startActivityForResult rather than startActivity is useful
				// if the user clicks 'Add to Inbox' from the Inbox bacause
				// the app can refresh the listing and show the new item in the
				// Inbox
				selectedTaskForEditing = task;
				intent.putExtra("taskId", task.id);				
				startActivityForResult(intent, 0);				
	    		return true;
	    	}    
	    	case (CONTEXT_MENU_ITEM_DELETE):
	    	{
				dbAdapter.deleteTask(task);
				taskItemAdapter.remove(task);
				taskItemAdapter.notifyDataSetChanged();
				
			    new AlertDialog.Builder(TasksActivity.this)
				.setMessage(getString(R.string.task_edit_success))
				.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				})
				.show();				
	    		return true;
	    	}
	    	case (CONTEXT_MENU_ITEM_MARK_AS_INCOMPLETE):
	    	{
				PlancakeTask newTask = dbAdapter.markTaskAsIncomplete(task);
				//taskItemAdapter.remove(task);
				//taskItemAdapter.notifyDataSetChanged();
				taskItemAdapter.remove(task);
				taskItemAdapter.insert(newTask, info.position);
				taskItemAdapter.notifyDataSetChanged();				
	    		return true;
	    	}
	    	case (CONTEXT_MENU_ITEM_MARK_AS_DONE):
	    	{
				PlancakeTask newTask = dbAdapter.markTaskAsDone(task);
				// LinearLayout v = (LinearLayout)tasksSetView.getChildAt(info.position);
				// TextView t = (TextView) v.findViewById(R.id.taskDescription);
				// t.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
				taskItemAdapter.remove(task);
				taskItemAdapter.insert(newTask, info.position);
				taskItemAdapter.notifyDataSetChanged();
	    		return true;
	    	}  	    	
    	}
    	
    	return false;
    }    

    @Override    
    public void onDestroy()
    {
    	this.dbAdapter.close();
    	super.onDestroy();
    }
}
