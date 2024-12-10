package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swu.apitemplate.ApiResult;
import org.swu.exception.IpAccessTooFrequentException;
import org.swu.object.FileStats;
import org.swu.object.User;
import org.swu.repository.FileRepository;
import org.swu.repository.UserRepository;
import org.swu.util.JwtTokenProvider;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class Login {
    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final FileRepository fileRepository;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    Login(UserRepository userRepository, FileRepository fileRepository){
        this.userRepository = userRepository;
        this.fileRepository = fileRepository;
    }

    public ApiResult<Map<String, Object>> login(String token){
        Map<String, Object> response = new HashMap<>();

        /**
         * @throws IpAccessTooFrequentException 请求太频繁
         */
        try{
            if (token.isEmpty()) {
                // 如果Token为空，则生成一个新的Token
                String userId = generateUserId();  // 生成9位用户ID
                String username = generateUsername(); // 生成随机用户名
                String datestamp = generateDatestamp(); // 生成创建时间戳
                String newToken = jwtTokenProvider.generateToken(userId);

                // 将新生成的用户信息存入数据库
                saveUserToDatabase(userId, username, datestamp);
                FileStats userFileStats = new FileStats(userId, 1, 1, 1, 1, 1,
                        1, 1, 1, 1);
                fileRepository.save(userFileStats);
                response.put("token",newToken);
                response.put("userid",userId);
                response.put("name",username);
                return ApiResult.of(200,"新用户已创建，登陆成功", response);
            }

            // 如果Token不为空，验证其有效性
            if (!jwtTokenProvider.validateToken(token)) {
                // 如果Token过期或无效
                String userId = jwtTokenProvider.getUserIdFromToken(token);

                // 生成新的Token
                String newToken = jwtTokenProvider.generateToken(userId);

                // 查询旧userid
                User user = userRepository.findByUserid(userId);
                String oldUserId = user.getUserid();

                // 生成新userid
                String newUserId = generateUserId();
                User modifiedUser = new User();
                modifiedUser.setUserid(newUserId);
                modifiedUser.setName(user.getName());
                modifiedUser.setDatestamp(user.getDatestamp());

                userRepository.save(modifiedUser);
                fileRepository.updateFileUserId(newUserId, oldUserId);
                fileRepository.updateFileStatsUserId(newUserId, oldUserId);
                userRepository.deleteByUserid(oldUserId);

                response.put("token",newToken);
                response.put("userid",newUserId);
                response.put("name",user.getName());
                return ApiResult.of(200,"token已更新，登陆成功", response);
            }

            // Token有效，允许登录
            response.put("token",token);
            return ApiResult.of(200,"登陆成功", response);
        }catch(IpAccessTooFrequentException e){
            return ApiResult.of(10000,"请求太频繁", response);
        }
    }

    // 生成9位用户ID
    private String generateUserId() {
        Random random = new Random();
        StringBuilder userId = new StringBuilder();
        for (int i = 0; i < 9; i++) {
            userId.append(random.nextInt(10));  // 生成数字
        }
        return userId.toString();
    }

    // 生成用户名（由大小写字母和数字组成，不超过9位）
    private String generateUsername() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder username = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 9; i++) {
            int index = random.nextInt(characters.length());
            username.append(characters.charAt(index));
        }
        return username.toString();
    }

    // 生成时间戳
    private String generateDatestamp() {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");
        return now.format(formatter);
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        return sdf.format(new Date());
    }

    // 保存用户到数据库
    private void saveUserToDatabase(String userId, String username, String datestamp) {
        User user = new User();
        user.setUserid(userId);
        user.setName(username);
        user.setDatestamp(datestamp);

        userRepository.save(user);  // 使用JPA保存到数据库
    }

}
