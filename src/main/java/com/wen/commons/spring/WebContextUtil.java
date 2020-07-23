package com.wen.commons.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * spring上下文工具
 * 
 * @author denis.huang
 * @since 2017年2月15日
 */
@Component
public class WebContextUtil implements ApplicationContextAware {
	private static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext context) throws BeansException {
		applicationContext = context;
	}

	/**
	 * 根据类型获取bean
	 * @param requiredType
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(Class<T> requiredType) {
		return applicationContext.getBean(requiredType);
	}

	/**
	 * 根据id和类型获取bean
	 * @param beanName
	 * @param type
	 * @param <T>
	 * @return
	 */
	public static <T> T getBean(String beanName, Class<T> type) {
		return applicationContext.getBean(beanName, type);
	}
}
