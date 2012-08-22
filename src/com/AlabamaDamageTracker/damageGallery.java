/* Taken with many modifications from "creating image gallery" on www.edumobile.org/android/android-beginner-tutorials/creating-image-gallery*/

package com.AlabamaDamageTracker;
import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class damageGallery extends Activity {

	public ArrayList<String> Strin = new ArrayList<String>();
	public long LocationID;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gallery);
		currentDamage info = ((currentDamage)getApplicationContext());
		LocationID = info.getLocationID();
		databaseHelper myDbHelper = new databaseHelper(getBaseContext());    
		myDbHelper.openDataBase();
		Cursor c = myDbHelper.getAllDamagePic(LocationID);
		try {
			do {
				//  Toast.makeText(damageGallery.this, "" + c.getCount() + "   " +c.getString(0), Toast.LENGTH_SHORT).show();
				// Toast.makeText(damageGallery.this, "Fail", Toast.LENGTH_SHORT).show();
				Strin.add(c.getString(0));
				//  Strin.add(c.getString(0));
			} while (c.moveToNext());

		}
		catch (Exception e){}

		try {Strin.add(c.getString(0));}catch (Exception e){}
		c.close();
		Gallery g = (Gallery) findViewById(R.id.gallery1);
		g.setAdapter( new ImageAdapter(this, Strin));

		g.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(@SuppressWarnings("rawtypes") AdapterView parent, View v, int position, long id) {
				String picture = (Strin.get(position));
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 4;

				Bitmap bitmap = BitmapFactory.decodeFile(picture, options);
				ImageView iv = (ImageView) findViewById(R.id.DamageView);
				iv.setImageBitmap(bitmap);
			}
		}); 
	}

	public class ImageAdapter extends BaseAdapter {
		int mGalleryItemBackground;
		private Context mContext;
		private ArrayList<String> v;
		public ImageAdapter(Context c, ArrayList<String> x) {
			mContext = c;
			v = (ArrayList<String>) x.clone();
			TypedArray a = obtainStyledAttributes(R.styleable.DamageGallery);
			mGalleryItemBackground = a.getResourceId(
					R.styleable.DamageGallery_android_galleryItemBackground, 0);
			a.recycle();
		}

		public int getCount() {
			try {int x = v.size();
			return x;}
			catch (Exception e) {return 0;}
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageView i = new ImageView(mContext);

			String picture = (Strin.get(position));
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;

			Bitmap bitmap = BitmapFactory.decodeFile(picture, options);
			i.setImageBitmap(bitmap);
			i.setLayoutParams(new Gallery.LayoutParams(300, 200));
			i.setScaleType(ImageView.ScaleType.FIT_XY);
			i.setBackgroundResource(mGalleryItemBackground);

			return i;
		}
	}
}