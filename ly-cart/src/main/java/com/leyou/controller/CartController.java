package com.leyou.controller;

import com.leyou.pojo.Cart;
import com.leyou.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.ResourceBundle;
@RestController
public class CartController {
    @Autowired
    private CartService cartService;

    @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
           //
        cartService.addCart(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();

    }

        @GetMapping
        public  ResponseEntity<List<Cart>> queryCarts(){
            List<Cart> carts=cartService.queryCarts();
            if(carts!=null&&carts.size()>0){
                return  ResponseEntity.ok(carts);

            }
            return  ResponseEntity.status(HttpStatus.NO_CONTENT).build();

    }


    @DeleteMapping("{skuId}")
    public  ResponseEntity<Void> deleteCart(@PathVariable("skuId") Long skuId){
        cartService.deleteCart(skuId);
        return  ResponseEntity.status(HttpStatus.CREATED).build();

    }

    @PutMapping("increment")
    public  ResponseEntity<Void> updateCart(@RequestBody Cart cart){
        cartService.updateCart(cart);
        return  ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
