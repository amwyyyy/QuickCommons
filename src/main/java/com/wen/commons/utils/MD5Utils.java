package com.wen.commons.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 这个非常有用
 * 
 * @author smartlv
 */

public abstract class MD5Utils {
	private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

	/**
	 * 用MD5算法加密字节数组
	 * 
	 * @param bytes
	 *            要加密的字节
	 * @return byte[] 加密后的字节数组，若加密失败，则返回null
	 */
	public static byte[] encode(byte[] bytes) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			byte[] digesta = digest.digest();
			return digesta;
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 用MD5算法加密后再转换成BASE64编码的字符串
	 * 
	 * @param bytes
	 * @return String
	 */
	public static String encode2Base64(byte[] bytes) {
		return Base64.getEncoder().encodeToString(MD5Utils.encode(bytes));
	}

	/**
	 * 计算文件的md5
	 * 
	 * @param filePath
	 *            文件路径
	 * @return md5结果，若加密失败，则返回null
	 */
	public static byte[] encodeFile(String filePath) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] buffer = new byte[1024];
			byte[] digesta = null;
			int readed = -1;
			try (FileInputStream fis = new FileInputStream(filePath)) {
				while ((readed = fis.read(buffer)) != -1) {
					digest.update(buffer, 0, readed);
				}
				digesta = digest.digest();
			} catch (IOException e) {
				MD5Utils.logger.error("IOException:" + filePath, e);
			}
			return digesta;
		} catch (NoSuchAlgorithmException e) {
			MD5Utils.logger.error("NoSuchAlgorithmException:MD5", e);
			return null;
		}
	}

	/**
	 * 计算文件的md5,转换成Base64 string
	 * 
	 * @param filePath
	 *            文件路径
	 * @return md5结果，若加密失败，则返回null
	 */
	public static String encodeFile2Base64(String filePath) {
		byte[] bytes = MD5Utils.encodeFile(filePath);
		if (bytes == null) {
			return null;
		}
		return Base64.getEncoder().encodeToString(bytes);
	}

	public final static String encodeForWx(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
