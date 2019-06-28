package com.leyou.upload.controller;

import com.leyou.upload.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("upload")
public class UploadController {
     @Autowired
     private UploadService uploadService;

     @PostMapping("image")
     public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file){
        String url= uploadService.uploadImage(file);
        //http://image.leyou.com/group1/M00/00/00/wKjkoF0DEsiACNGMAAIYYQ_DjXU305.PNG
         return  ResponseEntity.ok(url);


     }
}
