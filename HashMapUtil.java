package com.dev2.intern.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	
	public static Object mapConvertToVO(Map<?, ?> srcMap, Class<?> targetClass) {
		Object targetObject = null;
		Field targetField = null;
		boolean targetAccessible = false;
		
		try {
			Constructor<?> cons = targetClass.getConstructor();
			targetObject = cons.newInstance();
			
			Set<?> keys = srcMap.keySet();
			Iterator<?> iterator = keys.iterator();
			
			Field[] fields = targetClass.getDeclaredFields();

			@SuppressWarnings("unchecked")
			List<String> fieldNames = (List<String>) Arrays.asList(fields)
															.stream()
															.map(field -> {
																return field.getName();
															});

			while (iterator.hasNext()) {
				Object keyName = iterator.next();
				Object value = srcMap.get(keyName);
				
				boolean checkIncludeField = fieldNames.contains(keyName.toString());
				
				if (checkIncludeField) {
					try {
						targetField = targetClass.getDeclaredField(keyName.toString());
						targetAccessible = targetField.isAccessible();
						
						targetField.setAccessible(true);
						
						if (targetField.getType() == int.class || targetField.getType() == Integer.class) {
							value = Integer.parseInt((String)value);
						} else if (targetField.getType() == Date.class) {
							value = new Date(Long.parseLong((String)value));
						}
						
						targetField.set(targetObject, value);
					} catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
						e.printStackTrace();
					} finally {
						if (targetField != null) {
							targetField.setAccessible(targetAccessible);
							targetField = null;
						}
					}
				}
			}
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		
		return targetObject;
	}
	
}
