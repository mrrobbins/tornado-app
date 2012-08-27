package com.AlabamaDamageTracker;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageSwitcher;
import android.widget.ImageView;

public class ImagesActivity extends Activity implements ImageSwitcher.ViewFactory {
	
	private ImageAdapter ia;
	private Gallery gallery;
	private ImageSwitcher switcher;
	private String id;
	private final String TAG = "ImagesActivity";
	private DisplayMetrics dm;
	private int lock;
	private Thread switcherThread = null;
	
	private class OrientationState {
		
		public final Object adapterState;
		
		public OrientationState(Object adapterState) {
			this.adapterState = adapterState;
		}
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		this.setContentView(R.layout.image_gallery);
		switcher = (ImageSwitcher) this.findViewById(R.id.gallery_switcher);
		gallery = (Gallery) this.findViewById(R.id.gallery_filmstrip);
		switcher.setFactory(this);
        dm = this.getResources().getDisplayMetrics();
		
		OrientationState os = (OrientationState) getLastNonConfigurationInstance();
		if (os != null) {
			ia = new ImageAdapter(os.adapterState);
		}
		else {
			initImageAdapter();
		}
		gallery.setAdapter(ia);
		gallery.setOnItemSelectedListener(ia);
	}
	
	public int scale(float dp) {
		return Math.round(dm.density * dp);
	}
	
	private void initImageAdapter() {
		ia = new ImageAdapter();
	}
	
	private class ImageAdapter extends BaseAdapter implements AdapterView.OnItemSelectedListener {
		private Bitmap placeholder = null;
		private List<String> paths;
		private List<Bitmap> thumbnails;
		private final Long locationId;
		
