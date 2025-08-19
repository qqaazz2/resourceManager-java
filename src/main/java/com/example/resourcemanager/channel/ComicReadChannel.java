//package com.example.resourcemanager.channel;
//
//import ch.qos.logback.core.util.FileUtil;
//import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
//import com.example.resourcemanager.entity.Files;
//import com.example.resourcemanager.entity.books.Books;
//import com.example.resourcemanager.entity.books.BooksDetails;
//import com.example.resourcemanager.entity.comic.Comic;
//import com.example.resourcemanager.mapper.BooksDetailsMapper;
//import com.example.resourcemanager.mapper.BooksMapper;
//import com.example.resourcemanager.mapper.FilesMapper;
//import com.example.resourcemanager.mapper.comic.ComicMapper;
//import com.example.resourcemanager.util.FilesUtils;
//import jakarta.annotation.Resource;
//import jakarta.websocket.*;
//import jakarta.websocket.server.PathParam;
//import jakarta.websocket.server.ServerEndpoint;
//import org.apache.tomcat.util.http.fileupload.FileUtils;
//import org.springframework.beans.BeansException;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.io.*;
//import java.nio.ByteBuffer;
//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.zip.ZipEntry;
//import java.util.zip.ZipFile;
//import java.util.zip.ZipInputStream;
//
//@Component
//@ServerEndpoint(value = "/webSocket/comic/read/{detailsID}?isPc={isPc}")
//public class ComicReadChannel implements ApplicationContextAware {
//    private Session session;
//    private int pageNumber = 0;
//    private int isPc = 0;
//    private File tempFolder = new File(System.getProperty("java.io.tmpdir"), "comicImages");
//
//    FilesMapper filesMapper;
//
//    private Map<Integer, String> imageCache = new HashMap<>();  // 缓存临时文件路径
//
//    // 解压CBZ文件并将图片保存在临时文件夹中
//    private void extractImagesToTempFolder(String cbzFilePath) {
//        if (!tempFolder.exists()) {
//            tempFolder.mkdir();
//        }
//
//        try (ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(cbzFilePath))) {
//            ZipEntry zipEntry;
//
//            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
//                if (!zipEntry.isDirectory()) {
//                    File file = new File(tempFolder, zipEntry.getName());
//                    try (OutputStream outputStream = new FileOutputStream(file)) {
//                        int n;
//                        for (; ; ) {
//                            if ((n = zipInputStream.read()) < 0) break;
//                            outputStream.write(n);
//                        }
//                    }
//                    imageCache.put(pageNumber, file.getAbsolutePath());
//                    pageNumber++;
//                }
//                zipInputStream.closeEntry();  // 关闭当前条目
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    @OnOpen
//    public void onOpen(Session session, @PathParam("detailsID") Integer detailsID, @RequestParam("isPc") Integer isPc) throws IOException {
//        this.session = session;
//        this.isPc = isPc;
//        Files files = filesMapper.selectById(detailsID);
//        if (files != null) {
//            extractImagesToTempFolder(files.getFilePath());
//            sendImageToClient(0);
//        } else {
//            this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, "文件不存在"));
//        }
//    }
//
//    @OnClose
//    public void onClose(Session session) {
//        cleanUpTempFiles();
//    }
//
//    // 清理临时文件夹中的图片
//    private void cleanUpTempFiles() {
//        if (tempFolder != null && tempFolder.exists()) {
//            try {
//                FileUtils.deleteDirectory(tempFolder);  // 删除整个文件夹
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//    }
//
//    @OnMessage
//    public void onMessage(Integer message, Session session) {
//        sendImageToClient(message);
//    }
//
//    @OnError
//    public void onError(Session session, Throwable throwable) throws IOException {
//        this.session.close(new CloseReason(CloseReason.CloseCodes.UNEXPECTED_CONDITION, throwable.getMessage()));
//        System.out.println(throwable.getMessage());
//        throwable.printStackTrace();
//    }
//
//    @Override
//    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//
//    }
//
//    private void sendImageToClient(Integer pageNumber) {
//        try {
//            String imagePath = imageCache.get(pageNumber);
//            if (imagePath != null) {
//                File file = new File(imagePath);
//                byte[] readAllBytes = java.nio.file.Files.readAllBytes(file.toPath());
//                this.session.getBasicRemote().sendBinary(ByteBuffer.wrap(readAllBytes));
//            } else {
//                this.session.getBasicRemote().sendText("页码不存在");
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
////    @Override
////    public void setApplicationContext(ApplicationContext context) throws BeansException {
////        applicationContext = context;
////    }
//}