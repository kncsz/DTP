package priv.cgroup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import priv.cgroup.repository.TaskRepository;
import priv.cgroup.object.Task;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SearchTask {

    @Autowired
    private TaskRepository taskRepository;

    public Map<String, Object> search(String name, String startTime, String endTime) {
        Map<String, Object> response = new HashMap<>();

        try {
            // 转换日期格式
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy年M月d日");
            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd E HH:mm:ss");

            List<Task> tasks;

            // 如果只有 name
            if (name != null && !name.isEmpty() && (startTime == null || startTime.isEmpty()) && (endTime == null || endTime.isEmpty())) {
                tasks = taskRepository.findByName(name);

                // 如果只有 startTime 和 endTime
            } else if ((name == null || name.isEmpty()) && startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, inputFormatter);
                LocalDate endDate = LocalDate.parse(endTime, inputFormatter);

                if(startTimeIsLaterThanEndTime(startDate, endDate)){
                    response.put("status", 400);
                    response.put("message", "Start time cannot be after end time");
                    response.put("tasks", null);
                    return response;
                }

                // 转换为 LocalDateTime
                LocalDateTime startDateTime = startDate.atStartOfDay();  // 补充时间部分
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);  // 补充时间部分

                tasks = taskRepository.findByDateRange(startDateTime.format(dbFormatter), endDateTime.format(dbFormatter));

                // 如果三个参数都有
            } else if (name != null && !name.isEmpty() && startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
                LocalDate startDate = LocalDate.parse(startTime, inputFormatter);
                LocalDate endDate = LocalDate.parse(endTime, inputFormatter);

                if(startTimeIsLaterThanEndTime(startDate, endDate)){
                    response.put("status", 400);
                    response.put("message", "Start time cannot be after end time");
                    response.put("tasks", null);
                    return response;
                }

                // 转换为 LocalDateTime
                LocalDateTime startDateTime = startDate.atStartOfDay();  // 补充时间部分
                LocalDateTime endDateTime = endDate.atTime(23, 59, 59);  // 补充时间部分

                tasks = taskRepository.findByNameAndDateRange(name, startDateTime.format(dbFormatter), endDateTime.format(dbFormatter));

                // 如果都为空或没有匹配条件
            } else {
                response.put("status", 400);
                response.put("message", "Invalid parameters");
                response.put("tasks", null);
                return response;
            }

            response.put("status", 200);
            response.put("message", "success");
            response.put("tasks", tasks);

        } catch (Exception e) {
            response.put("status", 500);
            response.put("message", e.getMessage());
            response.put("tasks", null);
            return response;
        }

        return response;
    }

    private boolean startTimeIsLaterThanEndTime(LocalDate startDate, LocalDate endDate) {
        // 校验开始时间是否比结束时间晚
        return startDate.isAfter(endDate);
    }
}
