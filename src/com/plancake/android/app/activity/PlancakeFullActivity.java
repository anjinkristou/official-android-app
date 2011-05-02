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

import java.io.OutputStreamWriter;

import com.plancake.android.app.Account;
import com.plancake.android.app.R;
import com.plancake.android.app.Syncronizer;
import com.plancake.android.app.Utils;
import com.plancake.android.app.R.string;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

public class PlancakeFullActivity extends Activity{
	
	static final public int MENU_ITEM_ADD_TO_INBOX = Menu.FIRST + 5;	
	static final public int MENU_ITEM_FILTER_BY_LIST = Menu.FIRST + 10;
	static final public int MENU_ITEM_FILTER_BY_TAG = Menu.FIRST + 15;
	static final public int MENU_ITEM_CALENDAR = Menu.FIRST + 20;
	static final public int MENU_ITEM_SYNC = Menu.FIRST + 30;
	static final public int MENU_ITEM_STARRED = Menu.FIRST + 32;
	static final public int MENU_ITEM_RESET_DATA = Menu.FIRST + 35;
	static final public int MENU_ITEM_ABOUT = Menu.FIRST + 40;
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
    	super.onCreateOptionsMenu(menu);

    	MenuItem menuItemAddToInbox = menu.add(0, PlancakeFullActivity.MENU_ITEM_ADD_TO_INBOX, PlancakeFullActivity.MENU_ITEM_ADD_TO_INBOX, R.string.menu_item_add_to_inbox);
    	MenuItem menuItemFilterByList = menu.add(0, PlancakeFullActivity.MENU_ITEM_FILTER_BY_LIST, PlancakeFullActivity.MENU_ITEM_FILTER_BY_LIST, R.string.menu_item_filter_by_list);
    	MenuItem menuItemFilterByTag = menu.add(0, PlancakeFullActivity.MENU_ITEM_FILTER_BY_TAG, PlancakeFullActivity.MENU_ITEM_FILTER_BY_TAG, R.string.menu_item_filter_by_tag);
    	MenuItem menuItemCalendar = menu.add(0, PlancakeFullActivity.MENU_ITEM_CALENDAR, PlancakeFullActivity.MENU_ITEM_CALENDAR, R.string.menu_item_calendar);
    	MenuItem menuItemSync = menu.add(0, PlancakeFullActivity.MENU_ITEM_SYNC, PlancakeFullActivity.MENU_ITEM_SYNC, R.string.menu_item_sync);
    	MenuItem menuItemAbout = menu.add(0, PlancakeFullActivity.MENU_ITEM_ABOUT, PlancakeFullActivity.MENU_ITEM_ABOUT, R.string.menu_item_about);
    	MenuItem menuItemResetData = menu.add(0, PlancakeFullActivity.MENU_ITEM_RESET_DATA, PlancakeFullActivity.MENU_ITEM_RESET_DATA, R.string.menu_item_reset_data);
    	MenuItem menuItemStarred = menu.add(0, PlancakeFullActivity.MENU_ITEM_STARRED, PlancakeFullActivity.MENU_ITEM_STARRED, R.string.menu_item_starred);

    	
    	menuItemAddToInbox.setIcon(android.R.drawable.ic_menu_add);
    	menuItemFilterByList.setIcon(android.R.drawable.ic_search_category_default);
    	menuItemFilterByTag.setIcon(android.R.drawable.ic_menu_sort_by_size);
    	menuItemCalendar.setIcon(android.R.drawable.ic_menu_today);
    	menuItemSync.setIcon(android.R.drawable.ic_menu_upload);
    	menuItemAbout.setIcon(android.R.drawable.ic_menu_help);
    	menuItemStarred.setIcon(R.drawable.ic_menu_starred);      	
    	// ic_menu_upload - scheduled for uplaod

    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {   	
    	super.onOptionsItemSelected(item);
    	
    	switch (item.getItemId())
    	{
	    	case (PlancakeFullActivity.MENU_ITEM_ADD_TO_INBOX):
	    	{
	    		String className = this.getClass().getName();    		
	    		
	    		// if we are in the EditInboxActivity we don't want to do this before
	    		// that activity itself will do something by overriding the 
	    		// onOptionsItemSelected method
	    		if (!className.equals("com.plancake.android.app.activity.EditInboxActivity"))
	    		{
	    			Intent intent = new Intent(this, EditInboxActivity.class);
					// startActivityForResult rather than startActivity is useful
					// if the user clicks 'Add to Inbox' from the Inbox bacause
					// the app can refresh the listing and show the new item in the
					// Inbox
					startActivityForResult(intent, 0);
	    		}
	    		return true;
	    	}
	    	case (PlancakeFullActivity.MENU_ITEM_FILTER_BY_LIST):
	    	{
				Intent intent = new Intent(this, ListsActivity.class);
				startActivity(intent);		    		
	    		return true;
	    	}
	    	case (PlancakeFullActivity.MENU_ITEM_FILTER_BY_TAG):
	    	{
				Intent intent = new Intent(this, TagsActivity.class);
				startActivity(intent);		    		
	    		return true;
	    	}
	    	case (PlancakeFullActivity.MENU_ITEM_CALENDAR):
	    	{
				Intent intent = new Intent(this, TasksActivity.class);
				startActivity(intent);		    		
	    		return true;
	    	}
	    	case (PlancakeFullActivity.MENU_ITEM_STARRED):
	    	{
    			Intent starredTasksIntent = new Intent(this, TasksActivity.class);
    			starredTasksIntent.putExtra("starred", 1);
    			startActivity(starredTasksIntent);		    		
	    		return true;
	    	}	    	
	    	case (PlancakeFullActivity.MENU_ITEM_SYNC):
	    	{
	            final Account account = new Account(this);
	            
                if (account.isLastSyncTooOld())
                {
                	showAlertDialog(getString(R.string.last_sync_too_old_error));                	
                	return false;
                }	    		
	    		
                if (account.syncsLeftToday(this) == 0)
                {
                	showAlertDialog(getString(R.string.no_more_sync_left) + " " + getString(R.string.become_supporter_for_more_syncs));                	
                	return false;
                }                
                
	    		// checking an Internet connection is available
                if (! Utils.isNetworkAvailable(this))
                {
                	showAlertDialog(getString(R.string.internet_connection_is_needed));                	
                	return false;
                }
	    		// checking the authentication is still valid
	            if (account.authInfoAlreadySet())
	            {     	
                	// checking auth info are correct
    				if (! account.checkAuthentication(this, false))
    				{
    					// something wrong, let's get the user to authenticate again;
    					// probably they changed their password
                    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlancakeFullActivity.this);
                    	alertDialog.setMessage(getString(R.string.wrong_authentication));
                		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
                			@Override
                			public void onClick(DialogInterface dialog, int which) {
            					Intent intent = new Intent(PlancakeFullActivity.this, UserAuthenticationActivity.class);
            					startActivity(intent);
                			}
                		});	    		      	
                    	alertDialog.show();                    	
    					
    					return false;
    				}
	            }
	    		
