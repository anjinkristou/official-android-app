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

package com.plancake.android.app;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.plancake.api.client.PlancakeApiClient;
import com.plancake.api.client.PlancakeApiException;
import com.plancake.api.client.PlancakeListForApi;
import com.plancake.api.client.PlancakeRepetitionOptionForApi;
import com.plancake.api.client.PlancakeSettingsForApi;
import com.plancake.api.client.PlancakeTagForApi;
import com.plancake.api.client.PlancakeTaskForApi;
import com.plancake.api.client.PlancakeTasksFilterOptions;

import android.app.Activity;
import android.database.Cursor;
import android.util.Log;

public class Syncronizer {

	private Activity callerActivity;
	
	private PlancakeApiClient plancakeApiClient;
	
	private Account account;
	
	private DbAdapter dbAdapter;
	
	private int mode;
	
	public static final int SYNCRONIZER_FULL_MODE = 1;	
	public static final int SYNCRONIZER_DELTA_MODE = 2;
	
	public Syncronizer(Activity _callerActivity, int _mode)
	{
		callerActivity = _callerActivity;
		plancakeApiClient = null;
		account = null;
		mode = _mode;
		
		dbAdapter = new DbAdapter(callerActivity);
		dbAdapter.open();		
	}
		
	/**
	 * 
	 * @return false if there was an error during syncronization (i.e.: lost connection)
	 * @throws Exception 
	 */
	public boolean syncronize() throws Exception
	{
    	if (! Utils.isNetworkAvailable(this.callerActivity))
    	{
    		return false;    		     		
    	}
    	
    	account = new Account(this.callerActivity);    	

    	String apiKey = this.callerActivity.getString(R.string.plancake_api_key);
    	String apiSecret = this.callerActivity.getString(R.string.plancake_api_secret);    	
    	String apiEndpointUrl = this.callerActivity.getString(R.string.plancake_api_endpoint_url); 
    	String emailAddress = account.getEmailAddress();
    	String password = account.getPassword();

		plancakeApiClient = new PlancakeApiClient(apiKey, 
													apiSecret, 
													apiEndpointUrl,
													emailAddress,
													password);
		
		long fromTimestamp = 0;
		long toTimestamp = 0;

		boolean ok = true;
		
    	if(mode == SYNCRONIZER_FULL_MODE)
    	{
    		try
    		{
	    		// in this case, all the tables should be empty, but, just in case, we launch:
	    		dbAdapter.resetAllTables();		
	    		
	    		// we actually don't need to get the user settings
	    		// downloadUserSettingsFromPlancake()
	    		downloadListsFromPlancake();
	    		downloadRepetitionsFromPlancake();
	    		downloadTagsFromPlancake();
	    		downloadTasksFromPlancake();
				toTimestamp = plancakeApiClient.getServerTime();	    		
    		}
    		catch(Exception e)
    		{
    			ok = false;
    		}
    	}
    	else if(mode == SYNCRONIZER_DELTA_MODE)
    	{
    		// we actually don't need to get the user settings
    		// downloadUserSettingsFromPlancake() 
			try
			{
				// the beauty of all the commands in this try/catch block is that
				// if one of them fails throwing an exception they can re-run
				// with no side-effect
				
				fromTimestamp = account.getLastSyncTime();
				toTimestamp = plancakeApiClient.getServerTime();
				
				sendLocalInbox();
				sendCompletedTasks();
				
				String[] whatHasChanged = plancakeApiClient.whatHasChanged(fromTimestamp, toTimestamp);			
				
				String whatHasChangedItem = null;
				for (int j=0; j < whatHasChanged.length; j++)
				{
					whatHasChangedItem = whatHasChanged[j];		
					
					if(whatHasChangedItem.equals("repetitions"))
					{
						this.syncRepetitions(fromTimestamp, toTimestamp);	
					}
					if(whatHasChangedItem.equals("lists"))
					{	
						this.syncLists(fromTimestamp, toTimestamp);	
					}
					if(whatHasChangedItem.equals("tags"))
					{
						this.syncTags(fromTimestamp, toTimestamp);	
					}
					if(whatHasChangedItem.equals("tasks"))
					{
						this.syncTasks(fromTimestamp, toTimestamp);	
					}					
					if(whatHasChangedItem.equals("deletedTasks"))
					{
						this.syncDeletedTasks(fromTimestamp, toTimestamp);	
					}	
					if(whatHasChangedItem.equals("deletedTags"))
					{
						this.syncDeletedTags(fromTimestamp, toTimestamp);	
					}	
					if(whatHasChangedItem.equals("deletedLists"))
					{
						this.syncDeletedLists(fromTimestamp, toTimestamp);	
					}	
				}			
			} catch(Exception e) {
				e.printStackTrace();
				ok = false;
			}    			  			
    	}
    	
    	try
    	{
    		dbAdapter.close();
    	}
		catch (Exception e3)
		{
		}    	
    	
    	if (!ok)
    	{
    		return false;
    	}
		
		// if we are here everything went ok
    	
		try
		{
	    	account.setLastSyncTime(toTimestamp);
		}
		catch(Exception e)
		{
			return false;
		}
		
		return true;
	}
	
