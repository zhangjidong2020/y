package com.leyou.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.auth.entity.UserInfo;
import com.leyou.common.utils.JsonUtils;
import com.leyou.interceptors.LoginInterceptor;
import com.leyou.pojo.Cart;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    static  final  String KEY_PREFIX="ly:cart:uid:";
    public void addCart(Cart cart) {

        UserInfo userInfo = LoginInterceptor.getLoginUser();
        //根据当前用户的id,在redis存储的数据
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());//hgetall ly:cart:uid:32
        //map <ly:cart:uid:userId,skuId1,sku1,skuId2,sku2>

        //hset myhash username jack age 16
        //hget myhash username
        //hget myhash age
        //hgetall myhash

        Object skuObj = ops.get(cart.getSkuId().toString());//hget ly:cart:uid:32  1232
        //hget myhash username


        // json字符串
        if(skuObj!=null){
            Cart cart1 = JsonUtils.nativeRead(skuObj.toString(), new TypeReference<Cart>() {
            });
            cart1.setNum(cart1.getNum()+cart.getNum());
            ops.put(cart.getSkuId().toString(), JsonUtils.serialize(cart1));
            //hset myhash username jack
        }else{
            //
            ops.put(cart.getSkuId().toString(), JsonUtils.serialize(cart));

        }
    }

    public List<Cart> queryCarts() {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        //hgetall ly:cart:uid:33

        //map <userId,skuId,sku>
        List<Object> skusObj = ops.values();

        List<Cart>  carts= new ArrayList<>();

        if(skusObj!=null){
            skusObj.forEach(sukObj->{
                carts.add(JsonUtils.nativeRead(sukObj.toString(), new TypeReference<Cart>() {
                }));

            });

        }
        return carts;

    }

    public void deleteCart(Long skuId) {
         UserInfo userInfo = LoginInterceptor.getLoginUser();
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());
        //hdel myhash skuId.toString()
        ops.delete(skuId.toString());
    }

    public void updateCart(Cart cart) {
        UserInfo userInfo = LoginInterceptor.getLoginUser();
        BoundHashOperations<String, Object, Object> ops = stringRedisTemplate.boundHashOps(KEY_PREFIX + userInfo.getId());

        String skuJson=ops.get(cart.getSkuId().toString()).toString();
        Cart storeCart=JsonUtils.nativeRead(skuJson, new TypeReference<Cart>() {
        });
        storeCart.setNum(storeCart.getNum()+1);

        ops.put(storeCart.getSkuId().toString(),JsonUtils.serialize(storeCart));

    }
}
