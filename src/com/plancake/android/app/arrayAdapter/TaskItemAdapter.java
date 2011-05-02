/*************************************************************************************
* ===================================================================================*
* Software by: Danyuki Software Limited                                              *
* This file is part of Plancake.                                                     *
*                                                                                    *
* Copyright 2009-2010-2011 by:     Danyuki Software Limited                          *
* Support, News, Updates at:  http://www.plancake.com                                *
* Licensed under the AGPL version 3 license.                                         *
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

package com.plancake.android.app.arrayAdapter;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Paint;

import java.util.*;

import com.plancake.android.app.DbAdapter;
import com.plancake.android.app.PlancakeTask;
import com.plancake.android.app.R;
import com.plancake.android.app.R.color;
import com.plancake.android.app.R.id;
import com.plancake.android.app.activity.TasksActivity;

import android.text.util.Linkify;
import android.util.Log;
import android.view.*;
import android.widget.*;

public class TaskItemAdapter extends ArrayAdapter<PlancakeTask> {  
  int resource;
  Context context;
  int mode;
  
  private DbAdapter dbAdapter;  

  public TaskItemAdapter(Context context, int resource, List<PlancakeTask> _items, int mode, DbAdapter dbAdapter) {
    super(context, resource, _items);
    this.resource = resource;
    this.context  = context;
    this.mode = mode;
    this.dbAdapter = dbAdapter;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LinearLayout tasksView;
    Resources r = this.context.getResources();
    
    PlancakeTask item = getItem(position);
    
    if (convertView == null) {    	
      tasksView = new LinearLayout(getContext());
      String inflater = Context.LAYOUT_INFLATER_SERVICE;
      LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
      vi.inflate(this.resource, tasksView, true);
    } else {
    	tasksView = (LinearLayout) convertView;
    }

    String taskDescription = item.description;
    String taskNote = item.note;
    if ((taskNote != null) && (taskNote.length() > 0))
    {
    	taskDescription += " - " + r.getString(R.string.with_note).toUpperCase();
    }
    
    TextView taskDescriptionView = (TextView)tasksView.findViewById(R.id.taskDescription);      
    taskDescriptionView.setText(taskDescription);
    Linkify.addLinks(taskDescriptionView, Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);

    TextView taskListView = (TextView)tasksView.findViewById(R.id.taskList);
    if (mode != TasksActivity.FILTERED_BY_LIST_MODE)
    {    	
	    taskListView.setText("List: " +  dbAdapter.getListById(item.listId).name);
	    taskListView.setVisibility(View.VISIBLE);   
    }
    else
    {
	    taskListView.setVisibility(View.GONE);    	
    }
    taskListView.setBackgroundResource(R.color.task_standard_color);    
    
    String taskDueDate = item.dueDate;
    TextView taskDueDateView = (TextView)tasksView.findViewById(R.id.taskDueDate); 
    if ( (taskDueDate != null) && (taskDueDate.length() > 0) )
    {
		String repetitionExpression = "";
		// check whether the task is recurrent
		if (item.repetitionId > 0)
		{
			DbAdapter dbAdapter = new DbAdapter(context);
			dbAdapter.open();  			
			repetitionExpression = dbAdapter.getRepetitionExpression(item.repetitionId, item.repetitionParam);
			dbAdapter.close();    
		}
		
		
		String taskDueTime = "";
		// check whether the task is recurrent
		if (item.dueTime.length() > 0)
		{
			taskDueTime = "@" + item.getHumanReadableDueTime();   
		}
		
    	
		taskDueDateView.setText(repetitionExpression + "  " + taskDueTime  + "  " + item.getHumanReadableDueDate());
		taskDueDateView.setVisibility(View.VISIBLE);  
    }
    else
    {
      taskDueDateView.setText("");
      taskDueDateView.setVisibility(View.GONE);
    }

    
    if (item.isCompleted)
    {
    	taskDescriptionView.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
    else
    {
    	taskDescriptionView.setPaintFlags(taskDescriptionView.getPaintFlags() & ~Paint.STRIKE_THRU_TEXT_FLAG);    	
    }
    
    if (item.isModifiedLocally)
    {
        // to show a task modified locally, we fill with a color the dueDate TextView. If that
    	// TextView is hidden because the task hasn't got a due date, we need to show it
    	if ((taskDueDate == null) || (taskDueDate.length() == 0))
    	{
    		taskDueDateView.setVisibility(View.VISIBLE);     		
    	}
    	taskDueDateView.setBackgroundResource(R.color.task_modified_locally_color);    	
    }    
    else if (item.isOverdue())
    {
    	taskDueDateView.setBackgroundResource(R.color.task_overdue_color);   	
    }
    else if (item.isDueToday())
    {
    	taskDueDateView.setBackgroundResource(R.color.task_due_today_color);     	
    }
    else if (item.isDueTomorrow())
    {
    	taskDueDateView.setBackgroundResource(R.color.task_due_tomorrow_color);     	
    }
    else
    {
    	taskDueDateView.setBackgroundResource(R.color.task_standard_color);    	
    }    
   
    
    if (item.isHeader)
    {
    	taskDescriptionView.setBackgroundResource(R.color.task_header_background_color);
    }
    else
    {
    	taskDescriptionView.setBackgroundResource(R.color.task_standard_color);
    }
    
    return tasksView;
  }
}
