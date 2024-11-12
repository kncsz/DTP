package priv.cgroup.service;

import cn.hutool.core.util.StrUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.cgroup.object.Cgroup;
import priv.cgroup.repository.CgroupRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SelectByCgroupPath {
    @Autowired
    private final CgroupRepository cgroupRepository;

    public SelectByCgroupPath(CgroupRepository cgroupRepository) {
        this.cgroupRepository = cgroupRepository;
    }

    public Map<String, Object> selectByCgroupPathPrefix(String cgroupPath) {
        Map<String, Object> response = new HashMap<>();
        try{
            List<Cgroup> selectedCgroup = cgroupRepository.findByCgroupPathPrefix(cgroupPath);
            if(!StrUtil.isEmptyIfStr(selectedCgroup)){
                response.put("status", 200);
                response.put("message", "success");
                response.put("cgroups", selectedCgroup);
            }else{
                response.put("status", 400);
                response.put("message", "error");
                response.put("cgroups", null);
            }
        }catch (Exception e){
            response.put("status",500);
            response.put("message",e.getMessage());
            response.put("cgroups", null);
            return response;
        }

        return response;
    }
}
