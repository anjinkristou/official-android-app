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

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.plancake.android.app.Account;
import com.plancake.android.app.R;
import com.plancake.android.app.Syncronizer;
import com.plancake.android.app.Utils;
import com.plancake.android.app.R.id;
import com.plancake.android.app.R.layout;
import com.plancake.android.app.R.string;
import com.plancake.api.client.*;

public class UserAuthenticationActivity extends Activity {
    /** Called when the activity is first created. */
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_authentication);
        
        final Account account = new Account(this);        
                
        Linkify.addLinks((TextView)findViewById(R.id.need_plancake_account), Linkify.WEB_URLS|Linkify.EMAIL_ADDRESSES);      	
		
	    Button authenticationDetailsSubmitButton = (Button)findViewById(R.id.plancake_authentication_submit_button);		 
	    authenticationDetailsSubmitButton.setOnClickListener(new OnClickListener() {				
			@Override
			public void onClick(View v) {
				if (account.checkAuthentication(UserAuthenticationActivity.this, true))
				{
					sync();
				}
				else
				{
		    		showErrorDialog(getString(R.string.wrong_authentication));						
				}
			}
		});
    }

    @Override    
    public void onStart()
    {
    	super.onStart();
    	this.checkInternetConnectionAvailable();
    }
    

    
    /**
     * This method assumes the authentication has happened successfully and the
     * authentication details have been saved in the SharedPreferences 
     */
    private void sync()
    {
		Account account = new Account(this);
		if (! account.hasNeverSynced())
		{
			// we are here if a lastSyncTime exists, thus we already syncronized once,
			// plus the user needed to fill the authentication form again, probably because they changed their password
			Intent intent = new Intent(UserAuthenticationActivity.this, ListsActivity.class);
			startActivity(intent);	
			// in order to prevent people from coming back to this activity
			UserAuthenticationActivity.this.finish();			
		}
		else
		{    	
			// launching the synchronization in a new Thread
			new SyncTask().execute();
		}
    }
    
    public void onStop()
    {
    	super.onStop();  	
    }

    private void checkInternetConnectionAvailable() {
    	if (! Utils.isNetworkAvailable(this))
    	{
    		this.showErrorDialog(getString(R.string.internet_connection_is_needed), true);    		     		
    	}
    }

    private void showErrorDialog(String message)
    {
    	this.showErrorDialog(message, false);
    }
    
    private void showErrorDialog(String message, boolean showRetryButton)
    {
    	AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	alertDialog.setMessage(message);
    	
    	if (showRetryButton)
    	{
    		alertDialog.setNegativeButton("Retry", new android.content.DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					Intent intent = new Intent(UserAuthenticationActivity.this, UserAuthenticationActivity.class);
					startActivity(intent);
				}
			});	
    	}
    	else
    	{
    		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
				@Override
				public void onClick(DialogInterface dialog, int which) {					
				}
			});	    		
    	}
    	
    	alertDialog.show();
    }
    
    
    
    private class SyncTask extends AsyncTask<Void, Void, Boolean> {
   
    	private final ProgressDialog progressDialog = new ProgressDialog(UserAuthenticationActivity.this);
    	
        protected void onPreExecute() {
        	this.progressDialog.setMessage(getString(R.string.full_syncing_lists_and_tasks));
        	this.progressDialog.show();
        }
   	
        protected Boolean doInBackground(Void... v) {
        	boolean error = false;
	        try
	        {
	    		Syncronizer syncronizer = new Syncronizer(UserAuthenticationActivity.this, Syncronizer.SYNCRONIZER_FULL_MODE);
	    		boolean syncronizationResult = syncronizer.syncronize();
	    		
	    		if (! syncronizationResult)
	    		{
	        		error = true;
	    		}
			}
	    	catch(Exception e)
	    	{
	    		error = true;
	    	}
	    	return new Boolean(!error);
        }

        protected void onPostExecute(Boolean result) {
            //this.progressDialog.dismiss();
            
            if (result.booleanValue()) // sync has been done successfully
            {
				Intent intent = new Intent(UserAuthenticationActivity.this, ListsActivity.class);
				startActivity(intent);
				// in order to prevent people from coming back to this activity				
				UserAuthenticationActivity.this.finish();
            }
            else
            {
            	showErrorDialog(getString(R.string.error_during_syncronization));
            }
        }
    }   
    
    
}
