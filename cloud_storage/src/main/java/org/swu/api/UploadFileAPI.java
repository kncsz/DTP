package org.swu.api;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swu.service.UploadFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins="true")
public class UploadFileAPI {
    @Autowired
    private UploadFile uploadFileService;

    @PostMapping("/api/fileManage/uploadFile")
    public Map<String, Object> uploadFile(HttpServletRequest request) {
        Map<String, Object> response = new HashMap<String, Object>();
        response = uploadFileService.uploadFile(request);
        return response;
    }
}
