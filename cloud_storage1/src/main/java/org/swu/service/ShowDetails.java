package org.swu.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.swu.apitemplate.ApiResult;
import org.swu.exception.InvalidFileException;
import org.swu.object.FileStats;
import org.swu.repository.FileRepository;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class ShowDetails {
    @Autowired
    private final FileRepository fileRepository;

    @Autowired
    private final RedisTemplate<String, Object> redisTemplate;

    ShowDetails(FileRepository fileRepository, RedisTemplate<String, Object> redisTemplate){
        this.fileRepository = fileRepository;
        this.redisTemplate = redisTemplate;
    }

    public ApiResult<Map<String, Object>> showDetails(String userid){
        try {
            String cacheKey = "user_file_stats_" + userid;

            // 先查 Redis 缓存
            String cachedJson = (String) redisTemplate.opsForValue().get(cacheKey);
            if (cachedJson != null) {
                // 如果缓存存在，直接反序列化缓存数据
                ObjectMapper objectMapper = new ObjectMapper();
                Map<String, Object> cachedData = objectMapper.readValue(cachedJson, Map.class);
                return ApiResult.of(200, "文件统计信息查询成功", cachedData);
            }

            // 从 filestats 表中获取统计信息
            FileStats stats = fileRepository.findByUserId(userid);

            if (stats == null) {
                return ApiResult.of(404, "未找到用户统计信息", null);
            }

            // 构建返回数据
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

            return ApiResult.of(200,"文件统计信息查询成功", data);
        } catch (JpaSystemException e) {
            return ApiResult.of(10002, "数据库异常：" + e.getMessage());
        } catch (InvalidFileException e) {
            return ApiResult.of(10001, "文件路径无效：" + e.getMessage());
        } catch (Exception e) {
            return ApiResult.error(500, "服务器错误：" + e.getMessage());
        }
    }
}
