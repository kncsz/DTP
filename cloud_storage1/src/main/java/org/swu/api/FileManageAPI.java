package org.swu.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.swu.apitemplate.ApiResult;
import org.swu.service.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/fileManage")
@CrossOrigin(origins="true")
public class FileManageAPI {
    @Autowired
    private UploadFile uploadFileService;

    @Autowired
    private DownloadFile downloadFileService;

    @Autowired
    private SearchFile searchFileService;

    @Autowired
    private Recycle recycleService;

    @Autowired
    private Recover recoverService;

    @Autowired
    private DeleteFile deleteFileService;

    @Autowired
    private ShowFile showFileService;

    @Autowired
    private ShowDetails showDetailsService;

    @Autowired
    private CreateDirectory createDirectoryService;

    @Autowired
    private GetAllFiles getAllFilesService;
    /**
     * 批量上传
     * @param request
     *
     */
//    @PostMapping("/uploadMultiFile")
//    public ApiResult<Map<String, Object>> searchFile(@RequestParam("name") String name) {
//        Map<String, Object> response = new HashMap<String, Object>();
//        return searchFileService.searchFile(name);
//    }


    @PostMapping("/uploadFile")
    public ApiResult<Map<String, Object>> uploadFile(HttpServletRequest request) {
        return uploadFileService.uploadFile(request);
    }

    @PostMapping("/createDirectory")
    public ApiResult<Map<String, Object>> createDirectory(@RequestBody Map<String, Object> requestBody) {
        return createDirectoryService.createDirectory(requestBody);
    }

    @GetMapping("/downloadFile")
    public ResponseEntity<Resource> downloadFile(@RequestParam("path") String path, @RequestParam("userid") String userid) {
        return downloadFileService.downloadFile(path, userid);
    }

    @GetMapping("/searchFile")
    public ApiResult<Map<String, Object>> searchFile(@RequestParam("name") String name, @RequestParam("userid") String userid) {
        return searchFileService.searchFile(name, userid);
    }

    @GetMapping("/showFile")
    public ApiResult<Map<String, Object>> showFile(@RequestParam("path") String path) {
        return showFileService.showFile(path);
    }

    @GetMapping("/showDetails")
    public ApiResult<Map<String, Object>> showDetails(@RequestParam("userid") String userid) {
        return showDetailsService.showDetails(userid);
    }

    @PutMapping("/recycle")
    public ApiResult<Map<String, Object>> recycle(@RequestBody Map<String, Object> requestBody) {
        return recycleService.recycle(requestBody);
    }

    @PutMapping("/recover")
    public ApiResult<Map<String, Object>> recover(@RequestBody Map<String, Object> requestBody) {
        return recoverService.recover(requestBody);
    }

    @DeleteMapping("/deleteFile")
    public ApiResult<Map<String, Object>> deleteFile(@RequestBody Map<String, Object> requestBody) {
        return deleteFileService.deleteFile(requestBody);
    }

    @GetMapping("/getAllFiles")
    public ApiResult<Map<String, Object>> getAllFiles() {
        return getAllFilesService.getAllFiles();
    }
}
