package com.example.resourcemanager.common;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class UploadFile {
    @Value("${file.upload}")
    String filePath;

    public List<String> upload(MultipartFile[] files, String url){
        File file = new File(filePath + url);
        if(!file.exists()){
            file.mkdirs();
        }
        List<String> list = new ArrayList();
        for (MultipartFile multipartFile:files) {
            try {
                multipartFile.transferTo(new File(file.getAbsolutePath()  + File.separator + multipartFile.getOriginalFilename()));
                list.add(multipartFile.getOriginalFilename());
            } catch (Exception e) {
                throw new BizException("4004", "文件上传失败");
            }
        }

        return list;
    }
}
