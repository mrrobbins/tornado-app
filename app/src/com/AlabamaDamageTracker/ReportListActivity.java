package com.AlabamaDamageTracker;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.AndroidCharacter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ReportListActivity extends ListActivity {	
	private static final String TAG = "ReportList";
	
	private List<ListItem> places;
	private ListAdapter adapter;
	
	private class ListItem {
		public final long id;
		public final String name;
				public ListItem(long id, String name) {
			this.id = id;
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
	}
	
	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.report_list_layout);
		
		adapter = getAdapter();
		setListAdapter(adapter);
	}
	
	protected void onResume() {
		super.onResume();
		adapter = getAdapter();
		setListAdapter(adapter);
	}
	
	private ArrayList<ListItem> getPlaces() {
		ArrayList<ListItem> places = new ArrayList<ListItem>();
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(this);
		try {
			for (Report r : dbh.getReports()) {
				places.add(new ListItem(r.id, r.description));
			}
		} finally {
			dbh.close();
		}
		return places;
	}
	
	private ListAdapter getAdapter() {
		ArrayList<ListItem> places = getPlaces();
		ListAdapter adapter = new ArrayAdapter<ListItem>(this, android.R.layout.simple_list_item_1, places);
		return adapter;
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {		ListItem clicked = (ListItem) adapter.getItem(position);
		Intent editReportIntent = new Intent(this, EditImageScreen.class);
		editReportIntent.putExtra(EditImageScreen.KEY_REPORT_ID, clicked.id);
		startActivity(editReportIntent);
	}
	
	public void uploadClicked(View clickedButton) {
		AlertDialog.Builder b = new AlertDialog.Builder(this);
		b.setTitle("Enter Login Details");
		LayoutInflater inflater = getLayoutInflater();
		
		final View v = inflater.inflate(R.layout.user_login_screen, null);
		b.setView(v);
		
		b.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { }
		});
		
		b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			
			public void onClick(DialogInterface dialog, int which) {
				if (places == null) return;
				for (ListItem li : places) {
					String server = ((EditText) v.findViewById(R.id.server_address)).getText().toString();
					String email = ((EditText) v.findViewById(R.id.user_email)).getText().toString();
					String password = ((EditText) v.findViewById(R.id.user_password)).getText().toString();
					Intent serviceIntent = new Intent(UploadService.ACTION_UPLOAD_IMAGES);
					serviceIntent.putExtra(UploadService.KEY_SERVER_ADDRESS, server);
					serviceIntent.putExtra(UploadService.KEY_EMAIL, email);
					serviceIntent.putExtra(UploadService.KEY_PASSWORD, password);
					startService(serviceIntent);
				}
			}
		});
		
		
		b.show();
	}
}
