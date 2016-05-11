// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class PasswordDialog$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.PasswordDialog target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427536, "field 'edt_username'");
    target.edt_username = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427538, "field 'edt_password'");
    target.edt_password = (android.widget.EditText) view;
    view = finder.findRequiredView(source, 2131427540, "method 'onSubmitButtonPressed'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onSubmitButtonPressed();
        }
      });
    view = finder.findRequiredView(source, 2131427541, "method 'onCancelButtonPressed'");
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onCancelButtonPressed();
        }
      });
  }

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.dialogs.PasswordDialog target) {
    target.edt_username = null;
    target.edt_password = null;
  }
}
