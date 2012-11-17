package com.AlabamaDamageTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class UploadService extends IntentService {

	public static final String ACTION_UPLOAD_IMAGES = "upload_images";

	public static final String KEY_REPORT_IDS = "report_ids";
	public static final String KEY_SERVER_ADDRESS = "server_address";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "password";

	private static final String loginPath = "/login";
	private static final String uploadPath = "/api/images/upload";

	private static final AsyncHttpClient client = new AsyncHttpClient();

	private final static String TAG = "UploadService";

	private static final int ongoingNotificationCode = 888;
	private static int doneNotificationCode = 0;

	private Context self = this;

	public UploadService() {
		super("UplaodServiceWorker");
	}

	private static class Wrapper<T> {
		public T wrapped;
		public Wrapper(T wrapped) {
			this.wrapped = wrapped;
		}
	}

	protected void updateOngoingNotification(int successful, int failed, int total) {
		String tickerText = "Upladeding photos (" + (successful + failed) + "/" + total + ")";
		Notification n = new Notification(
			android.R.drawable.ic_notification_overlay,
			tickerText,
			System.currentTimeMillis()
		);

		startForeground(ongoingNotificationCode, n);

	}

	protected void showDoneNotification(int successful, int failed, int total) {

		final String message = "Uploaded " + successful + "/" + total + " (" + failed + " failed)";
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		Notification n = new Notification(
			android.R.drawable.ic_notification_overlay,
			message,
			System.currentTimeMillis()
		);
		nm.notify(doneNotificationCode++, n);
	}
	
	protected void showBadLoginNotification() {

		final String message = "Upload failed: Invalid username or password";
		NotificationManager nm = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
		Notification n = new Notification(
			android.R.drawable.ic_notification_overlay,
			message,
			System.currentTimeMillis()
		);
		nm.notify(doneNotificationCode++, n);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "Handle New Intent");

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

		client.setTimeout(1000);
		
		PostResult loginResult = new Login(server, email, password).execute();
		Log.d(TAG, "Login Status: " + loginResult.toString());
		
	}
	
	private enum PostResult {
		SUCCESS, FAILURE, TIMEOUT
	}
	
	private class SynchronousPoster {
		
		private final AsyncHttpClient client;
		
		public SynchronousPoster(AsyncHttpClient client) {
			this.client = client;
		}
		
		public PostResult post(String url, RequestParams params) {
			Wrapper<PostResult> result = new Wrapper<PostResult>(null);
			Semaphore postAttemptComplete = new Semaphore(0);
			client.post(self, url, params, new PostHandler(postAttemptComplete, result));
			try {
				postAttemptComplete.acquire();
				return result.wrapped;
			} catch (InterruptedException ie) {
				return PostResult.TIMEOUT;
			}
		}
		
		private class PostHandler extends AsyncHttpResponseHandler {
			
			private final Wrapper<PostResult> result;
			private final Semaphore postAttempt;
			
			public PostHandler(Semaphore postAttemptComplete, Wrapper<PostResult> result) {
				this.postAttempt = postAttemptComplete;
				this.result = result;
			}
			
			@Override
			public void onSuccess(String content) {
				result.wrapped = PostResult.SUCCESS;
				postAttempt.release();
			}
			
			@Override
			public void onFailure(Throwable error, String content) {
				result.wrapped = PostResult.FAILURE;
				postAttempt.release();
			}
		}
	}
	
	private class Login {
		
		private static final String http = "http://";
		private static final String path = "/login";
		
		private final String url;
		private final String email;
		private final String password;
		
		private final SynchronousPoster poster = new SynchronousPoster(client);
		
		public Login(String server, String email, String password) {
			this.url = http + server + path;
			this.email = email;
			this.password = password;
		}
		
		public PostResult execute() {
			Log.d(TAG, "Logging in...");
			RequestParams params = new RequestParams();
			params.put("email", email);
			params.put("password", password);
			return poster.post(url,  params);
		}
		
	}
	
	private class Upload {
		
		static final String path = "/api/images/upload";
		
		private final String url;
		private final Report report;
		
		private final SynchronousPoster poster = new SynchronousPoster(client);
		
		public Upload(String server, long id) {
			
			DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
			Report report;
			try {
				report = dbh.getReport(id);
			} finally {
				dbh.close();
			}
			
			this.url = server + path;
			this.report = report;
			
		}
		
		public PostResult execute() {
			
			File picture = new File(report.picturePath);
			
			RequestParams params = new RequestParams();
			try {
				params.put(picture.getName(), picture);
			} catch (FileNotFoundException e) {
				return PostResult.FAILURE;
			}
			
			return poster.post(url,  params);
		}
		
	}

}
