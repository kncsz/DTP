package priv.cgroup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import priv.cgroup.object.Cgroup;
import priv.cgroup.object.Task;
import priv.cgroup.repository.CgroupRepository;
import priv.cgroup.repository.TaskRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@Service
public class CheckTaskExsistence {
    @Autowired
    private final TaskRepository taskRepository;

    @Autowired
    private final CgroupRepository cgroupRepository;

    public CheckTaskExsistence(TaskRepository taskRepository, CgroupRepository cgroupRepository) {
        this.taskRepository = taskRepository;
        this.cgroupRepository = cgroupRepository;
    }

    @Scheduled(fixedRate = 30000)
    public void checkTaskExsistence() {
        List<Task> tasks = taskRepository.findAll();

        for (Task task : tasks) {
            String pid = task.getPid();
            if (!isPidRunning(pid)) {
                // 使用Example和findOne查询Cgroup
                Cgroup exampleCgroup = new Cgroup();
                exampleCgroup.setCgroupPath(task.getPath()); // 使用任务的路径作为查询条件

                Example<Cgroup> example = Example.of(exampleCgroup);
                Cgroup cgroup = cgroupRepository.findOne(example).orElse(null);

                if (cgroup != null) {
                    String configDir = cgroup.getCgroupConfigDir();
                    String name = cgroup.getName();
                    String configFilePath = configDir + File.separator + name + ".json";

                    // 删除配置文件中 tasks 包含该 pid 的条目
                    removePidFromConfigFile(configFilePath, pid);
                }

                // 如果PID不存在，则删除该任务条目
                taskRepository.delete(task);
                System.out.println("Deleted task with PID: " + pid);
            }
        }
    }

    // 检查PID是否在运行
    private boolean isPidRunning(String pid) {
        try {
            String[] command = {"bash", "-c", "ps -p " + pid};
            ProcessBuilder processBuilder = new ProcessBuilder(command);
            Process process = processBuilder.start();

            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(pid)) {
                    return true;  // 找到了PID，说明它正在运行
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;  // 如果没有找到PID，说明它没有在运行
    }

    // 从配置文件中删除特定 pid 的条目
    private void removePidFromConfigFile(String configFilePath, String pid) {
        try {
            File configFile = new File(configFilePath);
            if (!configFile.exists()) {
                return;
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String content = new String(Files.readAllBytes(Paths.get(configFilePath)));
            ObjectNode configContent = (ObjectNode) objectMapper.readTree(content);

            if (configContent.has("tasks")) {
                ObjectNode tasksNode = (ObjectNode) configContent.get("tasks");

                if (tasksNode.has(pid)) {
                    tasksNode.remove(pid);  // 删除特定的 PID 键
                }

                objectMapper.writerWithDefaultPrettyPrinter().writeValue(configFile, configContent);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
