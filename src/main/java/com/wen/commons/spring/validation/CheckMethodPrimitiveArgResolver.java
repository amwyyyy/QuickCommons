package com.wen.commons.spring.validation;

import com.wen.commons.utils.StringUtils;
import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeMismatchException;
import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.AbstractNamedValueMethodArgumentResolver;

import javax.servlet.ServletException;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * 处理方法中基本类型参数的校验
 * 
 * @author tim.tang
 * @since 2016年9月5日
 */
public class CheckMethodPrimitiveArgResolver extends AbstractNamedValueMethodArgumentResolver {

	public CheckMethodPrimitiveArgResolver() {
		super(null);
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		// TODO 不处理数组类型
		// 处理基本类型
		return BeanUtils.isSimpleProperty(parameter.getParameterType());
	}

	@Override
	protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
		return new RequestParamNamedValueInfo();
	}

	@Override
	protected Object resolveName(String name, MethodParameter parameter, NativeWebRequest request)
			throws BindException {
		String[] paramValues = request.getParameterValues(name);

		checkNotNullOrEmpty(parameter, name, paramValues);

		if (paramValues != null && paramValues.length > 0) {
			String paramValue = paramValues[0];

			// min
			Min minAnn = parameter.getParameterAnnotation(Min.class);
			if (minAnn != null) {
				checkMin(name, paramValue, minAnn.value());
			}

			// max
			Max maxAnn = parameter.getParameterAnnotation(Max.class);
			if (maxAnn != null) {
				checkMax(name, paramValue, maxAnn.value());
			}

			// size
			Size sizeAnn = parameter.getParameterAnnotation(Size.class);
			if (sizeAnn != null) {
				checkSize(name, paramValue, sizeAnn.min(), sizeAnn.max());
			}

			// ID
			Id idAnn = parameter.getParameterAnnotation(Id.class);
			if (idAnn != null) {
				if (paramValues.length > 0) {
					int val = parseInt(paramValues[0]);
					if (val <= 0) {
						throwBindException("", name, "Id Constrain");
					}
				}
			}
		}

		// 无异常抛出
		return (paramValues != null && paramValues.length > 0) ? paramValues[0] : paramValues;

	}

	/**
	 * 检查字符串长度
	 * @param name 参数名
	 * @param paramValue 值
	 * @param min 最小值
	 * @param max 最大值
	 * @throws BindException
	 */
	private void checkSize(String name, String paramValue, int min, int max) throws BindException {
		int size = paramValue.length();
		if (size < min || size > max) {
			throwBindException("", name, "Size Constrain");
		}
	}

	/**
	 * 验证short,int,long
	 * @param name
	 * @param paramValue
	 * @param max
	 * @throws BindException
	 */
	private void checkMax(String name, String paramValue, long max) throws BindException {
		// 都转成long型
		long value = parseLong(paramValue);
		if (value > max) {
			throwBindException(paramValue, name, "Max Constrain");
		}
	}

	/**
	 * @param paramValue
	 * @return
	 */
	private long parseLong(String paramValue) {
		try {
			return Long.parseLong(paramValue);
		} catch (Exception e) {
			throw new TypeMismatchException(paramValue, Long.class);
		}
	}

	/**
	 * 验证short,int,long
	 * @param name
	 * @param paramValue
	 * @param min
	 * @throws BindException
	 */
	private void checkMin(String name, String paramValue, long min) throws BindException {
		// 都转成long型
		long value = parseLong(paramValue);
		if (value < min) {
			throwBindException(paramValue, name, "Min Constrain");
		}
	}

	/**
	 * 验证null与Empty
	 * @param paramValues
	 * @throws BindException
	 */
	private void checkNotNullOrEmpty(MethodParameter parameter, String name, String[] paramValues)
			throws BindException {
		// Not null
		NotNull nnann = parameter.getParameterAnnotation(NotNull.class);
		if (nnann != null) {
			if (parameter.getParameterType().equals(String.class)) {
				// 字符串类型可能是空串
				if (paramValues == null || (paramValues.length > 0 && paramValues[0] == null)) {
					throwBindException("", name, "may not be null");
				}
			} else {
				// 基本类型不能是空串
				if (paramValues == null || (paramValues.length > 0 && StringUtils.isEmpty(paramValues[0])))
				{
					throwBindException("", name, "may not be null");
				}
			}
		}

		// Not empty
		NotEmpty neann = parameter.getParameterAnnotation(NotEmpty.class);
		if (neann != null)
		{
			if (!parameter.getParameterType().equals(String.class))
			{
				throwBindException("", name, "only support string");
			}

			if (paramValues == null || (paramValues.length > 0 && StringUtils.isEmpty(paramValues[0])))
			{
				throwBindException("", name, "may not be empty");
			}
		}
	}

	/**
	 * 转成int类型
	 * @param paramValue
	 * @return
	 */
	private int parseInt(String paramValue) {
		try {
			return Integer.parseInt(paramValue);
		} catch (Exception e) {
			throw new TypeMismatchException(paramValue, Integer.class);
		}
	}

	private void throwBindException(Object target, String name, String msg) throws BindException {
		BindException exception = new BindException(target, name);
		ObjectError error = new ObjectError(name, null, null, msg);
		exception.addError(error);
		throw exception;
	}

	@Override
	protected void handleMissingValue(String name, MethodParameter parameter) throws ServletException {
		throw new MissingServletRequestParameterException(name, parameter.getParameterType().getSimpleName());
	}

	private static class RequestParamNamedValueInfo extends NamedValueInfo {
		RequestParamNamedValueInfo() {
			super("", false, ValueConstants.DEFAULT_NONE);
		}
	}
}
