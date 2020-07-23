package com.wen.commons.utils;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.redisson.api.RBatch;
import org.redisson.api.RBucket;
import org.redisson.api.RObjectAsync;
import org.redisson.api.RedissonClient;

import com.wen.commons.spring.WebContextUtil;

/**
 * 缓存工具
 * 
 * @author denis.huang<br/>
 *         2017年2月16日
 */
public abstract class CacheUtils {

	/** redisson客户端实现，有很多高级功能，如分布式锁 */
	private static volatile RedissonClient redisson; // double check 必须定义为volatile

	private static RedissonClient getRedisson() {
		if (redisson == null) {
			synchronized (CacheUtils.class) {
				if (redisson == null) {
					redisson = WebContextUtil.getBean(RedissonClient.class);
				}
			}
		}
		return redisson;
	}

	/**
	 * 保存对象到缓存
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, Object value) {
		getRedisson().getBucket(key).setAsync(value);
	}

	/**
	 * 保存对象到缓存，设置过期时间
	 * 
	 * @param key
	 * @param value
	 */
	public static void set(String key, Object value, long timeout, TimeUnit unit) {
		getRedisson().getBucket(key).setAsync(value, timeout, unit);
	}

	/**
	 * 获取缓存对象
	 * 
	 * @param key
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(String key) {
		return (T) getRedisson().getBucket(key).get();
	}

	/**
	 * 删除缓存
	 * 
	 * @param key
	 */
	public static void del(String key) {
		getRedisson().getBucket(key).deleteAsync();
	}

	/**
	 * 删除匹配的key
	 * 
	 * @param pattern
	 */
	public static void delForPattern(String pattern) {
		getRedisson().getBuckets().find(pattern).forEach(RObjectAsync::deleteAsync);
	}

	/**
	 * 删除一组缓存
	 * 
	 * @param keys
	 */
	public static void del(Set<String> keys) {
		RBatch batch = getRedisson().createBatch();

		keys.forEach(key -> batch.getBucket(key).deleteAsync());

		batch.executeSkipResultAsync();
	}

	/**
	 * 获取匹配的key
	 * 
	 * @param pattern
	 * @return
	 */
	public static Collection<String> keys(String pattern) {
		return getRedisson().getKeys().findKeysByPattern(pattern);
	}

	/**
	 * 获取匹配的值
	 * 
	 * @param pattern
	 * @return
	 */
	public static Collection<Object> values(String pattern) {
		return getRedisson().getBuckets().find(pattern).stream()
				.map(RBucket::get).collect(Collectors.toList());
	}

	/**
	 * 获取匹配的长度
	 * 
	 * @param pattern
	 * @return
	 */
	public static int size(String pattern) {
		return getRedisson().getKeys().findKeysByPattern(pattern).size();
	}

	/**
	 * 获取锁，已存在则返回false
	 * 
	 * @param key
	 * @return
	 */
	public static boolean getLock(String key) {
		return getRedisson().getBucket(key).trySet(1, 10, TimeUnit.SECONDS);
	}

	/**
	 * 释放锁
	 * 
	 * @param key
	 */
	public static void releaseLock(String key) {
		getRedisson().getBucket(key).delete();
	}
}
