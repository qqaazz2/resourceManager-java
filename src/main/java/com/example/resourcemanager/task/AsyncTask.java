package com.example.resourcemanager.task;

import com.example.resourcemanager.common.BizException;
import com.example.resourcemanager.entity.Files;
import com.example.resourcemanager.entity.MetaData;
import com.example.resourcemanager.service.FilesService;
import com.example.resourcemanager.service.impl.FilesServiceImpl;
import com.example.resourcemanager.util.FilesUtils;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class AsyncTask {
    @Value("${file.upload}")
    String filePath;

    @Resource
    FilesUtils filesUtils;

    @Resource
    FilesService filesService;

    String resourcesPath = "";
    String basePath;
    File resourcesFile;
    Integer currentFolderID;
    static int contentType;
    static List<Files> filesList = new ArrayList<>(); //数据中存储的文件信息集
    static HashMap<String, Files> checkMap = new HashMap<>(); //获取到已经检测出来的文件信息
    static List<Files> createFiles = new ArrayList<>(); //需要新增的文件夹
    static List<Files> renameFiles = new ArrayList<>(); //需要重命名的文件及文件夹
    ExecutorService executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(200));
    List<CheckFileTask> list = new ArrayList<>();

    Map<String, Integer> folders = new HashMap<>();//文件夹的FilesID;

    protected void deepFolder(File[] files, Integer type, String parentPath) {
        for (File file : files) {
            //判断文件是否为文件夹
            if (file.isDirectory()) {
                //判断文件夹中没有metadata 如果没有则为新创建的文件夹
                //(所有没有的metadata文件的文件夹都会当作是新的文件夹，包括人为删除的)
                if (!filesUtils.checkMetaFile(file)) {
                    createFiles.add(filesUtils.createFolder(file, currentFolderID, contentType));
                    deepFolder(file.listFiles(), type, parentPath + file.getName());
                    continue;
                }

                //获取文件夹中的metadata
                MetaData metaData = filesUtils.checkFolderName(file);
                String metaDataName = metaData.getName();

                //如果metadata中的文件夹名字和拿到的文件夹名字不同则为重命名文件夹
                //重命名的条件
                //1.metadata里的数据和文件夹名称不相等
                //2.数据库中存在metadata里的数据名称的值
                String renamePath = parentPath;
                String name = file.getName();
                if (!metaDataName.equals(name) && checkDbData(file.getParent() + File.separator + metaDataName)) {
                    Files filesData = checkMap.get(file.getParent() + File.separator + metaDataName); //拿到checkList的最新一条数据
                    currentFolderID = filesData.getId();

                    //更新Files实体类
                    filesData.setFileName(file.getName());
                    filesData.setModifiableName(file.getName());
                    filesData.setFilePath(file.getPath()); //判断修改文件信息
                    renameFiles.add(filesData);
                    //更新Files实体类
                    renamePath = renamePath + File.separator + metaDataName;
                    type = 2;//设置type为重命名
                } else if (metaDataName.equals(file.getName()) && !checkDbData(file.getPath())) {
                    //这里对（有metadata文件并且没有重命名，但数据库中找不到文件信息）的操作
                    createFiles.add(filesUtils.createFolder(file, currentFolderID, contentType));
                    deepFolder(file.listFiles(), type, parentPath + file.getName());
                    continue;
                } else {
                    //这里是对没有重命名且文件信息再数据库中存在的信息的操作
                    checkDbData(file.getPath());
                    Files filesData = checkMap.get(file.getPath()); //拿到checkList的最新一条数据
                    currentFolderID = filesData.getId();
                    renamePath = renamePath + File.separator + file.getName();
                }
                deepFolder(file.listFiles(), type, renamePath);
            } else if (file.isFile() && !filesUtils.isMetaFile(file)) {
                list.add(new CheckFileTask(file, parentPath, currentFolderID, type, filesUtils));
            }
        }
    }

    public void finish() {
        for (CheckFileTask checkFileTask : list) {
            executor.submit(checkFileTask);
        }
        executor.shutdown();
        try {
            boolean tasksCompleted = executor.awaitTermination(30, TimeUnit.SECONDS);
            System.out.println(tasksCompleted);
            while (!tasksCompleted){
                tasksCompleted = executor.awaitTermination(30, TimeUnit.SECONDS);
                System.out.println(tasksCompleted);
            }
            System.out.println("123131313123");
            // 所有任务完成后继续执行后续操作
            rename(); // 重命名文件操作
            if (!createFiles.isEmpty()) {
                create(); // 创建新的文件
            }
            remove();
        } catch (Exception e) {
            e.printStackTrace();
            executor.shutdownNow();
            Thread.currentThread().interrupt();
            throw new BizException("4000", "文件扫描失败");
        }
    }

    protected static boolean checkDbData(String checkPath) {
        List<Files> files = filesList.stream().filter(item -> item.getFilePath().equals(checkPath)).collect(Collectors.toList());
        if (files.isEmpty()) return false; //数据库中不存在这个文件的信息
        checkMap.put(checkPath, files.get(0));
        return true;
    }

    public void rename() {
        filesService.renameFiles(renameFiles);
        renameFiles.stream().filter(value -> value.getIsFolder() == 1).forEach(value -> filesUtils.editMetaData(new File(value.getFilePath())));
    }

    public abstract void create();

    public void remove() {
        List<Files> filesL = checkMap.values().stream().toList();
        filesList.removeAll(filesL); //数据中存储的文件信息集和获取到已经检测出来的文件信息的差集
        filesService.removerFiles(filesList); //删除数据库中的数据
    }

    public void start(String path) {
        //判断传入的文件夹路径是否为空
        if (path.equals("") || path.isEmpty()) {
            resourcesPath = basePath;
        } else {
            resourcesPath = path;
        }
        resourcesFile = new File(filePath + resourcesPath);

        if (resourcesFile.isFile()) throw new BizException("4000", "只能对文件夹扫描");
        executor = new ThreadPoolExecutor(4, 4, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingDeque<>(200));
        filesList = filesService.getByType(contentType);
        currentFolderID = -1;

        deepFolder(resourcesFile.listFiles(), 1, resourcesFile.getPath());
        finish();
    }

    public void getChildren(List<Files> files) {
        folders.putAll(files.stream().collect(Collectors.toMap(Files::getFilePath, Files::getId)));
        createFiles.stream().filter(value -> value.getIsFolder() == 2).forEach(value -> {
            System.out.println(folders.get(value.getFile().getParent()));
            value.setParentId(folders.get(value.getFile().getParent()));
        });
        createFiles.removeIf(value -> value.getIsFolder() == 1);
    }

    @PreDestroy
    public void onDestroy() {
        executor.shutdown();
    }
}

