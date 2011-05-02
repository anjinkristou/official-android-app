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

import java.util.Iterator;
import java.util.List;

import com.plancake.api.client.PlancakeListForApi;
import com.plancake.api.client.PlancakeRepetitionOptionForApi;
import com.plancake.api.client.PlancakeTagForApi;
import com.plancake.api.client.PlancakeTaskForApi;

import android.app.Application;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.sax.EndTextElementListener;
import android.util.Log;

/**
 * This class is a Singleton
 * 
 * This class is a bit cluttered to optimize resources
 * on a mobile device.
 *
 */
public class DbAdapter {

	private static final String DATABASE_NAME = "plancakeDatabase.db";
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_TABLE_LIST = "list";
	private static final String DATABASE_TABLE_REPETITION = "repetition";
	private static final String DATABASE_TABLE_TAG = "tag";	
	private static final String DATABASE_TABLE_TASK = "task";
	private static final String DATABASE_TABLE_TASKS_TAGS = "tasks_tags";
	
	private static final String DATABASE_COLUMN_LIST_ID = "id";
	public static final int DATABASE_COLUMN_LIST_ID_INDEX = 0;
	private static final String DATABASE_COLUMN_LIST_NAME = "name";
	public static final int DATABASE_COLUMN_LIST_NAME_INDEX = 1;
	private static final String DATABASE_COLUMN_LIST_SORT_ORDER = "sort_order";
	public static final int DATABASE_COLUMN_LIST_SORT_ORDER_INDEX = 2;
	private static final String DATABASE_COLUMN_LIST_IS_HEADER = "is_header";
	public static final int DATABASE_COLUMN_LIST_IS_HEADER_INDEX = 3;	
	private static final String DATABASE_COLUMN_LIST_IS_INBOX = "is_inbox";
	public static final int DATABASE_COLUMN_LIST_IS_INBOX_INDEX = 4;	
	
	private static final String DATABASE_COLUMN_REPETITION_ID = "id";
	public static final int DATABASE_COLUMN_REPETITION_ID_INDEX = 0;
	private static final String DATABASE_COLUMN_REPETITION_LABEL = "label";
	public static final int DATABASE_COLUMN_REPETITION_LABEL_INDEX = 1;
	private static final String DATABASE_COLUMN_REPETITION_NEEDS_PARAM = "needs_param";
	public static final int DATABASE_COLUMN_REPETITION_NEEDS_PARAM_INDEX = 2;
	private static final String DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL = "is_param_cardinal";
	public static final int DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL_INDEX = 3;
	
	private static final String DATABASE_COLUMN_TAG_ID = "id";
	public static final int DATABASE_COLUMN_TAG_ID_INDEX = 0;
	private static final String DATABASE_COLUMN_TAG_NAME = "name";
	public static final int DATABASE_COLUMN_TAG_NAME_INDEX = 1;		
	private static final String DATABASE_COLUMN_TAG_SORT_ORDER = "sort_order";
	public static final int DATABASE_COLUMN_TAG_SORT_ORDER_INDEX = 2;		
	
	private static final String DATABASE_COLUMN_TASK_ID = "id";
	public static final int DATABASE_COLUMN_TASK_ID_INDEX = 0;	
	private static final String DATABASE_COLUMN_TASK_LIST_ID = "list_id";
	public static final int DATABASE_COLUMN_TASK_LIST_ID_INDEX = 1;	
	private static final String DATABASE_COLUMN_TASK_DESCRIPTION = "description";
	public static final int DATABASE_COLUMN_TASK_DESCRIPTION_INDEX = 2;	
	private static final String DATABASE_COLUMN_TASK_SORT_ORDER = "sort_order";
	public static final int DATABASE_COLUMN_TASK_SORT_ORDER_INDEX = 3;	
	private static final String DATABASE_COLUMN_TASK_IS_STARRED = "is_starred";
	public static final int DATABASE_COLUMN_TASK_IS_STARRED_INDEX = 4;
	private static final String DATABASE_COLUMN_TASK_IS_HEADER = "is_header";
	public static final int DATABASE_COLUMN_TASK_IS_HEADER_INDEX = 5;	
	private static final String DATABASE_COLUMN_TASK_DUE_DATE = "due_date"; // format: yyyy-mm-dd
	public static final int DATABASE_COLUMN_TASK_DUE_DATE_INDEX = 6;
	private static final String DATABASE_COLUMN_TASK_DUE_TIME = "due_time"; // in the HH:mm 24h format (i.e.: 09:15, 19:13)
	public static final int DATABASE_COLUMN_TASK_DUE_TIME_INDEX = 7;
	private static final String DATABASE_COLUMN_TASK_REPETITION_ID = "repetition_id";
	public static final int DATABASE_COLUMN_TASK_REPETITION_ID_INDEX = 8;	
	private static final String DATABASE_COLUMN_TASK_REPETITION_PARAM = "repetition_param";
	public static final int DATABASE_COLUMN_TASK_REPETITION_PARAM_INDEX = 9;	
	private static final String DATABASE_COLUMN_TASK_IS_COMPLETED = "is_completed";
	public static final int DATABASE_COLUMN_TASK_IS_COMPLETED_INDEX = 10;
	private static final String DATABASE_COLUMN_TASK_IS_FROM_SYSTEM = "is_from_system";
	public static final int DATABASE_COLUMN_TASK_IS_FROM_SYSTEM_INDEX = 11;
	private static final String DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY = "is_modified_locally";
	public static final int DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY_INDEX = 12;	
	private static final String DATABASE_COLUMN_TASK_NOTE = "note";
	public static final int DATABASE_COLUMN_TASK_NOTE_INDEX = 13;	
	private static final String DATABASE_COLUMN_TASK_TAG_IDS = "tag_ids";
	public static final int DATABASE_COLUMN_TASK_TAG_IDS_INDEX = 14;
	private static final String DATABASE_COLUMN_TASK_UPDATED_AT = "updated_at";
	public static final int DATABASE_COLUMN_TASK_UPDATED_AT_INDEX = 15;		
	private static final String DATABASE_COLUMN_TASK_CREATED_AT = "created_at";
	public static final int DATABASE_COLUMN_TASK_CREATED_AT_INDEX = 16;		

