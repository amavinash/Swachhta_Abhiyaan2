package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import android.content.ContentValues;
import android.database.Cursor;

public class TalukaStruct extends BaseRegionStruct {
	public long district_id;
	
	public TalukaStruct() {
		super();
	}
	
	public TalukaStruct(long id, String name, long district_id) {
		super(id, name);
		this.district_id = district_id;
	}
	
	public TalukaStruct(Cursor cursor) {
		super(cursor);
		this.district_id = cursor.getLong(2);
	}
	
	public ContentValues getContentValues() {
		ContentValues content_val = super.getContentValues();
		content_val.put("district_id", district_id);
		return content_val;
	}
}
