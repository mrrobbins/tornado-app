package com.AlabamaDamageTracker;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class UploadService extends IntentService {
	
	public static final String ACTION_UPLOAD_IMAGE = "upload_image";
	
	public static final String KEY_REPORT_ID = "report_id";
	public static final String KEY_SERVER_ADDRESS = "server_address";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";
	
	private int currentNotification = 0;
	
	private Context self = this;
	
	private class UploadImageTask extends AsyncTask {

		@Override
		protected Object doInBackground(Object... params) {
			return null;
		}
		
	}

	public UploadService() {
		super("UplaodServiceWorker");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		
		long reportId = intent.getLongExtra(KEY_REPORT_ID, -1l);
		String username = intent.getStringExtra(KEY_EMAIL);
		String password = intent.getStringExtra(KEY_PASSWORD);
		
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			Report source = dbh.getReport(reportId);
			HttpClient client = new DefaultHttpClient();
			HttpContext context = new BasicHttpContext();
			Report r = dbh.getReport(reportId);
			NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
			Notification n = new Notification(android.R.drawable.ic_notification_overlay, "Upladed photo from " + r.time, System.currentTimeMillis());
			nm.notify(currentNotification++, n);
			
		} finally {
			dbh.close();
		}

		
		
	}

}
