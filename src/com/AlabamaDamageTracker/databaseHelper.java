/*** Much of this code and its idea / implementation was taken directly from 
 * "http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/"
 * No plagiarism was intended. -Kyle
 */

package com.AlabamaDamageTracker;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

public class databaseHelper extends SQLiteOpenHelper{

	private static String KEY_LAT = "gps_Lat";
	private static String KEY_LONG = "gps_longi";
	private static String KEY_STREETADDRESS = "street_address";
	private static String KEY_NOTESA = "notes_Audio";
	private static String KEY_NOTEST = "notes_Text";
	private static String KEY_USERID = "user_ID";
	private static String KEY_PICTUREID = "picture_ID";
	private static String KEY_USERNAME = "userName";
	private static String KEY_PICTURE = "picture";
	private static String KEY_LOCATIONID = "location_id";
	private static String KEY_DAMAGE = "Degree_of_Damage";
	private static String KEY_EF = "EF_Rating";

	private static String DATABASE_DAMAGENOTES = "DamageNotes";
	private static String DATABASE_USERS = "Users";
	private static String DATABSE_PICTURES = "Pictures";

	//The Android's default system path of your application database.
	private static String DB_PATH = "/data/data/com.AlabamaDamageTracker/databases/";

	private static String DB_NAME = "DamageTracker";

	private SQLiteDatabase myDataBase; 

	private final Context myContext;

	/**
	 * Constructor
	 * Takes and keeps a reference of the passed context in order to access to the application assets and resources.
	 * @param context
	 */
	public databaseHelper(Context context) {

		super(context, DB_NAME, null, 1);
		this.myContext = context;
	}	

	/**
	 * Creates a empty database on the system and rewrites it with your own database.
	 * */
	public void createDataBase() throws IOException{

		boolean dbExist = checkDataBase();

		if(dbExist){
			//do nothing - database already exist
		}else{

			//By calling this method and empty database will be created into the default system path
			//of your application so we are gonna be able to overwrite that database with our database.
			this.getReadableDatabase();

			try {

				copyDataBase();

			} catch (IOException e) {

				throw new Error("Error copying database");

			}
		}

	}

	/**
	 * Check if the database already exist to avoid re-copying the file each time you open the application.
	 * @return true if it exists, false if it doesn't
	 */
	private boolean checkDataBase(){

		SQLiteDatabase checkDB = null;

		try{
			String myPath = DB_PATH + DB_NAME;
			checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

		}catch(SQLiteException e){

			//database does't exist yet.

		}

		if(checkDB != null){

			checkDB.close();

		}

		return checkDB != null ? true : false;
	}

	/**
	 * Copies your database from your local assets-folder to the just created empty database in the
	 * system folder, from where it can be accessed and handled.
	 * This is done by transfering bytestream.
	 * */
	private void copyDataBase() throws IOException{

		//Open your local db as the input stream
		InputStream myInput = myContext.getAssets().open(DB_NAME);

		// Path to the just created empty db
		String outFileName = DB_PATH + DB_NAME;

		//Open the empty db as the output stream
		OutputStream myOutput = new FileOutputStream(outFileName);

		//transfer bytes from the inputfile to the outputfile
		byte[] buffer = new byte[1024];
		int length;
		while ((length = myInput.read(buffer))>0){
			myOutput.write(buffer, 0, length);
		}

		//Close the streams
		myOutput.flush();
		myOutput.close();
		myInput.close();

	}

