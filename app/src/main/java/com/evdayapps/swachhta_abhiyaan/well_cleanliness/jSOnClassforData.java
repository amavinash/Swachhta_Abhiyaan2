package com.evdayapps.swachhta_abhiyaan.well_cleanliness;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.UploadStructuresTags;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

public class jSOnClassforData {
	public JSONObject GetUserDetails(UserRegistration userRegistration) throws Exception {
		 JSONObject jObject = null;
		 String str = null ;
		 JSONObject localJSONObject = new JSONObject();
	      try {
			localJSONObject.put("UserName", userRegistration.getUsername());
			 localJSONObject.put("Password", userRegistration.getPassword());
			 localJSONObject.put(UploadStructuresTags.ApkVersion.name(),MainActivity.apkVersion);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	     
		HttpPost localHttpPost = new HttpPost(MainActivity.BASE_URL);
	      MultipartEntityBuilder localMultipartEntityBuilder = MultipartEntityBuilder.create();
	      localMultipartEntityBuilder.addTextBody("ValidateUser",localJSONObject.toString());
	      localHttpPost.setEntity(localMultipartEntityBuilder.build());
	      BasicResponseHandler localBasicResponseHandler = new BasicResponseHandler();
	      try {
			 str = (String)new DefaultHttpClient().execute(localHttpPost, localBasicResponseHandler);
			// Receiving side
//			 String[] separated = str.split();
//			 String tempStr = separated[0];
//			 byte ptextDemo[] = tempStr.getBytes("UTF-8");
//			 byte[] data = Base64.decode(ptextDemo, Base64.DEFAULT);
//			 String gzipDecoded = decompress(data);
//			 System.out.println(gzipDecoded);
//		   str = gzipDecoded;
	      
	      } catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	      System.out.println(str);
//	      System.out.println(str.toString());
	       jObject = new JSONObject(str);
//	       System.out.println(jObject.toString());
	      return jObject;
	}
	public static String decompress(byte[] compressed) throws IOException {
	    final int BUFFER_SIZE = 32;
	    ByteArrayInputStream is = new ByteArrayInputStream(compressed);
	    GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
	    StringBuilder string = new StringBuilder();
	    byte[] data = new byte[BUFFER_SIZE];
	    int bytesRead;
	    while ((bytesRead = gis.read(data)) != -1) {
	        string.append(new String(data, 0, bytesRead));
	    }
	    gis.close();
	    is.close();
	    
	    return string.toString();
	}

//	 public static byte[] compress(String string) throws IOException {
//		    ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
//		    GZIPOutputStream gos = new GZIPOutputStream(os);
//		    gos.write(string.getBytes());
//		    gos.close();
//		    byte[] compressed = os.toByteArray();
//		    os.close();
//		    return compressed;
//		}
	}

	/**
	 * Set up the {@link android.app.ActionBar}, if the API is available.
	 */







