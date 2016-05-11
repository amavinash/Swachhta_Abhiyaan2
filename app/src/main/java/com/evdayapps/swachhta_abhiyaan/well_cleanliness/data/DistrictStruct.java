package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import android.content.ContentValues;
import android.database.Cursor;

public class DistrictStruct extends BaseRegionStruct {
	public long state_id;
	
	public DistrictStruct() {
		super();
	}
	
	public DistrictStruct(long id, String name, long state_id) {
		super(id, name);
		this.state_id = state_id;
	}
	
	public DistrictStruct(Cursor cursor) {
		super(cursor);
		this.state_id = cursor.getLong(2);	
	}
	
	public ContentValues getContentValues() {
		ContentValues content_val = super.getContentValues();
		content_val.put("state_id", state_id);
		return content_val;
	}
}
