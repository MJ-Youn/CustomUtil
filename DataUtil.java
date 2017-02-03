package com.dev2.intern.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DataUtil {

	private static boolean equals(Field src, Field target) {
		if( src == null || target == null) {
			return false;
		}
		return src.getName().equals(target.getName()) && src.getType().equals(target.getType());
	}

	public static void converterData(Object srcObj, final Object targetObj) {

		Class<?> srcClass = srcObj.getClass();
		Class<?> targetClass = targetObj.getClass();

		boolean srcAccessible = false;
		boolean targetAccessible = false;
		Field srcField = null;

		boolean targetChanged = false;

		for (Field targetField : targetClass.getDeclaredFields()) {
			try {
				srcField = srcClass.getDeclaredField(targetField.getName());
				srcAccessible = srcField.isAccessible();
				if( !equals(srcField, targetField)){
					continue;
				}

				targetAccessible = targetField.isAccessible();
				targetChanged = true;

				srcField.setAccessible(true);
				targetField.setAccessible(true);

				targetField.set(targetObj, srcField.get(srcObj));

			} catch (NoSuchFieldException | SecurityException //
					| IllegalArgumentException | IllegalAccessException e) {
//				e.printStackTrace();
			} finally {
				if (srcField != null) {
					srcField.setAccessible(srcAccessible);

					srcField = null;
				}

				if( targetChanged ) {
					targetField.setAccessible(targetAccessible);

					targetChanged = false;
				}
			}
		}
	}

	public static <T> T converterData(Object obj, Class<T> genericType) {
		T t = null;
		try {
			t = genericType.newInstance();
			converterData(obj, t);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}

	public static void mapConvertToDataObject(Map<?, ?> srcMap, Object targetObject) {
		Field targetField = null;
		boolean targetAccessible = false;

		try {
			Set<?> keys = srcMap.keySet();
			Iterator<?> keyIterator = keys.iterator();
			Class<?> targetClass = targetObject.getClass();

			Field[] fields = targetClass.getDeclaredFields();

			@SuppressWarnings("unchecked")
			List<String> fieldNames = (List<String>) Arrays.asList(fields)
															.stream()
															.map(field -> {
																return field.getName();
															});

			while (keyIterator.hasNext()) {
				Object keyName = keyIterator.next();
				Object value = srcMap.get(keyName);

				boolean checkIncludeField = fieldNames.contains(keyName.toString());

				if (checkIncludeField) {
					try {
						targetField = targetClass.getDeclaredField(keyName.toString());
						targetAccessible = targetField.isAccessible();

						targetField.setAccessible(true);

						if (targetField.getType() == int.class || targetField.getType() == Integer.class) {
							value = Integer.parseInt((String)value);
						} else if (targetField.getType() == long.class || targetField.getType() == Long.class) {
							value = Long.parseLong((String)value);
						} else if (targetField.getType() == float.class || targetField.getType() == Float.class) {
							value = Float.parseFloat((String)value);
						} else if (targetField.getType() == boolean.class || targetField.getType() == Boolean.class) {
							value = Boolean.parseBoolean((String)value);
						} else if (targetField.getType() == Date.class) {
							value = new Date(Long.parseLong((String)value));
						} else {
              value = (String)value;
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
		} catch (IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
	}

	public static <T> T mapConvertToDataObject(Map<?, ?> srcMap, Class<T> genericType) {
		T t = null;

		try {
			t = genericType.newInstance();
			mapConvertToDataObject(srcMap, t);
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}

		return t;
	}

}
