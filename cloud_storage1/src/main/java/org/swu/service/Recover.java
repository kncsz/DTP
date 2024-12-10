package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.swu.apitemplate.ApiResult;
import org.swu.exception.InvalidFileException;
import org.swu.repository.FileRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
public class Recover {
    @Autowired
    private final FileRepository fileRepository;

    Recover(FileRepository fileReporitory){
        this.fileRepository = fileReporitory;
    }

    public ApiResult<Map<String, Object>> recover(@RequestBody Map<String, Object> requestBody){
        try{
            if (!requestBody.containsKey("paths")) {
                return ApiResult.of(400, "参数缺失");
            }

            List<String> paths = (List<String>) requestBody.get("paths");
            if(paths == null){
                return ApiResult.of(400, "参数无效");
            }

            for (String path : paths) {
                // 分割路径
                int lastSlashIndex = path.lastIndexOf("/");
                String dirName = path.substring(0, lastSlashIndex);
                String fileName = path.substring(lastSlashIndex + 1);

                String recovertime = getCurrentTime();
                // 调用服务方法更新数据库
                updateRecoverStatus(dirName, fileName, recovertime);
            }
            return ApiResult.of(200, "文件恢复成功");
        }catch (JpaSystemException e) {
            return ApiResult.of(10002, "数据库异常：" + e.getMessage());
        } catch (InvalidFileException e) {
            return ApiResult.of(10001, "文件路径无效：" + e.getMessage());
        } catch (Exception e) {
            return ApiResult.of(500, "服务器未知错误：" + e.getMessage());
        }
    }

    private void updateRecoverStatus(String dirName, String fileName, String recovertime) {
        fileRepository.updateIntrashStatus(dirName, fileName, false, recovertime);
    }

    private String getCurrentTime(){
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");
        return now.format(formatter);
    }
}
