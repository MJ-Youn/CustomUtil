package net.lguplus.subwaywifi.util;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.springframework.util.NumberUtils;

public class DataUtil {

	/**
	 * 데이터를 다른 타입의 데이터로 변환하기 위한 함수
	 * ex> VO to DTO, DTO to VO
	 *
	 * @param obj
	 *            데이터
	 * @param genericType
	 *            변환하기 위한 타입
	 * @return 변환한 데이터
	 */
	public static <T> T converterDataToData(Object obj, Class<T> genericType) {
		T t = null;

		try {
			t = genericType.newInstance();
			converterDataToData(obj, t);
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		}

		return t;
	}

	/**
	 * Object의 데이터를 옮기기 위한 함수
	 *
	 * @param srcObj
	 *            소스 데이터
	 * @param targetObj
	 *            결과 데이터
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static void converterDataToData(Object srcObj, final Object targetObj)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> srcClass = srcObj.getClass();
		Class<?> targetClass = targetObj.getClass();

		Field[] srcFields = srcClass.getDeclaredFields();
		Field[] targetFields = targetClass.getDeclaredFields();

		List<String> srcFieldNames = Arrays.asList(srcFields).stream().map(srcField -> (srcField.getName())).collect(Collectors.toList());
		List<String> targetFieldNames = (List<String>) Arrays.asList(targetFields).stream().map(targetField -> (targetField.getName())).collect(Collectors.toList());

		Field srcField;
		boolean srcAccessible;
		Field targetField;
		boolean targetAccessible;

		for (String srcFieldName : srcFieldNames) {
			if (targetFieldNames.contains(srcFieldName) == true) {
				srcField = srcClass.getDeclaredField(srcFieldName);
				srcAccessible = srcField.isAccessible();

				targetField = targetClass.getDeclaredField(srcFieldName);
				targetAccessible = targetField.isAccessible();

				if (equals(srcField, targetField) == false) {
					continue;
				} else {
					srcField.setAccessible(true);
					targetField.setAccessible(true);

					targetField.set(targetObj, srcField.get(srcObj));

					srcField.setAccessible(srcAccessible);
					targetField.setAccessible(targetAccessible);

					srcField = null;
					targetField = null;
				}
			}
		}
	}

	/**
	 * 맵 데이터를 원하는 타입으로 변환하기 위한 함수
	 *
	 * @param srcMap
	 *            소스 맵 데이터
	 * @param genericType
	 *            변환하려는 타입
	 * @return 변환한 데이터
	 */
	public static <T> T convertMapToData(Map<?, ?> srcMap, Class<T> genericType) {
		T t = null;

		try {
			t = genericType.newInstance();
			convertMapToData(srcMap, t);
		} catch (InstantiationException | IllegalAccessException | NoSuchFieldException | SecurityException
				| IllegalArgumentException e) {
			e.printStackTrace();
		}

		return t;
	}

	/**
	 * 맵 데이터를 Object로 저장하기 위한 함수
	 *
	 * @param srcMap
	 *            소스 데이터
	 * @param targetObject
	 *            변환하기 위한 타입
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static void convertMapToData(Map<?, ?> srcMap, Object targetObject)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Field targetField = null;
		boolean targetAccessible = false;

		Set<?> keys = srcMap.keySet();
		Iterator<?> keyIterator = keys.iterator();
		Class<?> targetClass = targetObject.getClass();

		Field[] fields = targetClass.getDeclaredFields();

		List<String> fieldNames = (List<String>) Arrays.asList(fields).stream().map(field -> (field.getName())).collect(Collectors.toList());

		while (keyIterator.hasNext()) {
			Object keyName = keyIterator.next();
			Object value = srcMap.get(keyName);

			if (fieldNames.contains(keyName.toString()) == true) {
				targetField = targetClass.getDeclaredField(keyName.toString());
				targetAccessible = targetField.isAccessible();

				targetField.setAccessible(true);

				value = convertObject(value, targetField);

				targetField.set(targetObject, value);

				targetField.setAccessible(targetAccessible);
				targetField = null;
			}
		}
	}

	private static boolean equals(Field src, Field target) {
		if (src == null || target == null) {
			return false;
		}
		return src.getName().equals(target.getName()) && src.getType().equals(target.getType());
	}

	private static Object convertObject(Object value, Field field) {
		if (field.getType() == int.class || field.getType() == Integer.class) {
			value = NumberUtils.parseNumber((String) value, Integer.class);
		} else if (field.getType() == long.class || field.getType() == Long.class) {
			value = NumberUtils.parseNumber((String) value, Long.class);
		} else if (field.getType() == float.class || field.getType() == Float.class) {
			value = NumberUtils.parseNumber((String) value, Float.class);
		} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
			value = BooleanUtils.toBoolean((String) value);
		} else if (field.getType() == Date.class) {
			value = new Date(NumberUtils.parseNumber((String) value, Long.class));
		} else {
			value = (String) value;
		}

		return value;
	}
}
