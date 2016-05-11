package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.DataSetObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.Utils;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog.Listener;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.SimpleFileChooserDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.Forms;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.enums.UploadStructuresTags;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncUploadTask;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncVerifyCode;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemSelected;

public class Fragment_UploadVideo extends BaseFormFragment {
    private final static String logtag = Fragment_UploadVideo.class.getSimpleName();
    private final static boolean DEBUG = true;

    private final static long KB = 1024, MB = 1024 * KB;
    String filePathToCompress = "";
    private FormSpinnerAdapter adapter = null;
    private File video_file = null;

    @InjectView(R.id.uploadvideofrag_serverurl)
    EditText edt_url;
    @InjectView(R.id.fragvideo_spinner_form)
    Spinner form_spinner;
    @InjectView(R.id.locationcode_edt)
    EditText edt_locationcode;
    @InjectView(R.id.comments_edt)
    EditText edt_comment;
    @InjectView(R.id.bottombarbtn_clickphoto)
    Button btn_capturevideo;
    @InjectView(R.id.videodetails_info)
    TextView videodetails_infotv;
    @InjectView(R.id.videodetails_btnview)
    TextView btn_viewvideo;
    @InjectView(R.id.videodetails_btndelete)
    TextView btn_deletevideo;
    @InjectView(R.id.uploadvideofrag_verificationdetails)
    TextView tv_verificationdetails;

    public Fragment_UploadVideo() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new FormSpinnerAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capturevideo, container, false);
        ButterKnife.inject(this, view);
        btn_capturevideo.setText(R.string.capture_video);
        form_spinner.setAdapter(adapter);
        setVideo(video_file);
        return view;
    }

    @OnItemSelected(R.id.fragvideo_spinner_form)
    void onItemSelected(int position) {
        // TODO
    }

    @OnClick(R.id.bottombarbtn_clickphoto)
    void onCaptureVideoBtnClicked() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        ((MainActivity) getActivity()).startVideoCaptureIntent();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder((MainActivity) getActivity());
        builder.setMessage("The Screen will be blank for some time after capturing video, please do not press any button.\n\n हिडिओ संपल्यावर काही काळ स्क्रीन कोरी असेल, कृपया कोणतेही बटण दाबू नका.").setPositiveButton("OK", dialogClickListener)
                .setNegativeButton("", dialogClickListener).show();
//		Utils.showErrorDialog(getActivity(), "Warning", "The Screen will be blank for some time after capturing video, please do not press any button.\n\n हिडिओ संपल्यावर काही काळ स्क्रीन कोरी असेल, कृपया कोणतेही बटण दाबू नका.");

