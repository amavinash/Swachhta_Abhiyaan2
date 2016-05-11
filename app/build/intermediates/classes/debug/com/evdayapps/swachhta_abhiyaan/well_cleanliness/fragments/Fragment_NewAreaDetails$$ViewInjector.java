// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_NewAreaDetails$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_NewAreaDetails target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427495, "field 'tv_date'");
    target.tv_date = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427496, "field 'spinner_state' and method 'onItemSelected'");
    target.spinner_state = (android.widget.Spinner) view;
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemSelected((android.widget.Spinner) p0);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427497, "field 'spinner_district' and method 'onItemSelected'");
    target.spinner_district = (android.widget.Spinner) view;
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemSelected((android.widget.Spinner) p0);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427498, "field 'spinner_taluka' and method 'onItemSelected'");
    target.spinner_taluka = (android.widget.Spinner) view;
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemSelected((android.widget.Spinner) p0);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427499, "field 'spinner_village' and method 'onItemSelected'");
    target.spinner_village = (android.widget.Spinner) view;
    ((android.widget.AdapterView<?>) view).setOnItemSelectedListener(
      new android.widget.AdapterView.OnItemSelectedListener() {
        @Override public void onItemSelected(
          android.widget.AdapterView<?> p0,
          android.view.View p1,
          int p2,
          long p3
        ) {
          target.onItemSelected((android.widget.Spinner) p0);
        }
        @Override public void onNothingSelected(
          android.widget.AdapterView<?> p0
        ) {
          
        }
      });
    view = finder.findRequiredView(source, 2131427502, "field 'edt_name'");
    target.edt_name = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427503, "field 'edt_address'");
    target.edt_address = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427504, "field 'edt_pincode'");
    target.edt_pincode = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427505, "field 'edt_mobilenumber'");
    target.edt_mobilenumber = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427506, "field 'edt_comment'");
    target.edt_comment = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427514, "field 'image_list'");
    target.image_list = (android.widget.LinearLayout) view;
    view = finder.findRequiredView(source, 2131427413, "method 'onPhotoButtonClicked'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onPhotoButtonClicked();
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

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_NewAreaDetails target) {
    target.tv_date = null;
    target.spinner_state = null;
    target.spinner_district = null;
    target.spinner_taluka = null;
    target.spinner_village = null;
    target.edt_name = null;
    target.edt_address = null;
    target.edt_pincode = null;
    target.edt_mobilenumber = null;
    target.edt_comment = null;
    target.image_list = null;
  }
}
