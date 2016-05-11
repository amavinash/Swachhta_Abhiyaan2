package com.evdayapps.swachhta_abhiyaan.well_cleanliness;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.DistrictStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.MySqliteOpenHelper;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.StateStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.data.TalukaStruct;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.ConfirmationDialog;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.BaseFormFragment;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_AddVillageDetails;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_CleanedAreaDetails;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_MainPage;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_NewAreaDetails;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_UploadVideo;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_VolunteerDetails;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * @author Shannon
 * @brief Main Activity. Acts as main control and inbetween
 */
public class MainActivity extends FragmentActivity implements LocationListener {
    public final static boolean DEBUG = true;
    private final static String logtag = MainActivity.class.getSimpleName();
    Uri imageUri;
    private final static boolean LIVE = false;
    //private final static String QA_URL = "http://eventstatus.in/CleanlinessDriveQA/SubmitMobileData.aspx";
    private final static String QA_URL = "http://eventstatus.in/WellCleanlinessDrive/SubmitMobileData.aspx";
    private final static String LIVE_URL = "http://103.29.156.118/CleanlinessDrive/SubmitMobileData.aspx";
    public final static String BASE_URL = QA_URL;
    public final static String apkVersion = "1.1.0";
    public final static String PREF_FILE = "SwachhtaPrefs", PREF_SHOWALLOPTIONS = "ShowAllOptions";
    public boolean SHOW_ALL_OPTIONS = false;
    private static final String CSV_SEPERATOR = ",";
    private static final int REQUESTCODE_CAPTUREIMAGE = 0, REQUESTCODE_CAPTUREVIDEO = 1;
    //private static final long VIDEOCAPTURE_LIMIT_SECONDS = 6;	// Does not work!!

    // Image Capture Intent Related
    private boolean IMAGE_CAPTURE_REQUESTED = false;
    public Location image_location = null;

    private int tap_count = 0;

    // Location Handling
    private static final boolean IGNORE_NONGPS_PROVIDER = false;
    private final static long LOCATION_DELAY = 2 * 1000;    // 2 Seconds
    private final static float LOCATION_MIN_DISTDIFF = 1;
    //private static final int LOCATION_TIMEOUT = 20 * 1000; // 20 Seconds
    private HandlerThread locationHandlerThread;
    private LocationManager lcnmgr;
    public Location current_location = null;
    private int currentLocationResource = R.drawable.ic_locationstatus_inactive;
    private ConfirmationDialog dialog_enablegps;

    // File Storage
    public File external_dir;
    String captureFileURI;

    public MySqliteOpenHelper conAdapter;

    // Data
    public SQLiteDatabase database;

    // Views
    @InjectView(R.id.mainactivity_topbar)
    View topbar;
    @InjectView(R.id.mainactivity_topbar_tvtitle)
    TextView tv_title;
    @InjectView(R.id.mainactivity_topbar_imglocationstatus)
    ImageView img_locationstatus;
    @InjectView(R.id.mainactivity_topbar_banner)
    ImageView img_banner;

    // Colors
    int color_primary_topbar = -1, color_secondary_topbar = -1;

    private Handler timeout_handler;

    private class TimeoutHandler implements Handler.Callback {
        public final static int MSG_TIMEOUTLOCATIONINFO = 0, MSG_TIMEOUTTAPCOUNT = 1;

        @Override
        public boolean handleMessage(Message arg0) {
            switch (arg0.what) {
                case MSG_TIMEOUTLOCATIONINFO:
                    current_location = null;
                    if (currentLocationResource == R.drawable.ic_locationstatus_active)
                        setLocationImage(R.drawable.ic_locationstatus_inactive);
                    return true;
                case MSG_TIMEOUTTAPCOUNT:
                    tap_count = 0;
                    return true;
            }
            return false;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        Utils.initInstance();
        SHOW_ALL_OPTIONS = getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).getBoolean(PREF_SHOWALLOPTIONS, false);

