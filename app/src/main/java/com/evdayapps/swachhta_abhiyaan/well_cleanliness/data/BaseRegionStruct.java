package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import android.content.ContentValues;
import android.database.Cursor;

public class BaseRegionStruct {
	public long id;
	public String name;
	
	public BaseRegionStruct() {}
	
	public BaseRegionStruct(long id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public BaseRegionStruct(Cursor cursor) {
		this.id = cursor.getLong(0);
		this.name = cursor.getString(1);
	}
	
	public ContentValues getContentValues() {
		ContentValues content_val = new ContentValues();
		content_val.put("id", id);
		content_val.put("name", name);
		return content_val;
	}
}
