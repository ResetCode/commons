package com.using.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * 序列化操作类
 * 
 * @author wangdx
 *
 */
public class SerializeUtils {
	/**
	 * 序列化
	 * 
	 * @param object
	 *            待序列号对象
	 * @throws Exception
	 */
	public static byte[] serialize(Object object) throws Exception {

		if (null == object)
			return null;

		ObjectOutputStream oos = null;

		ByteArrayOutputStream baos = null;

		try {

			baos = new ByteArrayOutputStream();

			oos = new ObjectOutputStream(baos);

			oos.writeObject(object);

			byte[] bytes = baos.toByteArray();

			return bytes;

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				oos.close();
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 反序列化
	 * 
	 * @param bytes
	 *            待反序列化字节数值
	 * @return
	 */
	public static Object unserialize(byte[] bytes) throws Exception {

		ByteArrayInputStream bais = null;

		if (null == bytes)
			return null;
		if (bytes.length == 1) {
			return new String(bytes);
		}
		try {
			bais = new ByteArrayInputStream(bytes);

			ObjectInputStream ois = new ObjectInputStream(bais);

			return ois.readObject();

		} catch (Exception e) {
			throw e;
		} finally {
			try {
				bais.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}