package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swu.apitemplate.ApiResult;
import org.swu.repository.FileRepository;
import org.swu.object.File;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GetAllFiles {

    @Autowired
    private final FileRepository fileRepository;

    public GetAllFiles(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    /**
     * 获取所有文件信息
     *
     * @return ApiResult<Map<String, Object>> 包含文件列表的结果
     */
    public ApiResult<Map<String, Object>> getAllFiles() {
        try {
            // 从 FileRepository 获取所有文件数据
            List<File> files = fileRepository.findAll();

            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("files", files);  // 将文件列表放入返回的数据中

            // 返回成功的 ApiResult
            return ApiResult.of(200, "查询成功", data);

        } catch (Exception e) {
            // 如果发生错误，返回失败的 ApiResult
            return ApiResult.error(500, "服务器错误：" + e.getMessage());
        }
    }
}

