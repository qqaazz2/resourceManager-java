package com.example.resourcemanager.common;

import com.example.resourcemanager.util.FileTypeUtils;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class UploadFile {
    @Value("${file.upload}")
    String filePath;

    public List<String> upload(MultipartFile[] files, String url,String[] formats){
        File file = new File(filePath + url);
        if(!file.exists()){
            file.mkdirs();
        }
        List<String> list = new ArrayList();
        for (MultipartFile multipartFile:files) {
            String originalFileName = multipartFile.getOriginalFilename();
            try {
                FileTypeUtils.getFileTypeBySuffix(originalFileName,formats);
                FileTypeUtils.getFileTypeByMagicNumber(multipartFile.getInputStream());
                multipartFile.transferTo(new File(file.getAbsolutePath()  + File.separator + multipartFile.getOriginalFilename()));
                list.add(multipartFile.getOriginalFilename());
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("4004", "文件上传失败");
            }
        }

        return list;
    }

    private boolean checkFormats(String fileFullName,String[] formats){
        String suffix = fileFullName.substring(fileFullName.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.stream(formats).anyMatch(suffix::contains);
    }
}
