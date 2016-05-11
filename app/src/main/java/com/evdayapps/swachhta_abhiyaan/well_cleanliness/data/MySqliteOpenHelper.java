package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;


public class MySqliteOpenHelper extends SQLiteOpenHelper {
	private final static String logtag = MySqliteOpenHelper.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;


	public static final String KEY_ROWID = "_id";
	 public static final String KEY_ID = "Id";
	 public static final String KEY_NAME = "Name";
	 public static final String KEY_PARENT_ID = "ParentId";
	 public static final String KEY_PARENT_NAME = "ParentName";
	 public static final String KEY_TYPE = "Type";
	 public static final String KEY_LOCATION_TYPE_ID = "LocationTypeId";
	 private static final String SQLITE_TABLE = "Country";
	 private static final String SQLITE_TABLE_USER = "USER";
	 
	 public static final String KEY_USERNAME = "Username";
	 public static final String KEY_PASSWORD = "Password";
	// Database Version
    private static final int DATABASE_VERSION = 1;
    public SQLiteDatabase mDb;
    // Database Name
    private static final String DATABASE_NAME = "Pratishthan";
   DatabaseTable databaseTable;
    
   private static final String DATABASE_CREATE =
		   "CREATE TABLE if not exists " + SQLITE_TABLE + " (" +
		   KEY_ROWID + " integer PRIMARY KEY autoincrement," +
		   KEY_ID + "," +
		   KEY_NAME + "," +
		   KEY_PARENT_ID + "," +
		   KEY_PARENT_NAME + "," +
		   KEY_TYPE + "," +
		   KEY_LOCATION_TYPE_ID +","+KEY_USERNAME+  ")";
   private static final String DATABASE_CREATE_USERTABLE =
		   "CREATE TABLE if not exists " + SQLITE_TABLE_USER + " (" +
		  
		 
		   KEY_USERNAME + "," +
		  