	/**
	 * Not used now
	 * 
	 * @return
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws PlancakeApiException 
	 * @throws MalformedURLException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 */
	private void downloadUserSettingsFromPlancake() 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException
	{
		PlancakeSettingsForApi plancakeSettings = plancakeApiClient.getUserSettings();
		account.setDateFormat(plancakeSettings.dateFormat);
		account.setDstActive(plancakeSettings.dstActive);
		account.setTimezoneDescription(plancakeSettings.timezoneDescription);
		account.setTimezoneDst(plancakeSettings.timezoneDst);
		account.setTimezoneOffset(plancakeSettings.timezoneOffset);
	}
	
	private void downloadListsFromPlancake() 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{
		List<PlancakeListForApi> lists = plancakeApiClient.getLists();
		
		  Iterator<PlancakeListForApi> iterator = lists.iterator();
		  while ( iterator.hasNext() ){			        
			  PlancakeListForApi list = iterator.next();
		      dbAdapter.insertList(list);
		  }										
	}
	
	private void downloadRepetitionsFromPlancake() throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{
		List<PlancakeRepetitionOptionForApi> repetitions = plancakeApiClient.getRepetitionOptions();
		  Iterator<PlancakeRepetitionOptionForApi> iterator = repetitions.iterator();
		  while ( iterator.hasNext() ){			        
			  PlancakeRepetitionOptionForApi repetition = iterator.next();
		      dbAdapter.insertRepetitionOption(repetition);
		  }									
	}
	
	private void downloadTagsFromPlancake() throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{
		List<PlancakeTagForApi> tags = plancakeApiClient.getTags();
		
		  Iterator<PlancakeTagForApi> iterator = tags.iterator();
		  while ( iterator.hasNext() ){			        
			  PlancakeTagForApi tag = iterator.next();
		      dbAdapter.insertTag(tag);
		  }									
	}	
	
	private void downloadTasksFromPlancake() throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{
		// get only non-completed tasks
		PlancakeTasksFilterOptions taskFilterOptions = new PlancakeTasksFilterOptions();
		taskFilterOptions.completed = false;
		List<PlancakeTaskForApi> tasks = plancakeApiClient.getTasks(taskFilterOptions);
		
		  Iterator<PlancakeTaskForApi> iterator = tasks.iterator();
		  while ( iterator.hasNext() ){			
			  PlancakeTaskForApi task = iterator.next();					  
		      dbAdapter.insertTask(task, false);
		  }									
	}
	
	/**
	 * Sends the tasks in the Inbox created locally and delete them because they don't have a global
	 * taskId (as they were generated locally).
	 * Straight after, I get the generated task with the global taskId and insert it locally.
	 * 
	 * I can't rely on the syncTasks method to get the task generated online because of the toTimestamp constraint.
	 *
	 * @throws Exception 
	 */
	private void sendLocalInbox() 
		throws Exception
	{		
		Cursor c = dbAdapter.getNewTasksFromLocalInbox();
		long localTaskId = 0;
		long globalTaskId = 0;
		PlancakeTask localTask = null;
		PlancakeTaskForApi regeneratedTask = null;
		PlancakeTasksFilterOptions filterOptions = new PlancakeTasksFilterOptions();

		if (c.moveToFirst())
		{
			do
			{
				localTaskId = c.getLong(DbAdapter.DATABASE_COLUMN_TASK_ID_INDEX);
				localTask = dbAdapter.getTaskById(localTaskId);
				globalTaskId = plancakeApiClient.addTask((PlancakeTaskForApi)localTask);
				if (globalTaskId > 0) // the task has been correctly received
				{
					dbAdapter.deleteTask(localTask);
					filterOptions.taskId = globalTaskId;
					regeneratedTask = plancakeApiClient.getTasks(filterOptions).get(0);					
					long a = dbAdapter.insertTask(regeneratedTask, false);
				}
				else
				{
					throw new Exception("error in Syncronizer.sendLocalInbox");
				}
			}
			while (c.moveToNext());
		}
	}
	
