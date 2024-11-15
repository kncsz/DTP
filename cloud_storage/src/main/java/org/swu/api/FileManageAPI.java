package org.swu.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swu.service.UploadFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/fileManage")
@CrossOrigin(origins="true")
public class FileManageAPI {
    @Autowired
    private UploadFile uploadFileService;

    @PostMapping("/uploadFile")
    public Map<String, Object> uploadFile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response = uploadFileService.uploadFile(request);
        return response;
    }

}