		   KEY_PASSWORD +  ")";
   
	
	public MySqliteOpenHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);	

		databaseTable=new DatabaseTable();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String sql_create_states_table = "Create table States(" +
				"id integer not null, " +
				"name text not null);";
		
		String sql_create_districts_table = "Create table Districts(" +
				"id integer not null, " +
				"name text not null, " +
				"state_id integer not null, " +
				"foreign key(state_id) references States(id));";
		
		String sql_create_talukas_table = "Create table Talukas(" +
				"id integer not null, " +
				"name text not null, " +
				"district_id integer not null, " +
				"foreign key(district_id) references Districts(id));";
		
		db.execSQL(sql_create_states_table);
		db.execSQL(sql_create_districts_table);
		db.execSQL(sql_create_talukas_table);
		db.execSQL(DATABASE_CREATE);
		db.execSQL(DATABASE_CREATE_USERTABLE);

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		 db.execSQL("DROP TABLE IF EXISTS Anti_Theft_app");
	        
	        // create fresh books table
	        this.onCreate(db);
	}

	 public long createCountry(String ID, String name, 
			   String parentname, String parentid, String locationTypeId, String type, String usernames) {
			mDb=this.getWritableDatabase();
			  ContentValues initialValues = new ContentValues();
			  initialValues.put(KEY_ID, ID);
			  initialValues.put(KEY_NAME, name);
			  initialValues.put(KEY_PARENT_NAME, parentname);
			  initialValues.put(KEY_PARENT_ID, parentid);
			  initialValues.put(KEY_LOCATION_TYPE_ID, locationTypeId);
			  initialValues.put(KEY_TYPE, type);
			  initialValues.put(KEY_USERNAME, usernames);
			 
			  return mDb.insert(SQLITE_TABLE, null, initialValues);
			 }
	 public long createuser(String name, 
			   String password) {
			mDb=this.getWritableDatabase();
			  ContentValues initialValues = new ContentValues();
			  initialValues.put(KEY_USERNAME, name);
			  initialValues.put(KEY_PASSWORD, password);
			 
			 
			  return mDb.insert(SQLITE_TABLE_USER, null, initialValues);
			 } 
	
	//---------------------------------------------------------------------
  
	/**
    * CRUD operations (create "add", read "get", update, delete) book + get all books + delete all books
	 * @param userName 
    */
	
	 public List<String> getAllState(String userName) {
	     List<String> STATElist = new LinkedList<String>();
	     STATElist.add("Select state");
	     // 1. build the query
	     String query = "SELECT  * FROM " + SQLITE_TABLE+" where TYPE='STATE' and UserName='"+userName+"'";

	 	// 2. get reference to writable DB
	     SQLiteDatabase db = this.getWritableDatabase();
	     Cursor cursor = db.rawQuery(query, null);

	     // 3. go over each row, build book and add it to list

	     if (cursor.moveToFirst()) {
	         do {
	         	
	         	//userRegistration.setTitle(cursor.getString(1));
	            // book.setAuthor(cursor.getString(2));

	             // Add book to books
	        	 STATElist.add(cursor.getString(2));
	         } while (cursor.moveToNext());
	     }
	     
			

	     // return books
	     return STATElist;
	 }
   
	 public List<String> getDistrict(String id, String username) {

		    List<String> STATElist = new LinkedList<String>();
		    STATElist.add("Select District");
		    // 1. build the query
		    String query = "SELECT  * FROM " + SQLITE_TABLE+" where TYPE='DISTRICT' and ParentId='"+id+"' and UserName='"+username+"' ORDER BY Name ASC";

			// 2. get reference to writable DB
		    SQLiteDatabase db = this.getWritableDatabase();
		    Cursor cursor = db.rawQuery(query, null);

		    // 3. go over each row, build book and add it to list

		    if (cursor.moveToFirst()) {
		        do {
		        	
		        	//userRegistration.setTitle(cursor.getString(1));
		           // book.setAuthor(cursor.getString(2));

		            // Add book to books
		       	 STATElist.add(cursor.getString(2));
		        } while (cursor.moveToNext());
		    }
		    
				

		    // return books
		    return STATElist;

		}

		public List<String> getAllTaluka(String id, String username) {

		    List<String> STATElist = new LinkedList<String>();
		    STATElist.add("Select Taluka");
		    // 1. build the query
		    String query = "SELECT  * FROM " + SQLITE_TABLE+" where TYPE='TALUKA' and ParentId='"+id+"' and UserName='"+username+"'ORDER BY Name ASC";
			// 2. get reference to writable DB
		    SQLiteDatabase db = this.getWritableDatabase();
		    Cursor cursor = db.rawQuery(query, null);

		    // 3. go over each row, build book and add it to list

		    if (cursor.moveToFirst()) {
		        do {
		        	
		        	//userRegistration.setTitle(cursor.getString(1));
		           // book.setAuthor(cursor.getString(2));

		            // Add book to books
		       	 STATElist.add(cursor.getString(2));
		        } while (cursor.moveToNext());
		    }
		    
				

		    // return books
		    return STATElist;

		}

		public List<String> getAllvillage(String id, String username) {

		    List<String> STATElist = new LinkedList<String>();
		    STATElist.add("Select a Village");
		    // 1. build the query
		    String query = "SELECT  * FROM " + SQLITE_TABLE+" where TYPE='VILLAGE' and ParentId='"+id+"'and UserName='"+username+"'ORDER BY Name ASC";

			// 2. get reference to writable DB
		    SQLiteDatabase db = this.getWritableDatabase();
		    Cursor cursor = db.rawQuery(query, null);

		    // 3. go over each row, build book and add it to list

		    if (cursor.moveToFirst()) {
		        do {
		        	
		        	//userRegistration.setTitle(cursor.getString(1));
		           // book.setAuthor(cursor.getString(2));

		            // Add book to books
		       	 STATElist.add(cursor.getString(2));
		        } while (cursor.moveToNext());
		    }
		    
				

		    // return books
		    return STATElist;

		}

		public List<String> getUSER(String name,String password) {

		    List<String> userlist = new LinkedList<String>();
		    
		    // 1. build the query
		    String query = "SELECT  * FROM " + SQLITE_TABLE_USER+" where Username='"+name+"' and Password='"+password+"'";

			// 2. get reference to writable DB
		    SQLiteDatabase db = this.getWritableDatabase();
		    Cursor cursor = db.rawQuery(query, null);

		    // 3. go over each row, build book and add it to list

		    if (cursor.moveToFirst()) {
		        do {
		        	
		        	//userRegistration.setTitle(cursor.getString(1));
		           // book.setAuthor(cursor.getString(2));

		            // Add book to books
		        	userlist.add(cursor.getString(1));
		        } while (cursor.moveToNext());
		    }
		    
				

		    // return books
		    return userlist;

		}
		
		public String getId(String forId,String type, String userName) {
			 String query = "SELECT  * FROM " + SQLITE_TABLE+" where Name='"+forId+"' and Type='"+type+"'and UserName='"+userName+"'";
				// 2. get reference to writable DB
			    SQLiteDatabase db = this.getWritableDatabase();
			    Cursor cursor = db.rawQuery(query, null);

			    // 3. go over each row, build book and add it to list
		String id="";
			    if (cursor.moveToFirst()) {
			        do {
			        	id=cursor.getString(1);
			        	//userRegistration.setTitle(cursor.getString(1));
			           // book.setAuthor(cursor.getString(2));

			            // Add book to books
			       
			        } while (cursor.moveToNext());
			    }
			    
			return id;
		}
		 public boolean deleteAllCountries(String usernames) {
				mDb=this.getWritableDatabase();
			  int doneDelete = 0;
			//  doneDelete = mDb.delete(SQLITE_TABLE, null , null);
			  mDb.delete(SQLITE_TABLE, KEY_USERNAME+" = ?", new String[] { usernames });
			  return doneDelete > 0;
			 
			 }
		 public boolean deleteuser(String usernames) {
				mDb=this.getWritableDatabase();
			  int doneDelete = 0;
			 // doneDelete = mDb.delete(SQLITE_TABLE_USER, null , null);
			  mDb.delete(SQLITE_TABLE_USER, KEY_USERNAME+" = ?", new String[] { usernames });
			  return doneDelete > 0;
			 
			 }
   
}
