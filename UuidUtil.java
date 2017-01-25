package com.dev2.intern.util;

import java.util.UUID;

public class UuidUtil {

	public static String createUuidWithoutHyphen() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}
	
	public static String createUuid() {
		return UUID.randomUUID().toString();
	}
}
