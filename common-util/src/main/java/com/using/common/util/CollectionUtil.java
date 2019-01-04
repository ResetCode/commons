package com.using.common.util;

import java.util.Collection;

/**
 * 请使用org.apache.commons.collections4.CollectionUtils.isEmpty
 * @author Admin
 *
 */
@Deprecated
public class CollectionUtil {
	public static boolean isBlank(Collection<?> collection) {
		return collection == null || collection.isEmpty();
	}

	public static boolean isNotBlank(Collection<?> collection) {
		return !isBlank(collection);
	}
}