        color_primary_topbar = getResources().getColor(R.color.brown_green);
        color_secondary_topbar = getResources().getColor(R.color.light_green);

        // LOCATION VARIABLES
        lcnmgr = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationHandlerThread = new HandlerThread("LocationHandler");
        locationHandlerThread.start();

        timeout_handler = new Handler(new TimeoutHandler());

        initialiseCacheDirectory();
        ImageLoader.getInstance();
        ImageLoader.getInstance().init(ImageLoaderConfiguration.createDefault(getApplicationContext()));

        populateDatabase();

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        setLocationImage(currentLocationResource);
        if (savedInstanceState == null)
            getSupportFragmentManager().beginTransaction().replace(R.id.mainactivity_fragmentcontainer, new Fragment_MainPage()).commit();
    }

    private void initialiseCacheDirectory() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            external_dir = this.getExternalCacheDir();
            if (!external_dir.exists())
                external_dir.mkdirs();
        }
    }

    public void setShowAllOptionsToTrue() {
        SHOW_ALL_OPTIONS = true;
        getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit().putBoolean(PREF_SHOWALLOPTIONS, true).commit();
    }

    private void setLocationImage(int resid) {
        img_locationstatus.setImageResource(resid);
        currentLocationResource = resid;
        switch (resid) {
            case R.drawable.ic_locationstatus_animated:
                ((AnimationDrawable) img_locationstatus.getDrawable()).start();
                break;
            case R.drawable.ic_locationstatus_active:
                break;
            case R.drawable.ic_locationstatus_inactive:
                break;
        }
    }

    /**
     * i
     *
     * @brief Check if the database has the info on states, districts, talukas. If it doesnt read the csv files in the assets folders and put them into the db
     */
    private void populateDatabase() {
        database = new MySqliteOpenHelper(getApplicationContext()).getWritableDatabase();

        // Check if db has data, if it does, return
        Cursor cursor = database.query("States", null, null, null, null, null, null);
        if (cursor != null) {
            long count = cursor.getCount();
            cursor.close();
            if (count > 0)
                return;
        }

        // Populate db with data from csv files
        try {
            ArrayList<StateStruct> states = readStateCsvFile();
            ArrayList<DistrictStruct> districts = readDistrictCsvFile();
            ArrayList<TalukaStruct> talukas = readTalukaCsvFile();

            database.beginTransaction();
            if (states.size() > 0 && districts.size() > 0 && talukas.size() > 0) {
//				for(StateStruct state : states)
//					database.insert("States", null, state.getContentValues());
//				for(DistrictStruct district : districts)
//					database.insert("Districts", null, district.getContentValues());
//				for(TalukaStruct taluka : talukas)
//					database.insert("Talukas", null, taluka.getContentValues());
            }
            database.setTransactionSuccessful();
        } catch (Exception e) {
            Toast.makeText(this, "Error Creating the database", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        } finally {
            database.endTransaction();
        }
    }

    /**
     * @return Arraylist containing all the states
     * @throws IOException
     * @brief Read the state file and make an array of the states with their ids
     */
    private ArrayList<StateStruct> readStateCsvFile() throws IOException {
        ArrayList<StateStruct> states = new ArrayList<StateStruct>();
        InputStream state_fis = getResources().getAssets().open("states.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(state_fis, "UTF-8"));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0) {
                String[] split = line.split(CSV_SEPERATOR);
                if (split != null && split.length == 2) {
                    StateStruct state = new StateStruct(Long.parseLong(split[0]), split[1]);
                    if (DEBUG)
                        Log.i(logtag, "state: " + state.getContentValues().toString());
                    states.add(state);
                }
            }
            line = reader.readLine();
        }
        reader.close();
        state_fis.close();
        return states;
    }

    /**
     * @return Arraylist containing all the states
     * @throws IOException
     * @brief Read the district file and make an array of the districts with their ids
     */
    private ArrayList<DistrictStruct> readDistrictCsvFile() throws IOException {
        ArrayList<DistrictStruct> districts = new ArrayList<DistrictStruct>();
        InputStream district_fis = getResources().getAssets().open("districts.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(district_fis, "UTF-8"));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0) {
                String[] split = line.split(CSV_SEPERATOR);
                if (split != null && split.length == 3) {
                    DistrictStruct district = new DistrictStruct(Long.parseLong(split[0]), split[1], Long.parseLong(split[2]));
                    if (DEBUG)
                        Log.i(logtag, "district: " + district.getContentValues().toString());
                    districts.add(district);
                }
            }

            line = reader.readLine();
        }
        reader.close();
        district_fis.close();
        return districts;
    }

    private ArrayList<TalukaStruct> readTalukaCsvFile() throws IOException {
        ArrayList<TalukaStruct> talukas = new ArrayList<TalukaStruct>();
        InputStream talukas_fis = getResources().getAssets().open("talukas.csv");
        BufferedReader reader = new BufferedReader(new InputStreamReader(talukas_fis, "UTF-8"));
        String line = reader.readLine();
        while (line != null) {
            if (line.length() > 0) {
                String[] split = line.split(CSV_SEPERATOR);
                if (split != null && split.length == 3) {
                    TalukaStruct taluka = new TalukaStruct(Long.parseLong(split[0]), split[1], Long.parseLong(split[2]));
                    if (DEBUG)
                        Log.i(logtag, "taluka: " + taluka.getContentValues().toString());
                    talukas.add(taluka);
                }
            }

            line = reader.readLine();
        }
        reader.close();
        talukas_fis.close();
        return talukas;
    }

    /**
     * @brief Utility function to show the "New Area Details" fragment
     */
    public void showNewAreaFragment() {
        Fragment_NewAreaDetails frag = new Fragment_NewAreaDetails();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.mainactivity_fragmentcontainer, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    public void showAddVillageFragment() {
        Fragment_AddVillageDetails frag = new Fragment_AddVillageDetails();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.mainactivity_fragmentcontainer, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    /**
     * @brief Utility function to show the "Volunteer Details" fragment
     */
    public void showVolunteerDetailsFragment() {
        Fragment_VolunteerDetails frag = new Fragment_VolunteerDetails();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.mainactivity_fragmentcontainer, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    /**
     * @brief Utility function to show the "Cleaned Area Details" fragment
     */
    public void showCleanedAreaDetailsFragment() {
        Fragment_CleanedAreaDetails frag = new Fragment_CleanedAreaDetails();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.mainactivity_fragmentcontainer, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    /**
     * @brief Utility function to show the "Upload Video" fragment
     */
    public void showVideoUploadFragment() {
        Fragment_UploadVideo frag = new Fragment_UploadVideo();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        trans.replace(R.id.mainactivity_fragmentcontainer, frag);
        trans.addToBackStack(null);
        trans.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (IMAGE_CAPTURE_REQUESTED)
            image_location = current_location;
        else
            registerLocationListeners();
        IMAGE_CAPTURE_REQUESTED = false;
    }

    /**
     * @brief Set up the listener for location changes. Displays he 'Enable GPS' dialog if GPS provider is disabled
     */
    private void registerLocationListeners() {
        // Network Location Listener
        lcnmgr.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, LOCATION_DELAY, LOCATION_MIN_DISTDIFF,
                this, locationHandlerThread.getLooper());
        // GPS Location Listener
        if (lcnmgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
            lcnmgr.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_DELAY, LOCATION_MIN_DISTDIFF,
                    this, locationHandlerThread.getLooper());
        else
            showEnableGpsDialog();

        setLocationImage(R.drawable.ic_locationstatus_inactive);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!IMAGE_CAPTURE_REQUESTED)
            unregisterListeners();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!IMAGE_CAPTURE_REQUESTED)
            locationHandlerThread.quit();
    }

    private void unregisterListeners() {
        lcnmgr.removeUpdates(this);
    }

    /**
     * @brief Listener for location changes. Set locaton image to the 'active location' image. If existing location is GPS provided and current is Network, only overwrite if delay is greater than 2*LOCATION_DELAY
     */
    @Override
    public void onLocationChanged(Location location) {
        if (DEBUG)
            Log.i(logtag, "onLocationChanged: Provider: " + location.getProvider() + " latitude: " + location.getLatitude() + " longitude: " + location.getLongitude() + " accuracy: " + location.getAccuracy());
        if (IGNORE_NONGPS_PROVIDER && !location.getProvider().equals(LocationManager.GPS_PROVIDER))
            return;

        if (currentLocationResource != R.drawable.ic_locationstatus_active) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setLocationImage(R.drawable.ic_locationstatus_active);
                }
            });
        }

        // Set Location if current location is null
        // If current_location is from GPS and location is from Network only overwrite if current_location is too old (4secs)
        if (current_location == null)
            current_location = location;
        else if ((current_location.getProvider().equalsIgnoreCase(LocationManager.GPS_PROVIDER)) && (location.getProvider().equalsIgnoreCase(LocationManager.NETWORK_PROVIDER))) {
            if (System.currentTimeMillis() - current_location.getTime() > 2 * LOCATION_DELAY)
                current_location = location;
        } else
            current_location = location;

        // TODO might wanna set a timeout here
        //timeout_handler.removeMessages(TimeoutHandler.MSG_TIMEOUTLOCATIONINFO);
        //timeout_handler.sendMessageDelayed(timeout_handler.obtainMessage(TimeoutHandler.MSG_TIMEOUTLOCATIONINFO), LOCATION_TIMEOUT);
    }

    @Override
    public void onProviderDisabled(String provider) {
        if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER)) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    showEnableGpsDialog();
                }
            });
        }
    }

    @Override
    public void onProviderEnabled(String arg0) {
        registerLocationListeners();
    }

    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
    }

    /**
     * @brief Display the 'Enable GPS' dialog. Open the location settings if the user chooses to enable gps
     */
    private void showEnableGpsDialog() {
        if (dialog_enablegps == null) {
            dialog_enablegps = new ConfirmationDialog(MainActivity.this, getString(R.string.gpsrequired_title), getString(R.string.gpsrequired_message), "No", "Yes",
                    new ConfirmationDialog.Listener() {
                        @Override
                        public void rightButtonPressed() {
                            Intent I = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(I);
                        }

                        @Override
                        public void leftButtonPressed() {
                            setLocationImage(R.drawable.ic_locationstatus_inactive);
                        }
                    });
        }
        if (!dialog_enablegps.isShowing())
            dialog_enablegps.show();
    }

    /**
     * @param withLocationInformation Should the location listener continue during the intent
     * @brief Initiate the image capture intent. Set the image_uri as extra info.
     */
    public void startImageCaptureIntent(boolean withLocationInformation) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(external_dir, Utils.getInstance().getDateForFilename(Calendar.getInstance(Locale.getDefault()).getTime()) + ".jpg");
        if (file != null) {
            Uri fileUri = Uri.fromFile(file);
            captureFileURI = file.getAbsolutePath();


            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "New Picture");
            values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
            imageUri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            if (withLocationInformation)
                IMAGE_CAPTURE_REQUESTED = true;
            startActivityForResult(cameraIntent, REQUESTCODE_CAPTUREIMAGE);
        } else {
            Toast.makeText(this, "Unable to create output file", Toast.LENGTH_SHORT).show();
            Log.e(logtag, "Error creating image file");
        }
    }

    /**
     * @brief Start the video capture intent with the file path
     */
    public void startVideoCaptureIntent() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        File file = new File(external_dir, Utils.getInstance().getDateForFilename(Calendar.getInstance(Locale.getDefault()).getTime()) + ".mp4");
        if (file != null) {
            Uri fileUri = Uri.fromFile(file);
            captureFileURI = file.getAbsolutePath();
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
            if (Fragment_MainPage.videoDuration != 0)
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, Fragment_MainPage.videoDuration);
            else
                cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 120);

            cameraIntent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
            startActivityForResult(cameraIntent, REQUESTCODE_CAPTUREVIDEO);
        } else {
            Toast.makeText(this, "Unable to create output file", Toast.LENGTH_SHORT).show();
            Log.e(logtag, "Error creating image file");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUESTCODE_CAPTUREIMAGE) {
                if (MainActivity.DEBUG)
                    Log.d(logtag, "Image saved to: " + captureFileURI);
                try {
                    //harshad code

//                    Uri tempImgUri;
//
//                    tempImgUri = Uri.parse(captureFileURI);

                    OutputStream stream;


                    Bitmap thumbnail;


                    thumbnail = MediaStore.Images.Media.getBitmap(

                            getContentResolver(), imageUri);


                    long times = System.currentTimeMillis();

                    String tempCompressedUrl = "sdcard/" + times + ".jpg";

                    try {
                        stream = new FileOutputStream("sdcard/" + times + ".jpg");

                        thumbnail.compress(CompressFormat.JPEG, 20, stream);
                        //hmk.put(count,targetPath+"/"+times+".jpg");

                        //hashmapForbyteArray.put(count, value);

                        stream.flush();

                        stream.close();

                        File imageFile = new File(tempCompressedUrl);

                        if (imageFile.canRead()) {

                            ((BaseFormFragment) getSupportFragmentManager().findFragmentById(R.id.mainactivity_fragmentcontainer)).addImage(tempCompressedUrl);

                        }

                        // myGallery.addView(insertPhoto((String)hmk.get(count),thumbnail));

//            	stream.flush();

//              stream.close();

                    } catch (FileNotFoundException e) {

                        // TODO Auto-generated catch block

//            e.printStackTrace();

                        Utils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_clicking_photo));

                    } catch (IOException e) {

                        // TODO Auto-generated catch block

//            e.printStackTrace();

                        Utils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_clicking_photo));

                    }
                    //harshad code end