//		PopupMenu addvideomenu = new PopupMenu(getActivity(), btn_capturevideo);
//		addvideomenu.inflate(R.menu.menu_addvideo);
//		addvideomenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
//			@Override
//			public boolean onMenuItemClick(MenuItem arg0) {
//				
//				switch(arg0.getItemId())
//				{
////					case R.id.menu_item_video_fromfolder:
////						onFromFileButtonPressed();
////						break;
//					case R.id.menu_item_video_fromcamera:
//						((MainActivity)getActivity()).startVideoCaptureIntent();
//						break;
//				}
//				return true;
//			}
//		});
//		addvideomenu.show();
    }

    private File getExternalStorageDirectory() {
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                File externdir = Environment.getExternalStorageDirectory();
                if (externdir.exists())
                    return externdir;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public void onFromFileButtonPressed() {
        File externdir = getExternalStorageDirectory();
        if (externdir == null) {
            Toast.makeText(getActivity(), "Unable to access sdcard", Toast.LENGTH_LONG).show();
            return;
        }

        SimpleFileChooserDialog dialog = new SimpleFileChooserDialog(getActivity(), "Choose Video", externdir);
        dialog.addListener(new SimpleFileChooserDialog.Listener() {
            @Override
            public boolean passesFilter(File file) {
                if (!file.getName().startsWith("")
                        && (file.isDirectory() || file.getName().endsWith(".mp4") || file.getName().endsWith(".3gp")))
                    return true;
                return false;
            }

            @Override
            public void onFileSelected(SimpleFileChooserDialog dialog, File selectedFile) {
                setVideo(selectedFile);
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @OnClick(R.id.bottombarbtn_submit)
    void onSubmitButtonClicked() {
        if (validateForm()) {
            UploadStruct_VideoUpload struct = makeUploadStruct();
            if (struct != null)
                uploadStructureToServer(struct);
        }
    }

    /**
     * @return true if all requirements are valid else false
     * @brief Validate the form
     */
    private boolean validateForm() {
        // Validate formid
        if (!Utils.isViewValid(getActivity(), form_spinner, R.string.warning_pickform))
            return false;
        // Validate location code
        if (!Utils.isViewValid(getActivity(), edt_locationcode, R.string.warning_enter_location_code))
            return false;
//		// Validate Comment
//		if(!Utils.isViewValid(getActivity(), edt_comment, R.string.warning_nocomment))
//			return false;
        // Validate video
        if (video_file == null) {
            Toast.makeText(getActivity(), R.string.warning_novideo, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    /**
     * @param struct
     * @brief Upload the datastruct to the server
     */
    private void uploadStructureToServer(UploadStruct_VideoUpload struct) {
        String uploadurl = "";

        if (Fragment_MainPage.urlForVideoUpload == null || Fragment_MainPage.urlForVideoUpload.equals("")) {
            uploadurl = edt_url.getText().length() > 0 ? edt_url.getText().toString() : MainActivity.BASE_URL;
        } else {
            uploadurl = Fragment_MainPage.urlForVideoUpload;
        }
        new AsyncUploadTask(getActivity(), uploadurl, Forms.UploadVideo, struct.toJSON().toString(), video_file,
                new AsyncUploadTask.Listener() {
                    @Override
                    public void handleSuccess(String response) {
                        if (DEBUG)
                            Log.i(logtag, "handleSuccess: response: " + response);
                        String cleanResponse = response.substring(0, response.indexOf("\n"));
                        if (cleanResponse.startsWith("-1")) {
                            cleanResponse = cleanResponse.replace("-1", "");
                            Utils.showErrorDialog(getActivity(), "Error",
                                    cleanResponse.length() > 2 ? cleanResponse : "The server encountered an error.\nPlease try again in a minute");
                        } else {
                            video_file.delete();//deleting uploaded file
                            new ConfirmationDialog(getActivity(), getActivity().getString(R.string.upload_successful), cleanResponse, null, "OK", new Listener() {
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
                        Utils.showErrorDialog(getActivity(), "Error", "An error occured while uploading to server.\nPlease try again");
                    }
                }).execute();
    }

    /**
     * @return
     * @brief Build the upload structure
     */
    private UploadStruct_VideoUpload makeUploadStruct() {
        UploadStruct_VideoUpload struct = new UploadStruct_VideoUpload();
        struct.code = Integer.parseInt(edt_locationcode.getText().toString());
        struct.comments = edt_comment.getText().toString();
        struct.form_id = form_spinner.getSelectedItemPosition();
        TelephonyManager tMgr = (TelephonyManager) ((MainActivity) getActivity()).getSystemService(Context.TELEPHONY_SERVICE);
        String tempmobileNo = tMgr.getLine1Number();

        if (tempmobileNo != null || !(tempmobileNo.equals("")) || tempmobileNo.matches("^([0]|(?:[0][0]|\\+)(91))([7-9]{1})([0-9]{9})$")) {
            struct.numberType = "mobile";
            struct.mobileNo = tempmobileNo;

        } else {
            struct.numberType = "imei";

            String imeiSIM1 = tMgr.getDeviceId();
            struct.mobileNo = imeiSIM1;

        }

        return struct;
    }

    @Override
    public void addVideo(String filename) {
        if (filename != null && filename.length() > 0)
            setVideo(new File(filename));
    }

    @OnClick(R.id.videodetails_btndelete)
    void onDeleteVideoButtonClicked() {
        video_file.delete();
        setVideo(null);
    }

    @OnClick(R.id.videodetails_btnview)
    void onViewVideoButtonClicked() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(video_file), "video/*");
        startActivity(intent);
    }

    @OnClick(R.id.uploadvideofrag_btnverify)
    void onVerifyButtonClicked() {
        if (edt_locationcode.getText().length() == 0) {
            Toast.makeText(getActivity(), "Please enter a location code", Toast.LENGTH_SHORT).show();
            edt_locationcode.requestFocus();
            return;
        }
        new AsyncVerifyCode(getActivity(), new AsyncVerifyCode.Listener() {
            @Override
            public void handleSuccess(String response) {
                if (DEBUG)
                    Log.i(logtag, "handleSuccess: response: " + response);
                String cleanResponse = response.substring(0, response.indexOf("\n"));
                if (cleanResponse.startsWith("-1"))
                    cleanResponse.replace("-1", "");
                tv_verificationdetails.setText(cleanResponse);
            }

            @Override
            public void handleFailure() {
            }
        }).execute(MainActivity.BASE_URL, edt_locationcode.getText().toString());
    }

    private void setVideo(File file) {

        if (video_file != null && btn_deletevideo.getVisibility() == View.VISIBLE) {
            try {
                video_file.delete();
                video_file = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (file != null) {


            filePathToCompress = file.getAbsolutePath();
            //LoadJNI vk = new LoadJNI();
            try {
                String workFolder = getActivity().getFilesDir().getAbsolutePath();
//          		String[] complexCommand = {"ffmpeg","-i", "/sdcard/videokit/in.mp4"};
                long times = System.currentTimeMillis();

                String tempCompressedVideoUrl = "sdcard/" + times + ".mp4";
                String[] complexCommand = {"ffmpeg", "-y", "-i", filePathToCompress, "-strict", "experimental", "-s", "720p", "-r", "80", "-vcodec", "mpeg4", "-b", "450k", "-ab", "48000", "-ac", "2", "-ar", "22050", tempCompressedVideoUrl};
                // change -b value for compression 
                //vk.run(complexCommand , workFolder , getActivity());
                Log.i("test", "ffmpeg4android finished successfully");
                File fileCompressed = new File(tempCompressedVideoUrl);
                video_file = fileCompressed;

//          		deleting original file
                File originalVideo = new File(filePathToCompress);
//          		originalVideo.delete();


            } catch (Throwable e) {
                Log.e("test", "vk run exception.", e);

            }
        }
//		video_file = file;
        videodetails_infotv.setText(video_file == null ? getString(R.string.videodetails_novideomsg) :
                TextUtils.expandTemplate(getText(R.string.videodetails_template), getFileSizeString(video_file.length())));
        btn_viewvideo.setVisibility(video_file == null ? View.GONE : View.VISIBLE);

        if (video_file != null)
            btn_deletevideo.setVisibility(View.VISIBLE);
        else
            btn_deletevideo.setVisibility(View.GONE);

    }

    private String getFileSizeString(long filelength) {
        if (filelength > MB)
            return String.format("%.2fMB", (float) (filelength / MB));
        else
            return String.format("%.2fKB", (float) (filelength / KB));
    }

    private static class UploadStruct_VideoUpload {
        int code = -1, form_id = -1;
        String comments, numberType = "", mobileNo = "";

        public UploadStruct_VideoUpload() {
        }

        public JSONObject toJSON() {
            try {
                JSONObject object = new JSONObject();
                object.put(UploadStructuresTags.Code.name(), code);
                object.put(UploadStructuresTags.TargetFormId.name(), form_id);
                object.put(UploadStructuresTags.Comment.name(), comments);
                object.put(UploadStructuresTags.ApkVersion.name(), MainActivity.apkVersion);
                object.put(UploadStructuresTags.NumberType.name(), numberType);
                object.put(UploadStructuresTags.NumberValue.name(), mobileNo);
                return object;
            } catch (JSONException e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    private class FormSpinnerAdapter implements SpinnerAdapter {
        private final static int COUNT = 4;

        @Override
        public int getCount() {
            return COUNT;
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return arg0;
        }

        @Override
        public int getItemViewType(int arg0) {
            return 0;
        }

        @Override
        public View getView(int arg0, View arg1, ViewGroup arg2) {
            if (arg1 == null)
                arg1 = LayoutInflater.from(getActivity()).inflate(R.layout.twoline_spinneritem, arg2, false);
            String lineoneres = "Select a form";
            String linetwores = "";
            switch (arg0) {
                case 0:
                    break;
                case 1:
                    lineoneres = getString(R.string.add_cleanliness_drive_marathi);
                    linetwores = getString(R.string.add_cleanliness_drive);
                    break;
                case 2:
                    lineoneres = getString(R.string.add_volunteer_drive_marathi);
                    linetwores = getString(R.string.add_volunteer_drive);
                    break;
                case 3:
                    lineoneres = getString(R.string.add_cleaned_area_details_marathi);
                    linetwores = getString(R.string.add_cleaned_area_details);
                    break;
            }

            ((TextView) arg1.findViewById(R.id.twolinespinneritem_lineone)).setText(lineoneres);
            ((TextView) arg1.findViewById(R.id.twolinespinneritem_linetwo)).setText(linetwores);
            return arg1;
        }

        @Override
        public int getViewTypeCount() {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public boolean isEmpty() {
            return false;
        }

        @Override
        public void registerDataSetObserver(DataSetObserver arg0) {
        }

        @Override
        public void unregisterDataSetObserver(DataSetObserver arg0) {
        }

        @Override
        public View getDropDownView(int arg0, View arg1, ViewGroup arg2) {
            if (arg1 == null)
                arg1 = LayoutInflater.from(getActivity()).inflate(R.layout.twoline_spinneritem, arg2, false);
            String lineoneres = "Select a form";
            String linetwores = "";
            switch (arg0) {
                case 0:
                    break;
                case 1:
                    lineoneres = getString(R.string.add_cleanliness_drive_marathi);
                    linetwores = getString(R.string.add_cleanliness_drive);
                    break;
                case 2:
                    lineoneres = getString(R.string.add_volunteer_drive_marathi);
                    linetwores = getString(R.string.add_volunteer_drive);
                    break;
                case 3:
                    lineoneres = getString(R.string.add_cleaned_area_details_marathi);
                    linetwores = getString(R.string.add_cleaned_area_details);
                    break;
            }

            ((TextView) arg1.findViewById(R.id.twolinespinneritem_lineone)).setText(lineoneres);
            ((TextView) arg1.findViewById(R.id.twolinespinneritem_linetwo)).setText(linetwores);
            return arg1;
        }
    }

}
