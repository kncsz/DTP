package priv.cgroup.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.cgroup.service.SelectByCgroupPath;

import java.util.HashMap;
import java.util.Map;

@RestController//解析为json而不是视图
@CrossOrigin(origins = "true")//允许跨域
public class SelectByCgroupPathAPI {
    @Autowired
    private SelectByCgroupPath selectByCgroupPathService;

    // 查询某个组下的下一级的所有子组
    @GetMapping("/api/user/selectChildCgroup")
    public Map<String, Object> selectByCgroupPathService(@RequestParam("cgroupPath") String cgroupPath) {
        Map<String, Object> response = new HashMap<>();
        response = selectByCgroupPathService.selectByCgroupPathPrefix(cgroupPath);
        return response;
    }
}