		public ImageAdapter() {
			
			CurrentDamage info = ((CurrentDamage) getApplicationContext());
			locationId = info.getLocationID();
			DatabaseHelper myDbHelper = new DatabaseHelper(getBaseContext());
			myDbHelper.openDataBase();
			Cursor c = myDbHelper.getAllDamagePic(locationId);
			
			paths = new ArrayList<String>();
			
			try {
				do {
					paths.add(c.getString(0));
				} while (c.moveToNext());
			} catch (Exception e) { }
			
			try {
				paths.add(c.getString(0));
			} catch (Exception e) { }
			
			c.close();
			
			AssetManager am = getAssets();
			
			thumbnails = new ArrayList<Bitmap>(paths.size());
			
			try {
				placeholder = BitmapFactory.decodeStream(am.open("placeholder"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			for (int i = paths.size(); i > 0; --i) {
				thumbnails.add(placeholder);
			}
			
			new Thread(new DiskThumbnailBuilder(paths)).start();
	
		}
		
		public ImageAdapter(Object state) {
			OrientationState os = (OrientationState) state; 
			this.placeholder = os.placeholder;
			this.thumbnails = os.thumbnails;
			this.paths = os.paths;
			this.locationId = os.locationId;
		}
		
		public Object getOrientationState() {
			return new OrientationState(placeholder, paths, thumbnails, locationId);
		}
		
		private class OrientationState {
			public final Bitmap placeholder;
			public final List<String> paths;
			public final List<Bitmap> thumbnails;
			public final long locationId;
			
			public OrientationState(
				Bitmap placeholder,
				List<String> paths,
				List<Bitmap> thumbnails,
				long locationId
			) {
				this.placeholder = placeholder;
				this.paths = paths;
				this.thumbnails = thumbnails;
				this.locationId = locationId;
			}
		}
		
		
		public int getCount() {
			return thumbnails.size();
		}

		public Object getItem(int position) {
			return thumbnails.get(position);
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			Log.d(TAG, "In getView");
			if (convertView == null) {
				convertView = new ImageView(ImagesActivity.this);
			}
			ImageView i = (ImageView) convertView;
			Bitmap bm = thumbnails.get(position);
			i.setImageBitmap(bm == null ? placeholder : bm);
            i.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.WRAP_CONTENT, scale(60)));
            i.setAdjustViewBounds(true);
			return i;
		}
		
		private class DiskThumbnailBuilder implements Runnable {
			private List<String> paths;
			
			public DiskThumbnailBuilder(List<String> paths) {
				this.paths = paths;
			}
			public void run() {
				for(int i = 0; i < paths.size(); i++) {
					try {
						Bitmap orig = null;
						Bitmap scaled = null;
						
						BitmapFactory.Options bmo = new BitmapFactory.Options();
						
						bmo.inJustDecodeBounds = true;
						bmo.inSampleSize = 1;
						
						InputStream is = new FileInputStream(paths.get(i));
						try {
							BitmapFactory.decodeStream(is, null, bmo);
						} finally {
							is.close();
						}
						
						int width = Math.round(bmo.outWidth * (float) scale(60) / bmo.outHeight);
						int height = scale(60);
						while (bmo.outWidth / bmo.inSampleSize > width && bmo.outHeight / bmo.inSampleSize > height) bmo.inSampleSize *= 2;
						bmo.inJustDecodeBounds = false;
						
						is = new FileInputStream(paths.get(i));
						try {
							orig = BitmapFactory.decodeStream(is, null, bmo);
						} finally {
							is.close();
						}
						scaled = Bitmap.createScaledBitmap(orig, width, height, true);
						orig.recycle();
						runOnUiThread(new ThumbnailPoster(i, scaled));
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				
			}
			
		}
		
		private class ThumbnailPoster implements Runnable {
			
			private int position;
			private Bitmap bm;
			
			public ThumbnailPoster(int position, Bitmap bm) {
				this.position = position;
				this.bm = bm;
			}
			
			public void run() {
				thumbnails.set(position, bm);
				ImageAdapter.this.notifyDataSetChanged();
			}
			
		}

		public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
			String path = paths.get(position);
			Bitmap thumbnail = thumbnails.get(position);
			lock++;
			if (ImagesActivity.this.switcherThread != null) {
				ImagesActivity.this.switcherThread.interrupt();
			}
			ImagesActivity.this.switcherThread =
				new Thread(new DiskSwitcherSetter(path, lock, thumbnail));
			ImagesActivity.this.switcherThread.start();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			
		}
	}

	public View makeView() {
		ImageView i = new ImageView(this);
		i.setLayoutParams(new ImageSwitcher.LayoutParams(
				ImageSwitcher.LayoutParams.FILL_PARENT, ImageSwitcher.LayoutParams.FILL_PARENT));
		i.setAdjustViewBounds(true);
		return i;
	}
	
	private String readJson() {
		return "";
	}
	
	private class DiskSwitcherSetter implements Runnable {
		private String path;
		private int lock;
		Bitmap thumbnail;
		
		public DiskSwitcherSetter(String path, int lock, Bitmap thumbnail) {
			this.path = path;
			this.lock = lock;
			this.thumbnail = thumbnail;
		}
		
		public void run() {
			Bitmap fullSize = null;
			runOnUiThread(new SwitcherPoster(thumbnail, lock, false));
			try {
				InputStream is = new FileInputStream(path);
				fullSize = BitmapFactory.decodeStream(is);
				is.close(); 
				
				int scaledHeight = 0;
				int scaledWidth = 0;
				for (int i = 0; i < 5 && scaledHeight == 0 && scaledWidth == 0; i++, Thread.sleep(15)) {
					int viewHeight = switcher.getHeight();
					int viewWidth = switcher.getWidth();
					
					int bmHeight = fullSize.getHeight();
					int bmWidth = fullSize.getWidth();
				
					boolean port = (viewHeight / (float) bmHeight) * bmWidth < viewWidth;
					scaledHeight = (port ? viewHeight :
						Math.round(bmHeight * viewWidth / (float) bmWidth));
					scaledWidth = (port ? Math.round(bmWidth * viewHeight / (float) bmHeight) :
						viewWidth);
				}
				
				Bitmap resized = null;
				if (scaledHeight != 0 && scaledWidth != 0)
					resized = Bitmap.createScaledBitmap(fullSize, scaledWidth, scaledHeight, true);
				
				fullSize.recycle();
				
				if (resized != null )runOnUiThread(new SwitcherPoster(resized, lock, true));
				
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
				if (fullSize != null) fullSize.recycle();
			}
		}
		
	}
	
	private class SwitcherPoster implements Runnable {
		private Bitmap bm;
		private int lock;
		private boolean canRecycle;

		public SwitcherPoster(Bitmap bm, int lock, boolean canRecyle) {
			this.bm = bm;
			this.lock = lock;
			this.canRecycle = canRecyle;
		}
		public void run() {
			if (ImagesActivity.this.lock == lock) {
				switcher.setImageDrawable(new BitmapDrawable(bm));
			}
			else if (canRecycle){
				bm.recycle();
			}
		}
		
	}
	
	@Override
	public Object onRetainNonConfigurationInstance() {
		Object adapterState = ia.getOrientationState();
		return new OrientationState(adapterState);
	}	
}
