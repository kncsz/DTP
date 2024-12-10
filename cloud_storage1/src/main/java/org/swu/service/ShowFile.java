package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swu.apitemplate.ApiResult;
import org.swu.object.File;
import org.swu.repository.FileRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShowFile {
    @Autowired
    private final FileRepository fileRepository;

    ShowFile(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }

    public ApiResult<Map<String, Object>> showFile(String path){
        Map<String, Object> response = new HashMap<>();
        try {
            // 查询符合条件的文件
            List<File> files = fileRepository.findAllByPath(path);

            // 判断查询结果是否为空
            if (files.isEmpty()) {
                return ApiResult.of(404, "未找到符合条件的文件", null);
            }

            response.put("files", files);
            // 返回成功结果
            return ApiResult.of(200, "文件查询成功", response);
        } catch (Exception e) {
            // 捕获异常并返回错误结果
            return ApiResult.error(500, "服务器错误：" + e.getMessage());
        }
    }
}
