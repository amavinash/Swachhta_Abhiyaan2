package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.content.Context;
import android.database.Cursor;
import android.location.LocationManager;
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
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class Fragment_NewAreaDetails extends BaseFormFragment {
    private final static String logtag = Fragment_NewAreaDetails.class.getSimpleName();
    private final static boolean DEBUG = MainActivity.DEBUG;

    private static final String ERRORCODE_FROMSERVER = "-1";

    private Date date;

    @InjectView(R.id.cleanlinessdrivefrag_date_tv)
    TextView tv_date;
    @InjectView(R.id.cleanlinessdrivefrag_statedropdown)
    Spinner spinner_state;
    @InjectView(R.id.cleanlinessdrivefrag_districtdropdown)
    Spinner spinner_district;
    @InjectView(R.id.cleanlinessdrivefrag_talukadropdown)
    Spinner spinner_taluka;
    @InjectView(R.id.cleanlinessdrivefrag_villageedt)
    Spinner spinner_village;
    @InjectView(R.id.cleanlinessdrivefrag_name)
    EditText edt_name;
    @InjectView(R.id.cleanlinessdrivefrag_address)
    EditText edt_address;
    @InjectView(R.id.cleanlinessdrivefrag_pincode)
    EditText edt_pincode;
    @InjectView(R.id.cleanlinessdrivefrag_mobilenumber)
    EditText edt_mobilenumber;
    @InjectView(R.id.cleanlinessdrivefrag_comment)
    EditText edt_comment;
    //	@InjectView(R.id.cleanlinessdrivefrag_garbageweight_edt) EditText edt_expgarbageweight;
    @InjectView(R.id.imagebar_imagelist)
    LinearLayout image_list;

    private final static int MAX_IMAGES = 3;
    ArrayList<String> image_filepaths;

    private BaseRegionStructAdapter state_adapter, district_adapter, taluka_adapter, village_adapter;

    public Fragment_NewAreaDetails() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        date = Calendar.getInstance(Locale.getDefault()).getTime();
        image_filepaths = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_cleanlinessdrive, container, false);
        ButterKnife.inject(this, view);

        if (state_adapter == null) {
            state_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
            populateStateList();
        }
        if (district_adapter == null) {
            district_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
            populateDistrictsForStateId(-1);
        }
        if (taluka_adapter == null) {
            taluka_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
            populateTalukasForDistrictId(-1);
        }
        if (village_adapter == null) {
            village_adapter = new BaseRegionStructAdapter(getActivity(), R.layout.spinner_item);
            populateVillageForTalukaId(-1);
        }

        spinner_state.setAdapter(state_adapter);
        spinner_district.setAdapter(district_adapter);
        spinner_taluka.setAdapter(taluka_adapter);
        spinner_village.setAdapter(village_adapter);


        tv_date.setText(Utils.getInstance().getDateForDisplay(date));
        ((MainActivity) getActivity()).setTopbarElementVisibility(true);
        ((MainActivity) getActivity()).setTitle("Cleanliness Drive Details");
        return view;
    }

    /**
     * @param //state_id id of the state in the database
     * @brief Fill the District spinner given a state as parameter
     */
    private void populateVillageForTalukaId(long taluka_id) {
        village_adapter.clear();
        village_adapter.add(new VillageStruct(-1, "Select a Village", -1));
        String ab[] = {"Id", "Name", "ParentId"};

        if (taluka_id == -1)
            return;


        System.out.println("taluka id :" + taluka_id);
        Cursor cursor = ((MainActivity) getActivity()).database.query("Country", ab, "ParentId = ? AND Username='" + PasswordDialog.globalUserName + "'", new String[]{String.valueOf(taluka_id)}, null, null, "Name");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                village_adapter.add(new VillageStruct(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    private void populateDistrictsForStateId(long state_id) {
        district_adapter.clear();
        district_adapter.add(new DistrictStruct(-1, "Select a District", -1));
        String ab[] = {"Id", "Name", "ParentId"};

        if (state_id == -1)
            return;


        System.out.println("state id :" + state_id);
        Cursor cursor = ((MainActivity) getActivity()).database.query("Country", ab, "ParentId = ? AND Username='" + PasswordDialog.globalUserName + "'", new String[]{String.valueOf(state_id)}, null, null, "Name");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                district_adapter.add(new DistrictStruct(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    /**
     * @param district_id id of the district
     * @brief Fill the Taluka spinner with the talukas for district from database
     */
    private void populateTalukasForDistrictId(long district_id) {
        taluka_adapter.clear();
        taluka_adapter.add(new TalukaStruct(-1, "Select a Taluka", -1));
        String ab[] = {"Id", "Name", "ParentId"};

        if (district_id == -1)
            return;

        Cursor cursor = ((MainActivity) getActivity()).database.query("Country", ab, "ParentId = ? AND Username='" + PasswordDialog.globalUserName + "'", new String[]{String.valueOf(district_id)}, null, null, "Name");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                taluka_adapter.add(new TalukaStruct(cursor));
                cursor.moveToNext();
            }
            cursor.close();
        }
    }

    /**
     * @brief Retrieve a list of states from the database and populate the state spinner adapter
     */
    private void populateStateList() {
        state_adapter.clear();
        state_adapter.add(new StateStruct(-1, "Select a state"));
        String ab[] = {"Id", "Name", "ParentId"};
        Cursor cursor = ((MainActivity) getActivity()).database.query("Country", ab, "Type='STATE' AND Username='" + PasswordDialog.globalUserName + "'", null, null, null, "Name");
//		Cursor cursor = ((MainActivity)getActivity()).database.query("Country", null, null, null, null, null, "name");
        if (cursor != null) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                state_adapter.add(new StateStruct(cursor));
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

    @OnItemSelected({R.id.cleanlinessdrivefrag_statedropdown, R.id.cleanlinessdrivefrag_districtdropdown, R.id.cleanlinessdrivefrag_talukadropdown, R.id.cleanlinessdrivefrag_villageedt})
    void onItemSelected(Spinner spinner) {
        switch (spinner.getId()) {
            case R.id.cleanlinessdrivefrag_statedropdown:
                populateDistrictsForStateId(((StateStruct) spinner_state.getSelectedItem()).id);
                break;
            case R.id.cleanlinessdrivefrag_districtdropdown:
                populateTalukasForDistrictId(((DistrictStruct) spinner_district.getSelectedItem()).id);
                break;
            case R.id.cleanlinessdrivefrag_talukadropdown:
                populateVillageForTalukaId(((TalukaStruct) spinner_taluka.getSelectedItem()).id);
                break;
            case R.id.cleanlinessdrivefrag_villageedt:
                break;
        }
    }

    @OnClick(R.id.bottombarbtn_clickphoto)
    void onPhotoButtonClicked() {
        if (image_filepaths.size() == MAX_IMAGES) {
            Toast.makeText(getActivity(), "3 images already clicked.\nRemove one if you want to add another", Toast.LENGTH_SHORT).show();
            return;
        }

        if (image_filepaths.size() == 0) {
            // Reject photo click if no location information available
            if (((MainActivity) getActivity()).current_location == null) {
                new ConfirmationDialog(getActivity(), getString(R.string.please_wait), getString(R.string.warning_nolocationinfo), getString(R.string.ok), null, null).show();
                return;
            }
            // Warn if location information is inaccurate network location information
            if (((MainActivity) getActivity()).current_location.getProvider().equalsIgnoreCase(LocationManager.NETWORK_PROVIDER)) {
                new ConfirmationDialog(getActivity(), getString(R.string.title_inaccurate_location_info), getString(R.string.warning_inaccurate_location_info), getString(R.string.wait), getString(R.string.click_now),
                        new ConfirmationDialog.Listener() {
                            @Override
                            public void rightButtonPressed() {
                                ((MainActivity) getActivity()).startImageCaptureIntent(image_filepaths.size() == 0);
                            }

                            @Override
                            public void leftButtonPressed() {
                                return;
                            }
                        }).show();
                return;
            }
        }

        ((MainActivity) getActivity()).startImageCaptureIntent(image_filepaths.size() == 0);
    }

    @Override
    public void addImage(String filepath) {
        image_filepaths.add(filepath);
        final View image_view = LayoutInflater.from(getActivity()).inflate(R.layout.image_layout, image_list, false);
        ImageView delete_button = (ImageView) image_view.findViewById(R.id.imagelayout_image);
        delete_button.setTag(filepath);
        delete_button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = (String) v.getTag();
                image_filepaths.remove(tag);
                if (DEBUG)
                    Toast.makeText(getActivity(), "Deleting file '" + tag + "'", Toast.LENGTH_SHORT).show();
                File file = new File(tag);
                file.delete();
                ((LinearLayout) image_view.getParent()).removeView(image_view);
            }
        });
        DisplayImageOptions opts = new DisplayImageOptions.Builder().considerExifParams(true).build();
        ImageLoader.getInstance().displayImage("file://" + filepath, delete_button, opts);
        image_list.addView(image_view);
    }

    @OnClick(R.id.bottombarbtn_submit)
    void onSubmitButtonPressed() {
        if (validateFields()) {
            UploadStructure_NewAreaDetails struct = makeUploadStruct();
            if (struct != null)
                uploadStructureToServer(struct);
        }
    }

    /**
     * @return
     * @brief Validate all the fields in the form, checking if any length is 0, or selection is 0
     */
    private boolean validateFields() {
        // Validate State
        if (!Utils.isViewValid(getActivity(), spinner_state, R.string.warning_pickstate))
            return false;
        // Validate District
        if (!Utils.isViewValid(getActivity(), spinner_district, R.string.warning_pickdistrict))
            return false;
        // Validate Taluka
        if (!Utils.isViewValid(getActivity(), spinner_taluka, R.string.warning_picktaluka))
            return false;
        // Validate Village/Ward
        if (!Utils.isViewValid(getActivity(), spinner_village, R.string.warning_entervillagename))
            return false;
        // Validate Village/Ward
        if (!Utils.isViewValid(getActivity(), edt_name, R.string.warning_invalidname))
            return false;

        if (!Utils.isViewValid(getActivity(), edt_address, R.string.warning_invalidaddress))
            return false;

        if (!Utils.isViewValid(getActivity(), edt_pincode, R.string.warning_invalidpincode))
            return false;

        if (!Utils.isViewValid(getActivity(), edt_mobilenumber, R.string.warning_invalidmobilenumber))
            return false;
//		// Validate Expected Garbage Weight
//		if(!Utils.isViewValid(getActivity(), edt_expgarbageweight, R.string.warning_invalid_garbageweight))
//			return false;
        // Validate Images
        if (image_filepaths.size() == 0) {
            Toast.makeText(getActivity(), R.string.warning_addphoto, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * @return
     * @brief Build an upload structure and fill it with information taken from the form fields
     */
    private UploadStructure_NewAreaDetails makeUploadStruct() {
        UploadStructure_NewAreaDetails newareadetstruct = new UploadStructure_NewAreaDetails();
        // Validate Location Information
        if (((MainActivity) getActivity()).image_location == null) {
            Toast.makeText(getActivity(), R.string.msg_nolocation_enablegps, Toast.LENGTH_LONG).show();
            return null;
        } else {
            newareadetstruct.latitude = ((MainActivity) getActivity()).image_location.getLatitude();
            newareadetstruct.longitude = ((MainActivity) getActivity()).image_location.getLongitude();
        }
        newareadetstruct.drive_date = date;
        newareadetstruct.taluka_id = (int) ((TalukaStruct) spinner_taluka.getSelectedItem()).id;
        newareadetstruct.villageward = ((VillageStruct) spinner_village.getSelectedItem()).name;
        newareadetstruct.username = edt_name.getText().toString();
        newareadetstruct.address = edt_address.getText().toString();
        newareadetstruct.pincode = edt_pincode.getText().toString();
        newareadetstruct.mobilenumber = edt_mobilenumber.getText().toString();
        newareadetstruct.comment = edt_comment.getText().toString();
//		newareadetstruct.garbage_weight = Double.parseDouble(edt_expgarbageweight.getText().toString());
        newareadetstruct.images = image_filepaths;

        TelephonyManager tMgr = (TelephonyManager) ((MainActivity) getActivity()).getSystemService(Context.TELEPHONY_SERVICE);
        String tempmobileNo = tMgr.getLine1Number();

        /*if (tempmobileNo != null || !(tempmobileNo.equals("")) || tempmobileNo.matches("^([0]|(?:[0][0]|\\+)(91))([7-9]{1})([0-9]{9})$")) {
            newareadetstruct.numberType = "mobile";
            newareadetstruct.mobileNo = tempmobileNo;

        } else {
            newareadetstruct.numberType = "imei";

            String imeiSIM1 = tMgr.getDeviceId();
            newareadetstruct.mobileNo = imeiSIM1;

        }*/
        newareadetstruct.numberType = "imei";
        newareadetstruct.mobileNo = "2222422";

        return newareadetstruct;
    }

    /**
     * @param struct
     * @brief Uploads a data structure to the server. Converts it to json first
     */
    private void uploadStructureToServer(UploadStructure_NewAreaDetails struct) {
        new AsyncUploadTask(getActivity(), MainActivity.BASE_URL, Forms.AddLocationDetails, struct.toJSON().toString(), null,
                new AsyncUploadTask.Listener() {
                    @Override
                    public void handleSuccess(String response) {
                        String cleanResponse = response.substring(0, response.indexOf("\n"));
                        if (cleanResponse.startsWith(ERRORCODE_FROMSERVER)) {
                            cleanResponse = cleanResponse.replace("-1", "");
                            Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error),
                                    cleanResponse.length() > 2 ? cleanResponse : getActivity().getString(R.string.msg_server_error));
                        } else {
                            ((MainActivity) getActivity()).setShowAllOptionsToTrue();
                            new ConfirmationDialog(getActivity(), getActivity().getString(R.string.upload_successful), cleanResponse, null, getString(R.string.ok), new Listener() {
                                @Override
                                public void rightButtonPressed() {
                                    getFragmentManager().popBackStackImmediate(0, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                                }

                                @Override
                                public void leftButtonPressed() {
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void handleFailure() {
                        Utils.showErrorDialog(getActivity(), getActivity().getString(R.string.error), getActivity().getString(R.string.msg_error_occured));
                    }
                }).execute();
    }


    private static class BaseRegionStructAdapter extends ArrayAdapter<BaseRegionStruct> implements SpinnerAdapter {

        public BaseRegionStructAdapter(Context context, int resource) {
            super(context, resource);
            // TODO Auto-generated constructor stub
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = super.getView(position, convertView, parent);
            ((TextView) view.findViewById(android.R.id.text1)).setText(getItem(position).name);
            return view;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null)
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item_dropdown, parent, false);
            ((TextView) convertView.findViewById(android.R.id.text1)).setText(getItem(position).name);
            return convertView;
        }
    }

    /**
     * Data struct class for easy JSON creation for New Area Details submission
     */
    private class UploadStructure_NewAreaDetails {
        int taluka_id = -1;
        String location, numberType = "", mobileNo = "", villageward = "", username = "", address = "", pincode = "", mobilenumber = "", comment = "";
        double latitude = -1, longitude = -1, garbage_weight = -1;
        Date drive_date = null;
        ArrayList<String> images = null;

        public JSONObject toJSON() {
            try {
                JSONObject json = new JSONObject();
                json.put(UploadStructuresTags.RegionId.name(), taluka_id);
                json.put(UploadStructuresTags.Name.name(), username);
                json.put(UploadStructuresTags.Address.name(), address);
                json.put(UploadStructuresTags.PinCode.name(), pincode);
                json.put(UploadStructuresTags.MobileNo.name(), mobilenumber);
                json.put(UploadStructuresTags.Latitude.name(), String.valueOf(latitude));
                json.put(UploadStructuresTags.Longitude.name(), String.valueOf(longitude));
                json.put(UploadStructuresTags.Comment.name(), comment);
                json.put(UploadStructuresTags.NumberValue.name(), mobileNo);
                json.put(UploadStructuresTags.NumberType.name(), numberType);

                //json.put(UploadStructuresTags.VillageWardNo.name(), villageward);
                //json.put(UploadStructuresTags.Location.name(), location);
                //json.put(UploadStructuresTags.ExpectedGarbageWt.name(), garbage_weight);
                //json.put(UploadStructuresTags.ApkVersion.name(), MainActivity.apkVersion);
                //json.put(UploadStructuresTags.GeoTagImage.name(),Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(0))), Base64.DEFAULT));
                json.put(UploadStructuresTags.Image1.name(),
                Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(0))), Base64.NO_WRAP));
                if (images.size() > 1)
                    json.put(UploadStructuresTags.Image2.name(),
                            Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(1))), Base64.NO_WRAP));
                else
                    json.put(UploadStructuresTags.Image2.name(), "");
                if (images.size() > 2)
                    json.put(UploadStructuresTags.Image3.name(),
                            Base64.encodeToString(Utils.getCompressedByteArrayForImage(Utils.getBitmap(images.get(2))), Base64.NO_WRAP));
                else
                    json.put(UploadStructuresTags.Image3.name(), "");
                //deleting images after base 64 conversion

                for (int i = 0; i < images.size(); i++) {
                    File tempimg = new File(images.get(i));
                    tempimg.delete();
                }
                //
                Log.e("JSON PAYLOAD", "" + json.toString());
                return json;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }
}