	private static final String DATABASE_COLUMN_TASKS_TAGS_TASK_ID = "task_id";
	public static final int DATABASE_COLUMN_TASKS_TAGS_TASK_ID_INDEX = 0;
	private static final String DATABASE_COLUMN_TASKS_TAGS_TAG_ID = "tag_id";
	public static final int DATABASE_COLUMN_TASKS_TAGS_TAG_ID_INDEX = 1;
	
	// there are these tables:
	// list, task, tag, tasks_tags, repetition
	private static final String DATABASE_CREATE_TABLE_LIST_STATEMENT = 
	   "CREATE TABLE " + DATABASE_TABLE_LIST + " (" +
		DATABASE_COLUMN_LIST_ID + " integer primary key, " +
		DATABASE_COLUMN_LIST_NAME + " text not null, " +
		DATABASE_COLUMN_LIST_SORT_ORDER + " integer not null, " +
		DATABASE_COLUMN_LIST_IS_HEADER + " integer(1) not null, " +
		DATABASE_COLUMN_LIST_IS_INBOX + " integer(1) not null" +		
		"); ";
	private static final String DATABASE_CREATE_TABLE_REPETITION_STATEMENT = 	
	   "CREATE TABLE " + DATABASE_TABLE_REPETITION + " (" +
	    DATABASE_COLUMN_REPETITION_ID + " integer primary key, " +
	    DATABASE_COLUMN_REPETITION_LABEL + " text not null, " +
	    DATABASE_COLUMN_REPETITION_NEEDS_PARAM + " integer(1) not null, " +
	    DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL + " integer(1) not null" +
		"); ";
	private static final String DATABASE_CREATE_TABLE_TAG_STATEMENT = 
	   "CREATE TABLE " + DATABASE_TABLE_TAG + " (" +
		DATABASE_COLUMN_TAG_ID + " integer primary key, " +
		DATABASE_COLUMN_TAG_NAME + " text not null, " +
		DATABASE_COLUMN_TAG_SORT_ORDER + " integer not null " +		
		"); ";
	private static final String DATABASE_CREATE_TABLE_TASK_STATEMENT = 
	   "CREATE TABLE " + DATABASE_TABLE_TASK + " (" +
		DATABASE_COLUMN_TASK_ID + " integer primary key, " +
		DATABASE_COLUMN_TASK_LIST_ID + " integer not null, " +
		DATABASE_COLUMN_TASK_DESCRIPTION + " text not null, " +
		DATABASE_COLUMN_TASK_SORT_ORDER + " integer not null, " +	
		DATABASE_COLUMN_TASK_IS_STARRED + " integer(1) not null, " +	
		DATABASE_COLUMN_TASK_IS_HEADER + " integer(1) not null, " +
		DATABASE_COLUMN_TASK_DUE_DATE + " text, " +	
		DATABASE_COLUMN_TASK_DUE_TIME + " text, " +	
		DATABASE_COLUMN_TASK_REPETITION_ID + " integer, " +	
		DATABASE_COLUMN_TASK_REPETITION_PARAM + " integer, " +
		DATABASE_COLUMN_TASK_IS_COMPLETED + " integer(1) not null, " +	
		DATABASE_COLUMN_TASK_IS_FROM_SYSTEM + " integer(1) not null, " +
		DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY + " integer(1) not null, " +		
		DATABASE_COLUMN_TASK_NOTE + " text, " +
		DATABASE_COLUMN_TASK_TAG_IDS + " text, " +	
		DATABASE_COLUMN_TASK_UPDATED_AT + " integer not null, " +	
		DATABASE_COLUMN_TASK_CREATED_AT + " integer not null " +		
		"); ";
	private static final String DATABASE_CREATE_TABLE_TASKS_TAGS_STATEMENT = 
	   "CREATE TABLE " + DATABASE_TABLE_TASKS_TAGS + " (" +
		DATABASE_COLUMN_TASKS_TAGS_TASK_ID + " integer, " +
		DATABASE_COLUMN_TASKS_TAGS_TAG_ID + " integer " +
		"); ";
	
