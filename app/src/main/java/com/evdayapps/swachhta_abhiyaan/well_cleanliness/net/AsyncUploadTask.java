package com.evdayapps.swachhta_abhiyaan.well_cleanliness.net;

import java.io.File;
import java.io.FileInputStream;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.AbstractHttpClient;
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
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.Forms;

/**
 * @brief Encapsulates all report uploading logic
 * @author Shannon
 */
public class AsyncUploadTask extends AsyncTask<Void, String, String> {
	private final static String logtag = AsyncUploadTask.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	
	private ProgressDialog mProgressDialog;
	private Context context;
	private Listener listener;
	public static interface Listener {
		public void handleSuccess(String response);
		public void handleFailure();
	}
	
	private String targetUrl, string_body;
	Forms form;
	private File file_body;
	
	/**
	 * @brief Constructor
	 * @param incontext Application context. Required to display progress dialog
	 * @param url url to upload to
	 * @param form form to upload to. Serves as entity name
	 * @param json data to upload as the value of the entity
	 * @param file Video file to upload. Null if no video to upload
	 * @param listener Listener to listen for finish/error
	 */
	public AsyncUploadTask(Context incontext, String url, Forms form, String json, File file, Listener listener) {
		context = incontext;
		this.listener = listener;
		this.targetUrl = url;
		this.form = form;
		this.string_body = json;
		this.file_body = file;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage(context.getString(R.string.uploading_data));
		mProgressDialog.setCancelable(false);
		mProgressDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				Toast.makeText(context, R.string.upload_cancelled, Toast.LENGTH_SHORT).show();
				AsyncUploadTask.this.cancel(true);
			}
		});
		mProgressDialog.show();
	}
	
	@Override
	protected String doInBackground(Void... params) {
		try 
		{
			HttpClient httpClient = new DefaultHttpClient();
			((AbstractHttpClient) httpClient).addRequestInterceptor(new RequestAcceptEncoding());
			((AbstractHttpClient) httpClient).addResponseInterceptor(new ResponseContentEncoding());
			
            HttpPost httpPost = new HttpPost(targetUrl);
            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
            builder.addTextBody(form.name(), string_body);
            if(file_body != null)
            	builder.addBinaryBody("Video", new FileInputStream(file_body),ContentType.create("video"), file_body.getName());
            httpPost.setEntity(builder.build());
            ResponseHandler<String> responseHandler = new BasicResponseHandler();
            String response =  httpClient.execute(httpPost, responseHandler);
            
            return response;
        }
		catch (Exception e) {
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
			Toast.makeText(context, R.string.error_uploading_file, Toast.LENGTH_LONG).show();
			listener.handleFailure();
		}
		else
		{	
			Toast.makeText(context, R.string.data_uploaded_successfully, Toast.LENGTH_SHORT).show();
			listener.handleSuccess(result);
		}
	}
}
