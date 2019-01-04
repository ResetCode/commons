package com.using.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeFilter;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.serializer.SimplePropertyPreFilter;

/**
 * @author liumh
 *
 */
public class FastJsonUtils {
	private static Logger logger = LoggerFactory.getLogger(FastJsonUtils.class);

	public static final SerializerFeature[] features;

	static {
		features = new SerializerFeature[] {
				SerializerFeature.WriteMapNullValue,
				SerializerFeature.WriteNullStringAsEmpty,
				SerializerFeature.WriteNullBooleanAsFalse,
				SerializerFeature.WriteNullListAsEmpty,
				SerializerFeature.WriteNullNumberAsZero };
	}

	public static SerializeFilter includeFilter(Class<?> clazz,
			String... fields) {
		SimplePropertyPreFilter includeFilter = new SimplePropertyPreFilter(
				clazz);
		try {
			for (String field : fields) {
				includeFilter.getIncludes().add(field);
			}
		} catch (Exception e) {
			logger.error(
					"Generate SerializeFilter fail! params is {},reason is {}",
					JSON.toJSONString(fields), e.getMessage());
		}
		return includeFilter;
	}

	public static SerializeFilter excludeFilter(Class<?> clazz,
			String... fields) {
		SimplePropertyPreFilter excludeFilter = new SimplePropertyPreFilter(
				clazz);
		try {
			for (String field : fields) {
				excludeFilter.getExcludes().add(field);
			}
		} catch (Exception e) {
			logger.error(
					"Generate SerializeFilter fail! params is {},reason is {}",
					JSON.toJSONString(fields), e);
		}
		return excludeFilter;
	}
	
}