				// launching the synchronization in a new Thread
				new SyncTask().execute();	    		
	    		return true;
	    	}
	    	case (PlancakeFullActivity.MENU_ITEM_RESET_DATA):
	    	{
				Intent intent = new Intent(this, ResetDataActivity.class);
				startActivity(intent);		    		
	    		return true;
	    	}		    	
	    	case (PlancakeFullActivity.MENU_ITEM_ABOUT):
	    	{
				Intent intent = new Intent(this, AboutActivity.class);
				startActivity(intent);		    		
	    		return true;
	    	}		    	
    	}
    	
    	return false;
    }
    
    private void showAlertDialog(String message)
    {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlancakeFullActivity.this);
    	alertDialog.setMessage(message);
		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {					
			}
		});	    		      	
    	alertDialog.show();
    }     
	
    private class SyncTask extends AsyncTask<Void, Void, Boolean> {
    	   
    	private final ProgressDialog progressDialog = new ProgressDialog(PlancakeFullActivity.this);
    	
        protected void onPreExecute() {
        	this.progressDialog.setMessage(getString(R.string.delta_syncing_lists_and_tasks));
        	this.progressDialog.show();
        }
   	
        protected Boolean doInBackground(Void... v) {
        	boolean error = false;
	        try
	        {
	    		Syncronizer syncronizer = new Syncronizer(PlancakeFullActivity.this, Syncronizer.SYNCRONIZER_DELTA_MODE);
	    		boolean syncronizationResult = syncronizer.syncronize();
	    		
	    		if (! syncronizationResult)
	    		{
	        		error = true;
	    		}
			}
	    	catch(Exception e)
	    	{
	    		Log.e("PLANCAKE", "change", e);
	    		error = true;
	    	}
	    	return new Boolean(!error);
        }

        protected void onPostExecute(Boolean result) {
            this.progressDialog.dismiss();
            
            
            
            if (result.booleanValue()) // sync has been done successfully
            {
            	// success message and tell how many synchronizations left
	            final Account account = new Account(PlancakeFullActivity.this);  
	            account.increaseNumberOfSyncsToday();
	            String message = getString(R.string.delta_sync_successful_and_left_syncs) + " "  + account.syncsLeftToday(PlancakeFullActivity.this) + ".";
	            
	            if (!account.isSupporter())
	            {
	            	message += " " + getString(R.string.become_supporter_for_more_syncs);
	            }
	            
	        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlancakeFullActivity.this);
	        	alertDialog.setTitle("SUCCESS");	        	
	        	alertDialog.setMessage(message);
	    		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
					@Override
					public void onClick(DialogInterface dialog, int which) {
		            	// by "redirecting" to the lists screen, we don't need to refresh the current screen
						Intent intent = new Intent(PlancakeFullActivity.this, ListsActivity.class);
						startActivity(intent); 
					}
				});	    		      	
	        	alertDialog.show();           	
            }
            else
            {
            	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlancakeFullActivity.this);
            	alertDialog.setTitle("ERROR");            	
            	alertDialog.setMessage(getString(R.string.error_during_syncronization));
        		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
    				@Override
    				public void onClick(DialogInterface dialog, int which) {
		            	// by "redirecting" to the lists screen, we don't need to refresh the current screen
						Intent intent = new Intent(PlancakeFullActivity.this, ListsActivity.class);
						startActivity(intent);     					
    				}
    			});	    		      	
            	alertDialog.show();            	
            }
        }
        
        private void showErrorDialog(String message, boolean isError)
        {
        	AlertDialog.Builder alertDialog = new AlertDialog.Builder(PlancakeFullActivity.this);
        	
        	if (isError)
        	{
        		alertDialog.setTitle("ERROR");
        	}
        	
        	alertDialog.setMessage(message);
    		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {					
				}
			});	    		      	
        	alertDialog.show();
        }        
    }
}
