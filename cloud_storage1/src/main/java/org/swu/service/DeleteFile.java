package org.swu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.tika.Tika;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.swu.apitemplate.ApiResult;
import org.swu.exception.InvalidFileException;
import org.swu.object.FileStats;
import org.swu.repository.FileRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Service
public class DeleteFile {
    @Autowired
    private final FileRepository fileRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    DeleteFile(FileRepository fileReporitory, RedisTemplate<String, Object> redisTemplate){
        this.fileRepository = fileReporitory;
        this.redisTemplate = redisTemplate;
    }

    public ApiResult<Map<String, Object>> deleteFile(@RequestBody Map<String, Object> requestBody){
        Map<String, Object> response = new ConcurrentHashMap<>();
        try{
            if (!requestBody.containsKey("paths") || !requestBody.containsKey("type")|| !requestBody.containsKey("userid")) {
                return ApiResult.of(400, "参数缺失");
            }

            List<String> paths = (List<String>) requestBody.get("paths");
            String type = requestBody.get("type").toString();
            String userid = requestBody.get("userid").toString();
            if(paths == null || type == null){
                return ApiResult.of(400, "参数无效");
            }
            paths.parallelStream().forEach(path -> {
                try {
                    if(type.equals("file")){
                        // 分割路径
                        int lastSlashIndex = path.lastIndexOf("/");
                        String dirName = path.substring(0, lastSlashIndex);
                        String fileName = path.substring(lastSlashIndex + 1);

                        // 获取 MIME 类型
                        Path filePath = Paths.get(path);
                        Tika tika = new Tika();
                        String mimeType = tika.detect(filePath.toFile());

                        // 获取文件的大小
                        long size = Files.size(filePath);

                        // 从 Linux 文件系统删除文件
                        deleteFile(path);

                        // 从数据库删除条目
                        fileRepository.deleteFile(dirName, fileName);

                        // 根据不同文件类型采取不同的操作
                        assert mimeType != null;
                        if (mimeType.startsWith("image/")) {
                            fileRepository.updateFileStatsWithImage1(userid, size, 1, 1);
                        } else if (mimeType.startsWith("video/")) {
                            fileRepository.updateFileStatsWithVideo1(userid, size, 1, 1);
                        } else if (mimeType.startsWith("audio/")) {
                            fileRepository.updateFileStatsWithAudio1(userid, size, 1, 1);
                        } else if (mimeType.equals("application/pdf") || mimeType.equals("application/msword") ||
                                mimeType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                            fileRepository.updateFileStatsWithDoc1(userid, size, 1, 1);
                        } else {
                            fileRepository.updateFileStatsWithOther1(userid, size, 1, 1);
                        }
                    }else if(type.equals("directory")){
                        // File directory = new File(path);
                        // 递归的删除文件
                        deleteDirectory(path);
                        fileRepository.deleteDirectory(path);
                    }
                    updateFileStatsCache(userid);
                } catch (IOException e) {
                    response.put(path, "文件删除失败：" + e.getMessage());
                } catch (JpaSystemException e) {
                    response.put(path, "数据库删除失败：" + e.getMessage());
                }
            });

            if (response.isEmpty()) {
                return ApiResult.of(200, "文件成功删除", null);
            } else {
                return ApiResult.of(207, "部分文件删除失败", response); // 207: 部分成功状态码
            }
        }catch (JpaSystemException e) {
            return ApiResult.of(10002, "数据库异常：" + e.getMessage(), null);
        } catch (InvalidFileException e) {
            return ApiResult.of(10001, "文件路径无效：" + e.getMessage(), null);
        } catch (Exception e) {
            return ApiResult.of(500, "服务器未知错误：" + e.getMessage(), null);
        }
    }

    private void deleteFile(String path) throws IOException {
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "rm", "-f", path);
        Process process = processBuilder.start();
    }

    private void deleteDirectory(String path) throws IOException {
        // 使用 Linux 命令 rm -rf 删除目录及其内容
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "rm", "-rf", path);
        Process process = processBuilder.start();
    }

//    private void deleteLinuxFile(String path) throws IOException {
//        Path filePath = Paths.get(path);
//        if (Files.exists(filePath)) {
//            Files.delete(filePath);
//        }
//    }
//
//    private boolean deleteDirectoryRecursively(File dir) {
//        if (dir.isDirectory()) {
//            File[] files = dir.listFiles();
//            if (files != null) {
//                for (File file : files) {
//                    // 递归删除子文件和子目录
//                    if (!deleteDirectoryRecursively(file)) {
//                        return false; // 如果有一个文件或目录未成功删除，则返回 false
//                    }
//                }
//            }
//        }
//        // 删除空目录或文件
//        return dir.delete();
//    }

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
}
