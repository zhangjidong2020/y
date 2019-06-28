package com.leyou.page.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;

@Service
public class FileService {

    @Value("${ly.thymeleaf.destPath}")
    private String destPath;//D:/develop/html/item

    @Autowired
    private  PageService pageService;
    @Autowired
    private TemplateEngine templateEngine;

    private File createPath(Long id) {
        //113
        if (id == null) {
            return null;
        }
        File dest = new File(this.destPath);//D:/develop/html/item
        if (!dest.exists()) {
            dest.mkdirs();
        }
       //new File(dest,113.html);
        return new File(dest, id + ".html");
    }

    /**
     * 判断某个商品的页面是否存在
     * @param id
     * @return
     */
    public boolean exists(Long id){
        //113
        return this.createPath(id).exists();//113.html
        //D:/develop/html/item  113.html
    }


    public void syncCreateHtml(Long spuId) {

        createHtml(spuId);
    }

    public void createHtml(Long spuId)  {
        //上下文，准备模型数据
        Context context = new Context();
        //调用之前写好加载数据
        context.setVariables(pageService.loadData(spuId));
        //准备文件路径
        File file= new File(destPath);
//        if(!file.exists()){
//            file.mkdirs();
//
//        }

       File filePath= new File(file,spuId+".html");
        // //D:/develop/html/item  113.html
        PrintWriter printWriter= null;
        try {
            printWriter = new PrintWriter(filePath,"UTF-8");
            templateEngine.process("item",context,printWriter);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    public void deleteHtml(Long id) {

       File file= new File(destPath,id+".html");

       if(file.exists()){
           file.delete();

       }
    }
}
