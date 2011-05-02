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

import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.plancake.api.client.PlancakeTaskForApi;

import android.util.Log;

public class PlancakeTask extends PlancakeTaskForApi {
	public boolean isModifiedLocally = false;
	
	public PlancakeTask(long _id, int _listId, String _description)
	{
		id = _id;
		listId = _listId;
		description = _description;
	}

	public String getHumanReadableDueDate()
	{
		if (isDueToday())
		{
			return "today";
		}
		if (isDueTomorrow())
		{
			return "tomorrow";
		}
		
		String[] dueDatePieces = this.dueDate.split("-");
		
		SimpleDateFormat dateformat = new SimpleDateFormat("EEE, d MMM yyyy");  
		Calendar cal = Calendar.getInstance();
		cal.set(Integer.parseInt(dueDatePieces[0]), Integer.parseInt(dueDatePieces[1])-1, Integer.parseInt(dueDatePieces[2]) );
		return dateformat.format(cal.getTime());		
	}
	
	/**
	 * Return the 12H format 
	 * 
	 * @return String
	 */
	public String getHumanReadableDueTime()
	{
		int dueTimeInt = Integer.parseInt(this.dueTime);
		int hours = (int) Math.floor((dueTimeInt / 100));
		int minutes = dueTimeInt % 100;
		
		SimpleDateFormat dateformat = new SimpleDateFormat("h:mma");  
		Calendar cal = Calendar.getInstance();
		cal.set(2011, 3, 18, hours, minutes);  // the date doesn't matter because we are only interested in time
		return dateformat.format(cal.getTime());		
	}	
	
	public boolean isDueToday()
	{
		if ((dueDate == null) || (dueDate.length() <= 0))
		{
			return false;
		}
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd"); 
		
		Calendar calToday = Calendar.getInstance();
		
		return dateformat.format(calToday.getTime()).equals(this.dueDate);
	}
	
	public boolean isDueTomorrow()
	{
		if ((dueDate == null) || (dueDate.length() <= 0))
		{
			return false;
		}		
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		
		Calendar calTomorrow = Calendar.getInstance();
		calTomorrow.set(calTomorrow.get(Calendar.YEAR), calTomorrow.get(Calendar.MONTH), calTomorrow.get(Calendar.DATE)+1);
		
		return dateformat.format(calTomorrow.getTime()).equals(this.dueDate);	
	}
	
	public boolean isOverdue()
	{
		if ((dueDate == null) || (dueDate.length() <= 0))
		{
			return false;
		}		
		
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyyMMdd");		
		
		Calendar calToday = Calendar.getInstance();
		
		int todayDateStamp =  Integer.parseInt(dateformat.format(calToday.getTime()));
		int taskDateStamp = Integer.parseInt(dueDate.replace("-", ""));

		return (taskDateStamp < todayDateStamp);		
	}
}
