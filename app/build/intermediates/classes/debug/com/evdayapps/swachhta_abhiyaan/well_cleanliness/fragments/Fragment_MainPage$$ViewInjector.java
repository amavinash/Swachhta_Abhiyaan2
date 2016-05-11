// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class Fragment_MainPage$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_MainPage target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427516, "field 'btn_login' and method 'onLoginButtonPressed'");
    target.btn_login = (android.widget.Button) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLoginButtonPressed();
        }
      });
    view = finder.findRequiredView(source, 2131427517, "field 'btn_adddrive' and method 'onAddDriveButtonClicked'");
    target.btn_adddrive = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddDriveButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427518, "field 'btn_addvolunteers' and method 'onAddVolunteerDetailsButtonClicked'");
    target.btn_addvolunteers = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddVolunteerDetailsButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427519, "field 'btn_addcleanedarea' and method 'onAddCleanedAreaDetails'");
    target.btn_addcleanedarea = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddCleanedAreaDetails();
        }
      });
    view = finder.findRequiredView(source, 2131427520, "field 'btn_capturevideo' and method 'onCaptureVideo'");
    target.btn_capturevideo = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCaptureVideo();
        }
      });
    view = finder.findRequiredView(source, 2131427521, "field 'btn_addvillage' and method 'onAddVillageButtonClicked'");
    target.btn_addvillage = view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onAddVillageButtonClicked();
        }
      });
  }

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.fragments.Fragment_MainPage target) {
    target.btn_login = null;
    target.btn_adddrive = null;
    target.btn_addvolunteers = null;
    target.btn_addcleanedarea = null;
    target.btn_capturevideo = null;
    target.btn_addvillage = null;
  }
}
