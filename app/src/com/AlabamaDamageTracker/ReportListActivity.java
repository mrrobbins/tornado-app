package com.AlabamaDamageTracker;

import java.util.ArrayList;
import java.util.List;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class ReportListActivity extends ListActivity {	
	private static final String TAG = "ReportList";
	
	private List<ListItem> places = new ArrayList<ListItem>();
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
		
		DatabaseHelper dbh = DatabaseHelper.openReadOnly(this);
		try {
			for (Report r : dbh.getReports()) {
				places.add(new ListItem(r.id, r.description));
			}
		} finally {
			dbh.close();
		}
		
		adapter = new ArrayAdapter<ListItem>(this, android.R.layout.simple_list_item_1, places);
		setListAdapter(adapter);
	}
	
	protected void onListItemClick(ListView l, View v, int position, long id) {		ListItem clicked = (ListItem) adapter.getItem(position);
		Intent editReportIntent = new Intent(this, EditImageScreen.class);
		editReportIntent.putExtra(EditImageScreen.KEY_REPORT_ID, clicked.id);
		startActivity(editReportIntent);
	}
	

}
