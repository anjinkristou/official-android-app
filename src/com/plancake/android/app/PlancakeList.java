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

import com.plancake.api.client.PlancakeListForApi;

import android.util.Log;

public class PlancakeList extends PlancakeListForApi {
	
	public PlancakeList(int id, String name, int sortOrder, boolean isHeader, boolean isInbox)
	{
		this.id = id;
		this.name = name;
		this.sortOrder = sortOrder;
		this.isHeader = isHeader;
		this.isInbox = isInbox;		
	}
	
	public boolean isTodoList()
	{		
		return ( (this.sortOrder == 1) && (this.isInbox == true) );
	}
}
