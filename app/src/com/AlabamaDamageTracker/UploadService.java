package com.AlabamaDamageTracker;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
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

	public static final String ACTION_UPLOAD_IMAGE = "upload_image";

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
		Log.d(TAG, "upload");

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

		RequestParams params = new RequestParams();
		params.put("email", email);
		params.put("password", password);
		
		final Semaphore doneWithIntent = new Semaphore(0);

		client.post(server+loginPath, params, new AsyncHttpResponseHandler() {

			public void onSuccess(byte[] binaryData) {
				ArrayList<Report> reports = new ArrayList<Report>(ids.length);
				DatabaseHelper dbh = DatabaseHelper.openReadOnly(self);
				try {
					for (long reportId : ids) reports.add(dbh.getReport(reportId));
				} finally {
					dbh.close();
				}


				final Semaphore s = new Semaphore(1);
				final ReadWriteLock rw = new ReentrantReadWriteLock();

				final int total = ids.length;
				final Wrapper<Integer> successfulCount = new Wrapper<Integer>(0);
				final Wrapper<Integer> failedCount = new Wrapper<Integer>(0);

				for (Report r : reports) {
					RequestParams params = new RequestParams();
					try {
						params.put(r.picturePath, new File(r.picturePath));
					} catch(FileNotFoundException fnf) {
						rw.writeLock().lock();
						try {
							failedCount.wrapped++;
						} finally {
							rw.writeLock().unlock();
						}
					}
					client.post(server+uploadPath, params, new AsyncHttpResponseHandler() {

						public void onSuccess(byte[] binaryData) {
							rw.writeLock().lock();
							try {
								successfulCount.wrapped++;
								s.release();
							} finally {
								rw.writeLock().unlock();
							}
						}
						public void onFailure(Throwable error, byte[] binaryData)  {
							rw.writeLock().lock();
							try {
								failedCount.wrapped++;
								s.release();
							} finally {
								rw.writeLock().unlock();
							}
						}

					});
				}

				while(true) {
					try {
						s.acquire();
					} catch (InterruptedException ie) {
						stopForeground(true);
						return;
					}
					rw.readLock().lock();
					try {
						if (failedCount.wrapped + successfulCount.wrapped == total) {
							showDoneNotification(successfulCount.wrapped, failedCount.wrapped, total);
							stopForeground(true);
							doneWithIntent.release();
							return;
						}
						updateOngoingNotification(successfulCount.wrapped, failedCount.wrapped, total);
					} finally {
						rw.readLock().unlock();
					}
				}



			}

			public void onFailure(Throwable error, byte[] binaryData) {
				showBadLoginNotification();
				doneWithIntent.release();
			}

		});
		
		try {
			doneWithIntent.acquire();
		} catch (InterruptedException ie) { }
	}

}