	public void openDataBase() throws SQLException{

		//Open the database
		String myPath = DB_PATH + DB_NAME;
		myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);

	}

	@Override
	public synchronized void close() {

		if(myDataBase != null)
			myDataBase.close();

		super.close();

	}

	@Override
	public void onCreate(SQLiteDatabase db) {

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}


	/*******
	 *******
	 Damage
	 *******
	 *******
	 */

	public long insertDamage(String lat, String longi, String address, String Audio, String Text, String UserID, String Pic, String Damage, String EF) 
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LAT, lat);
		initialValues.put(KEY_LONG, longi);
		initialValues.put(KEY_STREETADDRESS, address);
		initialValues.put(KEY_NOTESA, Audio);
		initialValues.put(KEY_NOTEST, Text);
		initialValues.put(KEY_USERID, UserID);
		initialValues.put(KEY_PICTUREID, Pic);
		initialValues.put(KEY_EF, EF);
		initialValues.put(KEY_DAMAGE, Damage);
		return myDataBase.insert(DATABASE_DAMAGENOTES, null, initialValues);
	}


	public void updateDamage(long id, String lat, String longi, String address, String Audio, String Text, String UserID, String Damage, String EF){
		ContentValues args = new ContentValues();
		args.put(KEY_LAT, lat);
		ContentValues args5 = new ContentValues();
		args5.put(KEY_LONG, longi);
		ContentValues args6 = new ContentValues();
		args6.put(KEY_DAMAGE, Damage);
		ContentValues args7 = new ContentValues();
		args7.put(KEY_EF, EF);
		ContentValues args1 = new ContentValues();
		args1.put(KEY_STREETADDRESS, address);
		ContentValues args2 = new ContentValues();
		args2.put(KEY_NOTESA, Audio);
		ContentValues args3 = new ContentValues();
		args3.put(KEY_NOTEST, Text);
		ContentValues args4 = new ContentValues();
		args4.put(KEY_USERID, UserID);

		myDataBase.update(DATABASE_DAMAGENOTES, args1, " _id like "+id,null);
		myDataBase.update(DATABASE_DAMAGENOTES, args2, " _id like "+id,null);
		myDataBase.update(DATABASE_DAMAGENOTES, args3, " _id like "+id,null);
		myDataBase.update(DATABASE_DAMAGENOTES, args4, " _id like "+id,null);
		//myDataBase.update(DATABASE_DAMAGENOTES, args5, " _id like "+id,null);
		myDataBase.update(DATABASE_DAMAGENOTES, args6, " _id like "+id,null);
		myDataBase.update(DATABASE_DAMAGENOTES, args7, " _id like "+id,null);

		return;
	}

	public void updatePic( long pic, long id){
		ContentValues args = new ContentValues();
		args.put("picture_ID", pic);
		myDataBase.update(DATABASE_DAMAGENOTES, args, " _id like "+id,null);
	}

	public Cursor getDamagePic(long id) 
	{
		String Z = "Select picture_ID from DamageNotes where _id like "+"'"+id+"'";
		Cursor mCursor = myDataBase.rawQuery(Z, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}        

		String x = mCursor.getString(0);
		String Z1 = "Select picture from Pictures where _id like "+"'"+x+"'";
		Cursor mCursor1 = myDataBase.rawQuery(Z1, null);
		if (mCursor1 != null) {
			mCursor1.moveToFirst();
		} 
		return mCursor1;
	}


	public void deleteRecord(long Id) 
	{
		myDataBase.delete(DATABASE_DAMAGENOTES, "_id like" + 
				"'" + Id+ "'", null);
		return; 		
	}


	public Cursor getAllLocations() 
	{
		String Z = "Select * from DamageNotes where picture_id is NOT NULL";
		Cursor mCursor = myDataBase.rawQuery(Z, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}        	
		return mCursor;
	}


	public Cursor getDamageByID(long id) 
	{
		String Z = "Select * from DamageNotes where _id like "+"'"+id+"'";
		Cursor mCursor = myDataBase.rawQuery(Z, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}        	
		return mCursor;
	}

	public void updateGPS(String Lat, long id, String Lon){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_LAT, Lat);
		ContentValues initialValues1 = new ContentValues();
		initialValues1.put(KEY_LONG, Lon);
		myDataBase.update(DATABASE_DAMAGENOTES, initialValues, " _id like "+id, null);
		myDataBase.update(DATABASE_DAMAGENOTES, initialValues1, " _id like "+id, null);
	}

	/*******
	 *******
	 USER
	 *******
	 *******
	 */

	public long insertUser(String User)
	{
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_USERNAME, User);
		return myDataBase.insert(DATABASE_USERS,null,initialValues);
	}

	/*******
	 *******
	 *Picture
	 *******
	 *******
	 */

	public Cursor getAllDamagePic(long id) 
	{
		String Z = "Select picture from Pictures where location_id like "+"'"+id+"'";
		Cursor mCursor = myDataBase.rawQuery(Z, null);
		if (mCursor != null) {
			mCursor.moveToFirst();
		}        	
		return mCursor;
	}


	public long insertPicture(String Picture, long id){
		ContentValues initialValues = new ContentValues();
		initialValues.put(KEY_PICTURE,Picture);
		initialValues.put(KEY_LOCATIONID, id);
		return myDataBase.insert(DATABSE_PICTURES, null, initialValues);
	}





}