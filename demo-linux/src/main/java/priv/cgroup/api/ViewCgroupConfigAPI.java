package priv.cgroup.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import priv.cgroup.service.ViewCgroupConfig;

import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins="true")
public class ViewCgroupConfigAPI {
    @Autowired
    private ViewCgroupConfig viewCgroupConfigService;

    @GetMapping("/api/user/viewCgroupConfig")
    public Map<String, Object> viewCgroupConfig(@RequestParam("name") String name,
                                                @RequestParam("controllerID") String controllerID,
                                                @RequestParam("hierarchy") int hierarchy,
                                                @RequestParam("filter") int filter) {
        Map<String, Object> response = new HashMap<>();
        response = viewCgroupConfigService.viewCgroupConfig(name, controllerID, hierarchy, filter);
        return response;
    }
}
