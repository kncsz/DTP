package priv.cgroup.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.cgroup.object.Task;
import priv.cgroup.repository.TaskRepository;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class CreateTask {

    @Autowired
    private final TaskRepository taskRepository;

    public CreateTask(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    /*
        @param commands 用户输入的命令
     */
    public Map<String, Object> createTask(Map<String, Object> requestBody) {
        Map<String, Object> response = new HashMap<>();

        List<String> commands = (List<String>) requestBody.get("commands");

        // 检查用户是否输入命令
        if (commands == null || commands.isEmpty()) {
            response.put("status", 400);
            response.put("message", "Command list is empty,please input a valid command");
            response.put("pid", null);
            return response;
        }

        // 获取前端参数
        String taskName = Optional.ofNullable(requestBody.get("taskName")).map(Object::toString).orElse("defaultName");
        String cgroupName = Optional.ofNullable(requestBody.get("cgroupName")).map(Object::toString).orElse("defaultCgroupName");
        String cgroupPath = Optional.ofNullable(requestBody.get("cgroupPath")).map(Object::toString).orElse("/default/path");
        String cgroupConfigDir = Optional.ofNullable(requestBody.get("cgroupConfigDir")).map(Object::toString).orElse("/default/config/dir");
        String description = Optional.ofNullable(requestBody.get("description")).map(Object::toString).orElse("defaultDescription");
        String fileName = Optional.ofNullable(requestBody.get("fileName")).map(Object::toString).orElse("");
        String filePath = Optional.ofNullable(requestBody.get("filePath")).map(Object::toString).orElse("");

        // 在数据库中查找是否有同名的taskName，如果有则阻止用户创建
        List<Task> tasks = taskRepository.findByName(taskName);
        boolean isNameConflict = tasks.stream().anyMatch(task -> task.getName().equals(taskName));
        if(isNameConflict) {
            response.put("status", 409);
            response.put("message", "Name conflict");
            response.put("pid", null);
            return response;
        }

        // 接收用户传入的命令并执行，但需要限制用户的输入，避免用户误操。
        // TODO

        try {
            // 进入用户文件目录
            String parentDir = filePath;

            // 获取命令
            String command = commands.getLast();

            /*
                组装命令，分几种情况
                1.若文件名fileName没有后缀，则执行filePath + File.seperator + fileName
                2.若后缀为.jar,则执行java -jar filePath + File.seperator + fileName
                3.若后缀为。py,则执行python filePath + File.seperator + fileName
                4.若后缀为.sh,则执行bash filePath + File.seperator + fileName
             */
            String fullCommand;
            if (fileName.endsWith(".jar")) {
                fullCommand = "java -jar " + filePath + File.separator + fileName;
            } else if (fileName.endsWith(".py")) {
                fullCommand = "python3 " + filePath + File.separator + fileName;
            } else if (fileName.endsWith(".sh")) {
                // 暂不支持shell
                fullCommand = "bash " + filePath + File.separator + fileName;
            } else if (fileName.endsWith(".exe")) {
                fullCommand = "wine " + filePath + File.separator + fileName;
            } else if(fileName.endsWith(".class")) {
                fullCommand = "java " + File.separator + fileName.substring(0, fileName.length()-6);
            } else {
                fullCommand = filePath + File.separator + fileName;
            }

            // TODO 判断文件是否损坏

            Process process = null;
            // 执行命令
            if(fullCommand.startsWith("/")) {
                Runtime runtime = Runtime.getRuntime();
                String[] cmdArray = {fullCommand};
                process = runtime.exec(cmdArray);
            }else{
                // 将 fullCommand 转换为 String[]，以空格为分割标准
                String[] cmdArray = fullCommand.split(" ");
                Runtime runtime = Runtime.getRuntime();
                process = runtime.exec(cmdArray);
//                ProcessBuilder pb = new ProcessBuilder(cmdArray);
//                process = pb.start();
            }

            // 获取进程pid
            ProcessHandle processHandler = process.toHandle();
            String pid = Long.toString(processHandler.pid());

            // 创建时间戳
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");
            String timestamp = now.format(formatter);

            // 分为用户指定了加入某个组和没有指定加入某个组
            if (!cgroupPath.equals("/default/path")) {
                RandomAccessFile file = new RandomAccessFile(cgroupPath + File.separator + "cgroup.procs", "rw");
                file.seek(0L); // 移动到文件末尾，准备追加
                file.writeBytes(pid + "\n");
                file.close();

                File configFile = new File(cgroupConfigDir + "/" + cgroupName + ".json");
                // 读取现有的配置文件
                ObjectMapper mapper = new ObjectMapper();
                JsonNode rootNode = mapper.readTree(configFile);
                ObjectNode tasksNode;

                // 检查是否存在 tasks 节点，如果不存在则创建
                if (rootNode.has("tasks")) {
                    tasksNode = (ObjectNode) rootNode.get("tasks");
                } else {
                    tasksNode = mapper.createObjectNode();
                    ((ObjectNode) rootNode).set("tasks", tasksNode);
                }

                // 直接设置 pid 值，而不是添加到数组
                tasksNode.put(pid, "");

                System.out.println("asuidgpiasugdiu");

                // 将修改后的内容写回配置文件
                mapper.writerWithDefaultPrettyPrinter().writeValue(configFile, rootNode);

                System.out.println("hahahahahaha");

                // 将任务信息加入数据库
                Task insertedTask = new Task(taskName, pid, cgroupPath, timestamp, description, true, "00:00:00");
                taskRepository.save(insertedTask);

                // 成功结果
                response.put("status", 200);
                response.put("message", "success");
                response.put("pid", pid);

            } else {
                // 将任务信息加入数据库
                Task insertedTask = new Task(taskName, pid, "", timestamp, description, true, "00:00:00");
                taskRepository.save(insertedTask);
                response.put("status", 200);
                response.put("message", "success");
                response.put("pid", pid);
            }

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", e.getMessage());
            response.put("pid", null);
            return response;
        }
        return response;
    }
}
