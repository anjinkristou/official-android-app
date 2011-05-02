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

import com.plancake.android.app.Account;
import com.plancake.android.app.DbAdapter;
import com.plancake.android.app.R;
import com.plancake.android.app.R.layout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class ResetDataActivity extends PlancakeFullActivity {	   
    DbAdapter dbAdapter;

    @Override     
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
    	alertDialog.setMessage(getString(R.string.before_resetting_data_warning));    	
		alertDialog.setNegativeButton("Cancel", new android.content.DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				Intent intent = new Intent(ResetDataActivity.this, ListsActivity.class);
				startActivity(intent);
				return;
			}
		});			
		alertDialog.setPositiveButton("OK", new android.content.DialogInterface.OnClickListener() {				
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dbAdapter = new DbAdapter(ResetDataActivity.this);
				dbAdapter.open();                
		        dbAdapter.resetAllTables();
		        dbAdapter.close();
		        
		        setContentView(R.layout.reset_data);
		        
		        Account account = new Account(ResetDataActivity.this);
		        account.resetPreferences();
		        
			    Button goToUserAuthenticationButton = (Button)findViewById(R.id.goToUserAuthentication);		 
			    goToUserAuthenticationButton.setOnClickListener(new OnClickListener() {	        
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(ResetDataActivity.this, UserAuthenticationActivity.class);
						startActivity(intent);
					}
				});  
			}
		});	    		    	
    	alertDialog.show();              
    }
}
