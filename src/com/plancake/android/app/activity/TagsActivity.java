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
import com.plancake.android.app.PlancakeTag;
import com.plancake.android.app.R;
import com.plancake.android.app.R.id;
import com.plancake.android.app.R.layout;
import com.plancake.android.app.arrayAdapter.TagItemAdapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class TagsActivity extends PlancakeFullActivity {

	private DbAdapter dbAdapter;
	
	private ArrayList<PlancakeTag> tags = null;
	private PlancakeTag selectedTag = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tags);      

        this.dbAdapter = new DbAdapter(this);
		this.dbAdapter.open();
        
        ListView tagsSetView = (ListView)findViewById(R.id.tagsSet);
        
        tags = new ArrayList<PlancakeTag>();
        
        Cursor tagsCursor = this.dbAdapter.getAllTags();              
        
        PlancakeTag tag;
        
        if (tagsCursor.moveToFirst())
        {
            do
        	{
            	tag = new PlancakeTag(tagsCursor.getInt(DbAdapter.DATABASE_COLUMN_TAG_ID_INDEX),
            						  tagsCursor.getString(DbAdapter.DATABASE_COLUMN_TAG_NAME_INDEX),
            						  tagsCursor.getInt(DbAdapter.DATABASE_COLUMN_TAG_SORT_ORDER_INDEX));
        		tags.add(tag);       		
        	} while(tagsCursor.moveToNext());
        }      
        
        if (tagsCursor != null)
        {
        	tagsCursor.close();
        }
        
        final TagItemAdapter tia;
        
        tia = new TagItemAdapter(this, R.layout.tag_item, tags);
        
        tagsSetView.setAdapter(tia);
       
        tagsSetView.setOnItemClickListener(new OnItemClickListener() {
        	public void onItemClick(AdapterView _av, View _v, int _index, long arg3)
        	{        		
        		selectedTag = tags.get(_index);      		
    		
    			// launching tasks activity        			
    			Intent getTasksIntent = new Intent(TagsActivity.this, TasksActivity.class);
    			getTasksIntent.putExtra("tagId", selectedTag.id);
    			startActivity(getTasksIntent);
        	}        	
        });
    }
    
    public void onDestroy()
    {
    	this.dbAdapter.close();
    	super.onDestroy();
    }
}
