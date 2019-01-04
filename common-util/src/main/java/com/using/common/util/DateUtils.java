package com.using.common.util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期帮助类
 * 
 * @author liumh
 *
 */
public class DateUtils {
	/**
	 * 获取day天后的日期
	 * 
	 * @param day
	 *            正数为当前日期之后
	 * @return
	 */
	public static String getDayDateValue(int day) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(getDayDate(day));
	}

	/**
	 * 获取day天后的日期
	 * 
	 * @param day
	 *            正数为当前日期之后
	 * @return
	 */
	public static Date getDayDate(int day) {
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, day);
		return c.getTime();
	}

	/**
	 * 获取month月后的日期
	 * 
	 * @param f
	 *            是否从1号开始计算
	 * @return
	 */
	public static String getMonthDateValue(int month, boolean f) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		return sdf.format(getMonthDate(month, f));

	}

	/**
	 * 获取month月后的日期
	 * 
	 * @param month
	 *            正数为当月之后
	 * @param f
	 *            是否从月初开始计算
	 * @return
	 */
	public static Date getMonthDate(int month, boolean f) {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.MONTH, month);
		if (f) {
			c.set(Calendar.DAY_OF_MONTH, 1);
		}

		return c.getTime();

	}

	/**
	 * 获取week周后的日期
	 * 
	 * @param week
	 *            正数为当前周之后
	 * @param f
	 *            是否从周一开始计算
	 * @return
	 */
	public static Date getWeekDate(int week, boolean f) {

		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.setFirstDayOfWeek(Calendar.MONDAY);
		c.add(Calendar.WEEK_OF_MONTH, week);
		if (f) {
			c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek());
		}
		return c.getTime();
	}

	/**
	 * 获取week周后的日期
	 * 
	 * @param week
	 *            正数为当前周之后
	 * @param f
	 *            是否从周一开始计算
	 * @return
	 */
	public static String getWeekDateValue(int week, boolean f) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(getWeekDate(week, f));
	}

	/**
	 * 获取开始和结束时间中间 所有的天数的日期
	 * 
	 * @param start
	 *            开始日期
	 * @param end
	 *            结束日期
	 * @param calendarType
	 * @return
	 */
	public static Date[] getDateArrays(Date start, Date end, int calendarType) {
		ArrayList<Date> ret = new ArrayList<Date>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(start);
		Date tmpDate = calendar.getTime();
		long endTime = end.getTime();
		while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
			ret.add(calendar.getTime());
			calendar.add(calendarType, 1);
			tmpDate = calendar.getTime();
		}
		Date[] dates = new Date[ret.size()];
		return (Date[]) ret.toArray(dates);
	}
}