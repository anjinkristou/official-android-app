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

import com.plancake.android.app.Account;
import com.plancake.android.app.DbAdapter;
import com.plancake.android.app.PlancakeList;
import com.plancake.android.app.R;
import com.plancake.android.app.Utils;
import com.plancake.android.app.R.id;
import com.plancake.android.app.R.layout;
import com.plancake.android.app.arrayAdapter.ListItemAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class ListsActivity extends PlancakeFullActivity {

	private DbAdapter dbAdapter;
	
	private ArrayList<PlancakeList> lists = null;
	private PlancakeList selectedList = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lists);    

        final Account account = new Account(this);
        if (account.authInfoAlreadySet())
        {
            if (Utils.isNetworkAvailable(this))
            {        	
            	// checking auth info are correct
				if (! account.checkAuthentication(this, false))
				{					
					// something wrong, let's get the user to authenticate again;
					// probably they changed their password
					Intent intent = new Intent(ListsActivity.this, UserAuthenticationActivity.class);
					startActivity(intent);
					return;
				}
            }
        }
        else
        {
			Intent intent = new Intent(ListsActivity.this, UserAuthenticationActivity.class);
			startActivity(intent);
			return;
        }        
        
        this.dbAdapter = new DbAdapter(this);
		this.dbAdapter.open();
        
        ListView listsSetView = (ListView)findViewById(R.id.listsSet);
        
        lists = new ArrayList<PlancakeList>();
        
        lists.add(this.dbAdapter.getListById(this.dbAdapter.getInboxListId()));
        lists.add(this.dbAdapter.getListById(this.dbAdapter.getTodoListId()));        
        
        Cursor listsCursor = this.dbAdapter.getAllUserLists();              
        
        PlancakeList list;
        
        if (listsCursor.moveToFirst())
        {
            do
        	{
            	list = new PlancakeList(listsCursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_ID_INDEX),
            							listsCursor.getString(DbAdapter.DATABASE_COLUMN_LIST_NAME_INDEX),
            							listsCursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER_INDEX),
            							DbAdapter.fromIntToBoolean(listsCursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER_INDEX)),
            							DbAdapter.fromIntToBoolean(listsCursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX_INDEX)));
        		lists.add(list);       		
        	} while(listsCursor.moveToNext());
        }
        
        if (listsCursor != null)
        {
        	listsCursor.close();
        }
        
        final ListItemAdapter lia;
        
        lia = new ListItemAdapter(this, R.layout.list_item, lists);
        
        listsSetView.setAdapter(lia);
       
        listsSetView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView _av, View _v, int _index, long arg3)
        	{        		
        		selectedList = lists.get(_index);

        		if (! selectedList.isHeader)
        		{
        			// launching tasks activity        			
        			Intent getTasksIntent = new Intent(ListsActivity.this, TasksActivity.class);
        			getTasksIntent.putExtra("listId", selectedList.id);
        			startActivity(getTasksIntent);
        		}
        	}        	
        });
    }
    
    public void onDestroy()
    {
    	this.dbAdapter.close();
    	super.onDestroy();
    }
}
