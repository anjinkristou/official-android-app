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

public class SimpleEncrypt 
{
   static final String key = "ciERfds934kDRrfd";

   public static String encryptString(String str)
   {
      StringBuffer sb = new StringBuffer (str);

      int lenStr = str.length();
      int lenKey = key.length();
	   
      //
      // For each character in our string, encrypt it...
      for ( int i = 0, j = 0; i < lenStr; i++, j++ ) 
      {
         if ( j >= lenKey ) j = 0;  // Wrap 'round to beginning of key string.

         //
         // XOR the chars together. Must cast back to char to avoid compile error. 
         //
         sb.setCharAt(i, (char)(str.charAt(i) ^ key.charAt(j))); 
      }

      return sb.toString();
   }
   
   public static String decryptString(String str)
   {
      //
      // To 'decrypt' the string, simply apply the same technique.
      return encryptString(str);
   }
}
