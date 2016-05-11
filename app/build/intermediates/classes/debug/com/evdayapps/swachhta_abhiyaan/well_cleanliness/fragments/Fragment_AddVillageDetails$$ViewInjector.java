// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_AddVillageDetails$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_AddVillageDetails target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427428, "field 'avtv_date'");
    target.avtv_date = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427431, "field 'avspinner_state' and method 'onItemSelected'");
    target.avspinner_state = (android.widget.Spinner) view;
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
    view = finder.findRequiredView(source, 2131427434, "field 'avspinner_district' and method 'onItemSelected'");
    target.avspinner_district = (android.widget.Spinner) view;
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
    view = finder.findRequiredView(source, 2131427437, "field 'avspinner_taluka' and method 'onItemSelected'");
    target.avspinner_taluka = (android.widget.Spinner) view;
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
    view = finder.findRequiredView(source, 2131427440, "field 'avspinner_village' and method 'onItemSelected'");
    target.avspinner_village = (android.widget.Spinner) view;
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
    view = finder.findRequiredView(source, 2131427442, "field 'edt_malevolunteers'");
    target.edt_malevolunteers = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427445, "field 'edt_areacleaned'");
    target.edt_areacleaned = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427448, "field 'edt_cleanedroad'");
    target.edt_cleanedroad = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427451, "field 'edt_cleanedshashore'");
    target.edt_cleanedshashore = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427458, "field 'edt_drygarbagewt'");
    target.edt_drygarbagewt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427461, "field 'edt_wetgarbagewt'");
    target.edt_wetgarbagewt = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427413, "field 'clickPhotoBtn'");
    target.clickPhotoBtn = (android.widget.Button) view;
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

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_AddVillageDetails target) {
    target.avtv_date = null;
    target.avspinner_state = null;
    target.avspinner_district = null;
    target.avspinner_taluka = null;
    target.avspinner_village = null;
    target.edt_malevolunteers = null;
    target.edt_areacleaned = null;
    target.edt_cleanedroad = null;
    target.edt_cleanedshashore = null;
    target.edt_drygarbagewt = null;
    target.edt_wetgarbagewt = null;
    target.clickPhotoBtn = null;
  }
}
