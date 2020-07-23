package com.wen.commons.spring;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.FactoryBean;

import com.wen.commons.utils.StringUtils;

/**
 * Redisson工厂
 *
 * @author denis.huang
 * @since 2017年2月16日
 */
@Deprecated
public class RedissonFactoryBean implements FactoryBean<RedissonClient>
{
    private String address;
    private String password;
    private Integer timeout = 3000;
    private Integer database = 0;
    private Integer connectionPoolSize = 5;
    private Integer minIdle = 2;

    @Override
    public RedissonClient getObject() throws Exception
    {
        Config config = new Config();
        SingleServerConfig ssc = config.useSingleServer();

        ssc.setAddress(address).setTimeout(timeout).setDatabase(database)
                .setConnectionPoolSize(connectionPoolSize).setConnectionMinimumIdleSize(minIdle);
        if (StringUtils.isNotEmpty(password)) {
            ssc.setPassword(password);
        }

        return Redisson.create(config);
    }

    @Override
    public Class<?> getObjectType()
    {
        return RedissonClient.class;
    }

    @Override
    public boolean isSingleton()
    {
        return true;
    }

    public void setAddress(String address)
    {
        this.address = address;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public void setTimeout(Integer timeout)
    {
        this.timeout = timeout;
    }

    public void setDatabase(Integer database)
    {
        this.database = database;
    }

    public void setConnectionPoolSize(Integer connectionPoolSize)
    {
        this.connectionPoolSize = connectionPoolSize;
    }

    public void setMinIdle(Integer minIdle)
    {
        this.minIdle = minIdle;
    }
}
