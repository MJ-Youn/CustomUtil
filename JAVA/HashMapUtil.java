package com.dev2.intern.util;

import java.util.HashMap;
import java.util.Map;

public class HashMapUtil {
	public static Map<Object, Object> createHashMap() {
		return new HashMap<Object, Object>();
	}

	public static Map<Object, Object> createHashMap(Object key, Object value) {
		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(key, value);

		return map;
	}

	public static void addMap(Map<Object, Object> map, Object key, Object value) {
		map.put(key, value);
	}
}
