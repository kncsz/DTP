package org.swu.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.swu.repository.FileRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Service
public class UploadFile {
    @Autowired
    private final FileRepository fileRepository;

    UploadFile(FileRepository fileReporitory){
        this.fileRepository = fileReporitory;
    }

    /**
     * @param request 请求参数,包含文件和路径
     */

    public Map<String, Object> uploadFile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<>();

        MultipartFile file;
        String path = "";
        String fileName = "";
        String description = "";
        String parentDir = "";
        String dirName = "";
        java.io.File fileForCheck = null;

        try{
            // 测试上传文件
            MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
            file = multipartRequest.getFile("file");

            assert file != null;
            fileName = file.getOriginalFilename();

            // 指向上传到服务器上的文件目录
            Path filePath = Paths.get("/home/kncsz/cloudstorage/userfile/user1/" + fileName);

            // 将上传的文件保存到目标路径
            Files.copy(file.getInputStream(), filePath);

            // 修改文件所有权
            changeFileOwnership("/home/kncsz/cloudstorage/userfile/user1/" + fileName, "kncsz");

            // 获取xdp分析结果

            // 根据结果判断数据包类型

            // 校验数据完整性

            // 数据加密

            // 上传

            // 更新数据库

            // 返回给前端成功结果
            response.put("status", 200);
            response.put("message", "success");
        }catch (IOException | InterruptedException e) {
            // 返回给前端失败结果
            response.put("status", 500);
            response.put("message", "Failed to upload file: " + e.getMessage());
            return response;
        }
        return response;
    }

    private void changeFileOwnership(String filePath, String user) throws IOException, InterruptedException {
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "chown", user, filePath);
        Process process = processBuilder.start();
        int resultCode = process.waitFor();
        if (resultCode != 0) {
            throw new IOException("Failed to change file ownership.");
        }
    }
}
