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

package com.plancake.android.app.arrayAdapter;


import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;

import java.util.*;

import com.plancake.android.app.PlancakeList;
import com.plancake.android.app.R;
import com.plancake.android.app.R.color;
import com.plancake.android.app.R.id;

import android.view.*;
import android.widget.*;

public class ListItemAdapter extends ArrayAdapter<PlancakeList> {

  int resource;
  Context context;

  public ListItemAdapter(Context context, int resource, List<PlancakeList> _items) {
    super(context, resource, _items);
    this.resource = resource;
    this.context  = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LinearLayout listsView = null;

    PlancakeList item = getItem(position);

    if (item != null)
    {    
	    String listName = item.name;
	    
	    if (convertView == null) {    	
	      listsView = new LinearLayout(getContext());
	      String inflater = Context.LAYOUT_INFLATER_SERVICE;
	      LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
	      vi.inflate(this.resource, listsView, true);
	    } else {
	    	listsView = (LinearLayout) convertView;
	    }
	
	    TextView listNameView = (TextView)listsView.findViewById(R.id.listName);      
	    listNameView.setText(listName);
	    
	    Resources r = this.context.getResources();
	    
	    if (item.isHeader)
	    {
	    	//listNameView.setBackgroundColor(R.color.list_header_background_color);
	    	listNameView.setBackgroundResource(R.color.list_header_background_color);
	    	//listNameView.setTextColor(R.color);
	    }
	    else
	    {
	    	listNameView.setBackgroundResource(R.color.list_item_background_color);
	    	//listNameView.setBackgroundColor(Color.BLACK);    	
	    	//listNameView.setTextColor(R.color);
	    }
    }

    return listsView;
  }
}
