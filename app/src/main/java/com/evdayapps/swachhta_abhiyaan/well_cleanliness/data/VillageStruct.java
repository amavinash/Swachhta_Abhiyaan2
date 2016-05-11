package com.evdayapps.swachhta_abhiyaan.well_cleanliness.data;

import android.content.ContentValues;
import android.database.Cursor;

public class VillageStruct extends BaseRegionStruct{
	public long taluka_id;
	
	public VillageStruct() {
		super();
	}

	public VillageStruct(long id, String name, long taluka_id) {
		super(id, name);
		this.taluka_id = taluka_id;
	}

	public VillageStruct(Cursor cursor) {
		super(cursor);
		this.taluka_id = cursor.getLong(2);
	}
	
	public ContentValues getContentValues() {
		ContentValues content_val = super.getContentValues();
		content_val.put("taluka_id", taluka_id);
		return content_val;
	}
}
