package com.wen.commons.spring;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wen.commons.spring.interceptor.AccessOriginInterceptor;
import com.wen.commons.spring.validation.CheckMethodPrimitiveArgResolver;
import com.wen.commons.spring.version.CustomRequestMappingHandlerMapping;
import com.wen.commons.utils.DateUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.List;

/**
 * spring mvc 配置类
 *
 * @author denis.huang
 * @since 2017/3/9
 */
@ComponentScan(basePackages = "com.wen.commons")
@EnableWebMvc
@EnableAspectJAutoProxy
public abstract class AbstractWebConfig extends WebMvcConfigurationSupport {
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        // 注解校验简单类型参数处理器
        // argumentResolvers.add(new CheckMethodPrimitiveArgResolver());
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        // 下载文件返回ResponseEntity<byte[]>
        converters.add(new ByteArrayHttpMessageConverter());

        // @ResponseBody 转换成Json格式返回
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        ObjectMapper objectMapper = converter.getObjectMapper();
        objectMapper.setDateFormat(DateUtils.DEFAULT_DATETIME_FORMATER.get());
        converters.add(converter);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 跨域处理拦截器
        registry.addInterceptor(new AccessOriginInterceptor());
    }

    @Override
    @Bean
    public RequestMappingHandlerMapping requestMappingHandlerMapping() {
        RequestMappingHandlerMapping handlerMapping = new CustomRequestMappingHandlerMapping();
        handlerMapping.setOrder(0);
        handlerMapping.setInterceptors(getInterceptors());
        return handlerMapping;
    }

    /**
     * 文件上传
     *
     * @return
     */
    @Bean(name = "multipartResolver")
    public CommonsMultipartResolver commonsMultipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }
}