//            		File imageFile = new File(captureFileURI);
//            		if(imageFile.canRead())
//        				((BaseFormFragment)getSupportFragmentManager().findFragmentById(R.id.mainactivity_fragmentcontainer)).addImage(captureFileURI);
                } catch (Exception e) {
                    Utils.showErrorDialog(this, getString(R.string.error), getString(R.string.error_clicking_photo));
                }
            } else if (requestCode == REQUESTCODE_CAPTUREVIDEO) {
                if (MainActivity.DEBUG)
                    Log.d(logtag, "Video saved to: " + captureFileURI);
                if (new File(captureFileURI).canRead()) {
                    ((BaseFormFragment) getSupportFragmentManager().findFragmentById(R.id.mainactivity_fragmentcontainer)).addVideo(captureFileURI);
                }
            }
        }
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    @OnClick(R.id.mainactivity_topbar_imglocationstatus)
    void onLocationButtonClicked() {
        if (tap_count == 20) {
            new ConfirmationDialog(this, "Developer Information", "This app was developed by EvdayApps.\nContact rodrtech@gmail.com for inquiries", null, "OK", null).show();
            tap_count = 0;
        } else {
            tap_count++;
            timeout_handler.removeMessages(TimeoutHandler.MSG_TIMEOUTTAPCOUNT);
            timeout_handler.sendMessageDelayed(timeout_handler.obtainMessage(TimeoutHandler.MSG_TIMEOUTTAPCOUNT), 2 * 1000);
        }
        if (!lcnmgr.isProviderEnabled(LocationManager.GPS_PROVIDER))
            showEnableGpsDialog();
        else
            Toast.makeText(this, "GPS is already enabled", Toast.LENGTH_SHORT).show();
    }

    public void setTopbarElementVisibility(boolean visible) {
        tv_title.setVisibility(visible ? View.VISIBLE : View.GONE);
        img_locationstatus.setVisibility(visible ? View.VISIBLE : View.GONE);
        img_banner.setVisibility(visible ? View.GONE : View.VISIBLE);
        topbar.setBackgroundColor(visible ? color_secondary_topbar : color_primary_topbar);
    }
}
