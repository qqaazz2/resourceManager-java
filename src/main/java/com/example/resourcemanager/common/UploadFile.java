package com.example.resourcemanager.common;

import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.util.FileTypeUtils;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Component
public class UploadFile {
    @Value("${file.upload}")
    String filePath;

    @Resource
    FilesUtils filesUtils;

    public List<File> upload(MultipartFile[] files, String url, String[] formats) {
        File file = new File(filePath + url);
        if (!file.exists()) {
            file.mkdirs();
        }
        List<File> list = new ArrayList();
        for (MultipartFile multipartFile : files) {
            String originalFileName = multipartFile.getOriginalFilename();
            try {
                FileTypeUtils.getFileTypeBySuffix(originalFileName, formats);
                FileTypeUtils.getFileTypeByMagicNumber(multipartFile.getInputStream());
                System.out.println(file.getAbsolutePath());
                File createFile = new File(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                multipartFile.transferTo(createFile);
                list.add(createFile);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("4004", "文件上传失败");
            }
        }

        return list;
    }

    public Map<String, Files> uploadFile(MultipartFile[] files, String url, String[] formats, Integer type) {
        File file = new File(filePath + url);
        List<File> list = new ArrayList();
        for (MultipartFile multipartFile : files) {
            String originalFileName = multipartFile.getOriginalFilename();
            try {
                FileTypeUtils.getFileTypeBySuffix(originalFileName, formats);
                FileTypeUtils.getFileTypeByMagicNumber(multipartFile.getInputStream());
                File createFile = new File(file.getAbsolutePath() + File.separator + multipartFile.getOriginalFilename());
                list.add(createFile);
            } catch (Exception e) {
                e.printStackTrace();
                throw new BizException("4004", "文件上传失败");
            }
        }

        return filesUtils.createFile(list, type);
    }


    private boolean checkFormats(String fileFullName, String[] formats) {
        String suffix = fileFullName.substring(fileFullName.lastIndexOf(".") + 1).toLowerCase();
        return Arrays.stream(formats).anyMatch(suffix::contains);
    }
}
