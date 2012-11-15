/*** Much of this code and its idea / implementation was taken directly from 
 * "http://www.reigndesign.com/blog/using-your-own-sqlite-database-in-android-applications/"
 * No plagiarism was intended. -Kyle
 */

package com.AlabamaDamageTracker;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.IDN;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DatabaseHelper {
	
	public static final String CN_ID = "_id";
	public static final String CN_DESC = "description";
	
	public static final String TABLE_REPORT = "report";
	
	public static final String CN_PIC_PATH = "picture_path";
	public static final String CN_TIME = "time";
	public static final String CN_LAT = "latitude";
	public static final String CN_LONG = "longitude";
	public static final String CN_ADDR = "address";
	public static final String CN_LOC_DESC = "location_description";
	public static final String CN_NOTES = "notes";
	public static final String CN_UPLOADED = "uploaded";
	public static final String CN_DI = "damage_indicator";
	public static final String CN_DOD = "degree_of_damage";
	
	public static final String TABLE_DI = "tornado_damge_indicator";
	
	public static final String CN_ABBREV = "abbreviation";
	
	public static final String TABLE_DOD = "tornado_degree_of_damage";
	public static final String CN_INDIC_ABBREV = "indicator_abbreviation";
	public static final String CN_WIND_LB = "loweset_windspeed";
	public static final String CN_WIND_EV = "expected_windspeed";
	public static final String CN_WIND_UB = "highest_windspeed";

	//The Android's default system path of your application database.
	private final File dataDirectory;

	public static final String DB_NAME = "db";
	
	private static final String TAG = "DatabaseHelper";
	private static final ReentrantLock CREATION_LOCK = new ReentrantLock();

	private SQLiteDatabase database; 
	
	private final Context context;

	private DatabaseHelper(Context context) {

		this.context = context;
		dataDirectory = context.getFilesDir();
		
		CREATION_LOCK.lock();
		try {
			if (!dbExists()) {
				try {
					copyDb();
				} catch (IOException e) {
					deleteDb();
					Log.e(TAG, Log.getStackTraceString(e));
				} catch (RuntimeException e) {
					deleteDb();
					throw e;
				}
			}
		} finally {
			CREATION_LOCK.unlock();
		}
		
	}
	
	public static DatabaseHelper openReadOnly(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		helper.readOnly();
		return helper;
	}
	
	public static DatabaseHelper openReadWrite(Context context) {
		DatabaseHelper helper = new DatabaseHelper(context);
		helper.readWrite();
		return helper;
	}
	
	private void readOnly() {
		database = SQLiteDatabase.openDatabase(
			new File(dataDirectory, DB_NAME).getAbsolutePath(),
			null,
			SQLiteDatabase.OPEN_READONLY
		);
	}
	
	private void readWrite() {
		database = SQLiteDatabase.openDatabase(
			new File(dataDirectory, DB_NAME).getAbsolutePath(),
			null,
			SQLiteDatabase.OPEN_READWRITE
		);
	}
	
	private void copyDb() throws IOException {
		File dbFile = new File(dataDirectory, DB_NAME);
		
		InputStream input = context.getAssets().open(DB_NAME);
		try {
			OutputStream output = new FileOutputStream(dbFile);
			try {
				
				byte[] buffer = new byte[1024];
				
				for (int read = 0; read != -1; read = input.read(buffer)) {
					output.write(buffer, 0, read);
				}
			} finally {
				output.close();
			}
		} finally {
			input.close();
		}
	}
	
	private boolean dbExists() {
		return new File(dataDirectory, DB_NAME).exists();
	}
	
	private void deleteDb() {
		new File(dataDirectory, DB_NAME).delete();
	}
	
	public void close() {
		database.close();
	}

	public long insertReport(Report report) {
		ContentValues cv = new ContentValues();
		
		if (report.id != null) cv.put(CN_ID, report.id);
		
		if (report.latitude != null) cv.put(CN_LAT, report.latitude);
		else cv.put(CN_LAT, -1d);
		
		if (report.longitude != null) cv.put(CN_LONG, report.longitude);
		else cv.put(CN_LONG, -1d);
		
		if (report.address != null) cv.put(CN_ADDR, report.address);
		else cv.put(CN_ADDR, "");
		
		if (report.notes != null) cv.put(CN_NOTES, report.notes);
		else cv.put(CN_NOTES, "");
		
		if (report.damageIndicator != null) cv.put(CN_DI, report.damageIndicator);
		else cv.put(CN_DI, -1);
		
		if (report.picturePath != null) cv.put(CN_PIC_PATH, report.picturePath);
		
		if (report.description != null) cv.put(CN_LOC_DESC, report.description);
		else cv.put(CN_LOC_DESC, "");
		
		if (report.time != null) cv.put(CN_TIME, report.time);
		else cv.put(CN_TIME, System.currentTimeMillis() / 1000L);
		
		if (report.uploaded != null) cv.put(CN_UPLOADED, report.uploaded);
		else cv.put(CN_UPLOADED, false);
		
		long b =  database.insert(TABLE_REPORT, null, cv);
		return b;
	}
	
	public boolean updateReport(Report report) {
		ContentValues cv = new ContentValues();
		
		if (report.notes != null) cv.put(CN_NOTES, report.notes);
		else cv.put(CN_NOTES, "");
		
		if (report.address != null) cv.put(CN_ADDR, report.address);
		else cv.put(CN_ADDR, "");
		
		if (report.damageIndicator != null) cv.put(CN_DI, report.damageIndicator);
		else cv.put(CN_DI, -1);
		
		String filter = CN_ID + " = " + report.id;
		int updateCnt = database.update(TABLE_REPORT, cv, filter, null);
		
		return updateCnt == 1 ? true : false;
	}
	
	public Report getReport(long id) {
		
		String filter = CN_ID + " = " + id;
		Cursor c = database.query(TABLE_REPORT, null, filter, null, null, null, null, "1");
		c.moveToFirst();
		
		int colId = c.getColumnIndex(CN_ID);
		int colLat = c.getColumnIndex(CN_LAT);
		int colLang = c.getColumnIndex(CN_LONG);
		int colAddr = c.getColumnIndex(CN_ADDR);
		int colPicPath = c.getColumnIndex(CN_PIC_PATH);
		int colUploaded = c.getColumnIndex(CN_UPLOADED);
		int colNotes = c.getColumnIndex(CN_NOTES);
		int colTime = c.getColumnIndex(CN_TIME);
		int colDesc = c.getColumnIndex(CN_LOC_DESC);
		int colDod = c.getColumnIndex(CN_DOD);
		int colDi = c.getColumnIndex(CN_DI);
		
		Report report = new Report();
		report.id = c.getLong(colId);
		report.address = c.getString(colAddr);
		report.notes = c.getString(colNotes);
		report.notes = c.getString(colNotes);
		report.latitude = c.getDouble(colLat);
		report.longitude = c.getDouble(colLang);
		report.picturePath = c.getString(colPicPath);
		report.time = c.getLong(colTime);
		report.uploaded = c.getInt(colUploaded) == 0 ? false : true;
		report.description = c.getString(colDesc);
		report.degreeOfDamage = c.getInt(colDod);
		report.damageIndicator = c.getInt(colDi);
		
		return report;
	}
	
	public List<Report> getReports() {
		
		Cursor c = database.query(TABLE_REPORT, null, null, null, null, null, null);
		
		int colId = c.getColumnIndex(CN_ID);
		int colLat = c.getColumnIndex(CN_LAT);
		int colLang = c.getColumnIndex(CN_LONG);
		int colAddr = c.getColumnIndex(CN_ADDR);
		int colPicPath = c.getColumnIndex(CN_PIC_PATH);
		int colUploaded = c.getColumnIndex(CN_UPLOADED);
		int colNotes = c.getColumnIndex(CN_NOTES);
		int colTime = c.getColumnIndex(CN_TIME);
		int colDesc = c.getColumnIndex(CN_LOC_DESC);
		int colDod = c.getColumnIndex(CN_DOD);
		int colDi = c.getColumnIndex(CN_DI);
		
		List<Report> reports = new ArrayList<Report>(c.getCount());
		c.moveToFirst();
		
		while (!c.isAfterLast()) {
			
			Report report = new Report();
			report.id = c.getLong(colId);
			report.address = c.getString(colAddr);
			report.notes = c.getString(colNotes);
			report.notes = c.getString(colNotes);
			report.latitude = c.getDouble(colLat);
			report.longitude = c.getDouble(colLang);
			report.picturePath = c.getString(colPicPath);
			report.time = c.getLong(colTime);
			report.uploaded = c.getInt(colUploaded) == 0 ? false : true;
			report.description = c.getString(colDesc);
			report.degreeOfDamage = c.getInt(colDod);
			report.damageIndicator = c.getInt(colDi);
			
			reports.add(report);
			c.moveToNext();
		}
		
		return reports;
		
	}
	
	public void setLattitude(long id, double newLatitude) {
		ContentValues cv = new ContentValues();
		cv.put(CN_LAT, newLatitude);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setLongitude(long id, double newLongitude) {
		ContentValues cv = new ContentValues();
		cv.put(CN_LONG, newLongitude);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setAddress(long id, String newAddress) {
		ContentValues cv = new ContentValues();
		cv.put(CN_ADDR, newAddress);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setNotes(long id, String newNotes) {
		ContentValues cv = new ContentValues();
		cv.put(CN_NOTES, newNotes);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setPhotoPath(long id, String newPhotoPath) {
		ContentValues cv = new ContentValues();
		cv.put(CN_PIC_PATH, newPhotoPath);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setIndicator(long id, int newDamageIndicator) {
		ContentValues cv = new ContentValues();
		cv.put(CN_DI, newDamageIndicator);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public void setDegree(long id, int newDegreeOfDamage) {
		ContentValues cv = new ContentValues();
		cv.put(CN_DOD, newDegreeOfDamage);
		database.update(TABLE_REPORT, cv, "id = " + id, null);
	}
	
	public double getLattitude(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_LAT},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getDouble(0);
	}
	
	public double getLongitude(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_LONG},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getLong(0);
	}
	
	public String getAddress(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_ADDR},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getString(0);
	}
	
	public String getNotes(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_NOTES},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getString(0);
	}
	
	public String getPhotoPath(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_PIC_PATH},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getString(0);
	}
	
	public int setIndicator(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_DI},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getInt(0);
	}
	
	public int getDegree(long id) {
		Cursor c = database.query(
			TABLE_REPORT,
			new String[] {CN_DOD},
			"id = " + String.valueOf(id),
			null,
			null,
			null,
			null,
			"1"
		);
		c.moveToFirst();
		return c.getInt(0);
	}
	
	public boolean deleteReport(long id) {
		int res = database.delete(TABLE_REPORT, CN_ID + " = " + String.valueOf(id), null);
		return (res == 0) ? false : true;
	}
	
	
	public DamageIndicator getIndicator(long id) {
		String filter = CN_ID + " = " + id;
		Cursor c = database.query(TABLE_DI, null, filter, null, null, null, CN_ID, "1");
		c.moveToFirst();
		
		int colId = c.getColumnIndex(CN_ID);
		int colDesc = c.getColumnIndex(CN_DESC);
		int colAbbrev = c.getColumnIndex(CN_ABBREV);
		
			
		DamageIndicator indicator = new DamageIndicator();
		indicator.id = c.getLong(colId);
		indicator.description = c.getString(colDesc);
		indicator.abbreviation = c.getString(colAbbrev);
			
		return indicator;
	}
	
	public List<DamageIndicator> getIndicators() {
		Cursor c = database.query(TABLE_DI, null, null, null, null, null, CN_ID);
		c.moveToFirst();
		List<DamageIndicator> indicators = new ArrayList<DamageIndicator>(c.getCount());
		
		int col_id = c.getColumnIndex(CN_ID);
		int col_desc = c.getColumnIndex(CN_DESC);
		int col_abbrev = c.getColumnIndex(CN_ABBREV);
		
		while (!c.isAfterLast()) {
			
			DamageIndicator indicator = new DamageIndicator();
			indicator.id = c.getLong(col_id);
			indicator.description = c.getString(col_desc);
			indicator.abbreviation = c.getString(col_abbrev);
			
			indicators.add(indicator);
			
			c.moveToNext();
		}
		return indicators;
	}
	
	public DegreeOfDamage getDegree(long id, String abbreviation) {
		String filter = CN_ID + " = " + id + ", " + CN_INDIC_ABBREV + " = " + abbreviation;
		Cursor c = database.query(TABLE_DOD, null, filter, null, null, null, null, "1");
		c.moveToFirst();
		
		final int colId = c.getColumnIndex(CN_ID);
		final int colDesc = c.getColumnIndex(CN_DESC);
		final int colIndicAbbrev = c.getColumnIndex(CN_INDIC_ABBREV);
		final int colWindLb = c.getColumnIndex(CN_WIND_LB);
		final int colWindEv = c.getColumnIndex(CN_WIND_EV);
		final int colWindUb = c.getColumnIndex(CN_WIND_UB);
		
		DegreeOfDamage degree = new DegreeOfDamage();
		degree.id = c.getLong(colId);
		degree.description = c.getString(colDesc);
		degree.indicatorAbbreviation = c.getString(colIndicAbbrev);
		degree.lowestWindspeed = c.getInt(colWindLb);
		degree.expectedWindspeed = c.getInt(colWindEv);
		degree.highestWindspeed = c.getInt(colWindUb);
		
		return degree;
	}
	
	public List<DegreeOfDamage> getDegrees() {
		Cursor c = database.query(TABLE_DOD, null, null, null, null, null, null);
		c.moveToFirst();
		
		final int colId = c.getColumnIndex(CN_ID);
		final int colDesc = c.getColumnIndex(CN_DESC);
		final int colIndicAbbrev = c.getColumnIndex(CN_INDIC_ABBREV);
		final int colWindLb = c.getColumnIndex(CN_WIND_LB);
		final int colWindEv = c.getColumnIndex(CN_WIND_EV);
		final int colWindUb = c.getColumnIndex(CN_WIND_UB);
		
		List<DegreeOfDamage> degrees = new ArrayList<DegreeOfDamage>(c.getCount());
		
		while (!c.isAfterLast()) {
			
			DegreeOfDamage degree = new DegreeOfDamage();
			
			degree.id = c.getLong(colId);
			degree.description = c.getString(colDesc);
			degree.indicatorAbbreviation = c.getString(colIndicAbbrev);
			degree.lowestWindspeed = c.getInt(colWindLb);
			degree.expectedWindspeed = c.getInt(colWindEv);
			degree.highestWindspeed = c.getInt(colWindUb);
			
			degrees.add(degree);
			
			c.moveToNext();
		}
		
		
		return degrees;
	}

}