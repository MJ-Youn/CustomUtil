package net.lguplus.subwaywifi.util;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.NumberUtils;

public class DataUtil {

	/**
	 * 데이터를 다른 타입의 데이터로 변환하기 위한 함수
	 * ex> VO to DTO, DTO to VO
	 *
	 * @param object
	 *            데이터
	 * @param genericType
	 *            변환하기 위한 타입
	 * @return 변환한 데이터
	 */
	public static <T> T converterDataToData(Object object, Class<T> genericType) {
		T t = null;

		try {
			t = genericType.newInstance();
			converterDataToData(object, t);
		} catch (IllegalArgumentException | IllegalAccessException | InstantiationException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		}

		return t;
	}

	/**
	 * Object의 데이터를 옮기기 위한 함수
	 *
	 * @param srcObject
	 *            소스 데이터
	 * @param targetObject
	 *            결과 데이터
	 * @throws SecurityException
	 * @throws NoSuchFieldException
	 * @throws IllegalAccessException
	 * @throws IllegalArgumentException
	 */
	private static void converterDataToData(Object srcObject, final Object targetObject)
			throws NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		Class<?> srcClass = srcObject.getClass();
		Class<?> targetClass = targetObject.getClass();

		Field[] srcFields = srcClass.getDeclaredFields();
		Field[] targetFields = targetClass.getDeclaredFields();

		List<String> srcFieldNames = Arrays.asList(srcFields).stream().map(srcField -> (srcField.getName())).collect(Collectors.toList());
		List<String> targetFieldNames = Arrays.asList(targetFields).stream().map(targetField -> (targetField.getName())).collect(Collectors.toList());

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

					targetField.set(targetObject, srcField.get(srcObject));

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

		Field[] targetFields = targetClass.getDeclaredFields();

		List<String> targetFieldNames = Arrays.asList(targetFields).stream().map(field -> (field.getName())).collect(Collectors.toList());

		while (keyIterator.hasNext()) {
			Object keyName = keyIterator.next();
			Object value = srcMap.get(keyName);

			if (targetFieldNames.contains(keyName.toString()) == true) {
				targetField = targetClass.getDeclaredField(keyName.toString());
				targetAccessible = targetField.isAccessible();

				targetField.setAccessible(true);

				value = convertObjectType(value, targetField);

				targetField.set(targetObject, value);

				targetField.setAccessible(targetAccessible);
				targetField = null;
			}
		}
	}

	/**
	 * Field의 이름과 타입을 가지고 같은 Field인지 확인
	 *
	 * @param src
	 * @param target
	 * @return
	 */
	private static boolean equals(Field src, Field target) {
		if (src == null || target == null) {
			return false;
		}
		return src.getName().equals(target.getName()) && src.getType().equals(target.getType());
	}

	/**
	 * Object의 타입을 Field 타입에 맞게 변경
	 *
	 * @param value
	 * @param field
	 * @return
	 */
	private static Object convertObjectType(Object value, Field field) {
		if (value == null) {

		} else if (value.getClass() == BigDecimal.class) {
			if (field.getType() == int.class || field.getType() == Integer.class) {
				value = ((BigDecimal) value).intValue();
			} else if (field.getType() == long.class || field.getType() == Long.class) {
				value = ((BigDecimal) value).longValue();
			} else if (field.getType() == float.class || field.getType() == Float.class) {
				value = ((BigDecimal) value).floatValue();
			} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
				value = (((BigDecimal) value).intValue() == 1) ? true : false;
			} else if (field.getType() == Date.class) {
				value = new Date(((BigDecimal) value).longValue());
			} else {
				value = value.toString();
			}
		} else {
			if (field.getType() == int.class || field.getType() == Integer.class) {
				value = NumberUtils.parseNumber((String) value, Integer.class);
			} else if (field.getType() == long.class || field.getType() == Long.class) {
				value = NumberUtils.parseNumber((String) value, Long.class);
			} else if (field.getType() == float.class || field.getType() == Float.class) {
				value = NumberUtils.parseNumber((String) value, Float.class);
			} else if (field.getType() == boolean.class || field.getType() == Boolean.class) {
				value = BooleanUtils.toBoolean((String) value);
			} else if (field.getType() == Date.class) {
				value = (Date) value;
			} else {
				value = (String) value;
			}
		}

		return value;
	}

	/**
	 * DB 쿼리 결과로 가져온 ResultSet을 원하는 데이터 형식으로 변환하기 위한 함수
	 *
	 * @param rs
	 * 			쿼리 결과
	 * @param genericType
	 * 			변환하려는 데이터 타입
	 * @return 변환한 데이터
	 */
	public static <T> T convertResultSetToData(ResultSet rs, Class<T> genericType) {
		T t = null;

		try {
			t = genericType.newInstance();
			convertResultSetToData(rs, t);
		} catch (InstantiationException | IllegalAccessException | SQLException | NoSuchFieldException
				| SecurityException e) {
			e.printStackTrace();
		}

		return t;
	}

	/**
	 * ResultSet데이터를 원하는 Object에 넣기 위한 함수
	 *
	 * @param rs
	 * 			쿼리 결과
	 * @param targetObject
	 * 			값을 넣으려는 Object
	 * @throws SQLException
	 * @throws NoSuchFieldException
	 * @throws SecurityException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private static void convertResultSetToData(ResultSet rs, final Object targetObject) throws SQLException,
			NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnCount = rsmd.getColumnCount();
		String columnName;

		Class<?> targetClass = targetObject.getClass();
		Field[] targetFields = targetClass.getDeclaredFields();
		List<String> targetFieldNames = Arrays.asList(targetFields).stream().map(targetField -> (targetField.getName())).collect(Collectors.toList());

		Field targetField;
		boolean targetAccessible;

		Object targetData;

		for (int i = 1; i <= columnCount; i++) {
			columnName = StringUtils.lowerCase(rsmd.getColumnName(i));

			if (targetFieldNames.contains(columnName) == true) {
				targetField = targetClass.getDeclaredField(columnName);
				targetAccessible = targetField.isAccessible();

				targetData = convertObjectType(rs.getObject(columnName), targetField);

				if (targetData == null && isNotObject(targetField) == true) {
					// data가 null인데 field가 obejct가 아닐 경우 값을 넣지 않는다.
				} else {
					targetField.setAccessible(true);
					targetField.set(targetObject, targetData);

					targetField.setAccessible(targetAccessible);
					targetField = null;
				}
			}
		}
	}

	/**
	 * field가 object가 아닌 변수타입인지 확인하는 함수
	 *
	 * @param field
	 * 			대상 field
	 * @return object가 아니면 true, 맞으면 false
	 */
	private static boolean isNotObject(Field field) {
		return (field.getType() == long.class || field.getType() == int.class || field.getType() == float.class || field.getType() == boolean.class);
	}

}
