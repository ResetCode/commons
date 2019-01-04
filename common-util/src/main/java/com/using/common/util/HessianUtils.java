package com.using.common.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.caucho.hessian.io.HessianInput;
import com.caucho.hessian.io.HessianOutput;

/**
 * @author liumh
 */
public class HessianUtils {
	public HessianUtils() {
		throw new Error("禁止实例化！");
	}

	public static byte[] serialize(Object target) throws IOException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		HessianOutput hot = new HessianOutput(os);
		hot.writeObject(target);
		byte[] bytes = os.toByteArray();
		hot.close();
		os.close();
		return bytes;
	}

	@SuppressWarnings("unchecked")
	public static <T> T deserialize(byte[] bytes, Class<T> clazz) throws IOException {
		ByteArrayInputStream is = new ByteArrayInputStream(bytes);
		HessianInput hit = new HessianInput(is);
		Object obj = hit.readObject(clazz);
		hit.close();
		is.close();
		return (T) obj;
	}
}