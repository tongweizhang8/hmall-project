package com.hmall.gateway.filter;

import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@EnableConfigurationProperties(AuthProperties.class)
//设置自己的过滤器，用来过滤当前用户有没有登录
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    //配置了exclude路径的配置项
    private final AuthProperties authProperties;

    //用来判断特殊路径（ant路径） 比如说/search/** ，需要new出来，因为没有被springboot管理
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();
    private final JwtTool jwtTool;
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //先得到request
        ServerHttpRequest request = exchange.getRequest();
        //判断白名单，如果有就放行
        if (isExclude(request.getPath().toString())){
            return chain.filter(exchange);
        }
        //如果没有白名单就要看有没有token
        String token = null;
        List<String> authorization = request.getHeaders().get("authorization");
        if (authorization != null) {
            token = authorization.get(0);
        }
        //检验并解析token
        Long userId;
        try {
            userId = jwtTool.parseToken(token);
        }catch (UnauthorizedException e)
        {
            //如果无效就拦截，使用response返回状态码401
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //传递获得的用户信息 如在header中设置user-info并设置userId的值，用来将userId传递给其他类
        //这种就需要设置拦截器，因为是配置类所以建议把它放到common包下
        ServerWebExchange webExchange = exchange.mutate()
                .request(builder -> builder.header("user-info", userId.toString()))
                .build();
        //最后放行 放行需要用新设置好的webExchange，因为里面有新设置好的header，用来让拦截器获取
        return chain.filter(webExchange);
    }
    private boolean isExclude (String path){
        List<String> excludePaths = authProperties.getExcludePaths();
        for (String excludePath : excludePaths) {
            if (antPathMatcher.match(excludePath,path)) {
                return true;
            }
        }
        return false;
    }

    //提高拦截器优先级，默认就好
    @Override
    public int getOrder() {
        return 0;
    }
}
