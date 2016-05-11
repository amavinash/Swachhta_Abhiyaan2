// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_UploadVideo$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_UploadVideo target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427465, "field 'edt_url'");
    target.edt_url = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427467, "field 'form_spinner' and method 'onItemSelected'");
    target.form_spinner = (android.widget.Spinner) view;
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemSelected(p2);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427469, "field 'edt_locationcode'");
    target.edt_locationcode = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427473, "field 'edt_comment'");
    target.edt_comment = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427413, "field 'btn_capturevideo' and method 'onCaptureVideoBtnClicked'");
    target.btn_capturevideo = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCaptureVideoBtnClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427477, "field 'videodetails_infotv'");
    target.videodetails_infotv = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427478, "field 'btn_viewvideo' and method 'onViewVideoButtonClicked'");
    target.btn_viewvideo = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onViewVideoButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427479, "field 'btn_deletevideo' and method 'onDeleteVideoButtonClicked'");
    target.btn_deletevideo = (android.widget.TextView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onDeleteVideoButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427471, "field 'tv_verificationdetails'");
    target.tv_verificationdetails = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427414, "method 'onSubmitButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSubmitButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427470, "method 'onVerifyButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onVerifyButtonClicked();
        }
      });
  }

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_UploadVideo target) {
    target.edt_url = null;
    target.form_spinner = null;
    target.edt_locationcode = null;
    target.edt_comment = null;
    target.btn_capturevideo = null;
    target.videodetails_infotv = null;
    target.btn_viewvideo = null;
    target.btn_deletevideo = null;
    target.tv_verificationdetails = null;
  }
}
