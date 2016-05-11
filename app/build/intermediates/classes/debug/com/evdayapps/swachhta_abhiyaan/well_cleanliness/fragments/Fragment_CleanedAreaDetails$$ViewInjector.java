// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_CleanedAreaDetails$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_CleanedAreaDetails target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427469, "field 'edt_code'");
    target.edt_code = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427482, "field 'edt_area'");
    target.edt_area = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427490, "field 'edt_garbagecollected_dry'");
    target.edt_garbagecollected_dry = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427493, "field 'edt_garbagecollected_wet'");
    target.edt_garbagecollected_wet = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427483, "field 'edt_roadlength'");
    target.edt_roadlength = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427486, "field 'edt_seashorelength'");
    target.edt_seashorelength = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427514, "field 'image_list'");
    target.image_list = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2131427481, "field 'tv_verificationdetails'");
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
    view = finder.findRequiredView(source, 2131427480, "method 'onVerifyButtonClicked'");
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

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_CleanedAreaDetails target) {
    target.edt_code = null;
    target.edt_area = null;
    target.edt_garbagecollected_dry = null;
    target.edt_garbagecollected_wet = null;
    target.edt_roadlength = null;
    target.edt_seashorelength = null;
    target.image_list = null;
    target.tv_verificationdetails = null;
  }
}
