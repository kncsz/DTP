package org.swu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.swu.apitemplate.ApiResult;
import org.swu.object.File;
import org.swu.object.FileStats;
import org.swu.repository.FileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UploadFile {
    @Autowired
    private final FileRepository fileRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    UploadFile(FileRepository fileReporitory, RedisTemplate<String, Object> redisTemplate){
        this.fileRepository = fileReporitory;
        this.redisTemplate = redisTemplate;
    }

    /**
     * @param request 请求参数,包含文件和路径
     */

    public ApiResult<Map<String, Object>> uploadFile(HttpServletRequest request) {
        MultipartFile file;
        String path = "";
        String userid = "";
        String fileName = "";
        String dirName = "";

        try{
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;

            // 文件
            file = multipartRequest.getFile("file");

            // 判断文件是否为空
            if (file == null) {
                return ApiResult.of(404,"文件为空");
            }

            // 绝对路径
            path = multipartRequest.getParameter("path");

            // 文件名
            fileName = file.getOriginalFilename();
            java.io.File File = new java.io.File(path);

            // 查询重复文件
            int lastSlashIndex = path.lastIndexOf("/");
            // 父路径
            dirName = path.substring(0, lastSlashIndex);
            List<org.swu.object.File> existingFile= fileRepository.findAllByPath(dirName);
            String finalFileName = fileName;
            boolean isNameConflict = existingFile.stream()
                    .anyMatch(fileCheck -> fileCheck.getName().equals(finalFileName) && fileCheck.getType().equals("file"));
            if(isNameConflict){
                return ApiResult.of(403,"文件已存在");
            }

            // 获取文件类型
            String mimeType = file.getContentType();

            // 用户ID
            userid = multipartRequest.getParameter("userid");

            // 创建时间
            String datestamp = getCurrentTime();

            // 指向上传到服务器上的文件目录
            Path filePath = Paths.get(path);

            // 确保父目录存在
            Path directoryPath = filePath.getParent();
            ensureDirectoryExists(directoryPath);

            // 异步上传文件
            saveFileAsync(file, filePath);

            // 文件大小
            long size = File.length();

            // 修改文件所有权
//            changeFileOwnership(path, "kncsz");

            org.swu.object.File insertedFile = new File(userid, fileName, dirName, datestamp,
                    datestamp, size, "file",
                    "这是一个文件", "",
                    false, false);

            // 父目录条目
            int lastSlashIndex1 = dirName.lastIndexOf("/");
            String parentDirName = dirName.substring(0, lastSlashIndex1);
            String dir = dirName.substring(lastSlashIndex1 + 1);


            fileRepository.save(insertedFile);
            fileRepository.updateByPathAndNameAndType(size, parentDirName, "directory", dir);

            // 根据不同文件类型采取不同的操作
            assert mimeType != null;
            if (mimeType.startsWith("image/")) {
                fileRepository.updateFileStatsWithImage(userid, size, 1, 1, 1);
            } else if (mimeType.startsWith("video/")) {
                fileRepository.updateFileStatsWithVideo(userid, size, 1, 1, 1);
            } else if (mimeType.startsWith("audio/")) {
                fileRepository.updateFileStatsWithAudio(userid, size, 1, 1, 1);
            } else if (mimeType.equals("application/pdf") || mimeType.equals("application/msword") ||
                    mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                fileRepository.updateFileStatsWithDoc(userid, size, 1, 1, 1);
            } else {
                fileRepository.updateFileStatsWithOther(userid, size, 1, 1, 1);
            }

            // 更新 Redis 缓存
            updateFileStatsCache(userid);
            return ApiResult.of(200,"文件上传成功");
        }catch (IOException e) {
            // 返回给前端失败结果
            return ApiResult.of(500, "文件上传失败" + e.getMessage());
        }catch (JpaSystemException e) {
            return ApiResult.of(10002, "数据库异常：" + e.getMessage());
        }
    }

    private void ensureDirectoryExists(Path directoryPath) throws IOException {
        if (!Files.exists(directoryPath)) {
            Files.createDirectories(directoryPath);
        }
    }

    // 更新 Redis 缓存中的文件统计信息
    private void updateFileStatsCache(String userid) {
        FileStats stats = fileRepository.findByUserId(userid);
        if (stats != null) {
            Map<String, Object> data = new HashMap<>();
            data.put("userid", userid);
            data.put("total_size", stats.getTotal_size());
            data.put("total_files", stats.getTotal_files());
            data.put("total_uploads", stats.getTotal_uploads());
            data.put("total_downloads", stats.getTotal_downloads());
            data.put("total_documents", stats.getTotal_documents());
            data.put("total_images", stats.getTotal_images());
            data.put("total_videos", stats.getTotal_videos());
            data.put("total_audios", stats.getTotal_audios());
            data.put("total_others", stats.getTotal_others());

            try {
                ObjectMapper objectMapper = new ObjectMapper();
                String jsonData = objectMapper.writeValueAsString(data); // 转换为 JSON 字符串

                redisTemplate.opsForValue().set("user_file_stats_" + userid, jsonData, 1, TimeUnit.HOURS);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 异步保存文件
     *
     * @param file     文件
     * @param filePath 文件保存路径
     * @throws IOException 异常
     */
    @Async
    public void saveFileAsync(MultipartFile file, Path filePath) throws IOException {
        saveFile(file, filePath);
    }

    /**
     * 分块保存文件
     *
     * @param file     文件
     * @param filePath 文件保存路径
     * @throws IOException 异常
     */
    private void saveFile(MultipartFile file, Path filePath) throws IOException {
        try (InputStream inputStream = file.getInputStream();
             // 使用零拷贝
//             FileChannel outputChannel = FileChannel.open(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
             OutputStream outputStream = Files.newOutputStream(filePath, StandardOpenOption.CREATE, StandardOpenOption.WRITE)) {
            byte[] buffer = new byte[65536]; // 64KB 缓冲区
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
//                outputChannel.write(ByteBuffer.wrap(buffer, 0, bytesRead));
                outputStream.write(buffer, 0, bytesRead);
            }
        }
    }

    /**
     * 修改文件所属权
     *
     * @param filePath 文件保存路径
     * @param user 所属用户
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */

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

    private String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");
        return now.format(formatter);
    }

    /**
     * 修改文件所属权
     *
     * @param filePath 文件保存路径
     * @param user 所属用户
     * @throws IOException 异常
     * @throws InterruptedException 异常
     */

//    private void changeFileOwnership(Path filePath, String user) throws IOException {
//        Files.setOwner(filePath, filePath.getFileSystem()
//                .getUserPrincipalLookupService()
//                .lookupPrincipalByName(user));
//    }
}
