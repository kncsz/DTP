package org.swu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.swu.object.FileStats;
import org.swu.repository.FileRepository;

import javax.crypto.Cipher;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class DownloadFile {
    @Autowired
    private final FileRepository fileRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    public DownloadFile(FileRepository fileRepository, RedisTemplate<String, Object> redisTemplate) {
        this.fileRepository = fileRepository;
        this.redisTemplate = redisTemplate;
    }

    // 私钥
    private static final String clientPrivateKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAKbNojYr8KlqKD/y" +
            "COd7QXu3e4TsrHd4sz3XgDYWEZZgYqIjVDcpcnlztwomgjMj9xSxdpyCc85GOGa0" +
            "lva1fNZpG6KXYS1xuFa9G7FRbaACoCL31TRv8t4TNkfQhQ7e2S7ZktqyUePWYLlz" +
            "u8hx5jXdriErRIx1jWK1q1NeEd3NAgMBAAECgYAws7Ob+4JeBLfRy9pbs/ovpCf1" +
            "bKEClQRIlyZBJHpoHKZPzt7k6D4bRfT4irvTMLoQmawXEGO9o3UOT8YQLHdRLitW" +
            "1CYKLy8k8ycyNpB/1L2vP+kHDzmM6Pr0IvkFgnbIFQmXeS5NBV+xOdlAYzuPFkCy" +
            "fUSOKdmt3F/Pbf9EhQJBANrF5Uaxmk7qGXfRV7tCT+f27eAWtYi2h/gJenLrmtke" +
            "Hg7SkgDiYHErJDns85va4cnhaAzAI1eSIHVaXh3JGXcCQQDDL9ns78LNDr/QuHN9" +
            "pmeDdlQfikeDKzW8dMcUIqGVX4WQJMptviZuf3cMvgm9+hDTVLvSePdTlA9YSCF4" +
            "VNPbAkEAvbe54XlpCKBIX7iiLRkPdGiV1qu614j7FqUZlAkvKrPMeywuQygNXHZ+" +
            "HuGWTIUfItQfSFdjDrEBBuPMFGZtdwJAV5N3xyyIjfMJM4AfKYhpN333HrOvhHX1" +
            "xVnsHOew8lGKnvMy9Gx11+xPISN/QYMa24dQQo5OAm0TOXwbsF73MwJAHzqaKZPs" +
            "EN08JunWDOKs3ZS+92maJIm1YGdYf5ipB8/Bm3wElnJsCiAeRqYKmPpAMlCZ5x+Z" +
            "AsuC1sjcp2r7xw==";

    public ResponseEntity<Resource> downloadFile(String path, String userid) {
        File file = new File(path);

        if (!file.exists()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header(HttpHeaders.CACHE_CONTROL, "must-revalidate")
                    .build();
        }

        try {
            // 解密文件
//            File decryptedFile = decryptFile(file, getPrivateKey());

            Path filePath = Paths.get(path);
            String mimeType = Files.probeContentType(filePath);
            if (mimeType == null) {
                mimeType = "application/octet-stream"; // 默认MIME类型
            }

            Resource resource = new FileSystemResource(file);
            HttpHeaders headers = new HttpHeaders();

            // 设置下载方式 Content-Disposition
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"");

            // 设置文件类型
            headers.add(HttpHeaders.CONTENT_TYPE, mimeType);

            // 更新下载数
            fileRepository.updateFileStatsWithDownload(userid, 1);
            updateFileStatsCache(userid);
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate")
                    .header(HttpHeaders.PRAGMA, "no-cache")
                    .build();
        }
    }

    private File decryptFile(File encryptedFile, PrivateKey privateKey) throws Exception {
        // 读取加密文件内容
        byte[] encryptedData = Files.readAllBytes(encryptedFile.toPath());

        // 初始化Cipher对象
        Cipher cipher = Cipher.getInstance("RSA");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // 解密数据
        byte[] decryptedData = cipher.doFinal(encryptedData);


        // 保存解密后的内容到临时文件
        File decryptedFile = File.createTempFile("decrypted_", ".tmp");
        try (FileChannel fileChannel = FileChannel.open(decryptedFile.toPath(), StandardOpenOption.WRITE)) {
            fileChannel.write(ByteBuffer.wrap(decryptedData));
        }

        return decryptedFile;
    }

    private PrivateKey getPrivateKey() throws Exception {
        // 将私钥字符串转换为PrivateKey对象
        byte[] keyBytes = Base64.getDecoder().decode(clientPrivateKey);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
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
}