package com.using.common.util;

import java.math.BigDecimal;

public class ArithUtils {
	private static final int DEF_DIV_SCALE = 10;

	private ArithUtils() {
	}

	/**
	 * 提供精确的加法运算。
	 * 
	 * @param array
	 *            加数
	 * @return 加数的和
	 */
	public static BigDecimal add(Object... array) {
		BigDecimal result = new BigDecimal(0);
		for (Object arr : array) {
			BigDecimal b = new BigDecimal(String.valueOf(arr));
			result = result.add(b);
		}
		return result;
	}

	/**
	 * 提供精确的减法运算。
	 * 
	 * @param f1
	 *            被减数
	 * @param array
	 *            减数集合
	 * @return 参数的差
	 */
	public static BigDecimal sub(Double f1, Object... array) {
		BigDecimal max = new BigDecimal(Double.toString(f1));
		for (Object arr : array) {
			BigDecimal b = new BigDecimal(String.valueOf(arr));
			max = max.subtract(b);
		}
		return max;
	}

	/**
	 * 提供精确的乘法运算。
	 * 
	 * @param array
	 *            乘数
	 * @return 乘数的和
	 */
	public static BigDecimal mul(Object... array) {

		BigDecimal result = new BigDecimal(1);
		for (Object arr : array) {
			BigDecimal b = new BigDecimal(String.valueOf(arr));
			result = result.multiply(b);
		}
		return result;

	}

	/**
	 * 提供（相对）精确的除法运算，当发生除不尽的情况时，精确到 小数点以后10位，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @return 两个参数的商
	 */
	public static BigDecimal div(Double f1, Double f2) {
		return div(f1, f2, DEF_DIV_SCALE);
	}

	/**
	 * 提供（相对）精确的除法运算。当发生除不尽的情况时，由scale参数指 定精度，以后的数字四舍五入。
	 * 
	 * @param v1
	 *            被除数
	 * @param v2
	 *            除数
	 * @param scale
	 *            表示表示需要精确到小数点以后几位。
	 * @return 两个参数的商
	 */
	public static BigDecimal div(Double d1, Double d2, int scale) {
		if (scale < 0) {
			throw new IllegalArgumentException("The scale must be a positive integer or zero");
		}
		BigDecimal b1 = new BigDecimal(Double.toString(d1));
		BigDecimal b2 = new BigDecimal(Double.toString(d2));
		return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP);

	}

}
