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

import java.sql.SQLException;

import com.plancake.android.app.DbAdapter;
import com.plancake.android.app.PlancakeTask;
import com.plancake.android.app.R;
import com.plancake.android.app.Syncronizer;
import com.plancake.android.app.R.layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class EditInboxActivity extends PlancakeFullActivity {	

    private long taskId = 0;
    
    EditText taskDescriptionView = null;
	EditText taskNoteView = null;    
	DbAdapter dbAdapter = null;
	
	public static final String EXTRA_RESULT_TASK_ID = "taskId";
	public static final String EXTRA_RESULT_IS_NEW = "isNew";
	
    @Override    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_inbox);
        
        dbAdapter = new DbAdapter(EditInboxActivity.this);
		dbAdapter.open();		        
        
        Bundle extras = getIntent().getExtras(); 
        if(extras !=null)
        {
        	taskId = extras.getLong("taskId");
        }      
        
        taskDescriptionView = (EditText)EditInboxActivity.this.findViewById(R.id.inbox_task_description); 
    	taskNoteView = (EditText)EditInboxActivity.this.findViewById(R.id.inbox_task_note);
    	
    	if (taskId > 0)
    	{
    		PlancakeTask task = dbAdapter.getTaskById(taskId);
        	taskDescriptionView.setText(task.description);
    		taskNoteView.setText(task.note);    		
    	}
    	
	    Button taskSubmitButton = (Button)findViewById(R.id.submit_inbox_task);		 
	    taskSubmitButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {				    	   	
				saveTaskToInbox();
			}
		});        
    }
    
    @Override
    public void onDestroy()
    {
    	this.dbAdapter.close();
    	super.onDestroy();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
    	super.onOptionsItemSelected(item);
    	
    	switch (item.getItemId())
    	{
	    	case (PlancakeFullActivity.MENU_ITEM_ADD_TO_INBOX):
	    	{
	    		saveTaskToInbox();
	    	}
    	}
    	
    	return true;
    }
    
    public void saveTaskToInbox()
    {
    	String taskDescription = taskDescriptionView.getText().toString();
    	final PlancakeTask editedTask; 
    	
    	if (taskDescription.length() > 0)
    	{		    	
	    	String taskNote = taskNoteView.getText().toString();				    	

    		try {
				editedTask = dbAdapter.editLocalInbox(taskId, taskDescription, taskNote);
				
			    new AlertDialog.Builder(EditInboxActivity.this)
				.setMessage(getString(R.string.task_edit_success))
				.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// we always return to the TasksActivity
						Intent result = new Intent();
						result.putExtra(EXTRA_RESULT_TASK_ID, editedTask.id);
						result.putExtra(EXTRA_RESULT_IS_NEW, (taskId == 0));
						setResult(RESULT_OK, result);
						finish();
					}
				})
				.show();						
			} catch (SQLException e) {
			}
    	}    	
    }
}
