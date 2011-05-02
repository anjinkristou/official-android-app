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

import com.plancake.android.app.PlancakeTag;
import com.plancake.android.app.R;
import com.plancake.android.app.R.id;

import android.view.*;
import android.widget.*;

public class TagItemAdapter extends ArrayAdapter<PlancakeTag> {

  int resource;
  Context context;

  public TagItemAdapter(Context context, int resource, List<PlancakeTag> _items) {
    super(context, resource, _items);
    this.resource = resource;
    this.context  = context;
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    LinearLayout tagsView;

    PlancakeTag item = getItem(position);

    String tagName = item.name;
    
    if (convertView == null) {    	
    	tagsView = new LinearLayout(getContext());
      String inflater = Context.LAYOUT_INFLATER_SERVICE;
      LayoutInflater vi = (LayoutInflater)getContext().getSystemService(inflater);
      vi.inflate(this.resource, tagsView, true);
    } else {
    	tagsView = (LinearLayout) convertView;
    }

    TextView tagNameView = (TextView)tagsView.findViewById(R.id.tagName);      
    tagNameView.setText(tagName);

    return tagsView;
  }
}
