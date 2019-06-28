package com.leyou.gateway.filter;

import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.gateway.config.FilterProperties;
import com.leyou.gateway.config.JwtProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Component
@EnableConfigurationProperties({JwtProperties.class,FilterProperties.class})
public class LoginFilter extends ZuulFilter {


    @Autowired
    private  JwtProperties jwtProperties;
    @Autowired
    private FilterProperties filterProperties;
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        //请求头之前，查看请求参数的顺序
        return FilterConstants.PRE_DECORATION_FILTER_ORDER-1;
    }

    @Override
    public boolean shouldFilter() {
        //获取用户请求url
        //获取请求上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
        String requestURI = request.getRequestURI();//http://api.leyou.com/api/user/check/mike/1

        List<String> allPaths = filterProperties.getAllowPaths();

        for(String s:allPaths){
            //   api/user/check
            if(requestURI.startsWith(s)){

                      return  false;
            }


        }
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        //获取请求上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        //获取request
        HttpServletRequest request = currentContext.getRequest();
       //eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzIsInVzZXJuYW1lIjoidG9tMTIzIiwiZXhwIjoxNTYxNTM1NjAwfQ.gEqBgronAmqMk6o1eZzdgMR_CHZQ5dokKYgVKa77AvBngK-XDBJfKYIc8mChQqp2g_-eQvo7D4qLF5WWWLIad9IuLVxkg5Tvn6liz_CAYKTlPJ25dW89AGx0Zn61gTP_m4igrR-OxD93wbLAgbnG1y5bsQ0q4A9dKH1GXX-S6ZI
        String token = CookieUtils.getCookieValue(request, jwtProperties.getCookieName());
        try {
            JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
        } catch (Exception e) {
            e.printStackTrace();
            currentContext.setResponseStatusCode(401);
            currentContext.setSendZuulResponse(false);
        }
        return null;
    }
}
