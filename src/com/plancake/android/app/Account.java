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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Calendar;

import com.plancake.api.client.PlancakeApiClient;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.EditText;

public class Account {

	private static final String PLANCAKE_APP_PREFS_TOKEN = "token";
	private static final String PLANCAKE_APP_PREFS_EMAIL_ADDRESS = "emailAddress";
	private static final String PLANCAKE_APP_PREFS_PASSWORD = "password";
	private static final String PLANCAKE_APP_PREFS_LAST_SYNC_TIME = "lastSyncTime";

	public static final String PLANCAKE_APP_PREFS_ENCRYPTED_SYNCS_NUMBER = "encryptedSyncsNumber";	
	
	private static final String PLANCAKE_APP_PREFS_TIMEZONE_DESCRIPTION = "timezoneDescription";	
	private static final String PLANCAKE_APP_PREFS_TIMEZONE_OFFSET = "timezoneOffset";
	private static final String PLANCAKE_APP_PREFS_TIMEZONE_DST = "timezoneDst";	
	private static final String PLANCAKE_APP_PREFS_DATE_FORMAT = "dateFormat";
	private static final String PLANCAKE_APP_PREFS_DST_ACTIVE = "dstActive";
	
	private static final String PLANCAKE_APP_PREFS = "PlancakeAppPrefs";
	private SharedPreferences sharedPreferences;
	private SharedPreferences.Editor sharedPreferencesEditor;
	
	private Activity activity;
	
	public Account(Activity activity)
	{
		this.activity = activity;
		
		sharedPreferences = activity.getSharedPreferences(Account.PLANCAKE_APP_PREFS, Activity.MODE_PRIVATE);
		this.sharedPreferencesEditor = sharedPreferences.edit();		
		
		if(! sharedPreferences.contains(this.PLANCAKE_APP_PREFS_LAST_SYNC_TIME))
		{
			this.setLastSyncTime(0);
		}
		if(! sharedPreferences.contains(this.PLANCAKE_APP_PREFS_EMAIL_ADDRESS))
		{
			this.setEmailAddress("");
		}
		if(! sharedPreferences.contains(this.PLANCAKE_APP_PREFS_PASSWORD))
		{
			this.setPassword("");
		}		
		if(! sharedPreferences.contains(this.PLANCAKE_APP_PREFS_TOKEN))
		{
			this.setToken("");
		}
		
		this.sharedPreferences = sharedPreferences;
		
		makeSureEncryptedSyncsNumberKeyExists();
	}
	
	public void resetPreferences()
	{
		sharedPreferencesEditor.clear().commit();
	}
	
	public boolean authInfoAlreadySet()
	{		
		return ( (getEmailAddress().length() > 0) &&
				 (getPassword().length() > 0) &&
				 (getLastSyncTime() > 0) ); 
	}
	
	public boolean isSupporter()
	{
		String token = this.getToken();
		return ((token.length() == 41) && (token.charAt(0) == '1'));
	}
	
	public boolean hasNeverSynced()
	{
		return !(this.getLastSyncTime() > 0);		
	}
	
