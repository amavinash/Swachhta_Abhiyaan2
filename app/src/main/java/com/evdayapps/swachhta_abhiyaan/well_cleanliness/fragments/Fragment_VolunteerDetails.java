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
 * @brief Volunteer Details page
 * @author Shannon
 */
public class Fragment_VolunteerDetails extends BaseFormFragment{
	private final static String logtag = Fragment_VolunteerDetails.class.getSimpleName();
	private final static boolean DEBUG = MainActivity.DEBUG;
	
	private Date date;
	
	@InjectView(R.id.volunteerdetailsfrag_code_edt) EditText edt_code;
	@InjectView(R.id.volunteerdetailsfrag_nummalevolunteers_edt) EditText edt_numvolunteers;
	@InjectView(R.id.imagebar_imagelist) LinearLayout image_list;
	@InjectView(R.id.volunteerdetailsfrag_locationcode_verificationinfo) TextView tv_verificationdetails;
	
	private final static int MAX_IMAGES = 3;
	ArrayList<String> image_filepaths;
	
	public Fragment_VolunteerDetails() {}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		date = Calendar.getInstance(Locale.getDefault()).getTime();
		image_filepaths = new ArrayList<String>();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_volunteerdetails, container, false);
		ButterKnife.inject(this, view);
		((MainActivity)getActivity()).setTopbarElementVisibility(true);
		((MainActivity)getActivity()).setTitle("Volunteer Details");
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
			Toast.makeText(getActivity(), R.string.warning_three_images_clicked, Toast.LENGTH_SHORT).show();
			return;
		}
		((MainActivity)getActivity()).startImageCaptureIntent(false);
	}
	
	@OnClick(R.id.volunteerdetailsfrag_btnverify)
	void onVerifyButtonClicked() {
		if(edt_code.getText().length() == 0)
		{
			Toast.makeText(getActivity(), R.string.warning_enter_location_code, Toast.LENGTH_SHORT).show();
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
			public void handleFailure() {}
		}).execute(MainActivity.BASE_URL, edt_code.getText().toString());
	}
	
	@Override
	public void addImage(String filepath) {
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
					Toast.makeText(getActivity(),"Deleting file '"+tag+"'", Toast.LENGTH_SHORT).show();
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
			UploadStructure_VolunteerDetails struct = makeUploadStructure();
			if(struct != null)
				uploadStructureToServer(struct);
		}
	}

	/**
	 * @brief Validate the fields in the form
	 * @return false if any field's data is invalid, else true
	 */
	private boolean validateFields() {
		// Validate Code
		if(!Utils.isViewValid(getActivity(), edt_code, R.string.warning_enter_location_code))
			return false;
		// Validate Village/Ward
		if(!Utils.isViewValid(getActivity(), edt_numvolunteers, R.string.warning_enter_num_volunteers))
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
	 * @brief Builds the upload structure
	 * @return upload structure
	 */
	private UploadStructure_VolunteerDetails makeUploadStructure() {
		UploadStructure_VolunteerDetails uploadstruct = new UploadStructure_VolunteerDetails();
		uploadstruct.code = Integer.parseInt(edt_code.getText().toString());
		uploadstruct.num_volunteers = Integer.parseInt(edt_numvolunteers.getText().toString());
		uploadstruct.images = image_filepaths;
		
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
		return uploadstruct;
	}

	private void uploadStructureToServer(UploadStructure_VolunteerDetails struct) {
		new AsyncUploadTask(getActivity(), MainActivity.BASE_URL, Forms.AddVolunteerDetails, struct.toJSON().toString(), null, 
				new AsyncUploadTask.Listener() {
			@Override
			public void handleSuccess(String response) {
				String cleanResponse = response.substring(0, response.indexOf("\n"));
				if(cleanResponse.startsWith("-1"))
				{
					cleanResponse = cleanResponse.replace("-1", "");
					Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error), 
							cleanResponse.length() > 2 ? cleanResponse : getActivity().getString(R.string.warning_servererror_tryagain));
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
				Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error), "An error occured while uploading to server.\nPlease try again");
			}
		}).execute();
	}
	
	/**
	 * Helper structure to assist in easy building of upload json data
	 * @author Shannon
	 *
	 */
	private class UploadStructure_VolunteerDetails {
		public int code = -1, num_volunteers = -1;
		public ArrayList<String> images;
		String numberType="",mobileNo="";
		public JSONObject toJSON() {
			try 
			{
				JSONObject json = new JSONObject();
				json.put(UploadStructuresTags.Code.name(), code);
				json.put(UploadStructuresTags.NoOfVolunteers.name(), num_volunteers);
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
				
				json.put(UploadStructuresTags.NumberType.name(), numberType);
				json.put(UploadStructuresTags.NumberValue.name(), mobileNo);
				
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
