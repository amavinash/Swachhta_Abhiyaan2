// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_VolunteerDetails$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_VolunteerDetails target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427507, "field 'edt_code'");
    target.edt_code = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427510, "field 'edt_numvolunteers'");
    target.edt_numvolunteers = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427514, "field 'image_list'");
    target.image_list = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2131427509, "field 'tv_verificationdetails'");
    target.tv_verificationdetails = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427413, "method 'onPhotoButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onPhotoButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427508, "method 'onVerifyButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onVerifyButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427414, "method 'onSubmitButtonPressed'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSubmitButtonPressed();
        }
      });
  }

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_VolunteerDetails target) {
    target.edt_code = null;
    target.edt_numvolunteers = null;
    target.image_list = null;
    target.tv_verificationdetails = null;
  }
}