	public void setToken(String token)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_TOKEN, token)
									.commit();
	}
	
	public String getToken()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_TOKEN, "");
	}
	
	public void setEmailAddress(String emailAddress)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_EMAIL_ADDRESS, emailAddress)
									.commit();
	}
	
	public String getEmailAddress()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_EMAIL_ADDRESS, "");
	}	
	
	public void setPassword(String password)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_PASSWORD, password)
									.commit();
	}
	
	public String getPassword()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_PASSWORD, "");
	}
	
	public void setLastSyncTime(long lastSyncTime)
	{
		this.sharedPreferencesEditor.putLong(Account.PLANCAKE_APP_PREFS_LAST_SYNC_TIME, lastSyncTime)
									.commit();
	}
	
	public long getLastSyncTime()
	{
		return this.sharedPreferences.getLong(Account.PLANCAKE_APP_PREFS_LAST_SYNC_TIME, 0L);
	}	
	
	public void setTimezoneDescription(String timezoneDescription)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_TIMEZONE_DESCRIPTION, timezoneDescription)
									.commit();
	}
	
	public String getTimezoneDescription()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_TIMEZONE_DESCRIPTION, "");
	}	
	
	public void setTimezoneOffset(String timezoneOffset)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_TIMEZONE_OFFSET, timezoneOffset)
									.commit();
	}
	
	public String getTimezoneOffset()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_TIMEZONE_OFFSET, "");
	}	
	
	public void setTimezoneDst(boolean timezoneDst)
	{
		this.sharedPreferencesEditor.putBoolean(Account.PLANCAKE_APP_PREFS_TIMEZONE_DST, timezoneDst)
									.commit();
	}
	
	public boolean getTimezoneDst()
	{
		return this.sharedPreferences.getBoolean(Account.PLANCAKE_APP_PREFS_TIMEZONE_DST, false);
	}		

	public void setDateFormat(String dateFormat)
	{
		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_DATE_FORMAT, dateFormat)
									.commit();
	}
	
	public String getDateFormat()
	{
		return this.sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_DATE_FORMAT, "");
	}	
	
	public void setDstActive(boolean dstActive)
	{
		this.sharedPreferencesEditor.putBoolean(Account.PLANCAKE_APP_PREFS_DST_ACTIVE, dstActive)
									.commit();
	}
	
	public boolean getDstActive()
	{
		return this.sharedPreferences.getBoolean(Account.PLANCAKE_APP_PREFS_DST_ACTIVE, false);
	}
	
	public boolean isLastSyncTooOld()
	{
		int maxDaysGapBetweenSyncs = Integer.parseInt(activity.getString(R.string.max_days_gap_between_syncs).trim());		
		return (getLastSyncTime() + (maxDaysGapBetweenSyncs*86400)) < Utils.getNowTimestamp();
	}
	
	
	public void makeSureEncryptedSyncsNumberKeyExists() 
	{
		if(! sharedPreferences.contains(Account.PLANCAKE_APP_PREFS_ENCRYPTED_SYNCS_NUMBER))
		{
			this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_ENCRYPTED_SYNCS_NUMBER, "")
			.commit();
		}
	}

	/**
	 * Information is simply encrypted like this:
	 * _ the day of the month (i.e. 25) is converted to its binary representation: xxxxXXXX
	 * _ the number of syncs (i.e.) is converted to its binary representation: yyyyYYYY
	 * _ then the two pieces of informations are merged this way: XXXXYYYYyyyyxxxx
	 * 
	 * @return
	 */
	private int getNumberOfSyncsToday()
	{
		String encryptedInfo = sharedPreferences.getString(Account.PLANCAKE_APP_PREFS_ENCRYPTED_SYNCS_NUMBER, "");
		
		if ( (encryptedInfo == null) ||  (encryptedInfo.length()== 0) )
		{
			return 0;
		}
		
		String binaryDayOfTheMonth = encryptedInfo.substring(12) + encryptedInfo.substring(0, 4);
		String binaryNumberOfSyncs = encryptedInfo.substring(8, 12) + encryptedInfo.substring(4, 8);
		
		int dayOfTheMonth = Integer.parseInt(binaryDayOfTheMonth, 2);
		int numberOfSyncs = Integer.parseInt(binaryNumberOfSyncs, 2); 
		
		Calendar calToday = Calendar.getInstance();
		int dayOfTheMonthToday = calToday.get(Calendar.DATE);
		
		if (dayOfTheMonth != dayOfTheMonthToday)
		{
			return 0;
		}
		else
		{
			return numberOfSyncs;
		}
	}

	/**
	 * Information is simply encrypted like this:
	 * _ the day of the month (i.e. 25) is converted to its binary representation: xxxxXXXX
	 * _ the number of syncs (i.e.) is converted to its binary representation: yyyyYYYY
	 * _ then the two pieces of informations are merged this way: XXXXYYYYyyyyxxxx
	 * 
	 * @return
	 */	
	public int increaseNumberOfSyncsToday()
	{
		Calendar calToday = Calendar.getInstance();
		int dayOfTheMonthToday = calToday.get(Calendar.DATE);
		
		int numberOfSyncsSoFar = getNumberOfSyncsToday();
		
		int newSyncNumber = numberOfSyncsSoFar+1;
		
		String binaryDayOfTheMonthToday = Utils.lpad(Integer.toBinaryString(dayOfTheMonthToday), 8, '0'); 
		String binaryNewSyncNumber = Utils.lpad(Integer.toBinaryString(newSyncNumber), 8, '0');
				
		String toStore = binaryDayOfTheMonthToday.substring(4) + binaryNewSyncNumber.substring(4) + 
						 binaryNewSyncNumber.substring(0, 4) + binaryDayOfTheMonthToday.substring(0, 4);

		this.sharedPreferencesEditor.putString(Account.PLANCAKE_APP_PREFS_ENCRYPTED_SYNCS_NUMBER, toStore)
		.commit();
		
		return newSyncNumber;
	}
	
	public int syncsLeftToday(Activity activity)
	{
		int maxNumberOfSyncsPerDay = 0;
		if (this.isSupporter())
		{
			maxNumberOfSyncsPerDay = Integer.parseInt(activity.getString(R.string.max_number_of_syncs_per_day_supporter_account).trim());
		}
		else
		{
			maxNumberOfSyncsPerDay = Integer.parseInt(activity.getString(R.string.max_number_of_syncs_per_day_free_account).trim());			
		}
		return (maxNumberOfSyncsPerDay - getNumberOfSyncsToday());
	}
	
    /**
     * 
     * @param context
     * @param fromUserInput - true if we check against what the user has input (that typically happens the first time the
     * 		user uses the application), false if we have the details already stored in the SharedPreferences
     * @return
     */
    public boolean checkAuthentication(Activity context, boolean fromUserInput)
    {    	
    	String apiKey = context.getString(R.string.plancake_api_key);
    	String apiSecret = context.getString(R.string.plancake_api_secret);    	
    	String apiEndpointUrl = context.getString(R.string.plancake_api_endpoint_url);  

    	String emailAddress = "";
    	String password = "";    	
	
    	if (fromUserInput)
    	{ 		
	    	EditText emailAddressEditText = (EditText)context.findViewById(R.id.plancake_email_address_edit_text);
	    	EditText passwordEditText = (EditText)context.findViewById(R.id.plancake_password_edit_text);    	
	    	emailAddress = emailAddressEditText.getText().toString().trim();
	    	password = passwordEditText.getText().toString().trim();    	
    	}
    	else
    	{		
	    	emailAddress = getEmailAddress();
	    	password = getPassword();    		
    	}  	
    	
    	PlancakeApiClient apiClient = null;
    	
    	boolean ok = true;
    	try
    	{ 
    		apiClient = new PlancakeApiClient(apiKey, apiSecret, apiEndpointUrl, emailAddress, password);
    		apiClient.getServerTime();
    		setEmailAddress(emailAddress);
    		setPassword(password);
    		setToken(apiClient.token);
    	}
    	catch(Exception e)
    	{
    		ok = false;
    	}
    	return ok;
    }	
}
