package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.Utils;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog.Listener;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.Forms;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.UploadStructuresTags;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncUploadTask;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncVerifyCode;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @brief Cleaned Area Details page
 * @author Shannon
 */
public class Fragment_CleanedAreaDetails extends BaseFormFragment{
	private final static String logtag = Fragment_CleanedAreaDetails.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	private final static int MAX_IMAGES = 3;
	
	private Date date;
	ArrayList<String> image_filepaths;
	
	@InjectView(R.id.locationcode_edt) EditText edt_code;
	@InjectView(R.id.cleanedareadetailsfrag_area_edt) EditText edt_area;
	@InjectView(R.id.cleanedareadetailsfrag_garbageweightdry_edt) EditText edt_garbagecollected_dry;
	@InjectView(R.id.cleanedareadetailsfrag_garbageweightwet_edt) EditText edt_garbagecollected_wet;
	@InjectView(R.id.cleanedareadetailsfrag_roadlengthcleaned_edt) EditText edt_roadlength;
	@InjectView(R.id.cleanedareadetailsfrag_seashorelengthcleaned_edt) EditText edt_seashorelength;
	@InjectView(R.id.imagebar_imagelist) LinearLayout image_list;
	@InjectView(R.id.cleanedareadetailsfrag_verificationdetails) TextView tv_verificationdetails;
	
