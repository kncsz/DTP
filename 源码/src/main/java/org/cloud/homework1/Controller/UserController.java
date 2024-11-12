package org.cloud.homework1.Controller;

import com.google.common.util.concurrent.RateLimiter;
import org.cloud.homework1.Entity.Device;
import org.cloud.homework1.JwtUtil;
import org.cloud.homework1.Service.DeviceService;
import org.cloud.homework1.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private JwtUtil jwtUtil;

    // 限流器
    private Map<String, RateLimiter> loginRateLimiters = new ConcurrentHashMap<>();
    private Map<Long, RateLimiter> deviceRateLimiters = new ConcurrentHashMap<>();

    // 注册接口
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> user) {
        String username = user.get("username");
        String password = user.get("password");
        String email = user.get("email");

        String result = userService.registerUser(username, password, email);
        if ("用户注册成功".equals(result)) {
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
    }

    // 登录接口，限制每个IP每分钟最多5次请求
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials, HttpServletRequest request) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        String ip = request.getRemoteAddr();

        // 获取或创建针对该IP的RateLimiter，限速为每分钟5次
        loginRateLimiters.putIfAbsent(ip, RateLimiter.create(5.0 / 60.0));  // 每分钟5次
        RateLimiter rateLimiter = loginRateLimiters.get(ip);

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("请求过于频繁，请稍后再试");
        }

        String token = userService.loginUser(username, password);
        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("用户名或密码错误");
        }

        Map<String, String> map = new ConcurrentHashMap<>();
        map.put("token", token);
        return ResponseEntity.ok(map);
    }

    // 获取设备详情接口，限制每个设备ID每小时最多3次请求
    @GetMapping("/devices/{id}")
    public ResponseEntity<?> getDevice(@PathVariable Long id, @RequestHeader("Authorization") String token) {
        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("无效的Token");
        }


        // 获取或创建针对该设备ID的RateLimiter，限速为每小时3次
        deviceRateLimiters.putIfAbsent(id, RateLimiter.create(3.0 / 3600.0));  // 每小时3次
        RateLimiter rateLimiter = deviceRateLimiters.get(id);

        if (!rateLimiter.tryAcquire()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("请求过于频繁，请稍后再试");
        }

        Device device = deviceService.getDeviceById(id);
        if (device == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("设备未找到");
        }

        return ResponseEntity.ok(device);
    }
}

