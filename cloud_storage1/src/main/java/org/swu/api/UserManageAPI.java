package org.swu.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.swu.apitemplate.ApiResult;
import org.swu.service.Login;
import org.swu.util.JwtTokenProvider;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins="true")
public class UserManageAPI {
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private Login loginservice;

    // 登录接口，前端传递token（如果有）
    @GetMapping("/login")
    public ApiResult<Map<String, Object>> login(@RequestParam(value = "token", required = false) String token) {
        return loginservice.login(token);
    }
}