	// variable to hold the database instance
	private SQLiteDatabase db;
	
	// context of the application using the database
	private final Context context;
	
	// database open/upgrade helper
	private DbHelper dbHelper;
	
	public DbAdapter(Context context)
	{
		this.context = context;
		this.dbHelper = new DbHelper(context, DbAdapter.DATABASE_NAME, null, DbAdapter.DATABASE_VERSION);
	}
	
	public DbAdapter open() throws SQLException
	{
		this.db = this.dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close()
	{
		this.db.close();
	}
	
	public void resetAllTables()
	{
		this.db.execSQL("DROP TABLE IF EXISTS " + DbAdapter.DATABASE_TABLE_LIST);
		this.db.execSQL("DROP TABLE IF EXISTS " + DbAdapter.DATABASE_TABLE_REPETITION);
		this.db.execSQL("DROP TABLE IF EXISTS " + DbAdapter.DATABASE_TABLE_TAG);
		this.db.execSQL("DROP TABLE IF EXISTS " + DbAdapter.DATABASE_TABLE_TASK);
		this.db.execSQL("DROP TABLE IF EXISTS " + DbAdapter.DATABASE_TABLE_TASKS_TAGS);
		createTables(this.db);
	}
	
	private static void createTables(SQLiteDatabase db)
	{
		db.execSQL(DbAdapter.DATABASE_CREATE_TABLE_LIST_STATEMENT);
		db.execSQL(DbAdapter.DATABASE_CREATE_TABLE_REPETITION_STATEMENT);
		db.execSQL(DbAdapter.DATABASE_CREATE_TABLE_TAG_STATEMENT);
		db.execSQL(DbAdapter.DATABASE_CREATE_TABLE_TASK_STATEMENT);
		db.execSQL(DbAdapter.DATABASE_CREATE_TABLE_TASKS_TAGS_STATEMENT);		
	}
	
	private static class DbHelper extends SQLiteOpenHelper
	{
		public DbHelper(Context context, String name, CursorFactory factory, int version)
		{
				super(context, name, factory, version);
		}
		
		// Called when no database exists in disk and the helper class needs to create a new one
		@Override
		public void onCreate(SQLiteDatabase _db)
		{
			DbAdapter.createTables(_db);			
		}

		// Called when there is a database version mismatch meaning that the version of the database
		// on disk needs to be upgraded to the current version
		@Override
		public void onUpgrade(SQLiteDatabase _db, int oldVersion, int newVersion)
		{
			String updateStatement = ""; // here the SQL statement for performing the update
			_db.execSQL(updateStatement);
		}				
	}

///////////////////////////////////////////////////////////////
/////////   I N S E R T   M E T H O D S    ////////////////////
///////////////////////////////////////////////////////////////	

	/**
	 * We don't need to specify the parameter 'deep' (as for the other similar methods)
	 * because the insertTask method populated also the tasks_tags table
	 */
	public long replaceOrInsertTask(PlancakeTaskForApi task, boolean isModifiedLocally) throws java.sql.SQLException
	{
		long ret = 0;
		db.beginTransaction();
		try
		{
			deleteTask(task);
			ret = insertTask(task, isModifiedLocally);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
		return ret;
	}

	public long replaceOrInsertList(PlancakeListForApi list, boolean deep) throws java.sql.SQLException
	{
		long ret = 0;
		db.beginTransaction();
		try
		{
			deleteList(list, deep);
			ret = insertList(list);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
		return ret;
	}	

	public long replaceOrInsertTag(PlancakeTagForApi tag, boolean deep) throws java.sql.SQLException
	{
		long ret = 0;
		db.beginTransaction();
		try
		{
			deleteTag(tag, deep);
			ret = insertTag(tag);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
		return ret;
	}		

	public long replaceOrInsertRepetitionOption(PlancakeRepetitionOptionForApi repetition) throws java.sql.SQLException
	{
		long ret = 0;
		db.beginTransaction();
		try
		{
			deleteRepetition(repetition);
			ret = insertRepetitionOption(repetition);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
		return ret;
	}		
	
	/**
	 * 
	 * @param task
	 * @param isModifiedLocally
	 * @return
	 * @throws java.sql.SQLException
	 */
	public long insertTask(PlancakeTaskForApi task, boolean isModifiedLocally) throws java.sql.SQLException
	{	
		if (task == null) return 0;
		
		ContentValues values = new ContentValues();
		values.put(DATABASE_COLUMN_TASK_ID, task.id);
		values.put(DATABASE_COLUMN_TASK_LIST_ID, task.listId);
		values.put(DATABASE_COLUMN_TASK_DESCRIPTION, task.description);		
		values.put(DATABASE_COLUMN_TASK_SORT_ORDER, task.sortOrder);	
		values.put(DATABASE_COLUMN_TASK_IS_STARRED, task.isStarred);	
		values.put(DATABASE_COLUMN_TASK_IS_HEADER, task.isHeader);
		values.put(DATABASE_COLUMN_TASK_DUE_DATE, task.dueDate);
		values.put(DATABASE_COLUMN_TASK_DUE_TIME, task.dueTime);
		values.put(DATABASE_COLUMN_TASK_REPETITION_ID, task.repetitionId);	
		values.put(DATABASE_COLUMN_TASK_REPETITION_PARAM, task.repetitionParam);
		values.put(DATABASE_COLUMN_TASK_IS_COMPLETED, task.isCompleted);	
		values.put(DATABASE_COLUMN_TASK_IS_FROM_SYSTEM, task.isFromSystem);
		values.put(DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY, isModifiedLocally);		
		values.put(DATABASE_COLUMN_TASK_NOTE, task.note);
		values.put(DATABASE_COLUMN_TASK_TAG_IDS, task.tagIds);	
		values.put(DATABASE_COLUMN_TASK_UPDATED_AT, task.updatedAt);	
		values.put(DATABASE_COLUMN_TASK_CREATED_AT, task.createdAt);		
		long insertId = this.db.insert(DATABASE_TABLE_TASK, null, values);
		
		if (insertId < 0)
		{
			throw new java.sql.SQLException("Error while trying to insert a task.");
		}
		
		if (task.tagIds != null)
		{
			String[] tagIds = task.tagIds.split(",");
			long insertTagId = 0L;
				
			for(int i=0; i<tagIds.length; i++ )
			{
				values = new ContentValues();
				values.put(DATABASE_COLUMN_TASKS_TAGS_TAG_ID, tagIds[i]);
				values.put(DATABASE_COLUMN_TASKS_TAGS_TASK_ID, task.id);
				insertTagId = this.db.insert(DATABASE_TABLE_TASKS_TAGS, null, values);
				if (insertTagId < 0)
				{
					throw new java.sql.SQLException("Error while trying to insert a task-tag relation.");
				}			
			}
		}
		
		return insertId;
	}
	
	public long insertRepetitionOption(PlancakeRepetitionOptionForApi repetition) throws java.sql.SQLException
	{
		if (repetition == null) return 0;		
		
		ContentValues values = new ContentValues();
		values.put(DATABASE_COLUMN_REPETITION_ID, repetition.id);
		values.put(DATABASE_COLUMN_REPETITION_LABEL, repetition.label);
		values.put(DATABASE_COLUMN_REPETITION_NEEDS_PARAM, DbAdapter.fromBooleanToInt(repetition.needsParam));
		values.put(DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL, DbAdapter.fromBooleanToInt(repetition.isParamCardinal));        
		long insertId = this.db.insert(DATABASE_TABLE_REPETITION, null, values);
		
		if (insertId < 0)
		{
			throw new java.sql.SQLException("Error while trying to insert a repetition.");
		}
		
		return insertId;
	}
	
	public long insertList(PlancakeListForApi list) throws java.sql.SQLException
	{
		if (list == null) return 0;		
		
		ContentValues values = new ContentValues();
		values.put(DATABASE_COLUMN_LIST_ID, list.id);
		values.put(DATABASE_COLUMN_LIST_NAME, list.name);
		values.put(DATABASE_COLUMN_LIST_SORT_ORDER, list.sortOrder);
		values.put(DATABASE_COLUMN_LIST_IS_HEADER, DbAdapter.fromBooleanToInt(list.isHeader));
		values.put(DATABASE_COLUMN_LIST_IS_INBOX, DbAdapter.fromBooleanToInt(list.isInbox));		
		long insertId = this.db.insert(DATABASE_TABLE_LIST, null, values);
		
		if (insertId < 0)
		{
			throw new java.sql.SQLException("Error while trying to insert a list.");
		}		
		
		return insertId;
	}
	
	public long insertTag(PlancakeTagForApi tag) throws java.sql.SQLException
	{
		if (tag == null) return 0;	
		
		ContentValues values = new ContentValues();
		values.put(DATABASE_COLUMN_TAG_ID, tag.id);
		values.put(DATABASE_COLUMN_TAG_NAME, tag.name);
		values.put(DATABASE_COLUMN_TAG_SORT_ORDER, tag.sortOrder);	
		long insertId = this.db.insert(DATABASE_TABLE_TAG, null, values);
		
		if (insertId < 0)
		{
			throw new java.sql.SQLException("Error while trying to insert a task");
		}		
		
		return insertId;
	}	

///////////////////////////////////////////////////////////////
/////////   D E L E T E    M E T H O D S    ////////////////////
///////////////////////////////////////////////////////////////		
	
	public void deleteTask(PlancakeTaskForApi task)
	{
		if (task == null) return;
		
		long taskId = task.id;
		
		db.beginTransaction();
		try
		{
			db.execSQL("DELETE FROM " + DATABASE_TABLE_TASK + " WHERE " + DATABASE_COLUMN_TASK_ID + "=" + taskId);
			db.execSQL("DELETE FROM " + DATABASE_TABLE_TASKS_TAGS + " WHERE " + DATABASE_COLUMN_TASKS_TAGS_TASK_ID + "=" + taskId);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
	}

	public void deleteRepetition(PlancakeRepetitionOptionForApi repetition)
	{
		if (repetition == null) return;		
		
		db.execSQL("DELETE FROM " + DATABASE_TABLE_REPETITION + " WHERE " + DATABASE_COLUMN_REPETITION_ID + "=" + repetition.id);
	}	
	
	public void deleteTag(PlancakeTagForApi tag, boolean deep)
	{
		if (tag == null) return;		
		
		int tagId = tag.id;
		
		db.beginTransaction();
		try
		{
			db.execSQL("DELETE FROM " + DATABASE_TABLE_TAG + " WHERE " + DATABASE_COLUMN_TAG_ID + "=" + tagId);
			if (deep)
			{
				db.execSQL("DELETE FROM " + DATABASE_TABLE_TASKS_TAGS + " WHERE " + DATABASE_COLUMN_TASKS_TAGS_TAG_ID + "=" + tagId);
			}
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}		
	}
	
	public void deleteList(PlancakeListForApi list, boolean deep)
	{
		if (list == null) return;
		
		int listId = list.id;
		
		db.beginTransaction();
		try
		{
			if (deep)
			{
				// deleting all the tasks for that list first
				Cursor tasksInListCursor = getTasksByListId(listId);
				long taskId = 0;
				PlancakeTask task = null;
				if (tasksInListCursor.moveToFirst())
				{
		            do
		        	{          	
		            	taskId = tasksInListCursor.getLong(DbAdapter.DATABASE_COLUMN_TASK_ID_INDEX);
		            	task = getTaskById(taskId);
		            	deleteTask(task);
		        	} while(tasksInListCursor.moveToNext());			
				}
				
				if (tasksInListCursor != null)
				{
					tasksInListCursor.close();
				}
			}
			db.execSQL("DELETE FROM " + DATABASE_TABLE_LIST + " WHERE " + DATABASE_COLUMN_LIST_ID + "=" + listId);
			db.setTransactionSuccessful(); 
		} finally {
			db.endTransaction();
		}
	}	
	
///////////////////////////////////////////////////////////////
/////////   G E T T E R S    M E T H O D S    /////////////////
///////////////////////////////////////////////////////////////

	public PlancakeTask getTaskById(long taskId)
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_ID + "=" + taskId;
		Cursor c = this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, null);
		
		if (c.moveToFirst())
		{
			PlancakeTask task = getTaskFromCursor(c);
			
			if (c != null)
			{
				c.close();
			}
			
			return task;
		}

		if (c != null)
		{
			c.close();
		}		
		
		return null;
	}	
	
	public PlancakeList getListById(int listId)
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_LIST_ID, DbAdapter.DATABASE_COLUMN_LIST_NAME, 
				DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER, DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER, DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX};
		Cursor cursor  = this.db.query(DATABASE_TABLE_LIST, resultColumns, DbAdapter.DATABASE_COLUMN_LIST_ID + "=" + listId, 
				null, null, null, null);	
		
		if (cursor.moveToFirst())
		{		
			PlancakeList list = new PlancakeList(listId, cursor.getString(DbAdapter.DATABASE_COLUMN_LIST_NAME_INDEX), 
					cursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER_INDEX),
					DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER_INDEX)),
					DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX_INDEX)));
			
			if (cursor != null)
			{
				cursor.close();
			}
			
			return list;
		}
		
		if (cursor != null)
		{
			cursor.close();
		}		
		
		return null;
	}
	
	public PlancakeTag getTagById(int tagId)
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_TAG_ID, DbAdapter.DATABASE_COLUMN_TAG_NAME, DbAdapter.DATABASE_COLUMN_TAG_SORT_ORDER};
		Cursor cursor  = this.db.query(DATABASE_TABLE_TAG, resultColumns, DbAdapter.DATABASE_COLUMN_TAG_ID + "=" + tagId, 
				null, null, null, null);	
		
		if (cursor.moveToFirst())
		{		
			PlancakeTag tag = new PlancakeTag(tagId, cursor.getString(DbAdapter.DATABASE_COLUMN_TAG_NAME_INDEX), cursor.getInt(DbAdapter.DATABASE_COLUMN_TAG_SORT_ORDER_INDEX));
			
			if (cursor != null)
			{
				cursor.close();
			}			
			
			return tag;
		}
		
		if (cursor != null)
		{
			cursor.close();
		}		
		
		return null;
	}		
	
	public Cursor getAllUserLists()
	{
		String where = DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER + ">0";	
		
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_LIST_ID, 
												DbAdapter.DATABASE_COLUMN_LIST_NAME,
												DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER,
												DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER,
												DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX};
		return this.db.query(DATABASE_TABLE_LIST, resultColumns, where, null, null, null, DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER + " DESC");
	}

	public Cursor getTasksByListId(int listId)
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_LIST_ID + "=" + listId;
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " asc");		
	}	
	
	public Cursor getTasksWithDueDateByListId(int listId)
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_LIST_ID + "=" + listId + " AND " + 
								"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") > 0";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " asc");		
	}

	public Cursor getTasksWithoutDueDateByListId(int listId)
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_LIST_ID + "=" + listId + " AND " + 
					"(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " IS NULL OR " +		
					"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") = 0 )";	
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_SORT_ORDER + " desc");
	}	
	
	private String getCsvTaskIdsByTagId(int tagId)
	{
		String csvTaskIds = "";
		
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_TASKS_TAGS_TASK_ID, 
				   							   DbAdapter.DATABASE_COLUMN_TASKS_TAGS_TAG_ID};
		Cursor cursor  = this.db.query(DATABASE_TABLE_TASKS_TAGS, resultColumns, DbAdapter.DATABASE_COLUMN_TASKS_TAGS_TAG_ID + "=" + tagId, 
		null, null, null, null);	
		
		if (cursor.moveToFirst())
		{
            do
        	{          	
            	csvTaskIds += cursor.getInt(DbAdapter.DATABASE_COLUMN_TASKS_TAGS_TASK_ID_INDEX) + ",";           		
        	} while(cursor.moveToNext());			
		}
		
		if (cursor != null)
		{
			cursor.close();
		}
		
		if (csvTaskIds.length() > 0)
		{
			// removing the trailing comma
			csvTaskIds = csvTaskIds.substring(0,csvTaskIds.length()-1);
		}
		
		return csvTaskIds;
	}
	
	public Cursor getTasksWithDueDateByTagId(int tagId)
	{	
		String where = DbAdapter.DATABASE_COLUMN_TASK_ID + " IN (" + getCsvTaskIdsByTagId(tagId) + ") AND " + 
					"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") > 0";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " asc");
	}

	public Cursor getTasksWithoutDueDateByTagId(int tagId)
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_ID + " IN (" + getCsvTaskIdsByTagId(tagId) + ") AND " + 
					"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") = 0";	
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_SORT_ORDER + " desc");
	}		
	
	public Cursor getStarredTasksWithDueDate()
	{	
		String where = DbAdapter.DATABASE_COLUMN_TASK_IS_STARRED + " = 1 AND " + 
					"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") > 0";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " asc");
	}

	public Cursor getStarredTasksWithoutDueDate()
	{
		String where = DbAdapter.DATABASE_COLUMN_TASK_IS_STARRED + " = 1 AND " +  
					"length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") = 0";	
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_SORT_ORDER + " desc");
	}	
	
	public Cursor getTasksWithDueDate()
	{	
		String where = "length(" + DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + ") > 0";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE + " asc");
	}

	public Cursor getNewTasksFromLocalInbox()
	{	
		String where = DbAdapter.DATABASE_COLUMN_TASK_LIST_ID + "=" + getInboxListId() + " AND " +
					   DbAdapter.DATABASE_COLUMN_TASK_IS_COMPLETED + "=0  AND " +  // if they are completed, they are not locally new
					   DbAdapter.DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY + "=1";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, null);
	}	

	public Cursor getTasksCompletedLocally()
	{	
		String where = DbAdapter.DATABASE_COLUMN_TASK_IS_COMPLETED + "=1 AND " +
					   DbAdapter.DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY + "=1";
		return this.db.query(DATABASE_TABLE_TASK, this.getTaskResultColumns(), where, 
				null, null, null, null);
	}	
	
	private String[] getTaskResultColumns()
	{
		return new String[] {DbAdapter.DATABASE_COLUMN_TASK_ID, 
				   DbAdapter.DATABASE_COLUMN_TASK_LIST_ID, 
					DbAdapter.DATABASE_COLUMN_TASK_DESCRIPTION,
					DbAdapter.DATABASE_COLUMN_TASK_SORT_ORDER,
					DbAdapter.DATABASE_COLUMN_TASK_IS_STARRED,
					DbAdapter.DATABASE_COLUMN_TASK_IS_HEADER,
					DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE,
					DbAdapter.DATABASE_COLUMN_TASK_DUE_TIME,
					DbAdapter.DATABASE_COLUMN_TASK_REPETITION_ID,
					DbAdapter.DATABASE_COLUMN_TASK_REPETITION_PARAM,
					DbAdapter.DATABASE_COLUMN_TASK_IS_COMPLETED,
					DbAdapter.DATABASE_COLUMN_TASK_IS_FROM_SYSTEM,
					DbAdapter.DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY,					
					DbAdapter.DATABASE_COLUMN_TASK_NOTE,
					DbAdapter.DATABASE_COLUMN_TASK_TAG_IDS,
					DbAdapter.DATABASE_COLUMN_TASK_UPDATED_AT,
					DbAdapter.DATABASE_COLUMN_TASK_CREATED_AT
		};		
	}
	
    public PlancakeTask getTaskFromCursor(Cursor cursor)
    { 	
    	PlancakeTask task = new PlancakeTask(cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_ID_INDEX),
							    			cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_LIST_ID_INDEX),
							    			cursor.getString(DbAdapter.DATABASE_COLUMN_TASK_DESCRIPTION_INDEX));
    			
		task.sortOrder = cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_SORT_ORDER_INDEX);
		task.isStarred = DbAdapter.fromIntToBoolean(cursor.getInt((DbAdapter.DATABASE_COLUMN_TASK_IS_STARRED_INDEX)));
		task.isHeader = DbAdapter.fromIntToBoolean(cursor.getInt((DbAdapter.DATABASE_COLUMN_TASK_IS_HEADER_INDEX)));
		task.dueDate = cursor.getString(DbAdapter.DATABASE_COLUMN_TASK_DUE_DATE_INDEX);
		task.dueTime = cursor.getString(DbAdapter.DATABASE_COLUMN_TASK_DUE_TIME_INDEX);
		task.repetitionId = cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_REPETITION_ID_INDEX);
		task.repetitionParam = cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_REPETITION_PARAM_INDEX);
		task.isCompleted = DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_IS_COMPLETED_INDEX));
		task.isFromSystem = DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_IS_FROM_SYSTEM_INDEX));
		task.isModifiedLocally = DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY_INDEX));
		task.note = cursor.getString(DbAdapter.DATABASE_COLUMN_TASK_NOTE_INDEX);
		task.tagIds = cursor.getString(DbAdapter.DATABASE_COLUMN_TASK_TAG_IDS_INDEX);
		task.updatedAt = cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_UPDATED_AT_INDEX);
		task.createdAt = cursor.getInt(DbAdapter.DATABASE_COLUMN_TASK_CREATED_AT_INDEX);

		return task;
    }	
	
	public Cursor getAllTags()
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_TAG_ID, 
				DbAdapter.DATABASE_COLUMN_TAG_NAME, DbAdapter.DATABASE_COLUMN_TAG_SORT_ORDER};
		return this.db.query(DATABASE_TABLE_TAG, resultColumns, null, null, null, null, DbAdapter.DATABASE_COLUMN_TAG_SORT_ORDER + " DESC");		
	}

