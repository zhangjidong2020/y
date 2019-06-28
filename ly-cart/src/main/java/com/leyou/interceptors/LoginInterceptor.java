package com.leyou.interceptors;

import com.leyou.auth.entity.UserInfo;
import com.leyou.auth.utils.JwtUtils;
import com.leyou.common.utils.CookieUtils;
import com.leyou.config.JwtProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class LoginInterceptor extends HandlerInterceptorAdapter {

    private JwtProperties jwtProperties;
   //定义一个线程域，存放登录用户
    private  static  final  ThreadLocal<UserInfo> t1=new ThreadLocal<>();

    public  LoginInterceptor(JwtProperties jwtProperties){

        this.jwtProperties=jwtProperties;
    }


    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //查询
        String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
        //eyJhbGciOiJSUzI1NiJ9.eyJpZCI6MzIsInVzZXJuYW1lIjoidG9tMTIzIiwiZXhwIjoxNTYxNjA0MDcyfQ.aLaiuLikAIsVwTF_i8RRdNPjOLIXYhhD5qz-y8nHgav2Ce7JO4CrgZ0cYcQ8SyrODLxcq3RzlKFiAuShWwiuhIamy1fielS39Js8NijxYZDuEIhBctH0CEY29YeQj1lfWkIKBoqA0-rDJWQMgLQy_V1k2dZ6Og1JEtH17aZjKDA
        if(StringUtils.isBlank(token)){
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            return  false;

        }
       try{
           UserInfo userInfo = JwtUtils.getInfoFromToken(token, jwtProperties.getPublicKey());
           //userinfo放到线程对象
           t1.set(userInfo);
           return true;

       }catch (Exception e){
           response.setStatus(HttpStatus.UNAUTHORIZED.value());
           return  false;

       }

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }

    public  static  UserInfo getLoginUser(){
         return  t1.get();

    }
}