	/**
	 * Constructor
	 */
	public Fragment_CleanedAreaDetails() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		date = Calendar.getInstance(Locale.getDefault()).getTime();
		image_filepaths = new ArrayList<String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_cleanedareadetails, container, false);
		ButterKnife.inject(this, view);
		((MainActivity)getActivity()).setTopbarElementVisibility(true);
		((MainActivity)getActivity()).setTitle("Cleaned Area Details");
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}
	
	@OnClick(R.id.bottombarbtn_clickphoto)
	void onPhotoButtonClicked() {
		if(image_filepaths.size() == MAX_IMAGES)
		{
			Toast.makeText(getActivity(), "3 images already clicked.\nRemove one if you want to add another", Toast.LENGTH_SHORT).show();
			return;
		}
		((MainActivity)getActivity()).startImageCaptureIntent(false);
	}
	
	@OnClick(R.id.cleanedareadetailsfrag_btnverify)
	void onVerifyButtonClicked() {
		if(edt_code.getText().length() == 0)
		{
			Toast.makeText(getActivity(), R.string.msg_enter_location_code, Toast.LENGTH_SHORT).show();
			edt_code.requestFocus();
			return;
		}
		new AsyncVerifyCode(getActivity(), new AsyncVerifyCode.Listener() {
			@Override
			public void handleSuccess(String response) {
				String cleanResponse = response.substring(0, response.indexOf("\n"));
				if(cleanResponse.startsWith("-1"))
					cleanResponse.replace("-1", "");
				tv_verificationdetails.setText(cleanResponse);
			}
			
			@Override
			public void handleFailure() {
				
			}
		}).execute(MainActivity.BASE_URL, edt_code.getText().toString());
	}
	
	@Override
	public void addImage(String filepath) {
		if(DEBUG)
			Log.i(logtag,"addImage: Entered; filepath: "+filepath);
		image_filepaths.add(filepath);
		final View image_view = LayoutInflater.from(getActivity()).inflate(R.layout.image_layout, image_list, false);
		ImageView delete_button = (ImageView)image_view.findViewById(R.id.imagelayout_image);
		delete_button.setTag(filepath);
		delete_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String tag = (String) v.getTag();
				image_filepaths.remove(tag);
				if(DEBUG)
				{
					Log.i(logtag,"addImage: deletefile: "+tag);
					Toast.makeText(getActivity(),"Deleting file '"+tag+"'", Toast.LENGTH_SHORT).show();
				}
				File file = new File(tag);
				file.delete();
				((LinearLayout)image_view.getParent()).removeView(image_view);
			}
		});
		DisplayImageOptions opts = new DisplayImageOptions.Builder().considerExifParams(true).build();
		ImageLoader.getInstance().displayImage("file://"+filepath, delete_button, opts);
		image_list.addView(image_view);
	}
	
	@OnClick(R.id.bottombarbtn_submit)
	void onSubmitButtonPressed() {
		if(validateFields())
		{
			UploadStructure_CleanedAreaDetails struct = makeUploadStruct();
			if(struct != null)
				uploadDataToServer(struct);
		}
	}
	
	/**
	 * @brief Perform validation on the fields. Check if any of the fields are empty or contain invalid data
	 * @return
	 */
	private boolean validateFields() {
		// Validate Code
		if(!Utils.isViewValid(getActivity(), edt_code, R.string.msg_enter_location_code))
			return false;
		// Validate Area Cleaned
		if(!Utils.isViewValid(getActivity(), edt_area, R.string.msg_enter_roadlength_area, false) && 
				!Utils.isViewValid(getActivity(), edt_roadlength, R.string.msg_enter_roadlength_area, false))
		{
			Toast.makeText(getActivity(), R.string.msg_enter_roadlength_area, Toast.LENGTH_SHORT).show();
			edt_area.requestFocus();
			return false;
		}
		// Validate Dry Garbage Collected
		if(!Utils.isViewValid(getActivity(), edt_garbagecollected_dry, R.string.warning_nodrygarbageamount))
			return false;
		// Validate Wet Garbage Collected
		if(!Utils.isViewValid(getActivity(), edt_garbagecollected_wet, R.string.warning_nowetgarbageamount))
			return false;
		// Validate Images
		if(image_filepaths.size() == 0)
		{
			Toast.makeText(getActivity(), R.string.warning_addphoto, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	/**
	 * @brief Make the upload structure, populating it with data from the fields in the form
	 * @return
	 */
	private UploadStructure_CleanedAreaDetails makeUploadStruct() {
		UploadStructure_CleanedAreaDetails uploadstruct = new UploadStructure_CleanedAreaDetails();
		uploadstruct.code = Integer.parseInt(edt_code.getText().toString());
		uploadstruct.garbage_dry = Float.parseFloat(edt_garbagecollected_dry.getText().toString());
		uploadstruct.garbage_wet = Float.parseFloat(edt_garbagecollected_wet.getText().toString());
		if(edt_area.getText().length() > 0)
			uploadstruct.area_cleaned = Float.parseFloat(edt_area.getText().toString());
		if(edt_roadlength.getText().length() > 0)
			uploadstruct.cleaned_road_length = Float.parseFloat(edt_roadlength.getText().toString());
		if(edt_seashorelength.getText().length() > 0)
			uploadstruct.cleaned_seashore_length = Float.parseFloat(edt_seashorelength.getText().toString());
		
		TelephonyManager tMgr =(TelephonyManager)((MainActivity)getActivity()).getSystemService(Context.TELEPHONY_SERVICE);
		String tempmobileNo = tMgr.getLine1Number();
		
		if(tempmobileNo != null || !(tempmobileNo.equals("")) || tempmobileNo.matches("^([0]|(?:[0][0]|\\+)(91))([7-9]{1})([0-9]{9})$"))
		{
			uploadstruct.numberType = "mobile";
			uploadstruct.mobileNo = tempmobileNo;
			
		}
		else{
			uploadstruct.numberType = "imei";
			
			String imeiSIM1 = tMgr.getDeviceId();
			uploadstruct.mobileNo = imeiSIM1;
			
		}
		
		uploadstruct.images = image_filepaths;
		return uploadstruct;
	}

	/**
	 * @brief Upload the data to the server
	 * @param struct Structure to upload. Need to convert it to json first
	 */
	private void uploadDataToServer(UploadStructure_CleanedAreaDetails struct) {
		new AsyncUploadTask(getActivity(), MainActivity.BASE_URL, Forms.AddCleanedAreaDetails, struct.toJSON().toString(), null, 
				new AsyncUploadTask.Listener() {
			@Override
			public void handleSuccess(String response) {
				if(DEBUG)
					Log.i(logtag,"handleSuccess: response: "+response);
				String cleanResponse = response.substring(0, response.indexOf("\n"));
				if(cleanResponse.startsWith("-1"))
				{
					cleanResponse = cleanResponse.replace("-1", "");
					Utils.showErrorDialog(getActivity(), "Error", 
							cleanResponse.length() > 2 ? cleanResponse : "The server encountered an error.\nPlease try again in a minute");
				}
				else
				{
					new ConfirmationDialog(getActivity(), getActivity().getString(R.string.upload_successful), cleanResponse, null, "OK", new Listener() {
						@Override
						public void rightButtonPressed() {
							getFragmentManager().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
						}
						
						@Override
						public void leftButtonPressed() {}
					}).show();
				}
			}
			
			@Override
			public void handleFailure() {
				Utils.showErrorDialog(getActivity(), "Error", "An error occured while uploading data.\nPlease try again");
			}
		}).execute();
	}

	/**
	 * Upload Helper Structure; Structure to help in easy building of the upload data
	 * @author Shannon
	 */
	private class UploadStructure_CleanedAreaDetails {
		public int code = -1;
		String numberType="",mobileNo="";
		float area_cleaned = 0, cleaned_road_length = 0,cleaned_seashore_length=0,
				garbage_dry = 0, garbage_wet = 0;
		ArrayList<String> images;
		
		public JSONObject toJSON() {
			try 
			{
				JSONObject json = new JSONObject();
				json.put(UploadStructuresTags.Code.name(), code);
				json.put(UploadStructuresTags.AreaCleaned.name(), area_cleaned);
				json.put(UploadStructuresTags.CleanedRoadLength.name(), cleaned_road_length);
				json.put(UploadStructuresTags.CleanedSeaShore.name(), cleaned_seashore_length);
				
				json.put(UploadStructuresTags.DryGarbage.name(), garbage_dry);
				json.put(UploadStructuresTags.WetGarbage.name(), garbage_wet);
				
				json.put(UploadStructuresTags.NumberType.name(), numberType);
				json.put(UploadStructuresTags.NumberValue.name(), mobileNo);
				json.put(UploadStructuresTags.ApkVersion.name(),MainActivity.apkVersion);
				json.put(UploadStructuresTags.Image1.name(), 
						Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(0))), Base64.DEFAULT));
				if(images.size() > 1)
					json.put(UploadStructuresTags.Image2.name(), 
							Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(1))), Base64.DEFAULT));
				else
					json.put(UploadStructuresTags.Image2.name(),"");
				if(images.size() > 2)
					json.put(UploadStructuresTags.Image3.name(), 
							Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(2))), Base64.DEFAULT));
				else
					json.put(UploadStructuresTags.Image3.name(),"");
				
				for(int i=0; i<images.size();i++){
					File tempimg = new File(images.get(i));
					tempimg.delete();
				}


				return json;
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
	}
}
