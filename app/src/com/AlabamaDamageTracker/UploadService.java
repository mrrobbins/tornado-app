package com.AlabamaDamageTracker;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.R;
import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;

public class UploadService extends IntentService {

	public static final String ACTION_UPLOAD_IMAGES = "upload_images";

	public static final String KEY_REPORT_IDS = "report_ids";
	public static final String KEY_SERVER_ADDRESS = "server_address";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";

	private static final String loginPath = "/login";
	private static final String uploadPath = "/api/images/upload";

    private HttpClient client = new DefaultHttpClient();
    private HttpContext contenxt = new BasicHttpContext();

	private final static String TAG = "UploadService";

	private static final int ongoingNotificationCode = 888;
	private static int doneNotificationCode = 999;
	
	private Context self = this;

	public UploadService() {
		super("UplaodServiceWorker");
	}
	
	protected void badUsernameOrPassword() {
		
		NotificationCompat.Builder nb =
			new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("Damage Report")
			.setContentText("Bad Email or Password")
			.setTicker("Bad Email or Password")
			.setSmallIcon(android.R.drawable.ic_dialog_alert);
		
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(doneNotificationCode, nb.build());
	}
	
	protected void loginUnableToConnect() {
		
		NotificationCompat.Builder nb =
			new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("Damage Report")
			.setContentText("Unable to connect to server")
			.setTicker("Unable to connect")
			.setSmallIcon(android.R.drawable.ic_dialog_alert);
		
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(doneNotificationCode, nb.build());
	}
	
	protected void badServerName() {
		
		NotificationCompat.Builder nb =
			new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("Damage Report")
			.setContentText("Invalid server name")
			.setTicker("Invalid server")
			.setSmallIcon(android.R.drawable.ic_dialog_alert);
		
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(doneNotificationCode, nb.build());
	}
	
	protected void statusNotifcation(int done, int total) {
		NotificationCompat.Builder nb =
			new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("Damage Report")
			.setContentText("Uploading Images")
			.setSmallIcon(android.R.drawable.ic_menu_upload)
			.setProgress(total, done, false);
		
		startForeground(ongoingNotificationCode, nb.build());
	}
	
	protected void doneNotification(int done, int failed) {
		NotificationCompat.Builder nb =
			new NotificationCompat.Builder(getApplicationContext())
			.setContentTitle("Damage Report")
			.setContentText("Completed upload (" + failed + "/" + (done + failed) + " failed)")
			.setSmallIcon(android.R.drawable.ic_menu_upload);
		
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		nm.notify(doneNotificationCode++, nb.build());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
	    HttpParams params = client.getParams();
	    
	    HttpConnectionParams.setConnectionTimeout(params, 20000);
	    HttpConnectionParams.setSoTimeout(params, 20000);

		final long[] ids = intent.getLongArrayExtra(KEY_REPORT_IDS);

		if (ids == null) {
			Log.e(TAG, "Report ids not passed in Intent");
			return;
		}

		final String server = intent.getStringExtra(KEY_SERVER_ADDRESS);
		if (server == null) {
			Log.e(TAG, "No server address in Intent");
			return;
		}

		final String email = intent.getStringExtra(KEY_EMAIL);
		if (email == null) {
			Log.e(TAG, "No email in Intent");
			return;
		}


		String password = intent.getStringExtra(KEY_PASSWORD);
		if (password == null) {
			Log.e(TAG, "No password in Intent");
			return;
		}
		
		boolean loggedin = login(server, email, password);
		if (!loggedin) {
			stopForeground(true);
			return;
		}
		
		ArrayList<Report> reports = new ArrayList<Report>(ids.length);
		
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			for (long id : ids) {
				Report r = dbh.getReport(id);
				reports.add(r);
			}
		} finally {
			dbh.close();
		}
		
		int done = 0;
		int failed = 0;
		int total = ids.length;
		for (Report r : reports) {
			if (upload(server, r)) {
				r.uploaded = true;
				DatabaseHelper dbh1 = DatabaseHelper.openReadWrite(self);
				try {
					dbh1.updateReport(r);
				} finally {
					dbh1.close();
				}
				done++;
			} else failed++;
			statusNotifcation(done, total);
		}
		
		doneNotification(done, failed);
		stopForeground(true);
		
	}
	
	private boolean login(String server, String email, String password) {
		
        HttpPost post = new HttpPost("http://" + server + loginPath);
        
        List<NameValuePair> pairs = new ArrayList<NameValuePair>(2);
        pairs.add(new BasicNameValuePair("email", email));
        pairs.add(new BasicNameValuePair("password", password));
		try {
			post.setEntity(new UrlEncodedFormEntity(pairs));
			HttpResponse response = client.execute(post, contenxt);
			if (response.getStatusLine().getStatusCode() != 200) {
				badUsernameOrPassword();
				return false;
			} else {
				return true;
			}
		} catch (IOException ioe) {
			loginUnableToConnect();
			return false;
		} catch (IllegalArgumentException iae) {
			badServerName();
			return false;
		}
	}
	
	private  boolean upload(String server, Report report) {
		
        HttpPost post = new HttpPost("http://" + server + uploadPath);
        
		File f = new File(report.picturePath);
		
        MimeTypeMap mimeTypes = MimeTypeMap.getSingleton();
        String extension = mimeTypes.getFileExtensionFromUrl(f.getName());
        String mimeType = mimeTypes.getMimeTypeFromExtension(extension);
        
        String key = f.getName();
		MultipartEntity mpe = new MultipartEntity();
		mpe.addPart(key, new FileBody(f, mimeType));
		try {
			mpe.addPart(key + "|latitude", new StringBody(String.valueOf(report.latitude)));
			mpe.addPart(key + "|longitude", new StringBody(String.valueOf(report.longitude)));
			mpe.addPart(key + "|indicator", new StringBody(String.valueOf(report.damageIndicator)));
			mpe.addPart(key + "|degree", new StringBody(String.valueOf(report.degreeOfDamage)));
			mpe.addPart(key + "|notes", new StringBody(report.notes));
			mpe.addPart(key + "|address", new StringBody(report.address));
			mpe.addPart(key + "|time", new StringBody(String.valueOf(report.time)));
		} catch (UnsupportedEncodingException use) { }
		
		post.setEntity(mpe);
		
		try {
			HttpResponse response = client.execute(post, contenxt);
			return response.getStatusLine().getStatusCode() == 200;
		} catch (IOException ioe) {
			return false;
		}
		
	}
	

}
