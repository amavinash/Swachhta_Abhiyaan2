package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.UserRegistration;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.MySqliteOpenHelper;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.PasswordDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.jSOnClassforData;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.net.AsyncCheckAllowVideoAccess;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.villageinfo;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class Fragment_MainPage extends Fragment {
    private final static String logtag = Fragment_MainPage.class.getSimpleName();
    private final static boolean DEBUG = MainActivity.DEBUG;


    //h s
    JSONObject jsonObj = null;
    String valueOfarray = "";
    ProgressDialog progressBar;
    private int progressBarStatus = 0;
    private Handler progressBarbHandler = new Handler();
    private long fileSize = 0;
    String statusofuser = "";
    String usernames = "";
    List<String> Statelist = new LinkedList<String>();
    TextView t1;

    // h s


    final UserRegistration userRegistration = new UserRegistration();


    MySqliteOpenHelper conAdapter;
    // This stores a reference to the actual item in the checkbox
    //h e

    //h e

    private static final String UPLOADVIDEOCHECK_VALIDRESPONSE = "1";
    public static String urlForVideoUpload = "";
    public static int videoDuration = 0;
    private boolean isLoggedIn = false;
    @InjectView(R.id.mainfrag_btnlogin)
    Button btn_login;
    @InjectView(R.id.mainfrag_btnadddrive)
    View btn_adddrive;
    @InjectView(R.id.mainfrag_btnaddvolunteerdrive)
    View btn_addvolunteers;
    @InjectView(R.id.mainfrag_btnaddcleanedareadetails)
    View btn_addcleanedarea;
    @InjectView(R.id.mainfrag_btncapturevideo)
    View btn_capturevideo;
    @InjectView(R.id.mainfrag_btnaddvillage)
    View btn_addvillage;

    public Fragment_MainPage() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.mainpage_fragment, container, false);

        conAdapter = new MySqliteOpenHelper(getActivity());

        ButterKnife.inject(this, view);
        handleLoggedIn(isLoggedIn);
        ((MainActivity) getActivity()).setTopbarElementVisibility(false);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    @OnClick(R.id.mainfrag_btnadddrive)
    void onAddDriveButtonClicked() {
        ((MainActivity) getActivity()).showNewAreaFragment();
    }

    @OnClick(R.id.mainfrag_btnaddvillage)
    void onAddVillageButtonClicked() {
        ((MainActivity) getActivity()).showAddVillageFragment();
    }

    @OnClick(R.id.mainfrag_btnaddvolunteerdrive)
    void onAddVolunteerDetailsButtonClicked() {
        ((MainActivity) getActivity()).showVolunteerDetailsFragment();
    }

    @OnClick(R.id.mainfrag_btnaddcleanedareadetails)
    void onAddCleanedAreaDetails() {
        ((MainActivity) getActivity()).showCleanedAreaDetailsFragment();
    }

    @OnClick(R.id.mainfrag_btncapturevideo)
    void onCaptureVideo() {
        new AsyncCheckAllowVideoAccess(getActivity(), new AsyncCheckAllowVideoAccess.Listener() {

            Boolean videoAccessFlag = false;

            @Override
            public void handleSuccess(String response) {
                try {
                    JSONObject jObject = new JSONObject(response);
//					JSONObject jsonObj1=new JSONObject();
//					jsonObj1= jObject.getJSONObject("VideoConfig");

                    urlForVideoUpload = jObject.getString("VideoUploadServerURL");
                    videoDuration = Integer.parseInt(jObject.getString("VideoDuration"));

                    if (jObject.getString("AccessFlag").equals("YES")) {
                        videoAccessFlag = true;
                    }

                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (DEBUG)
                    Log.i(logtag, "onCaptureVideo handleSuccess: response: " + response);
                if (videoAccessFlag)
                    ((MainActivity) getActivity()).showVideoUploadFragment();
                else
                    Toast.makeText(getActivity(), "Video Upload access is currently disabled", Toast.LENGTH_LONG).show();
            }

            @Override
            public void handleFailure() {
//				((MainActivity)getActivity()).showVideoUploadFragment();
            }
        }).execute(MainActivity.BASE_URL, "");

    }

    @OnClick(R.id.mainfrag_btnlogin)
    void onLoginButtonPressed() {
        if (isLoggedIn) {
            handleLoggedIn(false);
            return;
        }
        new PasswordDialog(getActivity(), new PasswordDialog.Listener() {

            @Override
            public void onRightButtonPressed(final PasswordDialog dialog, String username, String password, boolean b) {
                /* Test against server *
				 	new AsyncPasswordCheck(getActivity(), new AsyncPasswordCheck.Listener() {
				 	@Override
					public void handleSuccess(String response) {
						if(DEBUG)
							Log.i(logtag,"handleSuccess: response: "+response);
						handleLoggedIn(true);
						dialog.dismiss();
					}
					
					@Override
					public void handleFailure() {
						Toast.makeText(getActivity(), "Error while logging in", Toast.LENGTH_LONG).show();
					}
				}).execute(MainActivity.BASE_URL, username, password);
				*/
				
				/* Test Against Local Hardcoded values */
                final String userNa = username;
                final String passWo = password;

                if (b == true) {
                    final ProgressDialog ringProgressDialog = ProgressDialog.show(getActivity(), "Please wait ", "Downloading Data ...", true);
                    ringProgressDialog.setCancelable(false);


                    Thread th = new Thread(new Runnable() {
                        Handler handler = new Handler();
                        private long startTime = System.currentTimeMillis();

                        public void run() {

                            try {
                                // Here you should write your time consuming task...
                                // Let the progress ring for 10 seconds...

                                userRegistration.setUsername(userNa);
                                userRegistration.setPassword(passWo);

                                try {

                                    jsonObj = new JSONObject();
                                    jSOnClassforData json = new jSOnClassforData();
                                    //callprogress(v.getContext());
                                    jsonObj = json.GetUserDetails(userRegistration);

                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (ClientProtocolException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                JSONArray jArray = null;
                                JSONObject jsonObj1 = new JSONObject();
                                JSONObject jsonObj2 = new JSONObject();

                                jsonObj1 = jsonObj.getJSONObject("NewDataSet");
                                jsonObj2 = jsonObj1.getJSONObject("UserData");
                                statusofuser = jsonObj2.getString("Status");

                                usernames = jsonObj2.getString("UserName");
                                String passwords = jsonObj2.getString("Password");

                                int sizeOfjson = jsonObj1.length();
                                System.out.println("user status " + statusofuser);

                                if (statusofuser.equals("VALID")) {
                                    Statelist = conAdapter.getAllState(usernames);
                                    if (Statelist.size() > 1) {
                                        conAdapter.deleteAllCountries(usernames);
                                        conAdapter.deleteuser(usernames);
                                    }
                                    conAdapter.createuser(usernames, passwords);


                                    jArray = jsonObj1.getJSONArray("Region");
                                    System.out.println("data:" + jArray);


                                    //System.out.println("*****JARRAY*****"+jArray.length());
                                    villageinfo villageinfos = new villageinfo();

                                    for (int i = 0; i < jArray.length(); i++) {


                                        JSONObject json_data;

                                        json_data = jArray.getJSONObject(i);


                                        String id = json_data.getString("Id");
                                        String name = json_data.getString("Name");
                                        String ParentName = json_data.getString("ParentName");
                                        //System.out.println(ParentName);
                                        String ParentId = json_data.getString("ParentId");
                                        String LocationTypeId = json_data.getString("LocationTypeId");
                                        String type = json_data.getString("Type");


                                        conAdapter.createCountry(id, name, "ParentName", ParentId, LocationTypeId, type, usernames);
                                        //mySQLiteHelper.addVillageinfo(villageinfos);


                                    }
                                }
                                Thread.sleep(3000);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            handler.post(new Runnable() {
                                public void run() {
                                    if (statusofuser.equals("VALID")) {
                                        handleLoggedIn(true);

                                        Toast.makeText(getActivity(), "Your username , password is " + statusofuser, Toast.LENGTH_LONG).show();
//												                  	Intent im=new Intent(getActivity(),spindisplay.class);
//												                  	im.putExtra("username", usernames);
//																	startActivity(im);
                                    } else if (statusofuser.equals("INVALID")) {
                                        Toast.makeText(getActivity(), "Your username , password is " + statusofuser, Toast.LENGTH_LONG).show();
                                    } else if (statusofuser.equals("")) {
//												                    		Toast.makeText(getActivity(), jsonObj.toString(), Toast.LENGTH_LONG).show();
                                        Toast.makeText(getActivity(), "Error processing data", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            ringProgressDialog.dismiss();

                        }

                    });

                    th.start();

                } else {
                    List<String> userlist = new LinkedList<String>();
                    userlist = conAdapter.getUSER(userNa, passWo);
                    if (userlist.size() >= 1) {
                        handleLoggedIn(true);
//						Intent im=new Intent(getActivity(),spindisplay.class);
//						im.putExtra("username", userNa);
//						startActivity(im);

                    } else {
                        Toast.makeText(getActivity(), "Your username or password is Invalid Or No data found with this user", Toast.LENGTH_LONG).show();
                    }
                }
                dialog.dismiss();

            }

            @Override
            public void onLeftButtonPressed() {
            }
        }).show();
    }

    private void handleLoggedIn(boolean loggedin) {
        isLoggedIn = loggedin;
        btn_adddrive.setEnabled(loggedin);
        if (!loggedin || ((MainActivity) getActivity()).SHOW_ALL_OPTIONS) {
            btn_addcleanedarea.setEnabled(loggedin);
            btn_addvolunteers.setEnabled(loggedin);
            btn_capturevideo.setEnabled(loggedin);
            btn_addvillage.setEnabled(loggedin);
        }
        btn_login.setText(isLoggedIn ? R.string.logout : R.string.login);
    }
}


// json for temp data -- use in jarray
//[
// {
//     "ParentName": "",
//     "Name": "MAHARASHTRA",
//     "ParentId": "0",
//     "Id": "1",
//     "LocationTypeId": "2",
//     "Type": "STATE"
// },
// {
//     "ParentName": "",
//     "Name": "NANDURBAR",
//     "ParentId": "1",
//     "Id": "497",
//     "LocationTypeId": "3",
//     "Type": "DISTRICT"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "AKKALKUWA",
//     "ParentId": "497",
//     "Id": "3950",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "AKRANI",
//     "ParentId": "497",
//     "Id": "3951",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "TALODE",
//     "ParentId": "497",
//     "Id": "3952",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "SHAHADE",
//     "ParentId": "497",
//     "Id": "3953",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "NANDURBAR",
//     "ParentId": "497",
//     "Id": "3954",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NANDURBAR",
//     "Name": "NAWAPUR",
//     "ParentId": "497",
//     "Id": "3955",
//     "LocationTypeId": "4",
//     "Type": "TALUKA"
// },
// {
//     "ParentName": "NAWAPUR",
//     "Name": "LAKKAD KOT",
//     "ParentId": "3955",
//     "Id": "525788",
//     "LocationTypeId": "5",
//     "Type": "VILLAGE"
// },
// {
//     "ParentName": "NAWAPUR",
//     "Name": "HIRAFALI",
//     "ParentId": "3955",
//     "Id": "525789",
//     "LocationTypeId": "5",
//     "Type": "VILLAGE"
// },
// {
//     "ParentName": "NAWAPUR",
//     "Name": "KHOKARWADA",
//     "ParentId": "3955",
//     "Id": "525790",
//     "LocationTypeId": "5",
//     "Type": "VILLAGE"
// }
//]