	/**
	 * Sends the signals to mark the tasks as done and deletes them locally (because the Android app doesn't
	 * list completed tasks by default).
	 * Straight after, I get the task from online because if it was a recurrent task, it would be still active with a new dueDate.
	 *
	 * I can't rely on the syncTasks method to get the task generated online because of the toTimestamp constraint.
	 * 
	 * @throws IOException 
	 * @throws URISyntaxException 
	 * @throws PlancakeApiException 
	 * @throws MalformedURLException 
	 * @throws NoSuchAlgorithmException 
	 * @throws UnsupportedEncodingException 
	 * @throws SQLException 
	 */
	private void sendCompletedTasks() 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{				
		Cursor c = dbAdapter.getTasksCompletedLocally();
		long localTaskId = 0;
		long globalTaskId = 0;
		PlancakeTask localTask = null;
		PlancakeTaskForApi regeneratedTask = null;
		PlancakeTasksFilterOptions filterOptions = new PlancakeTasksFilterOptions();
		
		if (c.moveToFirst())
		{
			do
			{
				localTaskId = c.getLong(DbAdapter.DATABASE_COLUMN_TASK_ID_INDEX);
				localTask = dbAdapter.getTaskById(localTaskId);
				globalTaskId = plancakeApiClient.completeTask(localTaskId, localTask.dueDate);
				if (globalTaskId > 0)
				{
					dbAdapter.deleteTask(localTask);
					// if the task was repetitive, we need to insert the next occurence
					filterOptions.taskId = globalTaskId;
					regeneratedTask = plancakeApiClient.getTasks(filterOptions).get(0);
					if (regeneratedTask.isCompleted == false)
					{
						dbAdapter.insertTask(regeneratedTask, false);
					}
				}
			}
			while (c.moveToNext());
		}		
	}
	
	private void syncRepetitions(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{	
		List<PlancakeRepetitionOptionForApi> repetitions = plancakeApiClient.getRepetitionOptions(fromTimestamp, toTimestamp);
		Iterator<PlancakeRepetitionOptionForApi> it = repetitions.iterator();
		while (it.hasNext())
		{
			dbAdapter.replaceOrInsertRepetitionOption(it.next());
		}
	}
	
	private void syncLists(long fromTimestamp, long toTimestamp) throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{				
		List<PlancakeListForApi> lists = plancakeApiClient.getLists(fromTimestamp, toTimestamp);
		Iterator<PlancakeListForApi> it = lists.iterator();
		while (it.hasNext())
		{
			// the deep flag is off, because we are not doing a real deletion (as it happens for the syncDeletedXXXX methods)
			// but it is an editing
			dbAdapter.replaceOrInsertList(it.next(), false);
		}		
	}
	
	private void syncTags(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{			
		List<PlancakeTagForApi> tags = plancakeApiClient.getTags(fromTimestamp, toTimestamp);
		Iterator<PlancakeTagForApi> it = tags.iterator();
		while (it.hasNext())
		{
			dbAdapter.replaceOrInsertTag(it.next(), false);
		}			
	}

	/**
	 * The Android app doesn't show completed task, thus every completed task from online is deleted locally
	 */
	private void syncTasks(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException, SQLException
	{		
		PlancakeTasksFilterOptions filterOptions = new PlancakeTasksFilterOptions();
		filterOptions.fromTimestamp = fromTimestamp;
		filterOptions.toTimestamp = toTimestamp;
		filterOptions.completed = false; /*************** DIFFERENCE ***************/
		List<PlancakeTaskForApi> tasks = plancakeApiClient.getTasks(filterOptions);
		Iterator<PlancakeTaskForApi> it = tasks.iterator();
		PlancakeTaskForApi editedTask = null;
		while (it.hasNext())
		{
			editedTask = it.next();
			dbAdapter.replaceOrInsertTask(editedTask, /* this is not deep but isModifiedLocally */ false);
		}
		
		filterOptions = new PlancakeTasksFilterOptions();
		filterOptions.fromTimestamp = fromTimestamp;
		filterOptions.toTimestamp = toTimestamp;
		filterOptions.completed = true; /*************** DIFFERENCE ***************/
		tasks = plancakeApiClient.getTasks(filterOptions);
		it = tasks.iterator();
		editedTask = null;
		while (it.hasNext())
		{
			editedTask = it.next();			
			dbAdapter.deleteTask(editedTask);
		}		
	}
	
	private void syncDeletedTasks(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException
	{	
		ArrayList<Long> deletedTaskIds = plancakeApiClient.getDeletedTasks(fromTimestamp, toTimestamp);
		Iterator<Long> it = deletedTaskIds.iterator();
		while (it.hasNext())
		{
			dbAdapter.deleteTask(dbAdapter.getTaskById(it.next()));
		}			
	}
	
	private void syncDeletedTags(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException
	{	
		ArrayList<Integer> deletedTagIds = plancakeApiClient.getDeletedTags(fromTimestamp, toTimestamp);
		Iterator<Integer> it = deletedTagIds.iterator();
		while (it.hasNext())
		{
			dbAdapter.deleteTag(dbAdapter.getTagById(it.next()), true);
		}		
	}
	
	private void syncDeletedLists(long fromTimestamp, long toTimestamp) 
		throws UnsupportedEncodingException, NoSuchAlgorithmException, MalformedURLException, PlancakeApiException, URISyntaxException, IOException
	{		
		ArrayList<Integer> deletedListIds = plancakeApiClient.getDeletedLists(fromTimestamp, toTimestamp);
		Iterator<Integer> it = deletedListIds.iterator();
		while (it.hasNext())
		{
			dbAdapter.deleteList(dbAdapter.getListById(it.next()), true);
		}			
	}
}
