package com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity;
import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;

/**
 * @brief Password dialog for password entry
 * @author Shannon
 */
public class PasswordDialog extends Dialog {
	private static final int MIN_USERNAME_LENGTH = 2;
	private static final int MIN_PASSWORD_LENGTH = 1;
	public static String globalUserName = "";
	@InjectView(R.id.passworddialog_username_edt) EditText edt_username;
	@InjectView(R.id.passworddialog_password_edt) EditText edt_password;
	CheckBox cb;
	private Listener listener;
	public static interface Listener {
		public void onLeftButtonPressed();
		public void onRightButtonPressed(PasswordDialog dialog, String username, String password, boolean b);
	}
	
	public PasswordDialog(Context context, Listener listener) {
		super(context);
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		setContentView(R.layout.password_dialog);
		// globally 
		TextView versionTxtView = (TextView)findViewById(R.id.versiontxt);

		//in your OnCreate() method
		versionTxtView.setText("v"+MainActivity.apkVersion);
		ButterKnife.inject(this);
	    cb = (CheckBox)findViewById(R.id.checkBox1);
	}

	@OnClick(R.id.passworddialog_submit_btn)
	void onSubmitButtonPressed() {
		globalUserName = edt_username.getText().toString();
		if(validate())
			if(cb.isChecked()==true)
			listener.onRightButtonPressed(this, edt_username.getText().toString(), edt_password.getText().toString(),true);
			else

				listener.onRightButtonPressed(this, edt_username.getText().toString(), edt_password.getText().toString(),false);
				
	}
	
	@OnClick(R.id.passworddialog_cancel_btn)
	void onCancelButtonPressed() {
		dismiss();
	}
	
	/**
	 * @brief Validate the text fields in the dialog. Just checks for minimum length
	 * @return true if all the fields are valid else false
	 */
	private boolean validate() {
		if(edt_username.getText().length() < MIN_USERNAME_LENGTH)
		{
			Toast.makeText(getContext(), R.string.msg_enter_username, Toast.LENGTH_SHORT).show();
			return false;
		}
		if(edt_password.getText().length() < MIN_PASSWORD_LENGTH)
		{
			Toast.makeText(getContext(), R.string.msg_enter_password, Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
}
