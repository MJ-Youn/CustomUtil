package com.dev2.intern.util;

import java.util.UUID;

/**
 *
 * @author MJYoun
 * @since 2017. 03. 06.
 *
 */
public class UuidUtil {

	public static String createUuidWithoutHyphen() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	public static String createUuid() {
		return UUID.randomUUID().toString();
	}
}
