package com.wen.commons.mybatis;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReadWriteLock;

import org.apache.ibatis.cache.Cache;

import com.wen.commons.utils.CacheUtils;

/**
 * mybatis缓存,jedis实现
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
public class JedisCache implements Cache {

	/** 缓存id */
	private String id;

	public JedisCache(String id) {
		this.id = id;
	}

	@Override
	public String getId() {
		return id;
	}

	private String getStringKey(Object key) {
		return key.toString().replace(":", "#");
	}

	@Override
	public void putObject(Object key, Object value) {
		if (value != null) {
			CacheUtils.set(getStringKey(key), value, 30, TimeUnit.MINUTES);
		}
	}

	@Override
	public Object getObject(Object key) {
		return CacheUtils.get(getStringKey(key));
	}

	@Override
	public Object removeObject(Object key) {
		Object obj = CacheUtils.get(getStringKey(key));

		CacheUtils.del(getStringKey(key));
		return obj;
	}

	@Override
	public void clear() {
		CacheUtils.delForPattern("*" + id + "*");
	}

	@Override
	public int getSize() {
		return CacheUtils.size("*SqlSessionFactoryBean");
	}

	@Override
	public ReadWriteLock getReadWriteLock() {
		return null;
	}
}
