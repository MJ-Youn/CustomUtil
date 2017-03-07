package net.lguplus.subwaywifi.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.NumberUtils;

public class DataUtil {

	private static boolean equals(Field src, Field target) {
		if( src == null || target == null) {
			return false;
		}
		return src.getName().equals(target.getName()) && src.getType().equals(target.getType());
	}

	/**
	 * Object의 데이터를 옮기기 위한 함수
	 * 
	 * @param srcObj
	 * 			소스 데이터
	 * @param targetObj
	 * 			결과 데이터
	 */
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

			} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
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
 
	/**
	 * 데이터를 다른 타입으로 변환하기 위한 함수
	 * 
	 * @param obj
	 * 			데이터
	 * @param genericType
	 * 			변환하기 위한 타입
	 * @return 변환한 데이터
	 */
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

	/**
	 * 맵 데이터를 Object로 저장하기 위한 함수
	 * 
	 * @param srcMap
	 * 			소스 데이터
	 * @param targetObject
	 * 			변환하기 위한 타입
	 */
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
							value = NumberUtils.parseNumber((String)value, Integer.class);
						} else if (targetField.getType() == long.class || targetField.getType() == Long.class) {
							value = NumberUtils.parseNumber((String)value, Long.class);
						} else if (targetField.getType() == float.class || targetField.getType() == Float.class) {
							value = NumberUtils.parseNumber((String)value, Float.class);
						} else if (targetField.getType() == boolean.class || targetField.getType() == Boolean.class) {
							value = BooleanUtils.toBoolean((String)value);
						} else if (targetField.getType() == Date.class) {
							value = new Date(NumberUtils.parseNumber((String)value, Long.class));
						} else {
							value = (String) value;
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

	/**
	 * 맵 데이터를 원하는 타입으로 변환하기 위한 함수
	 * 
	 * @param srcMap
	 * 			소스 맵 데이터
	 * @param genericType
	 * 			변환하려는 타입
	 * @return 변환한 데이터
	 */
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
