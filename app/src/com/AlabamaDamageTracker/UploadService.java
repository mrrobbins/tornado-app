package com.AlabamaDamageTracker;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

public class UploadService extends IntentService {
	
	public static final String KEY_REPORT_ID = "report_id";
	public static final String KEY_USERNAME = "username";
	public static final String KEY_PASSWORD = "password";
	
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
		String username = intent.getStringExtra(KEY_USERNAME);
		String password = intent.getStringExtra(KEY_PASSWORD);
		
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
		try {
			Report source = dbh.getReport(reportId);
			HttpClient client = new DefaultHttpClient();
			HttpContext context = new BasicHttpContext();
			
		} finally {
			dbh.close();
		}

		
		
	}

}
