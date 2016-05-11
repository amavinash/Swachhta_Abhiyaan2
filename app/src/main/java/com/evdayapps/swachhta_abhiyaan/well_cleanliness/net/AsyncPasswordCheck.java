package com.evdayapps.swachhta_abhiyaan.well_cleanliness.net;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;


public class AsyncPasswordCheck extends AsyncTask<String, String, String> {
	private final static String logtag = AsyncPasswordCheck.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	
	private ProgressDialog mProgressDialog;
	private Context context;
	private Listener listener;
	public static interface Listener {
		public void handleSuccess(String response);
		public void handleFailure();
	}
	
	public AsyncPasswordCheck(Context incontext, Listener listener) {
		context = incontext;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(context.getString(R.string.checking_password));
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(context, "Password Check cancelled", Toast.LENGTH_SHORT).show();
				AsyncPasswordCheck.this.cancel(true);
			}
		});
		mProgressDialog.show();
		super.onPreExecute();
	}
	
	@Override
	protected String doInBackground(String... params) {
		try 
		{
            JSONObject object = new JSONObject();
            object.put("UserName", params[1]);
            object.put("Password", params[2]);
            if(DEBUG)
            	Log.i(logtag,"doInBackground: json: "+object.toString());
            
            HttpPost httpPost = new HttpPost(params[0]);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addTextBody("LoginUser", object.toString());
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
		catch(Exception e) {
			e.printStackTrace();
		}
        return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if(DEBUG)
			Log.d(logtag,"onPostExecute: result: "+result);
		super.onPostExecute(result);
		mProgressDialog.dismiss();
		if(result == null)
		{
			Toast.makeText(context, "Error while checking password. Please try again", Toast.LENGTH_LONG).show();
			listener.handleFailure();
		}
		else
			listener.handleSuccess(result);
	}
}
