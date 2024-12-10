package org.swu.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.swu.apitemplate.ApiResult;
import org.swu.object.File;
import org.swu.repository.FileRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class SearchFile {
    @Autowired
    private final FileRepository fileRepository;

    SearchFile(FileRepository fileRepository){
        this.fileRepository = fileRepository;
    }

    public ApiResult<Map<String, Object>> searchFile(String name, String userid){
        Map<String, Object> response = new HashMap<>();
        try{
            // 处理用户输入，构建正则表达式
            // String regex = buildRegexFromInput(name);

            // 查询所有包含给定name的文件
            List<File> files = fileRepository.findByNameLikeRegex(name, userid);

            if(files != null){
                response.put("files", files);
                return ApiResult.of(200,"搜索到文件", response);
            }else{
                return ApiResult.of(404,"搜索的文件不存在", null);
            }
        }catch(Exception e){
            return ApiResult.of(500,"服务器错误", null);
        }
    }

    // 根据用户输入构建正则表达式
    private String buildRegexFromInput(String name) {
        // 转义特殊字符
        String escapedName = Pattern.quote(name); // 自动转义所有正则特殊字符

        // 构建一个正则表达式，使得每个字符之间可以有任意数量的字符
        // 例如，用户输入 "sto.txt" 会被转换为 ".*s.*t.*o.*\.txt"
        StringBuilder regexBuilder = new StringBuilder();
        for (char c : name.toCharArray()) {
            regexBuilder.append(".*").append(c);
        }
        regexBuilder.append(".*");  // 末尾匹配任意字符，允许文件名结尾

        // 处理文件扩展名
        regexBuilder.append("\\.").append(Pattern.quote(name.substring(name.lastIndexOf(".") + 1)));

        return regexBuilder.toString();
    }

}
