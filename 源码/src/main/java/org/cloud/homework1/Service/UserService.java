package org.cloud.homework1.Service;


import org.cloud.homework1.Entity.User;
import org.cloud.homework1.JwtUtil;
import org.cloud.homework1.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // 注册用户
    public String registerUser(String username, String password, String email) {
        if (password.length() < 8 || !password.matches(".*[a-zA-Z].*") || !password.matches(".*\\d.*")) {
            return "密码必须至少8个字符，包含字母和数字";
        }

        if (userRepository.findByUsername(username) != null) {
            return "用户名已存在";
        }

        User newUser = new User(username, password, email, "USER");
        userRepository.save(newUser);

        return "用户注册成功";
    }

    // 登录用户，返回JWT Token
    public String loginUser(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null || !user.getPassword().equals(password)) {
            return null;  // 登录失败
        }

        return jwtUtil.generateToken(user.getId(), user.getRole());  // 登录成功，返回Token
    }
}
