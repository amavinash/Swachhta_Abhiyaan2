package com.evdayapps.swachhta_abhiyaan.well_cleanliness;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog.Listener;

public class Utils {
	
	private final static String FORMAT_DATE_DISPLAY = "E, dd MMM, yyyy", 
									FORMAT_DATE_FILENAME = "yyyyMMdd_kkmmss", 
									FORMAT_DATE_SERVER = "dd/MM/yyyy";
	private final static int IMAGE_MAX_HEIGHT = 600, IMAGE_MAX_WIDTH = 800;
	
	private static Utils instance;
	
	public Utils() {}
	
	public static void initInstance() {
		instance = new Utils();
	}
	
	public static Utils getInstance() {
		return instance;
	}
	
	public String getDateForDisplay(Date date) {
		return new SimpleDateFormat(FORMAT_DATE_DISPLAY, Locale.getDefault()).format(date);
	}
	
	public String getDateForFilename(Date date) {
		return new SimpleDateFormat(FORMAT_DATE_FILENAME, Locale.getDefault()).format(date);
	}
	
	public String getDateForServer(Date date) {
		return new SimpleDateFormat(FORMAT_DATE_SERVER, Locale.getDefault()).format(date);
	}

	public static int calculateInSampleSize(int width, int height) {
	    int inSampleSize = 1;
	
	    if(height > IMAGE_MAX_HEIGHT || width > IMAGE_MAX_WIDTH)
	    {
	        final int halfHeight = height / 2;
	        final int halfWidth = width / 2;
	
	        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
	        // height and width larger than the requested height and width.
	        while ((halfHeight / inSampleSize) > IMAGE_MAX_HEIGHT
	                && (halfWidth / inSampleSize) > IMAGE_MAX_WIDTH)
	        {
	            inSampleSize *= 2;
	        }
	    }
	
	    return inSampleSize;
    }
	
	public static Bitmap getBitmap(String filepath) {
		try
		{
			ExifInterface exif = new ExifInterface(filepath);
			int imgheight = exif.getAttributeInt(ExifInterface.TAG_IMAGE_LENGTH, 0);
			int imgwidth = exif.getAttributeInt(ExifInterface.TAG_IMAGE_WIDTH, 0);
			int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 0);
			
			// Get Image (size and rotate as per need)
			BitmapFactory.Options bitmapoptions = new BitmapFactory.Options();
			bitmapoptions.inSampleSize = calculateInSampleSize(imgwidth, imgheight);
	        bitmapoptions.inJustDecodeBounds = false;
	        
	        Bitmap bitmap = BitmapFactory.decodeFile(filepath, bitmapoptions);
	        if(orientation != ExifInterface.ORIENTATION_NORMAL)
	        {
	        	Matrix matrix = new Matrix();
				switch(orientation)
				{
					case ExifInterface.ORIENTATION_ROTATE_90:
						matrix.postRotate(90);
						break;
					case ExifInterface.ORIENTATION_ROTATE_180:
						matrix.postRotate(180);
						break;
					case ExifInterface.ORIENTATION_ROTATE_270:
						matrix.postRotate(270);
						break;
					default:
						break;
				}
				bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			}
	        return bitmap;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
	}
	
	public static byte[] getCompressedByteArrayForImage(Bitmap bitmap) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, 75, bos);
        byte[] data = bos.toByteArray();
        return data;
	}
	
	public static void showErrorDialog(Activity activity, String title, String message) {
		new ConfirmationDialog(activity, title, message, null, "OK", new Listener() {
			@Override
			public void rightButtonPressed() {}
			
			@Override
			public void leftButtonPressed() {}
		}).show();
	}
	
	public static boolean isViewValid(Context context, View view, int error_msg) {
		return isViewValid(context, view, error_msg, true);
	}
	
	public static boolean isViewValid(Context context, View view, int error_msg, boolean showToastMessage) {
		if(view instanceof EditText)
		{
			if(((EditText)view).getText().length() == 0)
			{
				if(showToastMessage)
					Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show();
				view.requestFocus();
				return false;
			}
		}
		else if(view instanceof Spinner)
		{
			if(((Spinner)view).getSelectedItemPosition() == 0)
			{
				if(showToastMessage)
					Toast.makeText(context, error_msg, Toast.LENGTH_SHORT).show();
				return false;
			}
		}
		return true;
	}
}