///////////////////////////////////////////////////////////////
/////////   U T I L I T Y    M E T H O D S    /////////////////
///////////////////////////////////////////////////////////////		
	private long getMaxTaskId()
	{
		String query = "SELECT MAX(" + DATABASE_COLUMN_TASK_ID + ") AS max_id FROM " + DATABASE_TABLE_TASK;
		
		Cursor cursor = db.rawQuery(query, null);
		
		int taskId = 0;		
		if (cursor.moveToFirst())
		{
			do
			{
				taskId = cursor.getInt(0);           		
			} while(cursor.moveToNext());			
		}
		
		if (cursor != null)
		{
			cursor.close();
		}
		
		return taskId;
	}

	public int getInboxListId()
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_LIST_ID, 
		DbAdapter.DATABASE_COLUMN_LIST_NAME,
		DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER,
		DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER,
		DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX};
		Cursor cursor = this.db.query(DATABASE_TABLE_LIST, resultColumns, 
				DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER + "=0 AND " + DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX + "=1", 
		        null, null, null, null);	
		
		int listId = 0;
		
		if (cursor.moveToFirst())
		{
			do
			{          	
				listId = cursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_ID_INDEX);           		
			} while(cursor.moveToNext());			
		}
		
		if (cursor != null)
		{
			cursor.close();
		}		
		
		return listId;
	}

	public int getTodoListId()
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_LIST_ID, 
		DbAdapter.DATABASE_COLUMN_LIST_NAME,
		DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER,
		DbAdapter.DATABASE_COLUMN_LIST_IS_HEADER,
		DbAdapter.DATABASE_COLUMN_LIST_IS_INBOX	};
		Cursor cursor = this.db.query(DATABASE_TABLE_LIST, resultColumns, 
				DbAdapter.DATABASE_COLUMN_LIST_SORT_ORDER + "=0 AND " + DbAdapter.DATABASE_COLUMN_LIST_NAME + "='Todo'", 
		        null, null, null, null);	
		
		int listId = 0;
		
		if (cursor.moveToFirst())
		{
			do
			{          	
				listId = cursor.getInt(DbAdapter.DATABASE_COLUMN_LIST_ID_INDEX);           		
			} while(cursor.moveToNext());			
		}
		
		if (cursor != null)
		{
			cursor.close();
		}		
		
		return listId;
	}
	
	/**
	* If id=0 a new task is created, otherwise an old task is edited
	* 
	* @param id
	* @param description
	* @param note
	* @return
	* @throws java.sql.SQLException
	*/
	public PlancakeTask editLocalInbox(long id, String description, String note) throws java.sql.SQLException
	{
		if (note == null)
		{
			note = "";
		}
		
		long taskId = (id == 0) ?  getMaxTaskId()+1 : id;	
		PlancakeTask inboxTask = new PlancakeTask(taskId, getInboxListId(), description);		
		inboxTask.note = note;
		
		replaceOrInsertTask(inboxTask, true);
		
		return inboxTask;
	}	
		
	
	public PlancakeTask markTaskAsDone(PlancakeTask task)
	{
		long taskId = task.id;

		String q = "UPDATE " + DATABASE_TABLE_TASK + 
				   " SET " + DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY + "=1, " +
				   DATABASE_COLUMN_TASK_IS_COMPLETED + "=1 " +
				   " WHERE " + DATABASE_COLUMN_TASK_ID + "=" + taskId;
		db.execSQL(q);
		return getTaskById(taskId);		
	}

	public PlancakeTask markTaskAsIncomplete(PlancakeTask task)
	{
		long taskId = task.id;

		String q = "UPDATE " + DATABASE_TABLE_TASK + 
				   " SET " + DATABASE_COLUMN_TASK_IS_MODIFIED_LOCALLY + "=0, " +
				   DATABASE_COLUMN_TASK_IS_COMPLETED + "=0 " +
				   " WHERE " + DATABASE_COLUMN_TASK_ID + "=" + taskId;
		db.execSQL(q);	
		return getTaskById(taskId);
	}	
	
	public static int fromBooleanToInt(boolean b)
	{
		return b ? 1 : 0;
	}
	
	
	public static boolean fromIntToBoolean(int i)
	{
		return (i==0) ? false : true;
	}
	
	public String getRepetitionExpression(int repetitionId, int repetitionParam)
	{
		String[] resultColumns = new String[] {DbAdapter.DATABASE_COLUMN_REPETITION_ID, 
				   							   DbAdapter.DATABASE_COLUMN_REPETITION_LABEL, 
				   							   DbAdapter.DATABASE_COLUMN_REPETITION_NEEDS_PARAM, 
				   							   DbAdapter.DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL};
		Cursor cursor  = this.db.query(DATABASE_TABLE_REPETITION, resultColumns, DbAdapter.DATABASE_COLUMN_REPETITION_ID + "=" + repetitionId, 
				null, null, null, null);	

		if (cursor.moveToFirst())
		{
			if (repetitionId == 34) // this is the special repetition for selected weekdays in the week
			{
				String binaryRepresentation = Utils.lpad(Integer.toBinaryString(repetitionParam), 7, '0');

				String repetitionExpression = this.context.getString(R.string.on_for_weekdays) + ' ';
				
				if (binaryRepresentation.charAt(0) == '1') // scheduled for Sunday
				{
					repetitionExpression += this.context.getString(R.string.sunday_abbreviation) + ',';
				}

				if (binaryRepresentation.charAt(1) == '1') // scheduled for Monday
				{
					repetitionExpression += this.context.getString(R.string.monday_abbreviation) + ',';
				}
				
				if (binaryRepresentation.charAt(2) == '1') // scheduled for Tuesday
				{
					repetitionExpression += this.context.getString(R.string.tuesday_abbreviation) + ',';
				}
				
				if (binaryRepresentation.charAt(3) == '1') // scheduled for Wednesday
				{
					repetitionExpression += this.context.getString(R.string.wednesday_abbreviation) + ',';
				}
				
				if (binaryRepresentation.charAt(4) == '1') // scheduled for Thursday
				{
					repetitionExpression += this.context.getString(R.string.thursday_abbreviation) + ',';
				}
				
				if (binaryRepresentation.charAt(5) == '1') // scheduled for Friday
				{
					repetitionExpression += this.context.getString(R.string.friday_abbreviation) + ',';
				}
				
				if (binaryRepresentation.charAt(6) == '1') // scheduled for Saturday
				{
					repetitionExpression += this.context.getString(R.string.saturday_abbreviation) + ',';
				}
				
				// removing the last character that is going to be a comma
				repetitionExpression = repetitionExpression.substring(0, repetitionExpression.length() - 1);
				
				return repetitionExpression;
			}
			else
			{
				boolean needsParam = DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_REPETITION_NEEDS_PARAM_INDEX));
				boolean isParamCardinal = DbAdapter.fromIntToBoolean(cursor.getInt(DbAdapter.DATABASE_COLUMN_REPETITION_IS_PARAM_CARDINAL_INDEX));
				
				String label = cursor.getString(DbAdapter.DATABASE_COLUMN_REPETITION_LABEL_INDEX);
				String param = null;
				if (! needsParam)
				{
					return label;
				}
				else
				{					
					if (isParamCardinal)
					{					
						param = Integer.toString(repetitionParam);
					}
					else
					{
					    final String[] ordinalNumbersAbbr = new String[] {"", "1st", "2nd", "3rd", "4th", "5th", "6th", 
					    	"7th", "8th", "9th", "10th", "11th", "12th", "13th", "14th", "15th", "16th", 
					    	"17th", "18th", "19th", "20th", "21st", "22nd", "23rd", "24th", "25th", "26th", 
					    	"27th", "28th", "29th", "30th"};
					    param = ordinalNumbersAbbr[repetitionParam];
					}
				}
				
				if (cursor != null)
				{
					cursor.close();
				}
				
				return label.replace("[select later]", param);
			}
		}
		
		if (cursor != null)
		{
			cursor.close();
		}
		
		return null;		
	}
}
