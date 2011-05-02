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

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Menu;

public class Utils {
	
    public static boolean isNetworkAvailable(Context activity) {
        ConnectivityManager connectivityManager 
              = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null) {
            return networkInfo.isAvailable();
        }
        return false;
    }
    
    public static long getNowTimestamp()
    {
		return (System.currentTimeMillis() / 1000L);
    }
    
    public static String rpad(String str, int size, char padChar)
    {
        if (str.length() < size)
        {
            char[] temp = new char[size];
            int i = 0;

            while (i < str.length())
            {
                temp[i] = str.charAt(i);
                i++;
            }

            while (i < size)
            {
                temp[i] = padChar;
                i++;
            }

            str = new String(temp);
        }

        return str;
    }
    
    public static String lpad(String str, int size, char padChar)
    {
        if (str.length() < size)
        {
            char[] temp = new char[size];
            int i = 0;
            int paddingWidth = size - str.length();

            while (i < paddingWidth)
            {
                temp[i] = padChar;
                i++;
            }            
            
            int j = 0;
            while (i < size)
            {
                temp[i] = str.charAt(j);
                i++;
                j++;
            }

            str = new String(temp);
        }

        return str;
    }     
}
