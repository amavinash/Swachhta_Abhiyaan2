package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import android.content.ContentValues;
import android.database.Cursor;

public class StateStruct extends BaseRegionStruct {
	
	public StateStruct() {
		super();
	}
	
	public StateStruct(long id, String name) {
		super(id, name);
	}
	
	public StateStruct(Cursor cursor) {
		super(cursor);
	}
	
	public ContentValues getContentValues() {
		ContentValues content_val = super.getContentValues();
		return content_val;
	}
}
