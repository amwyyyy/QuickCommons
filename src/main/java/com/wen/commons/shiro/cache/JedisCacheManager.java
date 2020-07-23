package com.wen.commons.shiro.cache;

import org.apache.shiro.cache.AbstractCacheManager;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;

/**
 * shiro缓存管理器
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
@Component("jedisCacheManager")
public class JedisCacheManager extends AbstractCacheManager {
	@Override
	protected Cache<Object, Object> createCache(String name) throws CacheException {
		return new JedisCache(name);
	}
}
