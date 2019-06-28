package com.leyou.page.listeners;


import com.leyou.page.service.FileService;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class GoodsListener {


    @Autowired
    private FileService fileService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.create.page.quene", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.insert","item.update"}))
    public  void listenCreate(Long id){
        if(id==null){

            return;
        }
        fileService.createHtml(id);

    }


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "ly.delete.page.quene", durable = "true"),
            exchange = @Exchange(
                    value = "ly.item.exchange",
                    type = ExchangeTypes.TOPIC
            ),
            key = {"item.delete"}))
    public  void listenDelete(Long id){
        if(id==null){

            return;
        }
        fileService.deleteHtml(id);

    }



}
