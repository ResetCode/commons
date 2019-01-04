package com.using.common.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 实体构造类
 * 
 * @author liumohan
 */
public class BeanUtils {
	/**
	 * 
	 * @param resultType
	 * @param param
	 * @return
	 * @throws Exception
	 */
	public static <T> T instance(Class<T> resultType, Object... param) throws Exception {
		if (param.length == 0 || param.length % 2 != 0) {
			return null;
		}

		T result = null;
		try {
			Class<?> cls = Class.forName(resultType.getName());
			result = resultType.newInstance();
			for (int i = 0; i < param.length; i++) {
				String key = param[i].toString();
				Object value = param[++i];

				Field field = cls.getDeclaredField(key);
				Class<?> fieldType = field.getType();
				if (!value.getClass().equals(fieldType)) {
					throw new Exception("column [" + key + "] type error," + value.getClass().getName() + " can't cast "
							+ fieldType.getName());
				}
				StringBuffer beanFieldName = new StringBuffer(key);
				beanFieldName.setCharAt(0, Character.toUpperCase(key.charAt(0)));

				Method method = cls.getDeclaredMethod("set" + beanFieldName.toString(), fieldType);
				method.invoke(result, value);
			}
		} catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchFieldException
				| SecurityException | NoSuchMethodException | IllegalArgumentException | InvocationTargetException e) {
			throw e;
		}
		return result;
	}

	/**
	 * 获取对象的指定属性值 必须为无参Get方法
	 * 
	 * @param obj
	 *            目标对象
	 * @param fieldName
	 *            目标属性名
	 * @return
	 * @throws Exception
	 */
	public static Object getFieldValue(Object obj, String fieldName) throws Exception {

		if (obj == null || fieldName == null || fieldName.length() == 0) {
			throw new Exception("方法参数有误！");
		}

		Class<?> cls = obj.getClass();
		StringBuffer beanFieldName = new StringBuffer(fieldName);
		beanFieldName.setCharAt(0, Character.toUpperCase(fieldName.charAt(0)));
		Method method;
		try {
			method = cls.getDeclaredMethod("get" + beanFieldName.toString());
			return method.invoke(obj);
		} catch (NoSuchMethodException e) {
			Class<?> superCls = cls.getSuperclass();
			method = superCls.getDeclaredMethod("get" + beanFieldName.toString());
			return method.invoke(obj);
		} catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			throw e;
		}
	}
}