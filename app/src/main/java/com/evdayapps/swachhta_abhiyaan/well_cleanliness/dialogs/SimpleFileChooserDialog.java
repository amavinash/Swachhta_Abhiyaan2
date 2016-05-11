package com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.ThumbnailUtils;
import android.os.Bundle;
import android.provider.MediaStore.Images.Thumbnails;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;

/**
 * @brief Specialised file browser that displays only folders and mp4/3gp videos for Swachhta Abhiyaan purposes
 * @author Shannon
 *
 */
public class SimpleFileChooserDialog extends Dialog implements OnItemClickListener{
	private final static String logtag = SimpleFileChooserDialog.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	
	public static abstract class Listener{
		public void onFileSelected(SimpleFileChooserDialog dialog, File selectedFile){};
		public boolean passesFilter(File file) {
			return true;
		};
	}
	
	private File currentDirectory;
	private TextView tv_currentDirectory;
	private ListView listview_directorylisting;
	private FileListAdapter filelistadapter;
	private Listener listener;
	boolean showParent = true, parentDisplayed = false;
	private String title;
	private File initDirectory;
	
	public SimpleFileChooserDialog(Context context, String title, File initialDirectory){
		super(context);
		currentDirectory = initialDirectory;
		initDirectory = initialDirectory;
		this.title = title;
	}
	
	public void addListener(Listener listener){
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().requestFeature(Window.FEATURE_NO_TITLE);
		getWindow().getDecorView().setBackgroundColor(Color.TRANSPARENT);
		setContentView(R.layout.filechooser);
		((TextView)findViewById(R.id.filechooser_title)).setText(title);
		tv_currentDirectory = (TextView)findViewById(R.id.filechooser_currentdirectory);
		listview_directorylisting = (ListView)findViewById(R.id.filechooser_directorylist);
		filelistadapter = new FileListAdapter(getContext());
		listview_directorylisting.setAdapter(filelistadapter);
		listview_directorylisting.setOnItemClickListener(this);
		populateFileList();
	}
	
	/**
	 * @brief Get a list of files for the current directory. Iterate through the files and add a file to the adapter if it passes the filter
	 * @return
	 */
	private boolean populateFileList(){
		try
		{
			if(!currentDirectory.exists() || !currentDirectory.isDirectory())
				return false;
			tv_currentDirectory.setText(currentDirectory.getAbsolutePath());
			filelistadapter.clear();
			// Add parent file if valid and requested
			parentDisplayed = false;
			if(showParent && !currentDirectory.equals(initDirectory))
			{
				File parentFile = currentDirectory.getParentFile();
				if(parentFile != null)
				{
					filelistadapter.add(parentFile);
					parentDisplayed = true;
				}
			}
			// Add file list
			File[] fileList = currentDirectory.listFiles();
			// Order the files alphabetically and separating folders from files.
			Arrays.sort(fileList, new Comparator<File>() {
				public int compare(File file1, File file2) 
				{
					if(file1 != null && file2 != null) 
					{
						if(file1.isDirectory() && (!file2.isDirectory())) return -1;
						if(file2.isDirectory() && (!file1.isDirectory())) return 1;
						return file1.getName().compareToIgnoreCase(file2.getName());
					}
					return 0;
				}
			});
			// Iterate through files; add if a file passes Filter
			for(File file : fileList)
			{
				if(listener.passesFilter(file))
					filelistadapter.add(file);
			}
			listview_directorylisting.scrollTo(0, 0);
			return true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(filelistadapter.getItem(arg2).isDirectory())
		{
			currentDirectory = filelistadapter.getItem(arg2);
			populateFileList();
		}
		else
		{
			listener.onFileSelected(this, filelistadapter.getItem(arg2));
		}
	}
	

	/**
	 * @brief List Adapter for directory listing
	 * @details Generates the views for each directory item; sets the icon respectively
	 * @author Shannon
	 */
	private class FileListAdapter extends ArrayAdapter<File>{
		private Bitmap folder_icon = null;
		private Bitmap file_icon = null;
		
		/**
		 * @brief View Holder class for List Items
		 * @author Shannon
		 */
		private class FileListItemViewHolder{
			ImageView icon;
			TextView label;
		}
		
		public FileListAdapter(Context context) {
			super(context, R.layout.filelistadapter_item);
			folder_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_folder);
			file_icon = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_video);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = getLayoutInflater().inflate(R.layout.filelistadapter_item, parent, false);
			FileListItemViewHolder holder = (FileListItemViewHolder)convertView.getTag();
			if(holder == null)
			{
				holder = new FileListItemViewHolder();
				holder.icon = (ImageView)convertView.findViewById(R.id.filelistitem_icon);
				holder.label = (TextView)convertView.findViewById(R.id.filelistitem_name);
				convertView.setTag(holder);
			}
			
			File file = getItem(position);
			if(file.isDirectory())
				holder.icon.setImageBitmap(folder_icon);
			else if(isVideo(file.getName()))
				holder.icon.setImageBitmap(getVideoThumbnail(file));
			
			if(showParent && parentDisplayed && position==0)
				holder.label.setText("build/generated/source/aidl");
			else
				holder.label.setText(getItem(position).getName());
			return convertView;
		}
	}

	private boolean isVideo(String filename) {
		if(DEBUG)
			Log.i(logtag,"isVideo: filename: "+filename);
		if(filename.toLowerCase().endsWith(".mp4") || filename.toLowerCase().endsWith(".3gp"))
			return true;
		return false;
	}
	
	private Bitmap getVideoThumbnail(File file) {
		Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(file.getAbsolutePath(), Thumbnails.MINI_KIND);
		if(bitmap == null)
			bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.ic_video);
		return bitmap;
	}
	
	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.MATCH_PARENT;
	    getWindow().setAttributes(lp);
	}


	
}
