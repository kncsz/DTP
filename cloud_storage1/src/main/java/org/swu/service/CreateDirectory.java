package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.swu.apitemplate.ApiResult;
import org.swu.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CreateDirectory {
    @Autowired
    private final FileRepository fileRepository;

    CreateDirectory(FileRepository fileReporitory){
        this.fileRepository = fileReporitory;
    }

    public ApiResult<Map<String, Object>> createDirectory(@RequestBody Map<String, Object> requestBody){
        Map<String, Object> response = new HashMap<>();
        try{
            if (!requestBody.containsKey("path") || !requestBody.containsKey("name") || !requestBody.containsKey("userid")) {
                return ApiResult.of(400, "参数缺失");
            }

            String path = requestBody.get("path").toString();
            String name = requestBody.get("name").toString();
            String userid = requestBody.get("userid").toString();

            if(path == null || name == null || userid == null){
                return ApiResult.of(400, "参数无效");
            }

            // 构建文件对象
            File directory = new File(path);

            int lastSlashIndex = path.lastIndexOf("/");
            // 父路径
            String dirName = path.substring(0, lastSlashIndex);
            List<org.swu.object.File> existingDirectories = fileRepository.findAllByPath(dirName);
            boolean isNameConflict = existingDirectories.stream()
                    .anyMatch(file -> file.getName().equals(name) && file.getType().equals("directory"));
            if (isNameConflict) {
                return ApiResult.of(409, "文件夹已存在");
            }

            // 递归创建文件夹
            if (!createDirectory(directory.getAbsolutePath())) {
                return ApiResult.of(500, "创建文件夹失败");
            }

            // 用户名
            String username = getUsernameFromPath(path);

            // 设置权限
//            changeFileOwnership(path, "kncsz");

            String datestamp = getCurrentTime();

            org.swu.object.File insertedFile = new org.swu.object.File(userid, name, dirName, datestamp,
                    datestamp, 0, "directory",
                    "这是一个文件夹", "",
                    false, false);
            fileRepository.save(insertedFile);
            response.put("status", 200);
            // 创建文件夹
//            if(!directory.exists()){
//                if(directory.mkdir()){
//                    // 获取用户名
//                    String username = getUsernameFromPath(path);
//
//                    // 获取父路径
//                    int lastSlashIndex = path.lastIndexOf("/");
//                    String dirName = path.substring(0, lastSlashIndex);
//
//                    // 设置权限
//                    changeFileOwnership(path, username);
//
//                    String datestamp = getCurrentTime();
//
//                    org.swu.object.File insertedFile = new org.swu.object.File(userid, name, dirName, datestamp,
//                            datestamp, 0, "directory",
//                            "这是一个文件夹", "",
//                            false, false);
//                    fileRepository.save(insertedFile);
//                    response.put("status", 200);
//                }
//            }
            if(response.containsKey("status") && response.get("status").equals(200)){
                return ApiResult.of(200, "文件夹创建成功");
            }else {
                return ApiResult.of(500, "服务器发生未知错误");
            }
        }catch(JpaSystemException e){
            return ApiResult.of(10002, "数据库异常：" + e.getMessage());
        }catch (Exception e) {
            return ApiResult.of(500, "服务器未知错误：" + e.getMessage());
        }
    }

    private boolean createDirectory(String path) {
        File dir = new File(path);
        // 检查目录是否存在，如果不存在则创建
        if (!dir.exists()) {
            // 获取父目录
            File parentDir = dir.getParentFile();
            // 如果父目录不存在，递归创建父目录
            if (!parentDir.exists() && !createDirectory(parentDir.getAbsolutePath())) {
                return false;
            }
            // 创建当前目录
            return dir.mkdir();
        }
        return true;
    }

    private String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");
        return now.format(formatter);
    }

    private void changeFileOwnership(String filePath, String user) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "chown", user, filePath);
        Process process = processBuilder.start();
        int resultCode = process.waitFor();
        if (resultCode != 0) {
            throw new IOException("Failed to change file ownership.");
        }
    }

    private String getUsernameFromPath(String path){
        String prefix = "/home/kncsz/cloudstorage/userfile/";

        // 移除前缀
        String remainingPath = path.substring(prefix.length());

        // 提取"user/"之后的部分
        int userIndex = remainingPath.indexOf("user/") + "user/".length();
        return remainingPath.substring(userIndex);
    }
}
