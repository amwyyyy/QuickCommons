package com.wen.commons.utils.excel;

import java.util.Map;

/**
 * 导出excel基础类
 * 
 * @author denis.huang
 * @date 2016年4月27日
 */
public abstract class BaseEO<E>
{
    /** 导出时转换EO用的 */
    public abstract E convert(Map<String, Object> bean);
}
