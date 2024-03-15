package com.hmall.common.config;

import com.hmall.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


@Configuration
/**
第一种情况： 微服务想使用不同包下的拦截器会出现，拦截器配置类不在其他微服务配置类中，所以微服务扫描不到
 解决方法：基于SpringBoot的自动装配原理，我们要将其添加到本包下的resources目录下的META-INF/spring.factories文件中

第二种情况：因为我们过滤器（网关hm-gateway）用的是非堵塞响应式编程底层是WebFlux技术，不是SpringMvc技术，两者会冲突，
                  因为我们的gateway包引用了common包因此就有了MvcConfig类，又因为MvcConfig实现了WebMvcConfigurer
                 导致WebFlux和SpringMvc技术冲突，所以我们只要需要排除掉gateway不要让它扫描到本类即可
解放方法：需要用到拦截器都是微服务包，而微服务都有DispatcherServlet.class文件，所以我们只要加上使用此配置类的条件就可以
 **/
@ConditionalOnClass(DispatcherServlet.class)
public class MvcConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //不需要额外加excludePatterns和includePatterns方法来拦截白名单，因为我们已经在过滤器里过滤了
        registry.addInterceptor(new UserInfoInterceptor());
    }
}
