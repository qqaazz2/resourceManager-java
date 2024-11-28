package com.example.resourcemanager.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.MetaData;
import com.example.resourcemanager.service.impl.FilesServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class FilesUtils {
    @Value("${file.meta}")
    String metaName;
    @Resource
    FilesServiceImpl filesService;

    public Map<String, Files> createFile(List<File> fileList, Integer type) {
        Map<String, Files> map = new HashMap<>();
        List<Files> list = new ArrayList<>();
        for (File file : fileList) {
            if (!file.exists()) {
                file.mkdirs();
            } else {
                LambdaQueryWrapper<Files> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                lambdaQueryWrapper.eq(Files::getFileName, file.getName());
                Files files = filesService.getOne(lambdaQueryWrapper);
                files.setFile(file);
                map.put(files.getFileName(), files);
            }
            list.add(createFiles(file, type, -1));
        }
        filesService.saveBatch(list);
        map.putAll(list.stream().collect(Collectors.toMap(Files::getFileName, files -> files)));
        return map;
    }

    public Map<String, Files> saveDataBase(List<File> fileList, Integer type) {
        Map<String, Files> map = new HashMap<>();
        List<Files> list = new ArrayList<>();
        for (File file : fileList) {
            list.add(createFiles(file, type, -1));
        }
        filesService.saveBatch(list);
        map.putAll(list.stream().collect(Collectors.toMap(Files::getFileName, files -> files)));
        return map;
    }

    public Files createFiles(File file, Integer type, Integer parentId) {
        Files files = new Files();
        try {
            files.setFileName(file.getName());
            files.setFilePath(file.getPath());
            files.setType(type);
            files.setFileType(java.nio.file.Files.probeContentType(Path.of(file.getPath())));
            files.setFile(file);
            files.setHash(this.getFileChecksum(file));
            files.setModifiableName(file.getName());
            files.setParentId(parentId);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("获取文件类型失败");
        }
        return files;
    }

    public Files createFolder(File file, Integer parentId, Integer type,Integer size) {
        Files files = new Files();
        files.setFileName(file.getName());
        files.setFilePath(file.getPath());
        files.setFileType("folder");
        files.setFile(file);
        files.setIsFolder(1);
        files.setType(type);
        files.setFileSize(size);
        files.setModifiableName(file.getName());
        if (parentId != -1) files.setParentId(parentId);
        return files;
    }

    public boolean checkMetaFile(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        MetaData metaData = new MetaData();
        Path paths = Paths.get(file.getPath() + File.separator + metaName);
        boolean type = java.nio.file.Files.exists(paths);
        if (type) return true;
        try {
            java.nio.file.Files.createFile(paths);
//            if (isWinSystem()) java.nio.file.Files.setAttribute(paths, "dos:hidden", true);
            metaData.setName(file.getName());
            objectMapper.writeValue(paths.toFile(), metaData);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("4000", "创建元数据文件失败");
        }
        return false;
    }

    public MetaData checkFolderName(File file) {
        ObjectMapper objectMapper = new ObjectMapper();
        MetaData metaData = new MetaData();
        File metaFile = new File(file.getPath() + File.separator + metaName);
        try {
            metaData = objectMapper.readValue(metaFile, MetaData.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("4000", "读取元数据文件信息失败");
        }

        return metaData;
    }

    public String getFileChecksum(File file) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            FileInputStream fis = new FileInputStream(file);

            byte[] byteArray = new byte[1024];
            int bytesCount = 0;

            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
            fis.close();

            byte[] bytes = digest.digest();

            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new BizException("40000", "计算文件Hash值有误");
        }
    }

    public static boolean isImageFile(String fileName) {
        String lowerCaseFileName = fileName.toLowerCase();
        return lowerCaseFileName.endsWith(".jpg") || lowerCaseFileName.endsWith(".jpeg") ||
                lowerCaseFileName.endsWith(".png") || lowerCaseFileName.endsWith(".gif") ||
                lowerCaseFileName.endsWith(".bmp") || lowerCaseFileName.endsWith(".tiff") ||
                lowerCaseFileName.endsWith(".webp");
    }

    public static boolean isWinSystem() {
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        if (os.contains("win")) return true;
        return false;
    }

    public boolean isMetaFile(File file) {
        if (file.getName().equals(metaName)) return true;
        return false;
    }

    public static Float getImgMp(int width, int height) {
        return (float) (width * height) / 1000000;
    }

    public void editMetaData(File file){
        Path paths = Paths.get(file.getPath() + File.separator + metaName);
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            MetaData metaData = new MetaData();
            metaData.setName(file.getName());
            objectMapper.writeValue(paths.toFile(), metaData);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BizException("4000", "编辑元数据文件失败");
        }
    }
}
