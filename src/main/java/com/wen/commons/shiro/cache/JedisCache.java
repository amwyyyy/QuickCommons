package com.wen.commons.shiro.cache;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;

import com.wen.commons.utils.CacheUtils;

/**
 * shrio缓存，jedis实现
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
public class JedisCache implements Cache<Object, Object> {

	private String id;

	public JedisCache(String id) {
		this.id = id;
	}

	private String getKey(Object key) {
		return this.id + "_" + key.toString();
	}

	@Override
	public Object get(Object key) throws CacheException {
		return CacheUtils.get(getKey(key));
	}

	@Override
	public Object put(Object key, Object value) throws CacheException {
		Object old = CacheUtils.get(getKey(key));
		if (value != null) {
			CacheUtils.set(getKey(key), value, 30, TimeUnit.MINUTES);
		}
		return old;
	}

	@Override
	public Object remove(Object key) throws CacheException {
		Object old = CacheUtils.get(getKey(key));
		CacheUtils.del(getKey(key));
		return old;
	}

	@Override
	public void clear() throws CacheException {
		Set<String> keys = keys().stream().map(Object::toString).collect(Collectors.toSet());

		CacheUtils.del(keys);
	}

	@Override
	public int size() {
		return keys().size();
	}

	@Override
	public Set<Object> keys() {
		return new HashSet<>(CacheUtils.keys(this.id + "*"));
	}

	@Override
	public Collection<Object> values() {
		return CacheUtils.values(this.id + "*");
	}
}
