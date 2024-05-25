package com.example.resourcemanager.util;

import com.example.resourcemanager.common.BizException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FileTypeUtils {
    public static int default_check_length = 3;
    final static HashMap<String, String> fileTypeMap = new HashMap<>();

    static {
        fileTypeMap.put("ffd8ffe000104a464946", "jpg");
        fileTypeMap.put("89504e470d0a1a0a0000", "png");
        fileTypeMap.put("47494638396126026f01", "gif");
        fileTypeMap.put("00000020667479706d70", "mp4");
        fileTypeMap.put("504b0304140000000800", "zip");
        fileTypeMap.put("526172211a0700cf9073", "rar");
    }

    public static void getFileTypeBySuffix(String fileName, String[] formats) {
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
        if(Arrays.stream(formats).anyMatch(suffix::contains)) throw new BizException("4005", "文件上传格式不支持");
    }

    public static void getFileTypeByMagicNumber(InputStream inputStream) {
        Boolean isTrue = false;
        byte[] bytes = new byte[default_check_length];
        try {

            // 获取文件头前三位魔数的二进制
            inputStream.read(bytes, 0, bytes.length);
            // 文件头前三位魔数二进制转为16进制
            String code = bytesToHexString(bytes);
            for (Map.Entry<String, String> item : fileTypeMap.entrySet()) {
                if (code.equals(item.getKey())) {
                    isTrue = true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if(!isTrue) throw new BizException("4005", "文件上传格式不支持");
    }

    public static String bytesToHexString(byte[] bytes) {

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < bytes.length; i++) {

            int v = bytes[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {

                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }
}