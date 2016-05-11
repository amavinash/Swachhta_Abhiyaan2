package com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.evdayapps.swachhta_abhiyaan.well_cleanliness.R;

public class ConfirmationDialog extends Dialog {

	Listener listener;
	public interface Listener {
		public void leftButtonPressed();
		public void rightButtonPressed();
	}
	
	CharSequence title, message, lefttext, righttext;
	
	/**
	 * @brief Constructor
	 * @param context Context
	 * @param title Title of the dialog
	 * @param message Message to display in the dialog
	 * @param leftText Label for the left button
	 * @param rightText Label for the right button
	 * @param listener Listener for the button clicks
	 */
	public ConfirmationDialog(Context context, CharSequence title, CharSequence message, @Nullable CharSequence leftText, CharSequence rightText, Listener listener) {
		super(context);
		setCanceledOnTouchOutside(false);
		this.title = title;
		this.message = message;
		this.lefttext = leftText;
		this.righttext = rightText;
		this.listener = listener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.confirmationdialog);
		((TextView)findViewById(R.id.confdlg_title)).setText(title);
		((TextView)findViewById(R.id.confdlg_message)).setText(message);
		if(lefttext != null)
		{
			((Button)findViewById(R.id.confdlg_leftbtn)).setText(lefttext);
			((Button)findViewById(R.id.confdlg_leftbtn)).setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					if(listener != null)
						listener.leftButtonPressed();
				}
			});
		}
		else
			((Button)findViewById(R.id.confdlg_leftbtn)).setVisibility(View.GONE);
		if(righttext != null)
		{
			((Button)findViewById(R.id.confdlg_rightbtn)).setText(righttext);
			((Button)findViewById(R.id.confdlg_rightbtn)).setOnClickListener(new android.view.View.OnClickListener() {
				@Override
				public void onClick(View v) {
					dismiss();
					if(listener != null)
						listener.rightButtonPressed();
				}
			});
		}
		else
			findViewById(R.id.confdlg_rightbtn).setVisibility(View.GONE);
	}

	@Override
	public void show() {
		super.show();
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
	    lp.copyFrom(getWindow().getAttributes());
	    lp.width = WindowManager.LayoutParams.MATCH_PARENT;
	    lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
	    getWindow().setAttributes(lp);
	}
	
}
