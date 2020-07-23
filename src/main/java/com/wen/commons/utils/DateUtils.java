package com.wen.commons.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * 日期时间工具类
 */
public abstract class DateUtils {

	/** 默认的日期格式化器，格式为yyyy-MM-dd */
	public static final ThreadLocal<SimpleDateFormat> DEFAULT_DATE_FORMATER = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd"));
	/** 默认的时间格式化器，格式为yyyy-MM-dd HH:mm:ss */
	public static final ThreadLocal<SimpleDateFormat> DEFAULT_DATETIME_FORMATER = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));

	public static final ThreadLocal<SimpleDateFormat> YYYYMMDDHHMMSS = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMddHHmmss"));

	public static final ThreadLocal<SimpleDateFormat> YYYYMMDD = ThreadLocal.withInitial(() -> new SimpleDateFormat("yyyyMMdd"));

	/**
	 * 日期加年,可以向前加，向后加
	 * 
	 * @param date
	 *            日期
	 * @param year
	 *            年份
	 * @return 返回相加后的日期
	 */
	public static Date addYear(Date date, int year) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(Calendar.YEAR, year);
		c.add(Calendar.DATE, 1);
		return c.getTime();
	}

	/**
	 * 返回日期代表的毫秒
	 * 
	 * @param date
	 *            日期
	 * @return 返回毫秒
	 */
	public static long getMillis(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c.getTimeInMillis();
	}

	/**
	 * 获取日期 (时间置空)
	 * 
	 * @return
	 */
	public static Date getAsDate() {
		return getAsDate(null);
	}

	/**
	 * 获取日期 (时间置空)
	 * 
	 * @param now
	 * @return
	 */
	public static Date getAsDate(Date now) {
		Calendar cal = Calendar.getInstance();
		if (now != null) {
			cal.setTime(now);
		}

		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);

		return cal.getTime();
	}

	/**
	 * 取得当前日期（只有日期，没有时间，或者可以说是时间为0点0分0秒）
	 * 
	 * @return
	 * @throws ParseException
	 */
	public static Date getCurrentDate() {
		Date date = new Date();
		return parseDate(formatDate(date));
	}

	/**
	 * 用默认的日期格式，格式化一个Date对象
	 * 
	 * @param date
	 *            待被格式化日期
	 * @return “yyyy-MM-dd”格式的日期字符串
	 */
	public static String formatDate(Date date) {
		return date == null ? "" : DateUtils.DEFAULT_DATE_FORMATER.get().format(date);
	}

	/**
	 * 根据传入的格式，将日期对象格式化为日期字符串
	 * 
	 * @param date
	 *            待被格式化日期
	 * @param format
	 *            自定义日期格式器
	 * @return 格式后的日期字符串
	 */
	public static String formatDate(Date date, String format) {
		String s = "";

		if (date != null) {
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			s = sdf.format(date);
		}

		return s;
	}

	/**
	 * 用默认的日期时间格式，格式化一个Date对象
	 * 
	 * @param date
	 *            待被格式化日期
	 * @return “yyyy-MM-dd HH:mm:ss”格式的日期时间字符串
	 */
	public static String formatTime(Date date) {
		return date == null ? "" : DateUtils.DEFAULT_DATETIME_FORMATER.get().format(date);
	}

	/**
	 * 获取指定天数后的日期
	 * 
	 * @param baseDate
	 *            基准日期
	 * @param day
	 *            后推天数
	 * @return 后推后的天数
	 */
	public static Date getDateAfter(Date baseDate, int day) {
		Calendar now = Calendar.getInstance();
		now.setTime(baseDate);
		now.set(Calendar.DATE, now.get(Calendar.DATE) + day);
		return now.getTime();
	}

	/**
	 * 利用默认的格式（yyyy-MM-dd）将一个表示日期的字符串解析为日期对象
	 * 
	 * @param dateStr
	 *            待格式化日期字符串
	 * @return 格式化后日期对象
	 * @throws RuntimeException
	 */
	public static Date parseDate(String dateStr) {
		Date date;
		try {
			date = DateUtils.DEFAULT_DATE_FORMATER.get().parse(dateStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return date;
	}

	/**
	 * 利用默认的格式（yyyy-MM-dd HH:mm:ss）将一个表示时间的字符串解析为日期对象
	 * 
	 * @param timeStr
	 *            时间字符串
	 * @return 格式化后的日期对象
	 * @throws ParseException
	 */
	public static Date parseTime(String timeStr) {
		try {
			return DateUtils.DEFAULT_DATETIME_FORMATER.get().parse(timeStr);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将一个字符串，按照特定格式，解析为日期对象
	 * 
	 * @param datetimeStr
	 *            日期、时间、日期时间字符串
	 * @param format
	 *            自定义日期格式器
	 * @return 格式化后的日期对象
	 * @throws ParseException
	 */
	public static Date parseDateTime(String datetimeStr, String format) {
		Date date;
		try {
			date = new SimpleDateFormat(format).parse(datetimeStr);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}

		return date;
	}

	/**
	 * 得到当前年份
	 * 
	 * @return 当前年份
	 */
	public static int getCurrentYear() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.YEAR);
	}

	/**
	 * 得到当前月份（1至12）
	 * 
	 * @return 当前月份（1至12）
	 */
	public static int getCurrentMonth() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.MONTH) + 1;
	}

	/**
	 * 获取指定格式的当前系统时间
	 * 
	 * @param format
	 *            自定义日期格式器
	 * @return 当前系统时间
	 */
	public static String getCurrentTimeAsString(String format) {
		SimpleDateFormat t = new SimpleDateFormat(format);
		return t.format(new Date());
	}

	/**
	 * 获取当前为星期几,从星期日~星期六对应的值是1~7
	 * 
	 * @return 星期几
	 * @date: 2013年12月31日下午3:35:08
	 */
	public static int getDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取指定日期为星期几,从星期日~星期六对应的值是1~7
	 * 
	 * @param date
	 *            指定日期
	 * @return 星期几
	 * @date: 2013年12月31日下午3:45:35
	 */
	public static int getDayOfWeek(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取星期几的中文名称
	 * 
	 * @return 星期几
	 */
	public static String getChineseDayOfWeek() {
		Calendar cal = Calendar.getInstance();
		return DateUtils.getChineseDayOfWeek(cal.getTime());
	}

	/**
	 * 获取星期几的中文名称
	 * 
	 * @param date
	 *            指定日期
	 * @return 星期几
	 */
	public static String getChineseDayOfWeek(String date) {
		return DateUtils.getChineseDayOfWeek(DateUtils.parseDate(date));
	}

	/**
	 * 获取星期几的中文名称
	 * 
	 * @param date
	 *            指定日期
	 * @return 星期几
	 */
	public static String getChineseDayOfWeek(Date date) {
		int dateOfWeek = DateUtils.getDayOfWeek(date);
		if (dateOfWeek == Calendar.MONDAY) {
			return "星期一";
		} else if (dateOfWeek == Calendar.TUESDAY) {
			return "星期二";
		} else if (dateOfWeek == Calendar.WEDNESDAY) {
			return "星期三";
		} else if (dateOfWeek == Calendar.THURSDAY) {
			return "星期四";
		} else if (dateOfWeek == Calendar.FRIDAY) {
			return "星期五";
		} else if (dateOfWeek == Calendar.SATURDAY) {
			return "星期六";
		} else if (dateOfWeek == Calendar.SUNDAY) {
			return "星期日";
		}
		return null;
	}

	/**
	 * 获取当天为几号
	 * 
	 * @return 几号
	 */
	public static int getDayOfMonth() {
		LocalDate date = LocalDate.now();
		return date.getDayOfMonth();
	}

	/**
	 * 获取指定日期为几号
	 * 
	 * @param date
	 *            指定的日期
	 * @return 几号
	 */
	public static int getDayOfMonth(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zone);
		return dateTime.toLocalDate().getDayOfMonth();
	}

	/**
	 * 获取指定日期所在月份的最后一天是几号
	 * 
	 * @param date
	 *            指定日期
	 * @return 指定日期所在月份的最后一天是几号
	 */
	public static int getLastDayOfMonth(Date date) {
		Instant instant = date.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime dateTime = LocalDateTime.ofInstant(instant, zone);
		return dateTime.toLocalDate().lengthOfMonth();
	}

	/**
	 * 获取当前月份的最后一天
	 * 
	 * @return
	 */
	public static Date getCurrentMonthLastDay() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, -1);
		return calendar.getTime();
	}

	/**
	 * 获取指定日期为星期几,从星期日~星期六对应的值是1~7
	 * 
	 * @param date
	 *            指定日期
	 * @return 星期几
	 * @date: 2013年12月31日下午3:45:35
	 */
	public static int getDayOfWeek(String date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtils.parseDate(date));
		return cal.get(Calendar.DAY_OF_WEEK);
	}

	/**
	 * 获取当前月份的第一天
	 */
	public static Date getCurrentMonthFirstDay() {
		Calendar calendar = new GregorianCalendar();
		calendar.set(Calendar.DATE, 1);
		return calendar.getTime();
	}

	/**
	 * 获取指定月份的第一天(如:2015-06-01 00:00:00)
	 * 
	 * @param month
	 * @return
	 */
	public static Date getFirstDayByMonth(Date month) {
		Calendar cale = Calendar.getInstance();
		cale.setTime(month);
		cale.set(Calendar.DAY_OF_MONTH, 1);// 设置为1号,当前日期既为本月第一天
		return cale.getTime();
	}

	/**
	 * 获取指定月份的最后一天(如:2015-06-30 23:59:59)
	 * 
	 * @param month
	 * @return
	 */
	public static Date getLastDayByMonth(Date month) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(month);
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.SECOND, 59);

		return calendar.getTime();
	}

	/**
	 * 获取指定日期的开始时间<br/>
	 * 例如: 2015-03-23 00:00:00
	 * 
	 * @param day
	 * @return
	 */
	public static Date getFirstTimeByDay(Date day) {
		Instant instant = day.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime dt = LocalDateTime.ofInstant(instant, zone);
		return Date.from(dt.toLocalDate().atStartOfDay(zone).toInstant());
	}

	/**
	 * 获取指定日期的结束时间<br/>
	 * 例如： 2015-03-23 23:59:59
	 * 
	 * @param day
	 * @return
	 */
	public static Date getLastTimeByDay(Date day) {
		Instant instant = day.toInstant();
		ZoneId zone = ZoneId.systemDefault();
		LocalDateTime dt = LocalDateTime.ofInstant(instant, zone);

		LocalTime time = LocalTime.of(23, 59, 59);

		return Date.from(LocalDateTime.of(dt.toLocalDate(), time).atZone(zone).toInstant());
	}
}
