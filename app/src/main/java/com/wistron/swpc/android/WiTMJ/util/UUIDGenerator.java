package com.wistron.swpc.android.WiTMJ.util;

import java.util.UUID;

public class UUIDGenerator {

	public static String getUUID(){
		String str_UUID = "";
		UUID uuid = UUID.randomUUID();
		str_UUID = uuid.toString().replace("-", "");
		return str_UUID;
	}
	
}
