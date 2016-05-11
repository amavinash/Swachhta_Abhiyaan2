package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.Utils;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.BaseRegionStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.DistrictStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.StateStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.TalukaStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.VillageStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog.Listener;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.PasswordDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.Forms;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.UploadStructuresTags;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncUploadTask;

public class Fragment_AddVillageDetails extends BaseFormFragment{

	private final static boolean DEBUG = MainActivity.DEBUG;
	private static final String ERRORCODE_FROMSERVER = "-1";
	
	private Date date;
	
	@InjectView(R.id.addvillagedetails_date_tv) TextView avtv_date;
	@InjectView(R.id.addvillagefrag_statedropdown) Spinner avspinner_state;
	@InjectView(R.id.addvillagefrag_districtdropdown) Spinner avspinner_district;
	@InjectView(R.id.addvillagefrag_talukadropdown) Spinner avspinner_taluka;
	@InjectView(R.id.addvillagefrag_villageedt) Spinner avspinner_village;
//	@InjectView(R.id.addvillagefrag_locationedt) EditText avedt_location;
	@InjectView(R.id.addvillagemalevolunteers_edt) EditText edt_malevolunteers;
	@InjectView(R.id.addvillageareacleaned_edt) EditText edt_areacleaned;
	@InjectView(R.id.addvillagecleanedroadlength_edt) EditText edt_cleanedroad;
	@InjectView(R.id.addvillagecleanedseashore_edt) EditText edt_cleanedshashore;
	@InjectView(R.id.addvillagedetails_garbageweightdry_edt) EditText edt_drygarbagewt;
	@InjectView(R.id.addvillagedetails_garbageweightwet_edt) EditText edt_wetgarbagewt;
	@InjectView(R.id.bottombarbtn_clickphoto) Button clickPhotoBtn;
	private BaseRegionStructAdapter avstate_adapter, avdistrict_adapter, avtaluka_adapter, avvillage_adapter;

