package com.using.common.util;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liumh 20160915
 */
public class BeanCloneUtils {
	private BeanCloneUtils() {
		throw new Error("new obj fail");
	}

	/**
	 * persistent转换成dataTransfer
	 * 
	 * @param persistent
	 *            持久化类
	 * @param dataTransfer
	 *            数据传输类
	 * @throws Exception
	 */
	public static void toDto(Object persistent, Object dataTransfer) throws Exception {

		// 按照 key=key value=getKey形式存储所有 persistent的get方法
		Class<?> persistentClazz = persistent.getClass();
		Method persistentMethods[] = persistentClazz.getDeclaredMethods();
		Map<String, Object> map = new HashMap<>();
		int i = persistentMethods.length;
		for (int j = 0; j < i; j++) {

			Method persistentMethod = persistentMethods[j];
			String methodName = persistentMethod.getName();
			if (methodName.startsWith("get")) {
				String name = methodName.substring(3);
				map.put(name, persistentMethod);
			}
		}

		// 先读取data transfer object 子类，并进行反射
		Class<?> dataTransferClazz = dataTransfer.getClass();
		Method dataTransferMethods[] = dataTransferClazz.getDeclaredMethods();
		int k = dataTransferMethods.length;
		for (int l = 0; l < k; l++) {

			Method dataTransferMethod = dataTransferMethods[l];
			String methodName = dataTransferMethod.getName();

			if (!methodName.startsWith("set"))
				continue;
			String name = methodName.substring(3);
			if (!map.containsKey(name))
				continue;
			// 如果该方法为set方法，则取出方法进行反射
			Method m = (Method) map.get(name);
			try {
				Object o = m.invoke(persistent, new Object[0]);
				dataTransferMethod.invoke(dataTransfer, new Object[] { o });
			} catch (Exception e) {
				throw new Exception(String.format("Clone to data transfer object fail %s", e));
			}
		}

		// 再读取 data transfer object 父类，并进行反射
		Class<?> dataTransferSuperClass = dataTransferClazz.getSuperclass();
		if (null == dataTransferSuperClass)
			return;
		Method[] dataTransferSuperMethods = dataTransferSuperClass.getDeclaredMethods();
		int kr = dataTransferSuperMethods.length;
		for (int lr = 0; lr < kr; lr++) {
			Method method = dataTransferSuperMethods[lr];
			String methodName = method.getName();
			if (!methodName.startsWith("set"))
				continue;
			String name = methodName.substring(3);

			Method m = null;
			if (!map.containsKey(name))
				continue;
			m = (Method) map.get(name);
			try {
				Object obj = m.invoke(persistent, new Object[0]);
				method.invoke(dataTransfer, new Object[] { obj });
			} catch (Exception ex) {
				throw new Exception(String.format("Clone to data transfer object'super clazz fail %s", ex));
			}
		}

	}

	/**
	 * 将dataTransfer转换成persistent
	 * 
	 * @param dataTransfer
	 *            数据传输类
	 * @param persistent
	 *            数据持久化类
	 * @throws Exception
	 */
	public static void toPo(Object dataTransfer, Object persistent) throws Exception {

		// 按照 key=key value=getKey形式存储所有 dataTransfer的get方法
		Class<?> dataTransferClazz = dataTransfer.getClass();
		Method dataTransferMethods[] = dataTransferClazz.getDeclaredMethods();
		Map<String, Object> map = new HashMap<>();
		int i = dataTransferMethods.length;
		for (int j = 0; j < i; j++) {
			Method dataTransferMethod = dataTransferMethods[j];
			String methodName = dataTransferMethod.getName();
			if (methodName.startsWith("get")) {
				String name = methodName.substring(3);
				map.put(name, dataTransferMethod);
			}
		}

		// 超类 按照 key=key value=getKey形式存储所有 dataTransfer的get方法
		Class<?> dataTransferSuperClazz = dataTransferClazz.getSuperclass();
		Method[] dataTransferSuperMethods = dataTransferSuperClazz.getDeclaredMethods();
		Map<String, Object> superMap = new HashMap<>();
		int ir = dataTransferSuperMethods.length;
		for (int jr = 0; jr < ir; jr++) {

			Method dataTransferSuperMethod = dataTransferSuperMethods[jr];
			String methodName = dataTransferSuperMethod.getName();
			if (methodName.startsWith("get")) {
				String name = methodName.substring(3);
				superMap.put(name, dataTransferSuperMethod);
			}
		}

		// 进行反射赋值
		Class<?> persistentClazz = persistent.getClass();
		Method persistentMethods[] = persistentClazz.getDeclaredMethods();
		int l = persistentMethods.length;
		for (int k = 0; k < l; k++) {

			Method persistentMethod = persistentMethods[k];
			String methodName = persistentMethod.getName();
			if (!methodName.startsWith("set")) {
				continue;
			}
			String name = methodName.substring(3);

			Method m = null;
			// 先查询data transfer object中属性
			if (map.containsKey(name)) {
				m = (Method) map.get(name);
			}
			// 如果不存在则进行超类查询，并进行赋值
			else if (superMap.containsKey(name)) {
				m = (Method) superMap.get(name);
			} else {
				continue;
			}
			try {
				Object o = m.invoke(dataTransfer, new Object[0]);
				if (o != null) {
					persistentMethod.invoke(persistent, new Object[] { o });
				}
			} catch (Exception e) {
				throw new Exception(String.format("Clone persistent object fail %s", e));
			}
		}
	}

}