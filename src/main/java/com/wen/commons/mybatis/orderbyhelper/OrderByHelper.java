package com.wen.commons.mybatis.orderbyhelper;

import java.util.Properties;

import org.apache.ibatis.builder.StaticSqlSource;
import org.apache.ibatis.builder.annotation.ProviderSqlSource;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.RawSqlSource;
import org.apache.ibatis.scripting.xmltags.DynamicSqlSource;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.wen.commons.mybatis.orderbyhelper.sqlsource.OrderByDynamicSqlSource;
import com.wen.commons.mybatis.orderbyhelper.sqlsource.OrderByProviderSqlSource;
import com.wen.commons.mybatis.orderbyhelper.sqlsource.OrderByRawSqlSource;
import com.wen.commons.mybatis.orderbyhelper.sqlsource.OrderBySqlSource;
import com.wen.commons.mybatis.orderbyhelper.sqlsource.OrderByStaticSqlSource;

/**
 * 排序辅助类
 *
 * @author liuzh
 * @since 2015-06-26
 */
@Intercepts(@Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }))
public class OrderByHelper implements Interceptor {
	private static final ThreadLocal<String> ORDER_BY = new ThreadLocal<>();

	public static String getOrderBy() {
		String orderBy = ORDER_BY.get();
		if (orderBy == null || orderBy.length() == 0) {
			return null;
		}
		return orderBy;
	}

	/**
	 * 增加排序
	 *
	 * @param orderBy
	 */
	public static void orderBy(String orderBy) {
		ORDER_BY.set(orderBy);
	}

	/**
	 * 增加排序
	 *
	 * @param sortName 排序列名
	 * @param sortType 排序方式
	 */
	public static void orderBy(String sortName, String sortType) {
		ORDER_BY.set(sortName + " " + sortType);
	}

	/**
	 * 清除本地变量
	 */
	public static void clear() {
		ORDER_BY.remove();
	}

	/**
	 * 是否已经处理过
	 *
	 * @param ms
	 * @return
	 */
	public static boolean hasOrderBy(MappedStatement ms) {
		return ms.getSqlSource() instanceof OrderBySqlSource;
	}

	/**
	 * 不支持注解形式(ProviderSqlSource)的增加order by
	 *
	 * @param invocation
	 * @throws Throwable
	 */
	public static void processIntercept(Invocation invocation) throws Throwable {
		final Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		if (!hasOrderBy(ms)) {
			MetaObject msObject = SystemMetaObject.forObject(ms);
			// 判断是否自带order by，自带的情况下作为默认排序
			SqlSource sqlSource = ms.getSqlSource();
			if (sqlSource instanceof StaticSqlSource) {
				msObject.setValue("sqlSource", new OrderByStaticSqlSource((StaticSqlSource) sqlSource));
			} else if (sqlSource instanceof RawSqlSource) {
				msObject.setValue("sqlSource", new OrderByRawSqlSource((RawSqlSource) sqlSource));
			} else if (sqlSource instanceof ProviderSqlSource) {
				msObject.setValue("sqlSource", new OrderByProviderSqlSource((ProviderSqlSource) sqlSource));
			} else if (sqlSource instanceof DynamicSqlSource) {
				msObject.setValue("sqlSource", new OrderByDynamicSqlSource((DynamicSqlSource) sqlSource));
			} else {
				throw new RuntimeException("无法处理该类型[" + sqlSource.getClass() + "]的SqlSource");
			}
		}
	}

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		try {
			if (getOrderBy() != null) {
				processIntercept(invocation);
			}
			return invocation.proceed();
		} finally {
			clear();
		}
	}

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		} else {
			return target;
		}
	}

	@Override
	public void setProperties(Properties properties) {

	}
}
