package com.wen.commons.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 手机号码合法性检测
 * 
 * @author jaden.zhuo
 * @date 2016年1月6日
 */
public abstract class MobileUtils {
	// 带86的（移动电信、联通的号码）
	private static final String CheckMobileExt13 = "^8613[0-9]{9}$|^8614[57][0-9]{8}$|^8615[012356789][0-9]{8}$|^8618[0123456789][0-9]{8}$|^8617[0-9]{9}$";
	// 不带86的（移动电信、联通的号码）
	private static final String CheckMobileExt11 = "^13[0-9]{9}$|^14[57][0-9]{8}$|^15[012356789][0-9]{8}$|^18[0123456789][0-9]{8}$|^17[0-9]{9}$";

	private static Pattern mobileWith86Pattern = Pattern.compile(CheckMobileExt13);
	private static Pattern mobilePattern = Pattern.compile(CheckMobileExt11);

	/**
	 * 检测带86手机号码是否合法
	 * 
	 * @param mobile
	 *            手机号码
	 * @return true 合法 false 不合法
	 */
	public static boolean checkMobileWith86(String mobile) {
		boolean flag = checkMobileParam(mobile, 13);
		if (!flag) {
			return false;
		}

		Matcher m = mobileWith86Pattern.matcher(mobile);
		return m.find();
	}

	private static boolean checkMobileParam(String mobile, int len) {
		boolean flag = true;
		if (StringUtils.isEmpty(mobile)) {
			flag = false;
		} else if (mobile.length() > len) {
			flag = false;
		}
		return flag;
	}

	/**
	 * 检测手机号码是否合法（不带86）
	 * 
	 * @param mobile
	 *            手机号码
	 * @return true 合法 false 不合法
	 */
	public static boolean checkMobile(String mobile) {
		boolean flag = checkMobileParam(mobile, 11);
		if (!flag) {
			return false;
		}
		Matcher m = mobilePattern.matcher(mobile);
		return m.find();
	}

	/**
	 * 遮蔽手机号 150****4567
	 * 
	 * @param mobile
	 * @return
	 */
	public static String hiddenMobile(String mobile) {
		if (mobile == null || mobile.length() < 4) {
			return mobile;
		}

		int len = mobile.length();
		StringBuilder sb = new StringBuilder();
		sb.append(mobile.substring(0, 3)).append("****").append(mobile.substring(len - 4, len));
		return sb.toString();
	}
}