@AllArgsConstructor
class CheckFileTask extends Thread {
    private File file;
    private String parentPath;
    private Integer currentFolderID;
    private Integer type;
    private FilesUtils filesUtils;

    @Override
    public void run() {
        //判断这个文件是否数据库中
        String renamePath = type == 2 ? parentPath + File.separator + file.getName() : file.getPath();
        if (AsyncTask.checkDbData(renamePath)) {
            Files files = AsyncTask.checkMap.get(renamePath);
            String fileHash = filesUtils.getFileChecksum(file);
            //判断文件hash是否相同 不相同则为新文件
            if (!files.getHash().equals(fileHash)) {
                AsyncTask.createFiles.add(filesUtils.createFiles(file, 2, currentFolderID));
            } else {
                //判断上级文件夹是否重命名了 重命名就更改文件路径
                if (type == 2) {
                    //更新Files实体类
                    files.setFileName(file.getName());
                    files.setFilePath(file.getPath()); //判断修改文件信息
                    AsyncTask.renameFiles.add(files);
                    //更新Files实体类
                    System.out.println(renamePath + "1312312313123");
                }
            }
        } else {
            //不在 则为新的文件
            AsyncTask.createFiles.add(filesUtils.createFiles(file, AsyncTask.contentType, currentFolderID));
        }
//        System.out.println(renamePath);
    }
}