	public Fragment_AddVillageDetails(){}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		date = Calendar.getInstance(Locale.getDefault()).getTime();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_addvillagedetails, container, false);
		ButterKnife.inject(this, view);
		clickPhotoBtn.setVisibility(View.GONE);
		if(avstate_adapter == null)
		{
			avstate_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
			populateStateList();
		}
		if(avdistrict_adapter == null)
		{
			avdistrict_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
			populateDistrictsForStateId(-1);
		}
		if(avtaluka_adapter == null)
		{
			avtaluka_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
			populateTalukasForDistrictId(-1);
		}
		if(avvillage_adapter == null)
		{
			avvillage_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
			populateVillageForTalukaId(-1);
		}
		
		avspinner_state.setAdapter(avstate_adapter);
		avspinner_district.setAdapter(avdistrict_adapter);
		avspinner_taluka.setAdapter(avtaluka_adapter);
		avspinner_village.setAdapter(avvillage_adapter);
		
		avtv_date.setText(Utils.getInstance().getDateForDisplay(date));
		((MainActivity)getActivity()).setTopbarElementVisibility(true);
		((MainActivity)getActivity()).setTitle("Add Village Details");
		return view;
	}
	
	/**
	 * @brief Retrieve a list of states from the database and populate the state spinner adapter
	 */
	private void populateStateList() {
		avstate_adapter.clear();
		String ab[]={"Id","Name","ParentId"};
		avstate_adapter.add(new StateStruct(-1, "Select a state"));
		Cursor cursor = ((MainActivity)getActivity()).database.query("Country", ab, "Type='STATE' AND Username='"+PasswordDialog.globalUserName+"'",null, null, null, "Name");
		if(cursor != null)
		{
			cursor.moveToFirst();
			while(!cursor.isAfterLast())
			{
				avstate_adapter.add(new StateStruct(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
	}
	
	private void populateDistrictsForStateId(long state_id) {
		avdistrict_adapter.clear();
		avdistrict_adapter.add(new DistrictStruct(-1, "Select a District", -1));
		 String ab[]={"Id","Name","ParentId"};

		if(state_id == -1)
			return;
		
		
		System.out.println("state id :"+state_id);
		Cursor cursor = ((MainActivity)getActivity()).database.query("Country", ab, "ParentId = ? AND Username='"+PasswordDialog.globalUserName+"'", new String[]{String.valueOf(state_id)}, null, null, "Name");
		if(cursor != null)
		{
			cursor.moveToFirst();
			while(!cursor.isAfterLast())
			{
				avdistrict_adapter.add(new DistrictStruct(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
	}
	
	/**
	 * @brief Fill the Taluka spinner with the talukas for district from database
	 * @param district_id id of the district 
	 */
	private void populateTalukasForDistrictId(long district_id) {
		avtaluka_adapter.clear();
		avtaluka_adapter.add(new TalukaStruct(-1, "Select a Taluka", -1));
		 String ab[]={"Id","Name","ParentId"};

		if(district_id == -1)
			return;
		
		Cursor cursor = ((MainActivity)getActivity()).database.query("Country", ab, "ParentId = ? AND Username='"+PasswordDialog.globalUserName+"'", new String[]{String.valueOf(district_id)}, null, null, "Name");
		if(cursor != null)
		{
			cursor.moveToFirst();
			while(!cursor.isAfterLast())
			{
				avtaluka_adapter.add(new TalukaStruct(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
	}
	/**
	 * @brief Fill the District spinner given a state as parameter
	 * @param state_id id of the state in the database
	 */
	private void populateVillageForTalukaId(long taluka_id) {
		avvillage_adapter.clear();
		avvillage_adapter.add(new VillageStruct(-1, "Select a Village", -1));
		 String ab[]={"Id","Name","ParentId"};

		if(taluka_id == -1)
			return;
		
		
		System.out.println("taluka id :"+taluka_id);
		Cursor cursor = ((MainActivity)getActivity()).database.query("Country", ab, "ParentId = ? AND Username='"+PasswordDialog.globalUserName+"'", new String[]{String.valueOf(taluka_id)}, null, null, "Name");
		if(cursor != null)
		{
			cursor.moveToFirst();
			while(!cursor.isAfterLast())
			{
				avvillage_adapter.add(new VillageStruct(cursor));
				cursor.moveToNext();
			}
			cursor.close();
		}
	}
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		ButterKnife.reset(this);
	}
	
	@OnItemSelected({R.id.addvillagefrag_statedropdown, R.id.addvillagefrag_districtdropdown, R.id.addvillagefrag_talukadropdown,R.id.addvillagefrag_villageedt})
	void onItemSelected(Spinner spinner) {
		switch(spinner.getId())
		{
			case R.id.addvillagefrag_statedropdown:
				populateDistrictsForStateId(((StateStruct)avspinner_state.getSelectedItem()).id);
				break;
			case R.id.addvillagefrag_districtdropdown:
				populateTalukasForDistrictId(((DistrictStruct)avspinner_district.getSelectedItem()).id);
				break;
			case R.id.addvillagefrag_talukadropdown:
				populateVillageForTalukaId(((TalukaStruct)avspinner_taluka.getSelectedItem()).id);
				break;
			case R.id.addvillagefrag_villageedt:
				break;
		}
	}
//	@OnClick(R.id.bottombarbtn_clickphoto)
//	void onPhotoButtonClicked() {
//		
//	}
	
	@OnClick(R.id.bottombarbtn_submit)
	void onSubmitButtonPressed() {
		if(validateFields())
		{
			UploadStructure_AddVillageDetails struct = makeUploadStruct();
			if(struct != null)
				uploadStructureToServer(struct);
		}
	}
	/**
	 * @brief Validate all the fields in the form, checking if any length is 0, or selection is 0
	 * @return
	 */
	private boolean validateFields() {
		// Validate State
		if(!Utils.isViewValid(getActivity(), avspinner_state, R.string.warning_pickstate))
			return false;
		// Validate District
		if(!Utils.isViewValid(getActivity(), avspinner_district, R.string.warning_pickdistrict))
			return false;
		// Validate Taluka
		if(!Utils.isViewValid(getActivity(), avspinner_taluka, R.string.warning_picktaluka))
			return false;
		// Validate Village/Ward
		if(!Utils.isViewValid(getActivity(), avspinner_village, R.string.warning_entervillagename))
			return false;
		// Validate Village/Ward
//		if(!Utils.isViewValid(getActivity(), avedt_location, R.string.warning_invalidlocation))
//			return false;
		// Validate male volunteers
		if(!Utils.isViewValid(getActivity(), edt_malevolunteers, R.string.warning_enter_num_volunteers))
			return false;
		if(!Utils.isViewValid(getActivity(), edt_areacleaned, R.string.msg_enter_roadlength_area_village))
			return false;
		if(!Utils.isViewValid(getActivity(), edt_cleanedroad, R.string.msg_enter_roadlength))
			return false;
		if(!Utils.isViewValid(getActivity(), edt_cleanedshashore, R.string.msg_enter_seashore_length))
			return false;
		if(!Utils.isViewValid(getActivity(), edt_drygarbagewt, R.string.warning_nodrygarbageamount))
			return false;
		if(!Utils.isViewValid(getActivity(), edt_wetgarbagewt, R.string.warning_nowetgarbageamount))
			return false;
			
		return true;
	}
	
	/**
	 * @brief Build an upload structure and fill it with information taken from the form fields
	 * @return
	 */
	@SuppressWarnings("unused")
	private UploadStructure_AddVillageDetails makeUploadStruct() {
		UploadStructure_AddVillageDetails addvillagestruct = new UploadStructure_AddVillageDetails();
		
//		addvillagestruct.drive_date = date;
		addvillagestruct.regionId = (int) ((TalukaStruct)avspinner_taluka.getSelectedItem()).id;
		addvillagestruct.villageId = (int) ((VillageStruct)avspinner_village.getSelectedItem()).id;
		addvillagestruct.villageName = (String) ((VillageStruct)avspinner_village.getSelectedItem()).name;
//		addvillagestruct.locationName = avedt_location.getText().toString();
		addvillagestruct.malevolunteer = Integer.parseInt(edt_malevolunteers.getText().toString());
		addvillagestruct.areaCleaned = Double.parseDouble(edt_areacleaned.getText().toString());
		addvillagestruct.cleanedRoadLength = Double.parseDouble(edt_cleanedroad.getText().toString());
		addvillagestruct.cleanedSeaShore = Double.parseDouble(edt_cleanedshashore.getText().toString());
		addvillagestruct.dryGarbageWeight = Double.parseDouble(edt_drygarbagewt.getText().toString());
		addvillagestruct.wetGarbageWeight = Double.parseDouble(edt_wetgarbagewt.getText().toString());
		
		TelephonyManager tMgr =(TelephonyManager)((MainActivity)getActivity()).getSystemService(Context.TELEPHONY_SERVICE);
		String tempmobileNo = "00000";//tMgr.getLine1Number();
		
		if(tempmobileNo != null || !(tempmobileNo.equals("")) || tempmobileNo.matches("^([0]|(?:[0][0]|\\+)(91))([7-9]{1})([0-9]{9})$"))
		{
			addvillagestruct.numberType = "mobile";
			addvillagestruct.mobileNo = tempmobileNo;
			
		}
		else{
			addvillagestruct.numberType = "imei";
			
			String imeiSIM1 = tMgr.getDeviceId();
			addvillagestruct.mobileNo = imeiSIM1;
			
		}
		
		return addvillagestruct;
	}
	
	private static class BaseRegionStructAdapter extends ArrayAdapter<BaseRegionStruct> implements SpinnerAdapter {

		public BaseRegionStructAdapter(Context context, int resource) {
			super(context, resource);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			((TextView)view.findViewById(android.R.id.text1)).setText(getItem(position).name);
			return view;
		}
		
		@Override
		public View getDropDownView(int position, View convertView, ViewGroup parent) {
			if(convertView == null)
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_dropdown, parent, false);
			((TextView)convertView.findViewById(android.R.id.text1)).setText(getItem(position).name);
			return convertView;
		}
	}

	private class UploadStructure_AddVillageDetails {
		int regionId=-1,villageId=-1,malevolunteer=-1;
		String  villageName="",locationName="",mobileNo="",numberType="";
		double areaCleaned=-1,cleanedRoadLength=-1,cleanedSeaShore=-1,dryGarbageWeight=-1,wetGarbageWeight=-1;
//		Date drive_date = null;
		
		
		public JSONObject toJSON() {
			try
			{
				JSONObject json = new JSONObject();
				json.put(UploadStructuresTags.RegionId.name(), regionId);
				json.put(UploadStructuresTags.VillageId.name(), villageId);
				json.put(UploadStructuresTags.Village.name(), villageName);
				json.put(UploadStructuresTags.LocationName.name(), locationName);
				json.put(UploadStructuresTags.MaleVolunteer.name(), malevolunteer);
				json.put(UploadStructuresTags.AreaCleaned.name(), areaCleaned);
				json.put(UploadStructuresTags.CleanedRoadLength.name(), cleanedRoadLength);
				json.put(UploadStructuresTags.CleanedSeaShore.name(), cleanedSeaShore);
				json.put(UploadStructuresTags.DryGarbageWeight.name(), dryGarbageWeight);
				json.put(UploadStructuresTags.WetGarbageWeight.name(), wetGarbageWeight);
				json.put(UploadStructuresTags.NumberType.name(), numberType);
				json.put(UploadStructuresTags.NumberValue.name(), mobileNo);
				json.put(UploadStructuresTags.ApkVersion.name(),MainActivity.apkVersion);
				
				return json;
			}
			catch (JSONException e) 
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	/**
	 * @brief Uploads a data structure to the server. Converts it to json first
	 * @param struct
	 */
	private void uploadStructureToServer(UploadStructure_AddVillageDetails struct) {
		new AsyncUploadTask(getActivity(), MainActivity.BASE_URL, Forms.CleanlinessVillageDetails, struct.toJSON().toString(), null, 
				new AsyncUploadTask.Listener() {
				@Override
				public void handleSuccess(String response) {
					String cleanResponse = response.substring(0, response.indexOf("\n"));
					if(cleanResponse.startsWith(ERRORCODE_FROMSERVER))
					{
						cleanResponse = cleanResponse.replace("-1", "");
						Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error), 
								cleanResponse.length() > 2 ? cleanResponse : getActivity().getString(R.string.msg_server_error));
					}
					else
					{
						((MainActivity)getActivity()).setShowAllOptionsToTrue();
						new ConfirmationDialog(getActivity(), getActivity().getString(R.string.upload_successful), cleanResponse, null, getString(R.string.ok), new Listener() {
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
					Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.msg_error_occured));
				}
			}).execute();
	}

}
