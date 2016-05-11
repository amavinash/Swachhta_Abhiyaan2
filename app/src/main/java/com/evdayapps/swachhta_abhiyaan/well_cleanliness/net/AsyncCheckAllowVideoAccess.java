package com.evdayapps.swachhta_abhiyaan.well_cleanliness.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;

public class AsyncCheckAllowVideoAccess extends AsyncTask<String, Void, String>{
	private final static String logtag = AsyncCheckAllowVideoAccess.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	
	private ProgressDialog mProgressDialog;
	private Context context;
	private Listener listener;
	public static interface Listener {
		public void handleSuccess(String response);
		public void handleFailure();
	}
	
	public AsyncCheckAllowVideoAccess(Context context, Listener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(context.getString(R.string.checking_video_access));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(context, "Checking for video upload access cancelled", Toast.LENGTH_SHORT).show();
				AsyncCheckAllowVideoAccess.this.cancel(true);
			}
		});
		mProgressDialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try 
		{
            HttpPost httpPost = new HttpPost(params[0]);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("CheckVideoAccess", params[1]);
            httpPost.setEntity(builder.build());
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response = new DefaultHttpClient().execute(httpPost, responseHandler);
            
            return response;
        } 
		catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } 
		catch (ClientProtocolException e) {
            e.printStackTrace();
        } 
		catch (IOException e) {
            e.printStackTrace();
        }
        return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if(result == null)
		{
			Toast.makeText(context, "Error while checking if video access allowed", Toast.LENGTH_LONG).show();
			listener.handleFailure();
		}
		else
		{
			listener.handleSuccess(result);
		}
	}
}
