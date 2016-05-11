// Generated code from Butter Knife. Do not modify!
package com.evdayapps.swachhta_abhiyaan.well_cleanliness;

import android.view.View;
import butterknife.ButterKnife.Finder;

public class MainActivity$$ViewInjector {
  public static void inject(Finder finder, final com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity target, Object source) {
    View view;
    view = finder.findRequiredView(source, 2131427408, "field 'topbar'");
    target.topbar = view;
    view = finder.findRequiredView(source, 2131427409, "field 'tv_title'");
    target.tv_title = (android.widget.TextView) view;
    view = finder.findRequiredView(source, 2131427410, "field 'img_locationstatus' and method 'onLocationButtonClicked'");
    target.img_locationstatus = (android.widget.ImageView) view;
    view.setOnClickListener(
      new butterknife.internal.DebouncingOnClickListener() {
        @Override public void doClick(
          android.view.View p0
        ) {
          target.onLocationButtonClicked();
        }
      });
    view = finder.findRequiredView(source, 2131427411, "field 'img_banner'");
    target.img_banner = (android.widget.ImageView) view;
  }

  public static void reset(com.evdayapps.swachhta_abhiyaan.well_cleanliness.MainActivity target) {
    target.topbar = null;
    target.tv_title = null;
    target.img_locationstatus = null;
    target.img_banner = null;
  }
}
