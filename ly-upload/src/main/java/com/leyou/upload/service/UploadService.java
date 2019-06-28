package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class UploadService {

    @Autowired
    public FastFileStorageClient storageClient;


    public String uploadImage(MultipartFile file) {
       //vv.PNG
        String originalFilename = file.getOriginalFilename();
        String ext = StringUtils.substringAfterLast(originalFilename, ".");//PNG
        try {
            StorePath storePath = storageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
            if(storePath!=null){
                //http://image.leyou.com/group1/M00/00/00/wKjkoF0DEsiACNGMAAIYYQ_DjXU305.PNG
                return  "http://image.leyou.com/"+storePath.getFullPath();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return  null;
    }
}
