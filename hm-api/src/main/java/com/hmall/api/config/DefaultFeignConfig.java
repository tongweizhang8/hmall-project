package com.hmall.api.config;

import com.hmall.api.client.fallback.ItemClientFallback;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    //设置Feign的日志必须将在yaml总log的level调到debug然后配置config，并设置为bean，返回值得用Level.Full，然后把config放到要启动的微服务中的启动类中
    @Bean
    public Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }


    //因为common里的拦截器只是将网关（hm-gateway）过滤保存的用户信息传到调用它的微服务中。
    // 但微服务之间无法传递用户信息，因为微服务之间的调用是基于OpenFeign，所以微服务之间，需要用OpenFeign提供自己的拦截器来帮助各个微服务获取网关保存的信息
    @Bean
    public RequestInterceptor userInfoRequestInterceptor(){
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 获取登录用户
                Long userId = UserContext.getUser();
                if(userId == null) {
                    // 如果为空则直接跳过
                    return;
                }
                // 如果不为空则放入请求头中，传递给下游微服务
                requestTemplate.header("user-info", userId.toString());
            }
        };
    }

    //开启降级逻辑，需要在对应的微服务的openFeign接口上加上@FeignClient(value = "hm-item-service", fallbackFactory = ItemClientFallback.class)注解
    @Bean
    public ItemClientFallback itemClientFallback(){
        return new ItemClientFallback();
    }
}
